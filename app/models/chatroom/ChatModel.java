package models.chatroom;

public interface ChatModel {

    public void onHostJoin(ChatRoom.Join join);
    public void onHostTalk(ChatRoom.Talk talk);
    public void onHostQuit(ChatRoom.Quit quit);
    
    public void onGuestJoin(ChatRoom.Join join);
    public void onGuestTalk(ChatRoom.Talk talk);
    public void onGuestQuit(ChatRoom.Quit quit);
    
    public Boolean isModelOn();
    public void SwitchModel();
    public Boolean isHostOnline();
    
    public void StartDraw();
    public void DrawChat(String userId, String appId);
    public String whoCanTalk();
}
