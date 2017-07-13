package sagib.edu.tickcheck;

public class PrivateChatListItem {
    String otherUserDisplay;
    String privateChatUID;
    String otherUserUID;
    String date;

    public PrivateChatListItem(String otherUserDisplay, String privateChatUID, String otherUserUID, String date) {
        this.otherUserDisplay = otherUserDisplay;
        this.privateChatUID = privateChatUID;
        this.otherUserUID = otherUserUID;
        this.date = date;


    }

    public PrivateChatListItem() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOtherUserDisplay() {
        return otherUserDisplay;
    }

    public String getOtherUserUID() {
        return otherUserUID;
    }

    public void setOtherUserUID(String otherUserUID) {
        this.otherUserUID = otherUserUID;
    }

    public void setOtherUserDisplay(String otherUserDisplay) {
        this.otherUserDisplay = otherUserDisplay;
    }

    public String getPrivateChatUID() {
        return privateChatUID;
    }

    public void setPrivateChatUID(String privateChatUID) {
        this.privateChatUID = privateChatUID;
    }
}
