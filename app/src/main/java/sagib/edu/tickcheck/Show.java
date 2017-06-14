package sagib.edu.tickcheck;

import java.util.ArrayList;

/**
 * Created by sagib on 14/06/2017.
 */

public class Show {
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
        return getDay() + " " + getDateTime();
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

    public void setZones(ArrayList<Zone> zones) {
        this.zones = zones;
    }

    @Override
    public String toString() {
        return getPerformer() + " " + getArena() + isTicketsAvailable();
    }
}
