package sagib.edu.tickcheck;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by sagib on 14/06/2017.
 */

public class Show implements Parcelable {
    private String image;
    private String performer;
    private String arena;
    private String day;
    private String dateTime;
    private String link;
    private boolean ticketsAvailable;
    private ArrayList<Zone> zones;

    public Show(String image, String performer, String arena, String day, String dateTime, String link, boolean ticketsAvailable, ArrayList<Zone> zones) {
        this.image = image;
        this.performer = performer;
        this.arena = arena;
        this.day = day;
        this.dateTime = dateTime;
        this.link = link;
        this.ticketsAvailable = ticketsAvailable;
        this.zones = zones;
    }

    public String getDayDateTime(){
        return getDay() + " " + getDateTime().replace("2017", "2017 בשעה:");
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPerformer() {
        return performer;
    }

    public void setPerformer(String performer) {
        this.performer = performer;
    }

    public String getArena() {
        return arena;
    }

    public void setArena(String arena) {
        this.arena = arena;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public boolean isTicketsAvailable() {
        return ticketsAvailable;
    }

    public void setTicketsAvailable(boolean ticketsAvailable) {
        this.ticketsAvailable = ticketsAvailable;
    }

    public ArrayList<Zone> getZones() {
        return zones;
    }

    public int getFreeFromShow() {
        int total = 0;
        ArrayList<Zone> zones = this.getZones();
        for (Zone zone : zones) {
            total += zone.getFree();
        }
        return total;
    }

    public void setZones(ArrayList<Zone> zones) {
        this.zones = zones;
    }

    @Override
    public String toString() {
        return getPerformer() + " " + getArena() + isTicketsAvailable();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.image);
        dest.writeString(this.performer);
        dest.writeString(this.arena);
        dest.writeString(this.day);
        dest.writeString(this.dateTime);
        dest.writeString(this.link);
        dest.writeByte(this.ticketsAvailable ? (byte) 1 : (byte) 0);
        dest.writeList(this.zones);
    }

    protected Show(Parcel in) {
        this.image = in.readString();
        this.performer = in.readString();
        this.arena = in.readString();
        this.day = in.readString();
        this.dateTime = in.readString();
        this.link = in.readString();
        this.ticketsAvailable = in.readByte() != 0;
        this.zones = new ArrayList<Zone>();
        in.readList(this.zones, Zone.class.getClassLoader());
    }

    public static final Parcelable.Creator<Show> CREATOR = new Parcelable.Creator<Show>() {
        @Override
        public Show createFromParcel(Parcel source) {
            return new Show(source);
        }

        @Override
        public Show[] newArray(int size) {
            return new Show[size];
        }
    };
}
