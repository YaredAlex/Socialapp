package com.example.myapplication.model;

import java.util.Map;

public class DBUserInfoModel {
    private String uid;
    private Map<String,Object> userChatList;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Map<String, Object> getUserChatList() {
        return userChatList;
    }

    public void setUserChatList(Map<String, Object> userChatList) {
        this.userChatList = userChatList;
    }
}
