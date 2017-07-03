package sagib.edu.tickcheck;

import android.os.Parcel;
import android.os.Parcelable;

public class Zone implements Parcelable {
    private String name;
    private int capacity;
    private int free;

    public Zone(String name, int capacity, int free) {
        this.name = name;
        this.capacity = capacity;
        this.free = free;
    }

    public Zone() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getFree() {
        return free;
    }

    public void setFree(int free) {
        this.free = free;
    }

    @Override
    public String toString() {
        return String.format("%s - מקומות פנויים: %d\n",name,free);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeInt(this.capacity);
        dest.writeInt(this.free);
    }

    protected Zone(Parcel in) {
        this.name = in.readString();
        this.capacity = in.readInt();
        this.free = in.readInt();
    }

    public static final Parcelable.Creator<Zone> CREATOR = new Parcelable.Creator<Zone>() {
        @Override
        public Zone createFromParcel(Parcel source) {
            return new Zone(source);
        }

        @Override
        public Zone[] newArray(int size) {
            return new Zone[size];
        }
    };
}
