package sagib.edu.tickcheck;

/**
 * Created by sagib on 01/07/2017.
 */

public class PrivateChatListItem {
    String otherUserDisplay;
    String privateChatUID;
    String otherUserUID;

    public PrivateChatListItem(String otherUserDisplay, String privateChatUID, String otherUserUID) {
        this.otherUserDisplay = otherUserDisplay;
        this.privateChatUID = privateChatUID;
        this.otherUserUID = otherUserUID;

    }

    public PrivateChatListItem() {
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
