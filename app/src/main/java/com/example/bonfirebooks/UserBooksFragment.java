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
import android.widget.GridLayout;
import android.widget.ImageView;
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

    TextView txtV_my_books;

    GridLayout gridL_books;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        user = ((MainActivity) getActivity()).getUser();

        gridL_books = view.findViewById(R.id.gridL_books);
        txtV_my_books = view.findViewById(R.id.txtV_my_books);

        txtV_my_books.setText("My Books");

        HashMap<String, UserProfileBook> books = user.getBooks();

        for (int i = 0; i < books.size(); i++) {
            UserProfileBook currBook = books.get(String.valueOf(i));

            View bookView = getLayoutInflater().inflate(R.layout.wishlist_book_item, null);

            // add some spacing between the grid items
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.setMargins(10, 10, 10, 10);

            bookView.setLayoutParams(params);

            // new book view details
            ImageView book_image = bookView.findViewById(R.id.imgV_coverImage);
            TextView book_title = bookView.findViewById(R.id.txtV_book_title);
            TextView book_price = bookView.findViewById(R.id.txtV_book_price);
            TextView book_condition = bookView.findViewById(R.id.txtV_book_condition);

            // set book views image
            Glide.with(getContext()).load(currBook.getCoverImgUrl()).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    return false;
                }
            }).into(book_image);


            Glide.with(getContext()).load(currBook.getCoverImgUrl()).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    // get rid of the background resource when image loads
                    book_image.setBackground(null);
                    return false;
                }
            }).into(book_image);

            // set other book view details
            book_condition.setText(currBook.getConditon());
            book_title.setText(currBook.getTitle());

            DecimalFormat decimalFormat = new DecimalFormat("0.00");

            book_price.setText("$ " + decimalFormat.format(currBook.getPrice()));

            bookView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("book", currBook.toString());
                    getParentFragmentManager().beginTransaction().replace(R.id.frame_container, new UserBooksDetailsFragment(currBook)).addToBackStack(null).commit();
                }
            });

            // add the book to the layout
            gridL_books.addView(bookView);
        }
    }
}