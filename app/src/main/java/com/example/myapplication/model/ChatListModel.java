package com.example.myapplication.model;

import java.io.Serializable;

public class ChatListModel implements Serializable {
    String lastChat;
    String time;
    String userName;
    String imgUrl;
    String uid;
    String email;
    public ChatListModel(String imgurl){
        this.imgUrl = imgurl;
    }
    public ChatListModel(){

    }
    public String getLastChat() {
        return lastChat;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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


    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setLastChat(String lastChat) {
        this.lastChat = lastChat;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
