package com.example.bonfirebooks;

import java.io.Serializable;

public class WishlistBook implements Serializable {
    private String path;
    private String title;
    private String coverImgUrl;
    private Double price;

    public WishlistBook() {
    }

    public WishlistBook(String path, String title, String coverImgUrl, Double price) {
        this.path = path;
        this.title = title;
        this.coverImgUrl = coverImgUrl;
        this.price = price;
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

    public Double getPrice() {
        return price;
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

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("WishlistBook: \n");
        str.append("path: " + path + "\n");
        str.append("title: " + title + "\n");
        str.append("price: " + price + "\n");
        str.append("coverImgUrl: " + coverImgUrl + "\n");
        return str.toString();
    }
}
