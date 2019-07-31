package chatapp.classes.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class Message {

    private int id;
    private int senderId;

    private int receiver_id;
    private int groupId;
    private String content;
    private boolean isFile;
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private Date sending_date;

    private Object UserData;

    public Object getUserData() {
        return UserData;
    }

    public void setUserData(Object userData) {
        UserData = userData;
    }

    public Message() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(int receiver_id) {
        this.receiver_id = receiver_id;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isFile() {
        return isFile;
    }

    public void setFile(boolean file) {
        isFile = file;
    }

    public Date getSending_date() {
        return sending_date;
    }

    public void setSending_date(Date sending_date) {
        this.sending_date = sending_date;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", senderId=" + senderId +
                ", receiver_id=" + receiver_id +
                ", groupId=" + groupId +
                ", content='" + content + '\'' +
                ", isFile=" + isFile +
                ", sending_date=" + sending_date +
                '}';
    }
}
