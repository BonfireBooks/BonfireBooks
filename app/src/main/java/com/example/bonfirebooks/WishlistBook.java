package com.example.bonfirebooks;

import java.io.Serializable;

public class WishlistBook implements Serializable {
    private String bookId;
    private String title;
    private String condition;
    private String coverImgUrl;
    private Double price;

    public WishlistBook() {
    }

    public WishlistBook(String bookId, String title, String condition, String coverImgUrl, Double price) {
        this.bookId = bookId;
        this.title = title;
        this.condition = condition;
        this.coverImgUrl = coverImgUrl;
        this.price = price;
    }

    public WishlistBook(String title, String condition, String coverImgUrl, double price) {
        this.title = title;
        this.condition = condition;
        this.coverImgUrl = coverImgUrl;
        this.price = price;
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
        this.condition = condition;
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
        return str.toString();
    }
}
