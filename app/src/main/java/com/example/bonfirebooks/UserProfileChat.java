package com.example.bonfirebooks;

import java.io.Serializable;
import java.util.Date;

public class UserProfileChat implements Serializable {

    private String otherUserName;
    private String content;
    private Date time;

    public UserProfileChat() {

    }

    public UserProfileChat(String otherUserName, String content, Date time) {
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

    public Date getTime() {
        return time;
    }

    public void setOtherUserName(String otherUserName) {
        this.otherUserName = otherUserName;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("UserProfileChat: \n");
        str.append("otherUserName: " + otherUserName);

        if(content != null) {
            str.append( "\ncontent: " + content);
        }

        if(time != null) {
            str.append("\ntime: " + time.toString());
        }

        return str.toString();
    }

}
