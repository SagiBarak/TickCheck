package sagib.edu.tickcheck.models;

import android.os.Parcel;
import android.os.Parcelable;

public class PrivateMessage implements Parcelable {
    String senderUID;
    String recieverUID;
    String senderDisplayName;
    String recieverDisplayName;
    String date;
    String time;
    String message;
    String prvMessageUID;
    String receiverToken;

    public String getReceiverToken() {
        return receiverToken;
    }

    public void setReceiverToken(String receiverToken) {
        this.receiverToken = receiverToken;
    }

    public PrivateMessage(String senderUID, String recieverUID, String senderDisplayName, String recieverDisplayName, String date, String time, String message, String prvMessageUID, String receiverToken) {
        this.senderUID = senderUID;
        this.recieverUID = recieverUID;
        this.senderDisplayName = senderDisplayName;
        this.recieverDisplayName = recieverDisplayName;
        this.date = date;
        this.time = time;
        this.message = message;
        this.prvMessageUID = prvMessageUID;
        this.receiverToken = receiverToken;

    }

    public PrivateMessage() {
    }

    public String getSenderUID() {
        return senderUID;
    }

    public void setSenderUID(String senderUID) {
        this.senderUID = senderUID;
    }

    public String getRecieverUID() {
        return recieverUID;
    }

    public void setRecieverUID(String recieverUID) {
        this.recieverUID = recieverUID;
    }

    public String getSenderDisplayName() {
        return senderDisplayName;
    }

    public void setSenderDisplayName(String senderDisplayName) {
        this.senderDisplayName = senderDisplayName;
    }

    public String getRecieverDisplayName() {
        return recieverDisplayName;
    }

    public void setRecieverDisplayName(String recieverDisplayName) {
        this.recieverDisplayName = recieverDisplayName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPrvMessageUID() {
        return prvMessageUID;
    }

    public void setPrvMessageUID(String prvMessageUID) {
        this.prvMessageUID = prvMessageUID;
    }

    @Override
    public String toString() {
        return "PrivateMessage{" +
                "senderUID='" + senderUID + '\'' +
                ", recieverUID='" + recieverUID + '\'' +
                ", senderDisplayName='" + senderDisplayName + '\'' +
                ", recieverDisplayName='" + recieverDisplayName + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", message='" + message + '\'' +
                ", prvMessageUID='" + prvMessageUID + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.senderUID);
        dest.writeString(this.recieverUID);
        dest.writeString(this.senderDisplayName);
        dest.writeString(this.recieverDisplayName);
        dest.writeString(this.date);
        dest.writeString(this.time);
        dest.writeString(this.message);
        dest.writeString(this.prvMessageUID);
        dest.writeString(this.receiverToken);
    }

    protected PrivateMessage(Parcel in) {
        this.senderUID = in.readString();
        this.recieverUID = in.readString();
        this.senderDisplayName = in.readString();
        this.recieverDisplayName = in.readString();
        this.date = in.readString();
        this.time = in.readString();
        this.message = in.readString();
        this.prvMessageUID = in.readString();
        this.receiverToken = in.readString();
    }

    public static final Creator<PrivateMessage> CREATOR = new Creator<PrivateMessage>() {
        @Override
        public PrivateMessage createFromParcel(Parcel source) {
            return new PrivateMessage(source);
        }

        @Override
        public PrivateMessage[] newArray(int size) {
            return new PrivateMessage[size];
        }
    };
}
