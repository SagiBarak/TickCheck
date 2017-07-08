package sagib.edu.tickcheck;

import android.os.Parcel;
import android.os.Parcelable;

public class MyShow implements Parcelable {
    private String performer;
    private String dateTime;
    private String arena;
    private String image;
    private String myShowUID;
    private String date;
    private String eventID;

    public MyShow(String performer, String dateTime, String arena, String image, String myShowUID, String date, String eventID) {
        this.performer = performer;
        this.dateTime = dateTime;
        this.arena = arena;
        this.image = image;
        this.myShowUID = myShowUID;
        this.date = date;
        this.eventID = eventID;
    }

    public String getDate() {
        return date;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public void setDate(String date) {
        this.date = date;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.performer);
        dest.writeString(this.dateTime);
        dest.writeString(this.arena);
        dest.writeString(this.image);
        dest.writeString(this.myShowUID);
        dest.writeString(this.date);
        dest.writeString(this.eventID);
    }

    protected MyShow(Parcel in) {
        this.performer = in.readString();
        this.dateTime = in.readString();
        this.arena = in.readString();
        this.image = in.readString();
        this.myShowUID = in.readString();
        this.date = in.readString();
        this.eventID = in.readString();
    }

    public static final Parcelable.Creator<MyShow> CREATOR = new Parcelable.Creator<MyShow>() {
        @Override
        public MyShow createFromParcel(Parcel source) {
            return new MyShow(source);
        }

        @Override
        public MyShow[] newArray(int size) {
            return new MyShow[size];
        }
    };
}
