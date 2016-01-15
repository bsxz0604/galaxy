package models.chatroom;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import models.chatroom.ChatRoom.History;
import models.common.Account;
import models.users.AccountAppId;
import models.users.AppProfile;
import models.users.CharmValue;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import controllers.common.CodeGenerator;
import play.Logger;
import play.db.DB;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.WebSocket;
import play.mvc.WebSocket.Out;

public class RaceChatModel implements ChatModel {
    
    private Boolean m_isModelOn = false;
    private Boolean m_isHostOnline = false;
    private String m_guestTalkId = null;
    private Boolean m_isStartDraw = false;
    private String m_hostId = null;
    private Map<String, WebSocket.Out<JsonNode>> m_members = null;
    private List<History> recordList = null;
    private final int SIZE_THRESHOLD = 20;
    private final int MAX_MSG_SIZE = 4;
    private Map<String, Integer> drawedDict = null;
    private List<String> flowerWinnerList = null;
    private final int MIN_SEND_FLOWER_NUMBER = 50;
    private Date m_dmTime = new Date();
    private int m_dmInterval = 800;
    private final int MAX_MSG_LENGTH = 30;
    private Date m_drawStartTime = new Date();
    private Date m_hostJoinTime = null;
    private final int MAX_DRAW_TIMEOUT = 5000;
    
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        
    public RaceChatModel(String hostId, Map<String, WebSocket.Out<JsonNode>> members) {
        m_hostId = hostId;
        m_members = members;
        recordList = new ArrayList<History>();
        drawedDict = new HashMap<String, Integer>();
        flowerWinnerList = new ArrayList<String>();
    }
    
    public Boolean HandleMessage(Object message) {
        Boolean bRet = false;
        
        if (message instanceof ChatRoom.Join) {
            onHandleJoin((ChatRoom.Join)message);
            bRet = true;
        }
        else if (message instanceof ChatRoom.Talk) {
            onHandleTalk((ChatRoom.Talk)message);
            bRet = true;
        }
        else if (message instanceof ChatRoom.Quit) {
            onHandleQuit((ChatRoom.Quit)message);
            bRet = true;
        }
        else if (message instanceof ChatRoom.Present) {
            onHandlePresent((ChatRoom.Present)message);
            bRet = true;
        }
        else if (message instanceof ChatRoom.Draw) {
            onHandleDraw((ChatRoom.Draw)message);
            bRet = true;
        }
        
        return bRet;
    }
    
    private void onHandleDraw(ChatRoom.Draw draw) {
        if (draw.userId.compareToIgnoreCase(m_hostId) == 0) {
            onHostDraw();
        }
        else {
            onGuestDraw(draw.userId, draw.appId);
        }
    }
    
    private void onHostDraw() {
        if (m_isModelOn) {
            StartDraw();
        }
    }
    
    private void onGuestDraw(String userId, String appId) {
        if (m_isModelOn) {
            DrawChat(userId, appId);
        }
        else {
            //do nothing
            //notifySys(userId, Messages.get(""));
        }
    }
    
    private void onHandleJoin(ChatRoom.Join join) {
        
        //add the member
        if (!m_members.containsKey(join.userId)) {
            m_members.put(join.userId, join.channel);
        }
        else {
            return;
        }
        
        for (History history : recordList) {
            sendHistoryMessage(join.channel, history);
        }
        
        if (join.userId.compareToIgnoreCase(m_hostId) == 0) {
            onHostJoin(join);
        }
        else {
            onGuestJoin(join);
        }
        
        try {
            ChatRoomTime joinTime = ChatRoomTime.find.where()
                    .eq("account_id", join.userId)
                    .findUnique();
            if (joinTime == null) {
                joinTime = new ChatRoomTime();
                joinTime.id = CodeGenerator.GenerateUUId();
                joinTime.room_id = m_hostId;
                joinTime.action = "join";
                joinTime.action_time = new Date();
                joinTime.account =  Account.find.byId(join.userId);
                //joinTime.send_gift_number = CharmValue.find.where()
                //      .eq("sender_id", join.userId).findRowCount();
                Ebean.save(joinTime);
            } else {
                joinTime.action = "join";
                joinTime.action_time = new Date();
                joinTime.room_id = m_hostId;
                //joinTime.send_gift_number = CharmValue.find.where()
                //      .eq("sender_id", join.userId).findRowCount();
                Ebean.update(joinTime);
            }
        }
        catch (Throwable e) {
            Logger.info(e.getMessage());
        }
    }
    
