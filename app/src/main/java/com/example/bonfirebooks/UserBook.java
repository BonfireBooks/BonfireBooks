package com.example.bonfirebooks;

import com.google.firebase.Timestamp;

import java.util.HashMap;

public class UserBook {

    private double price;
    private String email;
    private String name;
    private String condition;
    private HashMap<String, String> images;
    private Timestamp time;

    public UserBook(double price, String email, String name, String condition, HashMap<String, String> images) {
        this.price = price;
        this.email = email;
        this.name = name;
        this.condition = condition;
        this.images = images;
    }

    public UserBook(double price, String email, String name, String condition, Timestamp time, HashMap<String, String> images) {
        this.price = price;
        this.email = email;
        this.name = name;
        this.condition = condition;
        this.time = time;
        this.images = images;
    }

    public void setPrice(double price) {
        this.price = price;
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
        str.append("email: " + email + "\n");
        str.append("name: " + name + "\n");
        str.append("condition: " + condition + "\n");
        str.append("time: " + time + "\n");

        if(images != null)
            str.append("images: " + images.toString());

        return str.toString();
    }
}
