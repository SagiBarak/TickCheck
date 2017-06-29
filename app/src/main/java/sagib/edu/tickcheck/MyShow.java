package sagib.edu.tickcheck;

/**
 * Created by sagib on 30/06/2017.
 */

public class MyShow {
    private String performer;
    private String dateTime;
    private String arena;
    private String image;
    private String myShowUID;

    public MyShow(String performer, String dateTime, String arena, String image, String myShowUID) {
        this.performer = performer;
        this.dateTime = dateTime;
        this.arena = arena;
        this.image = image;
        this.myShowUID = myShowUID;
    }

    public MyShow() {
    }

    public String getPerformer() {
        return performer;
    }

    public void setPerformer(String performer) {
        this.performer = performer;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getArena() {
        return arena;
    }

    public void setArena(String arena) {
        this.arena = arena;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMyShowUID() {
        return myShowUID;
    }

    public void setMyShowUID(String myShowUID) {
        this.myShowUID = myShowUID;
    }
}
