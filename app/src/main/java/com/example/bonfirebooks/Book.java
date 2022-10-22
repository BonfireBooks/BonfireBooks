package com.example.bonfirebooks;

import android.util.Log;

import java.util.HashMap;

public class Book {

    private double price;
    private String title;
    private String isbn10;
    private String isbn13;
    private String description;
    private String coverImgUrl;
    private HashMap<String, String> authors;

    public Book() {}

    public Book(double price, String title, String isbn10, String isbn13, String description, String coverImgUrl, HashMap<String, String> authors) {
        this.price = price;
        this.title = title;
        this.isbn10 = isbn10;
        this.isbn13 = isbn13;
        this.description = description;
        this.coverImgUrl = coverImgUrl;
        this.authors = authors;

        Log.d("thisDescript", description);

        Log.d("bookClass", this.toString());
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

    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("price: " + price + "\n");
        str.append("title: " + title.toString() + "\n");
        str.append("description: " + description.toString() + "\n");
        str.append("authors: " + authors.toString() + "\n");
        str.append("isbn10: " + isbn10 + "\n");
        str.append("isbn13: " + isbn13 + "\n");
        str.append("coverImgUrl: " + coverImgUrl);
        return str.toString();
    }
}
