package com.example.bonfirebooks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

import org.checkerframework.common.subtyping.qual.Bottom;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigationView = findViewById(R.id.bottomNavView);
        navigationView.setOnItemSelectedListener(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new UploadBookSearchFragment()).commit();
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new HomeFragment()).commit();
                break;
            case R.id.nav_wish_list:
                //  Todo -- add navigation to a wishlist fragment
                break;
            case R.id.nav_chats:
                //  Todo -- add navigation to a chats fragment
                break;
            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, new AccountFragment()).commit();
                break;
        }

        return true;
    }
}