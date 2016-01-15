package controllers.chatroom;

import static akka.pattern.Patterns.ask;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;

import models.chatroom.ChatRoom;
import models.chatroom.ChatRoomTime;
import models.users.AccountAppId;
import models.users.AppProfile;
import models.users.InterestFan;
import play.Logger;
import play.i18n.Messages;
import play.libs.Akka;
import play.libs.F;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.WebSocket;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import controllers.AppController;
import controllers.application.ApplicationController;

/**
 * 聊天socket
 * @author lipf
 *
 */
public class ChatRoomController extends AppController {
	
	static ConcurrentHashMap<String, ActorRef> s_rooms=new ConcurrentHashMap<String, ActorRef>();
	
	static Map<String,Object> isOnlines = new HashMap<String,Object>();
	
	static Map<String,Set<String>> roomNum = new HashMap<String,Set<String>>();
	
	
	 /**
	  * 加入到socket
	  */
  public WebSocket<JsonNode> join(final String roomId,final String toUserName,final String toUserId) {
	
	//Logger.debug("-------------------into websocket join method");
	  
	final String userId = session("userId");
	
	AppProfile user = AppProfile.find.where()
			.eq("account_id", userId)
			.eq("app_id", session("appId"))
			.findUnique();
	
	if (null == user) {
	    return null;
	}
	final String userName = user.name;
	
	//Logger.debug("-------------------get username by userId in session:userId="+userId+",username="+userName);
	
	ActorRef room = s_rooms.get(roomId);             
	
	
  	if(null==room){
  	AppProfile roomUser = AppProfile.find.where()
  				.eq("account_id", roomId)
  				.eq("app_id", session("appId"))
  				.findUnique();
  	//Logger.debug("-------------------create chatRoom,the chatRoomUser is:"+roomUser);
  		if(null==roomUser){
  			return  null;
  		}
  		
  		s_rooms.put(roomId, Akka.system().actorOf(ChatRoom.props(roomId)));
  		isOnlines.put(roomId, false);
  	}
  	
  	if(userId.equals(roomId)){
  		isOnlines.put(roomId, true);
  	}
      return new WebSocket<JsonNode>() {
          
          // Called when the Websocket Handshake is done.
          public void onReady(WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out){
              
              // Join the chat room.
              try { 
                  join(roomId, userName,userId, in, out);
              } catch (Exception ex) {
                  ex.printStackTrace();
              }
          }
      };
  }
  
  public Result notifySendingPresent(String roomId, String roomUser, String username, String userId, String presentName, Integer number) {
	  if (null != s_rooms) {
		  ActorRef room = s_rooms.get(roomId);
		  
		  if (null != room) {		      
			  //String message = String.format(Messages.get("present.sendmessage"), username, presentName, number);
			  room.tell(new ChatRoom.Present(username, roomId, presentName, number, (Boolean) isOnlines.get(roomId)), null);			  
		  }
	  }
	  
	  return ok();
  }
  
