package com.example.bonfirebooks;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserBooksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserBooksFragment extends Fragment {

    public UserBooksFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UserBooksFragment.
     */
    public static UserBooksFragment newInstance() {
        UserBooksFragment fragment = new UserBooksFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_books, container, false);
    }

    User user;

    ListView listV_books;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        user = ((MainActivity) getActivity()).getUser();

        listV_books = view.findViewById(R.id.listV_books);

        populateListView();

        listV_books.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                getParentFragmentManager().beginTransaction().replace(R.id.frame_container, new UserBookDetailsFragment(user.getBooks().get(String.valueOf(i)))).addToBackStack(null).commit();
            }
        });
    }

    private void populateListView() {
        HashMap<String, UserProfileBook> books = user.getBooks();
        if (books.size() != 0) {
            // change the visibilty of the views
            listV_books.setVisibility(View.VISIBLE);

            UserProfileBook[] userProfileBooks = new UserProfileBook[books.size()];

            for (int i = 0; i < books.size(); i++) {
                userProfileBooks[i] = books.get(String.valueOf(i));
            }

            // create and set the adapter for the list
            UserBooksListAdapter userBooksListAdapter = new UserBooksListAdapter(getActivity(), userProfileBooks);
            listV_books.setAdapter(userBooksListAdapter);
        } else {
            listV_books.setVisibility(View.GONE);
        }
    }
}