package com.example.bonfirebooks;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;

public class User implements Parcelable {

    // fields
    private String name;
    private String uid;
    private String email;
    private String phoneNumber;

    // collections
    private HashMap<String, WishlistBook> wishlist = null;
    private HashMap<String, UserProfileChat> chats = null;
    private HashMap<String, UserProfileBook> books = null;

    private String profileUri;

    public User() {
    }

    public User(String name, String uid, String email, String phoneNumber, HashMap<String, WishlistBook> wishlist, HashMap<String, UserProfileChat> chats, HashMap<String, UserProfileBook> books, String profileUri) {
        this.name = name;
        this.uid = uid;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.wishlist = wishlist;
        this.chats = chats;
        this.books = books;
        this.profileUri = profileUri;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfileUri() {
        return profileUri;
    }

    public void setProfileUri(String profileUri) {
        this.profileUri = profileUri;
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

    public HashMap<String, WishlistBook> getWishlist() {
        return wishlist;
    }

    public HashMap<String, UserProfileChat> getChats() {
        return chats;
    }

    public HashMap<String, UserProfileBook> getBooks() {
        return books;
    }

    public void addBook(UserProfileBook book) {
        this.books.put(String.valueOf(books.size()), book);
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

    public void setWishlist(HashMap<String, WishlistBook> wishlist) {
        this.wishlist = wishlist;
    }

    public void setChats(HashMap<String, UserProfileChat> chats) {
        this.chats = chats;
    }

    public void setBooks(HashMap<String, UserProfileBook> books) {
        this.books = books;
    }

    public void setBook(String index, UserProfileBook book) {
        this.books.put(index, book);
    }

    public void deleteBook(String index) {
        books.remove(index);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("User: \n");
        str.append("name: " + name + "\n");
        str.append("uid: " + uid + "\n");
        str.append("email: " + email + "\n");
        if (wishlist != null)
            str.append("wishlist: " + wishlist.toString() + "\n");
        if (chats != null)
            str.append("chats: " + chats.toString() + "\n");
        if (books != null)
            str.append("books: " + books.toString() + "\n");
        return str.toString();
    }

    public void setBooksFirebase() {
        HashMap<String, UserProfileBook> books = new HashMap<>();

        FirebaseFirestore.getInstance().collection("users").document(getUid()).collection("books").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d("getUserBooks", "Success");

                    int i = 0;
                    for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                        // create a new book book with the document data
                        UserProfileBook book = new UserProfileBook(doc.getString("seller"), doc.getId(), doc.getString("title"), doc.getString("coverImgUrl"), doc.getString("condition"), getUid(), doc.getString("parentBookId"), doc.getDouble("price"), doc.getDouble("maxPrice"), doc.getBoolean("isPublic"), (HashMap<String, String>) doc.get("images"));

                        // add the book to the users wishlist
                        books.put(String.valueOf(i), book);

                        i++;

                    }

                    setBooks(books);
                }
            }
        });
    }

    public void setUser(User user, Activity currActivity) {

        DocumentReference userDoc = FirebaseFirestore.getInstance().collection("users").document(user.getUid());

        userDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d("getUserDoc", "Success");

                    DocumentSnapshot taskResult = task.getResult();

                    // add user data to user object
                    user.setName(taskResult.getString("name"));
                    user.setEmail(taskResult.getString("email"));
                    user.setPhoneNumber(taskResult.getString("phoneNumber"));

                    // books as hashmap
                    HashMap<String, UserProfileBook> books = new HashMap<>();
                    userDoc.collection("books").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                Log.d("getUserBooks", "Success");

                                int i = 0;
                                for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                                    // create a new book book with the document data
                                    UserProfileBook book = new UserProfileBook(user.getName(), doc.getId(), doc.getString("title"), doc.getString("coverImgUrl"), doc.getString("condition"), user.getUid(), doc.getString("parentBookId"), doc.getDouble("price"), doc.getDouble("maxPrice"), doc.getBoolean("isPublic"), (HashMap<String, String>) doc.get("images"));

                                    // add the book to the users wishlist
                                    books.put(String.valueOf(i), book);

                                    i++;
                                }

                                // set the users wishlists
                                user.setBooks(books);
                                Log.d("booksSet", user.toString());

                                HashMap<String, WishlistBook> wishlist = new HashMap<>();

                                // get wishlist from firebase
                                userDoc.collection("wishlist").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("loadWishlistFirebase", "Successful");

                                            int i = 0;
                                            for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                                                // create a new wishlist book with the document data
                                                WishlistBook wBook = new WishlistBook(doc.getId(), doc.getString("title"),
                                                        doc.getString("condition"), doc.getString("coverImgUrl"), doc.getString("parentBookId"), doc.getDouble("price"), (HashMap<String, String>) doc.get("imagePaths"));

                                                // add the book to the users wishlist
                                                wishlist.put(String.valueOf(i), wBook);

                                                i++;
                                            }

                                            user.setWishlist(wishlist);

                                            HashMap<String, UserProfileChat> chats = new HashMap<>();

                                            userDoc.collection("chats").orderBy("time", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d("loadUserChats", "Successful");
                                                        int i = 0;
                                                        for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                                                            // create a new chat book with the document data

                                                            String otherUserId = "";

                                                            for (String key : doc.getData().keySet()) {
                                                                if (!key.equals("content") && !key.equals("time")) {
                                                                    otherUserId = key;
                                                                }
                                                            }

                                                            UserProfileChat chat = new UserProfileChat(doc.getId(), otherUserId, doc.getString(otherUserId), doc.getString("content"), doc.getTimestamp("time").toDate());

                                                            // add the book to the users wishlist
                                                            chats.put(String.valueOf(i), chat);

                                                            i++;
                                                        }

                                                        user.setChats(chats);


                                                        FirebaseStorage.getInstance().getReference().child("User Images").child(getUid()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Uri> task) {
                                                                if (task.isSuccessful()) {
                                                                    Log.d("getUserProfile", "Successful");
                                                                    user.setProfileUri(String.valueOf(task.getResult()));
                                                                } else {
                                                                    Log.d("getUserProfile", "Failed");
                                                                }

                                                                // create the new intent and switch activities
                                                                Intent intent = new Intent(currActivity.getApplication(), MainActivity.class);
                                                                intent.putExtra("user", (Parcelable) user);
                                                                currActivity.startActivity(intent);
                                                                currActivity.finish();
                                                            }
                                                        });
                                                    } else {
                                                        Log.d("loadUserChats", "Failed");
                                                    }
                                                }
                                            });
                                        } else {
                                            Log.d("loadWishlistFirebase", "Failed");
                                        }
                                    }
                                });
                            } else {
                                Log.d("getUserBooks", "Failed");
                            }
                        }
                    });
                } else {
                    Log.d("getUserDoc", "Failed");
                }
            }
        });
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
        parcel.writeString(this.phoneNumber);
        parcel.writeString(this.profileUri);
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
        phoneNumber = in.readString();
        profileUri = in.readString();
        wishlist = (HashMap<String, WishlistBook>) in.readSerializable();
        chats = (HashMap<String, UserProfileChat>) in.readSerializable();
        books = (HashMap<String, UserProfileBook>) in.readSerializable();
    }
}