    private void onHandleTalk(ChatRoom.Talk talk) {
        if (talk.userId.compareToIgnoreCase(m_hostId) == 0) {
            onHostTalk(talk);
        }
        else {
            onGuestTalk(talk);
        }
    }
    
    private void onHandleQuit(ChatRoom.Quit quit) {

        if(m_members.containsKey(quit.userId)){
            m_members.remove(quit.userId);
        }
        else {
            return;
        }
        
        if (quit.userId.compareToIgnoreCase(m_hostId) == 0) {
            onHostQuit(quit);
        }
        else {
            onGuestQuit(quit);
        }
        
        
        try {
            ChatRoomTime joinTime = ChatRoomTime.find.where()
                    .eq("account_id", quit.userId)
                    .findUnique();
            if (joinTime != null) {
                joinTime.action = "exit";
                joinTime.action_time = new Date();
                joinTime.room_id = m_hostId;
                joinTime.send_gift_number = CharmValue.find.where()
                      .eq("sender_id", quit.userId).findRowCount();
                Ebean.update(joinTime);
            }                    
        }
        catch (Throwable e) {
            Logger.info(e.getMessage());
        }
    }
    
    private void onHandlePresent(ChatRoom.Present present) {
        if (!m_isHostOnline) {
            String msgTxt = String.format(Messages.get("present.sendmessage"), present.username, present.presentName, present.number);
            notifyAll("present", present.username, "", "", present.toUserId, msgTxt, present.isOnline, false);            
        }
    }
    
    @Override
    public void onHostJoin(ChatRoom.Join join) {
        // TODO Auto-generated method stub
        m_isModelOn = true;
        m_isHostOnline = true;
        m_isStartDraw = false;
        m_guestTalkId = null;
        
        Calendar now = Calendar.getInstance();
        if (null == m_hostJoinTime) {
            m_hostJoinTime = new Date();
            drawedDict.clear();
        }
        else {
            Calendar lastTime = Calendar.getInstance();
            lastTime.setTime(m_hostJoinTime);
            
            if ((lastTime.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)) &&
                 lastTime.get(Calendar.YEAR) == now.get(Calendar.YEAR)) {
                //same day
            }
            else {
                drawedDict.clear();
            }
        }
        
        if (drawedDict.isEmpty()) {
            flowerWinnerList.clear();
            
            //add flower winners
            try {
                Connection conn = DB.getConnection();
                Statement statement = conn.createStatement();
                
                Calendar threeDaysAgo = Calendar.getInstance();
                threeDaysAgo.set(Calendar.DATE, -3);
                
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                
                String sql = String.format("select sender_id, count(sender_id) number from charm_value where charm_value = 1 and receiver_id = '%s' and create_time >= '%s' and create_time < '%s' group by sender_id order by number desc limit 3;",
                        m_hostId, sdf1.format(threeDaysAgo.getTime()), sdf1.format(now.getTime()));
                ResultSet results = statement.executeQuery(sql);
                
                while (results.next()) {
                    if (results.getInt(2) >= MIN_SEND_FLOWER_NUMBER) {
                        if (m_members.containsKey(results.getString(1))) {
                            flowerWinnerList.add(results.getString(1));                        
                        }
                    }
                }
                
                conn.close();
            }
            catch (Throwable e) {
                Logger.info(e.getMessage());
            }             
        }
                
