package com.example.bonfirebooks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.accounts.Account;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.bumptech.glide.manager.SupportRequestManagerFragment;
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

    BottomNavigationView navigationView;

    private HashMap<Integer, Book> booksByTitle = new HashMap<>();
    private HashMap<Integer, Book> booksByTime = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = getIntent().getParcelableExtra("user");
        firestore = FirebaseFirestore.getInstance();

        navigationView = findViewById(R.id.bottomNavView);
        navigationView.setOnItemSelectedListener(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new HomeFragment()).addToBackStack(null).commit();
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
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new HomeFragment()).addToBackStack(null).commit();
                break;
            case R.id.nav_wish_list:
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new WishlistFragment()).addToBackStack(null).commit();
                break;
            case R.id.nav_chats:
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new AllChatsFragment()).addToBackStack(null).commit();
                break;
            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new AccountFragment()).addToBackStack(null).commit();
                break;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        // fragment being switched to
        if (getSupportFragmentManager().getFragments().size() > 0) {

            String fragName = getSupportFragmentManager().getFragments().get(0).getClass().getName();

            // check that Glide hasn't taken over the top of the backstack
            if (fragName.equals(SupportRequestManagerFragment.class.getName())) {
                fragName = getSupportFragmentManager().getFragments().get(1).getClass().getName();
            }

            // set the bottom nav based on the fragment being switched into
            if (fragName.equals(HomeFragment.class.getName())) {
                navigationView.getMenu().findItem(R.id.nav_home).setChecked(true);
            } else if (fragName.equals(WishlistFragment.class.getName())) {
                navigationView.getMenu().findItem(R.id.nav_wish_list).setChecked(true);
            } else if (fragName.equals(AllChatsFragment.class.getName())) {
                navigationView.getMenu().findItem(R.id.nav_chats).setChecked(true);
            } else if (fragName.equals(AccountFragment.class.getName())) {
                navigationView.getMenu().findItem(R.id.nav_profile).setChecked(true);
            }
        } else {
            // if the user presses back when there are no previous fragments visited we can exit the app
            this.finishAffinity();
        }
    }
}