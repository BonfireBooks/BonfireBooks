package com.example.bonfirebooks;

import java.io.Serializable;

public class UserProfileBook implements Serializable {

    private String path;
    private String title;
    private String coverImgUrl;

    public UserProfileBook() {

    }

    public UserProfileBook(String path, String title, String coverImgUrl) {
        this.path = path;
        this.title = title;
        this.coverImgUrl = coverImgUrl;
    }

    public String getPath() {
        return path;
    }

    public String getTitle() {
        return title;
    }

    public String getCoverImgUrl() {
        return coverImgUrl;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCoverImgUrl(String coverImgUrl) {
        this.coverImgUrl = coverImgUrl;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("UserProfile: \n");
        str.append("path: " + path + "\n");
        str.append("title: " + title + "\n");
        str.append("coverImgUrl: " + coverImgUrl + "\n");
        return str.toString();
    }
}
