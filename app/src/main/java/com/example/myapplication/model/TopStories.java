package com.example.myapplication.model;

public class TopStories {
    private String imgUri;
    private String name;
   public TopStories(String imgUri){
       this.imgUri = imgUri;
   }
    public String getImgUri() {
        return imgUri;
    }

    public void setImgUri(String imgUri) {
        this.imgUri = imgUri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
