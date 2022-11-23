package com.example.bonfirebooks;

import java.io.Serializable;
import java.util.HashMap;

public class UserProfileBook implements Serializable {

    private String bookId;
    private String title;
    private String coverImgUrl;
    private String conditon;
    private Double price;
    private Boolean isPublic;
    HashMap<String, String> images;

    public UserProfileBook() {

    }

    public UserProfileBook(String bookId, String title, String coverImgUrl, String conditon, Double price, Boolean isPublic, HashMap<String, String> images) {
        this.bookId = bookId;
        this.title = title;
        this.coverImgUrl = coverImgUrl;
        this.price = price;
        this.isPublic = isPublic;
        this.images = images;

        switch(conditon) {
            case "like-new":
                this.conditon = "Like-New";
                break;
            case "good":
                this.conditon = "Good";
                break;
            case "fair":
                this.conditon = "Fair";
                break;
            case "poor":
                this.conditon = "Poor";
                break;
        }
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public HashMap<String, String> getImages() {
        return images;
    }

    public void setImages(HashMap<String, String> images) {
        this.images = images;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getConditon() {
        return conditon;
    }

    public void setConditon(String conditon) {
        this.conditon = conditon;
    }

    public String getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }

    public String getCoverImgUrl() {
        return coverImgUrl;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
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
        str.append("bookId: " + bookId + "\n");
        str.append("title: " + title + "\n");
        str.append("condition: " + conditon + "\n");
        str.append("price: " + price + "\n");
        str.append("coverImgUrl: " + coverImgUrl + "\n");
        return str.toString();
    }
}
