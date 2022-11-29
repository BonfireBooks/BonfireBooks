package com.example.bonfirebooks;

import java.io.Serializable;
import java.util.HashMap;

public class WishlistBook implements Serializable {
    private String bookId;
    private String title;
    private String condition;
    private String coverImgUrl;
    private String parentBookId;
    private Double price;
    private HashMap<String, String> imagePaths;

    public WishlistBook() {
    }

    public WishlistBook(String bookId, String title, String condition, String coverImgUrl, String parentBookId, Double price, HashMap<String, String> imagePaths) {
        this.bookId = bookId;
        this.title = title;
        this.coverImgUrl = coverImgUrl;
        this.parentBookId = parentBookId;
        this.price = price;
        this.imagePaths = imagePaths;

        switch(condition.toLowerCase()) {
            case "like-new":
                this.condition = "Like-New";
                break;
            case "good":
                this.condition = "Good";
                break;
            case "fair":
                this.condition = "Fair";
                break;
            case "poor":
                this.condition = "Poor";
                break;
        }
    }

    public String getParentBookId() {
        return parentBookId;
    }

    public void setParentBookId(String parentBookId) {
        this.parentBookId = parentBookId;
    }

    public HashMap<String, String> getImages() {
        return imagePaths;
    }

    public void setImages(HashMap<String, String> images) {
        this.imagePaths = images;
    }

    public String getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }

    public String getCondition() { return condition; }

    public String getCoverImgUrl() {
        return coverImgUrl;
    }

    public Double getPrice() {
        return price;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCondition(String condition) {
        switch(condition.toLowerCase()) {
            case "like-new":
                this.condition = "Like-New";
                break;
            case "good":
                this.condition = "Good";
                break;
            case "fair":
                this.condition = "Fair";
                break;
            case "poor":
                this.condition = "Poor";
                break;
        }
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
        str.append("bookId: " + bookId + "\n");
        str.append("title: " + title + "\n");
        str.append("price: " + price + "\n");
        str.append("condition: " + condition + "\n");
        str.append("coverImgUrl: " + coverImgUrl + "\n");
        str.append("parentBookId: " + parentBookId + "\n");
        return str.toString();
    }
}
