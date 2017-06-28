package sagib.edu.tickcheck;

/**
 * Created by sagib on 19/06/2017.
 */

public class BoardPost {
    private String contents;
    private String email;
    private String hour;
    private String date;
    private String postUID;
    private String userUID;
    private String userDisplay;


    public BoardPost() {
    }

    public BoardPost(String contents, String email, String hour, String date, String postUID, String userUID, String userDisplay) {
        this.contents = contents;
        this.email = email;
        this.hour = hour;
        this.date = date;
        this.postUID = postUID;
        this.userUID = userUID;
        this.userDisplay = userDisplay;


    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public String getUserDisplay() {
        return userDisplay;
    }

    public void setUserDisplay(String userDisplay) {
        this.userDisplay = userDisplay;
    }

    public String getContents() {
        return contents;
    }


    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getDate() {
        return date;
    }

    public String getPostUID() {
        return postUID;
    }

    public void setPostUID(String postUID) {
        this.postUID = postUID;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
