package com.example.bonfirebooks;

import java.io.Serializable;
import java.util.HashMap;

public class UserProfileBook implements Serializable {

    private String name;
    private String bookId;
    private String title;
    private String coverImgUrl;
    private String condition;
    private String parentBookId;
    private String owner;
    private Double price;
    private Double maxPrice;
    private Boolean isPublic;
    HashMap<String, String> images;

    public UserProfileBook() {

    }

    public UserProfileBook(String name, String bookId, String title, String coverImgUrl, String condition, String owner, String parentBookId, Double price, Double maxPrice, Boolean isPublic, HashMap<String, String> images) {
        this.name = name;
        this.bookId = bookId;
        this.title = title;
        this.coverImgUrl = coverImgUrl;
        this.parentBookId = parentBookId;
        this.owner = owner;
        this.price = price;
        this.maxPrice = maxPrice;
        this.isPublic = isPublic;
        this.images = images;

        if(condition != null) {
            switch (condition.toLowerCase()) {
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
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public String getParentBookId() {
        return parentBookId;
    }

    public void setParentBookId(String parentBookId) {
        this.parentBookId = parentBookId;
    }

    public Boolean getPublic() {
        return isPublic;
    }

    public void setPublic(Boolean aPublic) {
        isPublic = aPublic;
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

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
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
        str.append("condition: " + condition + "\n");
        str.append("price: " + price + "\n");
        str.append("coverImgUrl: " + coverImgUrl + "\n");
        return str.toString();
    }
}
