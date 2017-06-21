package sagib.edu.tickcheck;

/**
 * Created by sagib on 19/06/2017.
 */

public class BoardPost {
    private String title;
    private String contents;
    private String email;
    private String hour;
    private String date;

    public BoardPost() {
    }

    public BoardPost(String title, String contents, String email, String hour, String date) {
        this.title = title;
        this.contents = contents;
        this.email = email;
        this.hour = hour;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public String getContents() {
        return contents;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public void setDate(String date) {
        this.date = date;
    }
}
