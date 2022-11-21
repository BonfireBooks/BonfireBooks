package com.example.bonfirebooks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.LogDescriptor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;

public class SplashScreen extends AppCompatActivity {

    private FirebaseUser currUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        currUser = FirebaseAuth.getInstance().getCurrentUser();

        // check if user is currently logged in
        if (currUser == null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashScreen.this, StartActivity.class));
                    finish();
                }
            }, 1000);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    User user = new User();

                    // set user uid
                    user.setUid(currUser.getUid());

                    DocumentReference userDoc = FirebaseFirestore.getInstance().collection("users").document(user.getUid());
                    setUser(user, userDoc);
                }
            }, 0); // add artificial delay
        }

    }

    private void setUser(User user, DocumentReference userDoc) {
        userDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    // add log d
                    DocumentSnapshot taskResult = task.getResult();

                    // add user data to user object
                    user.setName(taskResult.getString("name"));
                    user.setEmail(taskResult.getString("email"));
                    user.setHofstraId(taskResult.getString("hofID"));

                    // books as hashmap
                    HashMap<String, UserProfileBook> books = new HashMap<>();
                    userDoc.collection("books").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                // add log d
                                int i = 0;
                                for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                                    // create a new book book with the document data
                                    UserProfileBook book = new UserProfileBook(doc.getId(), doc.getString("title"), doc.getString("coverImgUrl"), doc.getString("condition"), doc.getDouble("price"), doc.getBoolean("isPublic"), (HashMap<String, String>) doc.get("images"));

                                    // add the book to the users wishlist
                                    books.put(String.valueOf(i), book);

                                    i++;
                                }

                                // set the users wishlists
                                user.setBooks(books);

                                Log.d("booksSet", user.toString());


                                FirebaseStorage.getInstance().getReference().child("User Images").child(currUser.getUid()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("getUserProfile", "Successful");
                                            user.setProfileUri(String.valueOf(task.getResult()));
                                        } else {
                                            Log.d("getUserProfile", "Failed");
                                        }

                                        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                                        intent.putExtra("user", (Parcelable) user);
                                        startActivity(intent);

                                        finish();
                                    }
                                });

                            } else {
                                // add log d
                            }
                        }
                    });
                } else {
                    // add log d
                }
            }
        });
//        startActivity(new Intent(SplashScreen.this, MainActivity.class));
    }
}