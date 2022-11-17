package com.example.bonfirebooks;

import com.google.firebase.Timestamp;

import java.util.HashMap;

public class UserBook {

    private double price;
    private String bookId;
    private String email;
    private String name;
    private String condition;
    private String owner;
    private boolean isPublic;
    private HashMap<String, String> images;
    private Timestamp time;

    public UserBook(double price, String bookId, String email, String name, String condition, String owner, boolean isPublic, Timestamp time, HashMap<String, String> images) {
        this.price = price;
        this.bookId = bookId;
        this.email = email;
        this.name = name;
        this.condition = condition;
        this.owner = owner;
        this.isPublic = isPublic;
        this.time = time;
        this.images = images;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
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

    public HashMap<String, String> getImages() {
        return images;
    }

    public void setImages(HashMap<String, String> images) {
        this.images = images;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUserName(String name) {
        this.name = name;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public void setTime(Timestamp time) { this.time = time; }

    public void setPathsToImages(HashMap<String, String> images) {
        this.images = images;
    }

    public double getPrice() {
        return price;
    }

    public String getBookId() {
        return bookId;
    }

    public String getEmail() {
        return email;
    }

    public String getUserName() {
        return name;
    }

    public String getCondition() {
        return condition;
    }

    public Timestamp getTime() { return time; }

    public HashMap<String, String> getPathsToImages() {
        return images;
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("price: " + price + "\n");
        str.append("bookId: " + bookId + "\n");
        str.append("email: " + email + "\n");
        str.append("name: " + name + "\n");
        str.append("condition: " + condition + "\n");
        str.append("time: " + time + "\n");

        if(images != null)
            str.append("images: " + images.toString());

        return str.toString();
    }
}
