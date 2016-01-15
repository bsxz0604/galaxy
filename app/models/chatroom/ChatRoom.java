/** Author: Pengfei Li
 * Date: 2015-07-01
 * Description: ChatRoom. 
 */
package models.chatroom;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import play.Logger;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.WebSocket;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Creator;
import controllers.common.CodeGenerator;
import models.common.Account;
import models.users.CharmValue;
import models.users.InterestFan;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ChatRoom extends UntypedActor {

    // Members of this room.
    protected Map<String, WebSocket.Out<JsonNode>> m_members = null;
    //private List<History> recordList = null;
    //private final int MAX_MSG_SIZE = 4;
    //private final int SIZE_THRESHOLD = 20;
    protected String m_roomId=null;
    private SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
    private RaceChatModel m_chatModel = null;
    

    public static Props props(final String roomId){
        return Props.create(new Creator<ChatRoom>(){

            @Override
            public ChatRoom create() throws Exception {
                return new ChatRoom(roomId);
            }
        });
    }

    public ChatRoom(String roomId){
        m_members=new HashMap<String, WebSocket.Out<JsonNode>>();
        //recordList = new ArrayList<History>();
        m_roomId=roomId;
        m_chatModel = new RaceChatModel(roomId, m_members);
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (!m_chatModel.HandleMessage(message)) {
            unhandled(message);                
        }
        else {
            if (message instanceof Join) {
                getSender().tell("OK", getSelf());                
            }            
        }
        
        //if (message instanceof Present) {
            //Present present = (Present)message;
            //String msgTxt = String.format(Messages.get("present.sendmessage"), present.username, present.presentName, present.number);
            //notifyAll("present", present.username, "", "", present.toUserId, msgTxt, present.isOnline, false);
        //} else if(message instanceof Join) {

            // Received a Join message
            //Join join = (Join)message;

            // Check if this username is free.
            //if(m_members.containsKey(join.userId)) {
            //    getSender().tell("This username is already used", getSelf());
            //} else {
            // m_members.put(join.userId, join.channel);
                
                //for (History history : recordList) {
                //    sendHistoryMessage(join.channel, history);
                //}
                
                //notifyAll("join", join.username,join.userId,"","", "has entered the room",join.isOnline,join.isOwners);                    
                
//                ChatRoomTime joinTime = ChatRoomTime.find.where()
//                        .eq("account_id", join.userId)
//                        .findUnique();
//                if (joinTime == null) {
//                	joinTime = new ChatRoomTime();
//                	joinTime.id = CodeGenerator.GenerateUUId();
//                	joinTime.room_id = m_roomId;
//                    joinTime.action = "join";
//                    joinTime.action_time = new Date();
//                    joinTime.account =  Account.find.byId(join.userId);
//                    joinTime.send_gift_number = CharmValue.find.where()
//    					  .eq("sender_id", join.userId).findRowCount();
//                    Ebean.save(joinTime);
//                } else {
//                	joinTime.action = "join";
//                	joinTime.action_time = new Date();
//                	joinTime.room_id = m_roomId;
//                	joinTime.send_gift_number = CharmValue.find.where()
//      					  .eq("sender_id", join.userId).findRowCount();
//                	Ebean.update(joinTime);
//                }
//                getSender().tell("OK", getSelf());
            //} else if(message instanceof Talk)  {

            // Received a Talk message
            //Talk talk = (Talk)message;
//            notifyAll("talk", talk.username,talk.userId,talk.toUserName,talk.toUserId, talk.text,talk.isOnline,talk.isOwners);
//            
//            synchronized(this) {
//                if (recordList.size() >= MAX_MSG_SIZE) {
//                    recordList.remove(0);
//                }
//                recordList.add(new History(new Date(), talk));
//            }
            

        //} else if(message instanceof Quit)  {

            // Received a Quit message
//            Quit quit = (Quit)message;
//            if(m_members.containsKey(quit.userId)){
//                m_members.remove(quit.userId);
//                
//                notifyAll("quit", quit.username,quit.userId,"","", "has left the room",quit.isOnline,quit.isOwners);                    
//                
//                ChatRoomTime joinTime = ChatRoomTime.find.where()
//                        .eq("account_id", quit.userId)
//                        .findUnique();
//                if (joinTime == null) { // joinTime cannot be null for quit action
//                	unhandled(message);  
//                } else {
//                	joinTime.action = "exit";
//                	joinTime.action_time = new Date();
//                	joinTime.room_id = m_roomId;
//                	joinTime.send_gift_number = CharmValue.find.where()
//      					  .eq("sender_id", quit.userId).findRowCount();
//                	Ebean.update(joinTime);
//                }
//        } else {
//            unhandled(message);
//        }
//    }

  }

    // Send a Json event to all members
    
//    public void notifyAll(String kind, String user,String userId,String toUserName,String toUserId, String text,boolean isOnline,boolean isOwners) {
//
//        if (m_members.values().size() > SIZE_THRESHOLD) {
//            if (kind.compareToIgnoreCase("talk") != 0 &&
//                kind.compareToIgnoreCase("present") != 0) {
//                if (userId == null || m_roomId == null) {
//                    return;
//                }
//                
//                if (userId.compareToIgnoreCase(m_roomId) != 0) {
//                    return;
//                }
//                
//                //ban zhulaile
//                Logger.info(kind + ":" + userId + " " + user);
//            }
//        }
//        
//        for(WebSocket.Out<JsonNode> channel: m_members.values()) {
//            ObjectNode event = Json.newObject();
//            event.put("kind", kind);
//            event.put("user", user);
//            event.put("message", text);
//            event.put("userId", userId);
//            event.put("toUser", toUserName);
//            event.put("toUserId", toUserId);
//            event.put("isOnline", isOnline);
//            event.put("isOwners", isOwners);
//            event.put("peopleNum", m_members.keySet().size());
//            
////            ArrayNode m = event.putArray("members");
////            for(String u: m_members.keySet()) {
////                m.add(u);
////            }
//
//            try {
//                channel.write(event);                
//            }
//            catch (Throwable e) {
//            }
//        }
//    }
//    
//    private void sendHistoryMessage(WebSocket.Out<JsonNode> channel, History history) {
//        if (m_members.values().size() > SIZE_THRESHOLD) {
//            return;
//        }
//        
//        ObjectNode event = Json.newObject();
//        event.put("kind", "history");
//        event.put("user", history.talk.username);
//        event.put("message", history.talk.text);
//        event.put("userId", history.talk.userId);
//        event.put("toUser", history.talk.toUserName);
//        event.put("toUserId", history.talk.toUserId);
//        event.put("isOnline", history.talk.isOnline);
//        event.put("isOwners", history.talk.isOwners);
//        event.put("peopleNum", m_members.keySet().size());
//        event.put("time", sdf.format(history.date));
//        
//        channel.write(event);
//    }

    // -- Messages

    public static class Join {

        final String username;
        final String userId;
        final WebSocket.Out<JsonNode> channel;
        final boolean isOnline ;
        final boolean isOwners;
        
        public Join(String username,String userId, WebSocket.Out<JsonNode> channel,boolean isOnline,boolean isOwners) {
            this.username = username;
            this.userId = userId;
            this.channel = channel;
            this.isOnline =isOnline ;
            this.isOwners = isOwners;
        }

    }

    public static class Talk {

        final String username;
        final String userId;
        final String text;
        final String toUserName;
        final String toUserId;
        final boolean isOnline;
        final boolean isOwners;
        
        public Talk(String username,String userId,String toUserName,String toUserId, String text,boolean isOnline,boolean isOwners) {
            this.username = username;
            this.userId = userId;
            this.toUserName = toUserName;
            this.toUserId = toUserId;
            this.text = text;
            this.isOnline = isOnline;
            this.isOwners = isOwners;
        }
    }

    public static class Quit {

        final String username;
        final String userId;
        final boolean isOnline;
        final boolean isOwners;
        
        public Quit(String username,String userId,boolean isOnline,boolean isOwners) {
            this.username = username;
            this.userId = userId;
            this.isOnline = isOnline;
            this.isOwners = isOwners;
        }

    }
    
    public static class Present {
        final String username;
        final String toUserId;
        final String presentName;
        final Integer number;
        final boolean isOnline;
        
        public Present(String username, String toUserId, String presentName, Integer number, boolean isOnline) {
            this.username = username;
            this.toUserId = toUserId;
            this.presentName = presentName;
            this.number = number;
            this.isOnline = isOnline;
        }
    }
    
    public static class History {
        final Date date;
        final Talk talk;

        public History(Date date, Talk talk) {
            this.date = date;
            this.talk = talk;
        }        
    }
    
    public static class SystemMsg {
        
    }
    
    public static class Draw {
        final String roomId;
        final String userId;
        final String appId;
        
        public Draw(String roomId, String userId, String appId) {
            this.roomId = roomId;
            this.userId = userId;
            this.appId = appId;
        }
    }
}
