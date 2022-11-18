package com.example.bonfirebooks;

import android.util.Log;

import com.google.firebase.Timestamp;

import java.util.HashMap;

public class Book {

    private double price;
    private Double cheapestPrice;
    private String title;
    private String isbn10;
    private String isbn13;
    private String description;
    private String coverImgUrl;
    private String cheapestCondition;
    private HashMap<String, String> authors;
    private HashMap<String, String> categories;
    Timestamp time;

    public Book() {}

    public Book(double price, String title, String isbn10, String isbn13, String description, String coverImgUrl, HashMap<String, String> authors, HashMap<String, String> categories, Timestamp time) {
        this.price = price;
        this.title = title;
        this.isbn10 = isbn10;
        this.isbn13 = isbn13;
        this.description = description;
        this.coverImgUrl = coverImgUrl;
        this.authors = authors;
        this.categories = categories;
        this.time = time;
    }

    public String getCheapestCondition() {
        return cheapestCondition;
    }

    public void setCheapestCondition(String cheapestCondition) {
        this.cheapestCondition = cheapestCondition;
    }

    public Double getCheapestPrice() {
        return cheapestPrice;
    }

    public void setCheapestPrice(Double cheapestPrice) {
        this.cheapestPrice = cheapestPrice;
    }

    public void setCoverImgUrl(String coverImgUrl) {
        this.coverImgUrl = coverImgUrl;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public double getPrice() {
        return price;
    }

    public String getTitle() {
        return title;
    }

    public String getIsbn10() {
        return isbn10;
    }

    public String getIsbn13() {
        return isbn13;
    }

    public String getDescription() {
        return description;
    }

    public String getCoverImgUrl() {
        return coverImgUrl;
    }

    public HashMap<String, String> getAuthors() {
        return authors;
    }

    public HashMap<String, String> getCategories() {return categories;}

    public void setPrice(double price) {
        this.price = price;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setIsbn10(String isbn10) {
        this.isbn10 = isbn10;
    }

    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setcoverImgUrl(String coverImgUrl) {this.coverImgUrl = coverImgUrl; }

    public void setAuthors(HashMap<String, String> authors) {
        this.authors = authors;
    }

    public void setCategories(HashMap<String, String> categories) {
        this.categories = categories;
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("price: " + price + "\n");
        str.append("title: " + title.toString() + "\n");
        str.append("description: " + description.toString() + "\n");
        str.append("authors: " + authors.toString() + "\n");
        if (categories != null)
            str.append("categories: " + categories.toString() + "\n");
        str.append("isbn10: " + isbn10 + "\n");
        str.append("isbn13: " + isbn13 + "\n");
        str.append("coverImgUrl: " + coverImgUrl);
        return str.toString();
    }
}
