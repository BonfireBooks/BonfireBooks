package com.example.bonfirebooks;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class User implements Parcelable {

    // fields
    private String name;
    private String uid;
    private String email;
    private String hofstraId;

    // collections
    private HashMap<String, WishlistBook> wishlist = null;
    private HashMap<String, UserProfileChat> chats = null;
    private HashMap<String, UserProfileBook> books = null;

    public User() {}

    public User(String name, String uid, String email, String hofstraId, HashMap<String, WishlistBook> wishlist, HashMap<String, UserProfileChat> chats, HashMap<String, UserProfileBook> books) {
        this.name = name;
        this.uid = uid;
        this.email = email;
        this.hofstraId = hofstraId;
        this.wishlist = wishlist;
        this.chats = chats;
        this.books = books;
    }

    public String getName() {
        return name;
    }

    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }

    public String getHofstraId() {
        return hofstraId;
    }

    public HashMap<String, WishlistBook> getWishlist() {
        return wishlist;
    }

    public HashMap<String, UserProfileChat> getChats() {
        return chats;
    }

    public HashMap<String, UserProfileBook> getBooks() {
        return books;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setHofstraId(String hofstraId) {
        this.hofstraId = hofstraId;
    }

    public void setWishlist(HashMap<String, WishlistBook> wishlist) {
        this.wishlist = wishlist;
    }

    public void setChats(HashMap<String, UserProfileChat> chats) {
        this.chats = chats;
    }

    public void setBooks(HashMap<String, UserProfileBook> books) {
        this.books = books;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("User: \n");
        str.append("name: " + name + "\n");
        str.append("uid: " + uid + "\n");
        str.append("email: " + email + "\n");
        str.append("hofstraId: " + hofstraId + "\n");
        if (wishlist != null)
            str.append("wishlist: " + wishlist.toString() + "\n");
        if (chats != null)
            str.append("chats: " + chats.toString() + "\n");
        if (books != null)
            str.append("books: " + books.toString() + "\n");
        return str.toString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.name);
        parcel.writeString(this.uid);
        parcel.writeString(this.email);
        parcel.writeString(this.hofstraId);
        parcel.writeSerializable(this.wishlist);
        parcel.writeSerializable(this.chats);
        parcel.writeSerializable(this.books);
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

    private User(Parcel in) {
        name = in.readString();
        uid = in.readString();
        email = in.readString();
        hofstraId = in.readString();
        wishlist = (HashMap<String, WishlistBook>) in.readSerializable();
        chats = (HashMap<String, UserProfileChat>) in.readSerializable();
        books = (HashMap<String, UserProfileBook>) in.readSerializable();
    }
}
