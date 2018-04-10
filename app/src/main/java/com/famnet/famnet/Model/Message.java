package com.famnet.famnet.Model;

/**
 * Created by DungNguyen on 11/21/17.
 */

public class Message {
    private String text;
    private String sender;
    private String photoUrl;

    public Message() {
    }

    public Message(String text, String sender, String photoUrl) {
        this.text = text;
        this.sender = sender;
        this.photoUrl = photoUrl;
    }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }

    public String getSender() {
        return sender;
    }
    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