        m_dmTime = new Date();
        
        notifyAll("join", join.username,join.userId,"","", "",join.isOnline,join.isOwners);
        notifySys(join.userId, Messages.get("race.ruleForHost"), "system", null);
        notifySysAll(join.userId, Messages.get("race.hostOnline"), "system", null);
    }

    @Override
    public void onHostTalk(ChatRoom.Talk talk) {
        notifyAll("talk", talk.username,talk.userId,talk.toUserName,talk.toUserId, talk.text,talk.isOnline,talk.isOwners);
        
        synchronized(this) {
            if (recordList.size() >= MAX_MSG_SIZE) {
                recordList.remove(0);
            }
            recordList.add(new History(new Date(), talk));
        }        
    }

    @Override
    public void onHostQuit(ChatRoom.Quit quit) {
        // TODO Auto-generated method stub
        m_isModelOn = false;
        m_isHostOnline = false;
        m_isStartDraw = false;
        m_guestTalkId = null;
        
        notifyAll("quit", quit.username,quit.userId,"","", "",quit.isOnline,quit.isOwners);                            
    }

    @Override
    public void onGuestJoin(ChatRoom.Join join) {
        // TODO Auto-generated method stub
        if (m_isModelOn) {
            //display himself the rule
            notifySys(join.userId, Messages.get("race.hostOnline"), "system", null);
            notify("join", join.username,join.userId,"", join.userId, "",join.isOnline,join.isOwners);
        }
        else {
            if (!m_isHostOnline && m_members.values().size() <= SIZE_THRESHOLD) {
                //only host is offline, it will notify others                
                notifyAll("join", join.username,join.userId,"","", "has entered the room",join.isOnline,join.isOwners);                            
            }
        }
    }

    @Override
    public void onGuestTalk(ChatRoom.Talk talk) {
        // TODO Auto-generated method stub
        if (m_isModelOn) {
            if (m_guestTalkId != null && m_guestTalkId.compareToIgnoreCase(talk.userId) == 0) {
                //talk one time
                notifyAll("talk", talk.username,talk.userId,talk.toUserName,talk.toUserId, talk.text,talk.isOnline,talk.isOwners);
                
                synchronized(this) {
                    if (recordList.size() >= MAX_MSG_SIZE) {
                        recordList.remove(0);
                    }
                    recordList.add(new History(new Date(), talk));
                }                
                
                //m_guestTalkId = null;
            }
            else {
                //cannot talk
                //notifySys(talk.userId, Messages.get("race.noright"), "system", null);
                boolean bSend = false;
                
                synchronized(m_dmTime) {
                    if (new Date().getTime() - m_dmTime.getTime() >= m_dmInterval) {
                        m_dmTime = new Date();
                        bSend = true;
                    }
                    else {
                        return;
                    }
                }
                
                
                if (bSend && talk.text.length() <= MAX_MSG_LENGTH) {                        
                    notifyAll("tm", talk.username,talk.userId,talk.toUserName,talk.toUserId, talk.text,talk.isOnline,talk.isOwners);                                            
                }
                
            }
        }
        else {
            //talk
            notifyAll("talk", talk.username,talk.userId,talk.toUserName,talk.toUserId, talk.text,talk.isOnline,talk.isOwners);
            
            synchronized(this) {
                if (recordList.size() >= MAX_MSG_SIZE) {
                    recordList.remove(0);
                }
                recordList.add(new History(new Date(), talk));
            }             
        }
    }

    @Override
    public void onGuestQuit(ChatRoom.Quit quit) {
        // TODO Auto-generated method stub
        if (!m_isHostOnline && m_members.values().size() <= SIZE_THRESHOLD) {
            notifyAll("quit", quit.username,quit.userId,"","", "",quit.isOnline,quit.isOwners);                                
        }
        else {
            notify("quit", quit.username,quit.userId,"", m_hostId, "",quit.isOnline,quit.isOwners);
        }
    }

    @Override
    public Boolean isModelOn() {
        // TODO Auto-generated method stub
        return m_isModelOn;
    }
    
    @Override
    public void SwitchModel() {
        m_isModelOn = !m_isModelOn;
    }

    @Override
    public Boolean isHostOnline() {
        // TODO Auto-generated method stub
        return m_isHostOnline;
    }

    @Override
    public void StartDraw() {
        m_isStartDraw = true;
        
        for (int i = 3; i > 0; i--) {
            notifySysAll(null, String.format(Messages.get("race.prepare"), i), "system", null);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }            
        }
        
        if (drawedDict.size() >= m_members.size()) {
            drawedDict.clear();
        }
        
        m_drawStartTime = new Date();
        m_guestTalkId = null;
        notifySysAllFake();
        //notifySysAll(null, Messages.get("race.start"), "draw", "1");
    }
    
    @Override
    public synchronized void DrawChat(String userId, String appId) {
        if (m_isStartDraw) {            
            //AppProfile profile1 = AppProfile.find.byId(new AccountAppId(userId, appId));
            Double chance = CodeGenerator.random.nextDouble();
            //Logger.info("user:" + userId + " " + chance.toString());
            if (chance < 0.5f) {
                return;
            }
            
            //Logger.info("Size:" + flowerWinnerList.size());
            Boolean isSendFlower = false;
            //let the flower sender go first
            if (flowerWinnerList != null && !flowerWinnerList.isEmpty()) {
                for (String flowerUserId : flowerWinnerList) {
                    if (drawedDict.containsKey(flowerUserId)) {
                        continue;
                    }
                    userId = flowerUserId;
                    isSendFlower = true;
                    break;
                }
                
                if (isSendFlower) {
                    flowerWinnerList.remove(userId);
                }
            }            
                        
            if (!drawedDict.containsKey(userId)) {
                drawedDict.put(userId,  1);
            }
            else {
                if (new Date().getTime() - m_drawStartTime.getTime() >= MAX_DRAW_TIMEOUT) {
                    //nobody try to draw, need to clean drawedDict
                    //Logger.info("clear");
                    drawedDict.clear();
                    drawedDict.put(userId, 1);
                    flowerWinnerList.clear();
                }
                else {
                    return;
                }
            }
            
            //Logger.info("win");
            m_guestTalkId = userId;            
            m_isStartDraw = false;
            notifySys(userId, Messages.get("race.cong"), "draw", "0");
            
            AppProfile profile = AppProfile.find.byId(new AccountAppId(userId, appId));
            
            String msg = String.format(Messages.get("race.notify"), 
                    profile == null ? Messages.get("race.unknown") : profile.name);
            if (isSendFlower) {
                msg = Messages.get("race.sendflower") + msg;
            }
            notifySysAll(userId, msg, "draw", "0");
        }
        else {
            if (m_guestTalkId != null && m_guestTalkId.compareToIgnoreCase(userId) == 0) {
                notifySys(userId, Messages.get("race.hasright"), "draw", "0");
            }
            else {
                notifySys(userId, Messages.get("race.noright"), "draw", "0");
            }
        }
    }
    
    @Override
    public String whoCanTalk() {
        // TODO Auto-generated method stub
        return m_guestTalkId;
    }
    
    private void notifySys(String userId, String msg, String kind, String value) {
        ObjectNode event = Json.newObject();
        event.put("kind", kind);
        event.put("msg", msg);
        
        if (null != value) {
            event.put("value", value);
        }
        
        if (m_members.get(userId) != null) {
            m_members.get(userId).write(event);            
        }
    }
    
    private void notifySysAllFake() {    

        ObjectNode event = Json.newObject();
        event.put("kind", "draw");
        event.put("msg", Messages.get("race.start"));
        event.put("value", 1);
        
        List<Out<JsonNode>> nodeList = new ArrayList<Out<JsonNode>>();
        
        for (Entry<String, Out<JsonNode>> entry : m_members.entrySet()) {
            
            if (drawedDict.containsKey(entry.getKey())) {
                nodeList.add(entry.getValue());
                continue;
            }
            
            try {
                entry.getValue().write(event);
            }
            catch (Throwable e) {
                
            }
        }
        
        try {
            Thread.sleep(10);            
        }
        catch (Throwable e) {
            
        }
        
        for (Out<JsonNode> outNode : nodeList) {
            try {
                outNode.write(event);
            }
            catch (Throwable e) {
                
            }
        }
    }
    
    private void notifySysAll(String userIdtoExcept, String msg, String kind, String value) {
        for (Entry<String, Out<JsonNode>> entry : m_members.entrySet()) {
            
            if (userIdtoExcept != null && entry.getKey().compareToIgnoreCase(userIdtoExcept) == 0) {
                continue;
            }
            
            ObjectNode event = Json.newObject();
            event.put("kind", kind);
            event.put("msg", msg);
            
            if (value != null) {
                event.put("value", value);
            }
            
            try {
                entry.getValue().write(event);
            }
            catch (Throwable e) {
                
            }
        }        
    }
    
    private void notify(String kind, String user,String userId,String toUserName,String toUserId, String text,boolean isOnline,boolean isOwners) {
        if (m_members.get(toUserId) != null) {
            ObjectNode event = Json.newObject();
            event.put("kind", kind);
            event.put("user", user);
            event.put("message", text);
            event.put("userId", userId);
            event.put("toUser", toUserName);
            event.put("toUserId", toUserId);
            event.put("isOnline", isOnline);
            event.put("isOwners", isOwners);
            event.put("peopleNum", m_members.keySet().size());
            
            try {
                m_members.get(toUserId).write(event);                
            }
            catch (Throwable e) {
            }
        }
    }
    
    private void notifyAll(String kind, String user,String userId,String toUserName,String toUserId, String text,boolean isOnline,boolean isOwners) {

        /*
        if (m_members.values().size() > SIZE_THRESHOLD) {
            if (kind.compareToIgnoreCase("talk") != 0 &&
                kind.compareToIgnoreCase("present") != 0) {
                if (userId == null || m_roomId == null) {
                    return;
                }
                
                if (userId.compareToIgnoreCase(m_roomId) != 0) {
                    return;
                }
                
                //ban zhulaile
                Logger.info(kind + ":" + userId + " " + user);
            }
        }
        */
        
        for(WebSocket.Out<JsonNode> channel: m_members.values()) {
            ObjectNode event = Json.newObject();
            event.put("kind", kind);
            event.put("user", user);
            event.put("message", text);
            event.put("userId", userId);
            event.put("toUser", toUserName);
            event.put("toUserId", toUserId);
            event.put("isOnline", isOnline);
            event.put("isOwners", isOwners);
            event.put("peopleNum", m_members.keySet().size());
            
            try {
                channel.write(event);                
            }
            catch (Throwable e) {
            }
        }
    }
    
    private void sendHistoryMessage(WebSocket.Out<JsonNode> channel, History history) {
        if (m_members.values().size() > SIZE_THRESHOLD) {
            return;
        }
        
        ObjectNode event = Json.newObject();
        event.put("kind", "history");
        event.put("user", history.talk.username);
        event.put("message", history.talk.text);
        event.put("userId", history.talk.userId);
        event.put("toUser", history.talk.toUserName);
        event.put("toUserId", history.talk.toUserId);
        event.put("isOnline", history.talk.isOnline);
        event.put("isOwners", history.talk.isOwners);
        event.put("peopleNum", m_members.keySet().size());
        event.put("time", sdf.format(history.date));
        
        channel.write(event);
    }
}
