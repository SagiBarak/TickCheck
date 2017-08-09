package sagib.edu.tickcheck;

import com.google.firebase.auth.FirebaseUser;

public class User {

    private String displayName;
    private String profileImage = "https://cdn.pixabay.com/photo/2012/04/26/19/43/profile-42914_1280.png";
    private String uid;
    private String email;
    private String token;

    public User() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User(FirebaseUser user) {
        this.displayName = user.getDisplayName();
        if (user.getPhotoUrl() != null) {
            this.profileImage = user.getPhotoUrl().toString();

        }
        this.uid = user.getUid();
        this.email = user.getEmail();

    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        if (profileImage != null)
            this.profileImage = profileImage;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{" +
                "displayName='" + displayName + '\'' +
                ", profileImage='" + profileImage + '\'' +
                ", uid='" + uid + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
