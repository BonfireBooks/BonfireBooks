package com.example.bonfirebooks;

import com.google.firebase.Timestamp;

import java.io.Serializable;

public class UserProfileChat implements Serializable {

    private String otherUserName;
    private String content;
    private Timestamp time;

    public UserProfileChat() {

    }

    public UserProfileChat(String otherUserName, String content, Timestamp time) {
        this.otherUserName = otherUserName;
        this.content = content;
        this.time = time;
    }

    public String getOtherUserName() {
        return otherUserName;
    }

    public String getContent() {
        return content;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setOtherUserName(String otherUserName) {
        this.otherUserName = otherUserName;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("UserProfileChat: \n");
        str.append("otherUserName: " + otherUserName + "\n");
        str.append("content: " + content + "\n");
        str.append("time: " + time.toString() + "\n");
        return str.toString();
    }

}
