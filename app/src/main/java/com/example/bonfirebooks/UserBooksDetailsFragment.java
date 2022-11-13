package com.example.bonfirebooks;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserBooksDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserBooksDetailsFragment extends Fragment {

    public UserBooksDetailsFragment() {
        // Required empty public constructor
    }

    UserProfileBook userProfileBook;

    public UserBooksDetailsFragment(UserProfileBook userProfileBook) {
        this.userProfileBook = userProfileBook;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UserBooksDetailsFragment.
     */
    public static UserBooksDetailsFragment newInstance() {
        UserBooksDetailsFragment fragment = new UserBooksDetailsFragment();
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
        return inflater.inflate(R.layout.fragment_user_books_details, container, false);
    }

    FirebaseStorage firebaseStorage;

    HorizontalScrollView horizScrollV_images;
    LinearLayout linlayout_image_scroll;

    TextView txtV_book_title;
    TextView txtV_book_condition_edit;
    TextView txtV_book_price;

    Button btn_back;
    Button btn_edit_book;
    ConstraintLayout btn_view_uBooks;

    HashMap<Integer, Book> books;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseStorage = FirebaseStorage.getInstance();

        horizScrollV_images = view.findViewById(R.id.horizScrollV_images);
        linlayout_image_scroll = view.findViewById(R.id.linlayout_image_scroll);
        txtV_book_title = view.findViewById(R.id.txtV_book_title);
        txtV_book_condition_edit = view.findViewById(R.id.txtV_book_condition_edit);
        txtV_book_price = view.findViewById(R.id.txtV_book_price);
        btn_back = view.findViewById(R.id.btn_back);
        btn_edit_book = view.findViewById(R.id.btn_edit_book);
        btn_view_uBooks = view.findViewById(R.id.btn_view_uBooks);

        txtV_book_title.setText(userProfileBook.getTitle());
        txtV_book_condition_edit.setText(userProfileBook.getConditon());
        txtV_book_price.setText("$" + userProfileBook.getPrice());

        books = ((MainActivity)getActivity()).getBooksByTime();

        Log.d("userProfileBook", userProfileBook.toString());

        // add images to the scroll view
        HashMap<Integer, String> images = userProfileBook.getImages();
        if(images != null) {
            for(int i = 0; i < images.size(); i++) {
                int finalI = i;
                firebaseStorage.getReference().child("User Images").child(images.get(i)).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()) {
                            Log.d("loadImage", "Successful");
                            addImageToScroll(task.getResult(), finalI);
                        } else {
                            Log.d("loadImage", "Successful");
                        }
                    }
                });
            }
        }

        btn_edit_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction().replace(R.id.frame_container, new UserBookDetailsEditFragment(userProfileBook)).addToBackStack(null).commit();
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).onBackPressed();
            }
        });

        btn_view_uBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean hasBook = false;
                for(Book currBook : books.values()) {
                    if(currBook.getTitle().equals(userProfileBook.getTitle())) {
                        hasBook = true;
                        getParentFragmentManager().beginTransaction().replace(R.id.frame_container, new BookOffersFragment(currBook)).addToBackStack(null).commit();
                    }
                }

                if(!hasBook) {
                    Toast.makeText(getContext(), "Books could not be loaded", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addImageToScroll(Uri imgUri, int i) {
        View image = getLayoutInflater().inflate(R.layout.book_image_view, null);
        ImageView book_image = image.findViewById(R.id.imgV_image);

        Picasso.get().load(imgUri).into(book_image, new Callback() {
            @Override
            public void onSuccess() {
                book_image.setBackground(null);
            }

            @Override
            public void onError(Exception e) {
                // do nothing -- keep image not found background
            }
        });

        image.setPadding(0, 0, 20, 0);
        linlayout_image_scroll.addView(image, i);

    }
}