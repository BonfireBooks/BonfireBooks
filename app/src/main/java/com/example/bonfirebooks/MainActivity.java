package com.example.bonfirebooks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.common.subtyping.qual.Bottom;
import org.w3c.dom.Document;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    private User user;

    private FirebaseFirestore firestore;

    private HashMap<Integer, Book> booksByTitle = new HashMap<>();
    private HashMap<Integer, Book> booksByTime = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = getIntent().getParcelableExtra("user");
        firestore = FirebaseFirestore.getInstance();

        BottomNavigationView navigationView = findViewById(R.id.bottomNavView);
        navigationView.setOnItemSelectedListener(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new HomeFragment()).commit();
        }

    }

    public User getUser() {
        return user;
    }

    public HashMap<Integer, Book> getBooksByTitle() {
        return booksByTitle;
    }

    public void setBooksByTitle(HashMap<Integer, Book> books) {
        booksByTitle = books;
    }

    public HashMap<Integer, Book> getBooksByTime() {
        return booksByTime;
    }

    public void setBooksByTime(HashMap<Integer, Book> books) {
        booksByTime = books;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new HomeFragment()).commit();
                break;
            case R.id.nav_wish_list:
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new WishlistFragment()).commit();
                break;
            case R.id.nav_chats:
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new ChatFragment()).commit();
                break;
            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new AccountFragment()).commit();
                break;
        }

        return true;
    }
}