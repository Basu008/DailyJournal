package model;

import com.google.firebase.Timestamp;

public class Journal {
    private String userId;
    private String username;
    private String title;
    private String thought;
    private String imageUrl;
    private Timestamp timeAdded;

    public Journal(){}

    public Journal(String userId, String username, String title, String thought, String imageUrl, Timestamp timeAdded) {
        this.userId = userId;
        this.username = username;
        this.title = title;
        this.thought = thought;
        this.imageUrl = imageUrl;
        this.timeAdded = timeAdded;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThought() {
        return thought;
    }

    public void setThought(String thought) {
        this.thought = thought;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Timestamp getTimeAdded() {
        return timeAdded;
    }

    public void setTimeAdded(Timestamp timeAdded) {
        this.timeAdded = timeAdded;
    }
}