  public void join(final String roomId, final String username,final String userId, WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out) throws Exception{
	  boolean isOnline = (Boolean) isOnlines.get(roomId);

	  for(Map.Entry<String,ActorRef> entry : s_rooms.entrySet()){
        ActorRef rm=entry.getValue();
        rm.tell(new ChatRoom.Quit(username,userId,isOnline,userId.equals(roomId)), null);
	  }
    final ActorRef room = s_rooms.get(roomId);
    
    		
    String result = (String) Await.result(
					ask(room, new ChatRoom.Join(username,userId, out,isOnline,userId.equals(roomId)), 10000), 
					Duration.create(10, SECONDS)
	);                                                            //?
    Set<String> roomUser = roomNum.get(roomId);                   //?
    if(null==roomUser){
    	roomUser = new HashSet<String>();
    }
    boolean joinRoom = roomUser.add(userId);        //   userId   in the roomUser
    if(joinRoom){
    	roomNum.put(roomId, roomUser);               // create set<String>  roomId---roomUser  to account num of users
    }
    
    if("OK".equals(result)) {                     //websocket   hankshake  ???
        // For each event received on the socket,
        in.onMessage(new F.Callback<JsonNode>() {
            public void invoke(JsonNode event) {
                // Send a Talk message to the room.
                boolean isOnline = (Boolean) isOnlines.get(roomId);
                room.tell(new ChatRoom.Talk(username,userId,event.get("toUser").asText(),event.get("toUserId").asText(), event.get("text").asText(),isOnline,userId.equals(roomId)), null);
                //Logger.debug("-------------------charRoom takl:username="+username+",userId="+userId+",message="+event.get("text").asText());
            }
        });

        // When the socket is closed.
        in.onClose(new F.Callback0() {
            public void invoke() {
                // Send a Quit message to the room.
            	if(userId.equals(roomId)){
            		isOnlines.put(roomId, false);
              	}
                room.tell(new ChatRoom.Quit(username,userId,(Boolean)isOnlines.get(roomId),userId.equals(roomId)), null);
                Set<String> roomUser = roomNum.get(roomId);
                boolean joinRoom = roomUser.remove(userId);
                if(joinRoom){
                	roomNum.put(roomId, roomUser);
                }
                Logger.debug("-------------------charRoom quit:username="+username+",userId="+userId+",message=");
            }
        });

    } else {
    	roomUser = roomNum.get(roomId);
        joinRoom = roomUser.remove(userId);
        if(joinRoom){
        	roomNum.put(roomId, roomUser);
        }
        // Cannot connect, create a Json error.
        ObjectNode error = Json.newObject();
        error.put("error", result);

        // Send the error to the socket.
        out.write(error);

    }
  }
  
  public Result getRoomPeopleNum(String roomId){
	  if(null==roomNum.get(roomId)){
		  return ok(Json.toJson(0));
	  }
	  return ok(Json.toJson(roomNum.get(roomId).size()));
  }
  
  public Result startDraw() {
      String userId = session("userId");
      
      if (userId != null) {
          ActorRef room = s_rooms.get(userId);
          
          if (room != null) {
              room.tell(new ChatRoom.Draw(userId, userId, session("appId")), null);
          }
      }
      
      return ok();
  }
  
  public Result draw(String roomId) {
      String userId = session("userId");
      
      if (userId != null) {
          ActorRef room = s_rooms.get(roomId);
          room.tell(new ChatRoom.Draw(roomId, userId, session("appId")), null);
      }
      
      return ok();
  }
  
  public Result getRoomPeopleByPage(String roomId, Integer pageNumber, Integer sizePerPage) {
	  
      Object[] peopleList = roomNum.get(roomId).toArray();
//	  List<ChatRoomTime> peopleList = ChatRoomTime.find.where()
//				.eq("room_id", roomId)
//				.eq("action", "join")
//				.setFirstRow(pageNumber*sizePerPage)
//				.setMaxRows(sizePerPage)
//				.findList();
      int nStart = pageNumber * sizePerPage;
      int nEnd = nStart + sizePerPage;
      
      List<String> userIds = new ArrayList<String>();
      
      for (int i = pageNumber*sizePerPage; i < nEnd; i++) {
          if (i >= peopleList.length) {
              break;
          }
          
          userIds.add((String)peopleList[i]);
      }
		
		List<AppProfile> profileList = new ArrayList<AppProfile>();
		
		for (String userId : userIds) {
			AccountAppId id = new AccountAppId(userId, session("appId"));
			
			AppProfile profile = AppProfile.find.byId(id);
			
			if (profile != null && profile.head_image != null) {
				int idxOfSite = profile.head_image.indexOf(Messages.get("site.name"));
				if (idxOfSite >= 0) {
					String physicalFile = "public" + profile.head_image.substring(
							idxOfSite + Messages.get("site.name").length());
					
					File file = new File(physicalFile);
					BufferedImage img = null;
					try {
						img = ImageIO.read(file); 
			            BufferedImage blurImg = ApplicationController.resizeImage(img, 60);
						profile.head_imgbase64 = ApplicationController.getImageBase64(blurImg);					
					}
					catch (Throwable e) {
						img = null;
					}
				}
			}		
			profileList.add(profile);
		}
		return ok(Json.toJson(profileList));
  }
}
