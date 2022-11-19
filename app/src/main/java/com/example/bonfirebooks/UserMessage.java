package com.example.bonfirebooks;

import java.util.Date;

public class UserMessage {

    private String msgId;
    private String content;
    private String senderId;
    private Date time;

    public UserMessage(String msgId, String content, String senderId, Date time) {
        this.msgId = msgId;
        this.content = content;
        this.senderId = senderId;
        this.time = time;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "UserMessage{" +
                "msgId='" + msgId + '\'' +
                ", content='" + content + '\'' +
                ", senderId='" + senderId + '\'' +
                ", time=" + time +
                '}';
    }
}
