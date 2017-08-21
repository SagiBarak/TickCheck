package sagib.edu.tickcheck.models;

import android.os.Parcel;
import android.os.Parcelable;

public class BoardPost implements Parcelable {
    private String contents;
    private String email;
    private String hour;
    private String date;
    private String postUID;
    private String userUID;
    private String userDisplay;
    private String ticketsNumber;
    private String showTitle;
    private String showDate;
    private String showArena;
    private String showPrice;


    public BoardPost() {
    }

    public BoardPost(String contents, String email, String hour, String date, String postUID, String userUID, String userDisplay, String ticketsNumber, String showTitle, String showDate, String showArena, String showPrice) {
        this.contents = contents;
        this.email = email;
        this.hour = hour;
        this.date = date;
        this.postUID = postUID;
        this.userUID = userUID;
        this.userDisplay = userDisplay;
        this.ticketsNumber = ticketsNumber;
        this.showTitle = showTitle;
        this.showDate = showDate;
        this.showArena = showArena;
        this.showPrice = showPrice;


    }

    public String getTicketsNumber() {
        return ticketsNumber;
    }

    public void setTicketsNumber(String ticketsNumber) {
        this.ticketsNumber = ticketsNumber;
    }

    public String getShowTitle() {
        return showTitle;
    }

    public void setShowTitle(String showTitle) {
        this.showTitle = showTitle;
    }

    public String getShowDate() {
        return showDate;
    }

    public void setShowDate(String showDate) {
        this.showDate = showDate;
    }

    public String getShowArena() {
        return showArena;
    }

    public void setShowArena(String showArena) {
        this.showArena = showArena;
    }

    public String getShowPrice() {
        return showPrice;
    }

    public void setShowPrice(String showPrice) {
        this.showPrice = showPrice;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.contents);
        dest.writeString(this.email);
        dest.writeString(this.hour);
        dest.writeString(this.date);
        dest.writeString(this.postUID);
        dest.writeString(this.userUID);
        dest.writeString(this.userDisplay);
    }

    protected BoardPost(Parcel in) {
        this.contents = in.readString();
        this.email = in.readString();
        this.hour = in.readString();
        this.date = in.readString();
        this.postUID = in.readString();
        this.userUID = in.readString();
        this.userDisplay = in.readString();
    }

    public static final Creator<BoardPost> CREATOR = new Creator<BoardPost>() {
        @Override
        public BoardPost createFromParcel(Parcel source) {
            return new BoardPost(source);
        }

        @Override
        public BoardPost[] newArray(int size) {
            return new BoardPost[size];
        }
    };
}
