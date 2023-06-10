package com.example.bonfirebooks;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.text.DecimalFormat;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserBookDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserBookDetailsFragment extends Fragment {

    public UserBookDetailsFragment() {
        // Required empty public constructor
    }

    UserProfileBook userProfileBook;

    public UserBookDetailsFragment(UserProfileBook userProfileBook) {
        this.userProfileBook = userProfileBook;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UserBookDetailsFragment.
     */
    public static UserBookDetailsFragment newInstance() {
        UserBookDetailsFragment fragment = new UserBookDetailsFragment();
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

    User user;

    FirebaseFirestore firestore;
    FirebaseStorage firebaseStorage;

    HorizontalScrollView horizScrollV_images;
    LinearLayout linlayout_image_scroll;

    ImageView imgV_coverImage;

    TextView txtV_book_title;
    TextView txtV_book_condition_edit;
    TextView txtV_book_price;

    Button btn_edit_book;
    Button btn_view_uBooks;
    Button btn_delete_book;

    HashMap<Integer, Book> books;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        user = ((MainActivity)getActivity()).getUser();

        firestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        imgV_coverImage = view.findViewById(R.id.imgV_coverImage);
        horizScrollV_images = view.findViewById(R.id.horizScrollV_images);
        linlayout_image_scroll = view.findViewById(R.id.linlayout_image_scroll);
        txtV_book_title = view.findViewById(R.id.txtV_book_title);
        txtV_book_condition_edit = view.findViewById(R.id.txtV_book_condition_edit);
        txtV_book_price = view.findViewById(R.id.txtV_book_price);
        btn_edit_book = view.findViewById(R.id.btn_edit_book);
        btn_view_uBooks = view.findViewById(R.id.btn_view_uBooks);
        btn_delete_book = view.findViewById(R.id.btn_delete_book);

        txtV_book_title.setText(userProfileBook.getTitle());
        txtV_book_condition_edit.setText(userProfileBook.getCondition());

        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        txtV_book_price.setText("$ " + decimalFormat.format(userProfileBook.getPrice()));

        books = ((MainActivity) getActivity()).getBooksByTime();

        Log.d("userProfileBook", userProfileBook.toString());

        // add images to the scroll view
        HashMap<String, String> images = userProfileBook.getImages();
        if (images.size() != 0) {
            imgV_coverImage.setVisibility(View.GONE);
            horizScrollV_images.setVisibility(View.VISIBLE);

            for (int i = 0; i < images.size() && images.get(String.valueOf(i)) != null; i++) {
                int finalI = i;
                firebaseStorage.getReference().child(images.get(String.valueOf(i))).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Log.d("loadImage", "Successful");
                            addImageToScroll(task.getResult(), linlayout_image_scroll.getChildCount());
                        } else {
                            Log.d("loadImage", "Successful");
                        }
                    }
                });
            }
        } else {
            imgV_coverImage.setVisibility(View.VISIBLE);
            horizScrollV_images.setVisibility(View.GONE);
        }

        btn_edit_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction().replace(R.id.frame_container, new UserBookDetailsEditFragment(userProfileBook)).addToBackStack(null).commit();
            }
        });

        btn_view_uBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean hasBook = false;
                for (Book currBook : books.values()) {
                    if (currBook.getTitle().equals(userProfileBook.getTitle())) {
                        hasBook = true;
                        getParentFragmentManager().beginTransaction().replace(R.id.frame_container, new BookOffersFragment(currBook)).addToBackStack(null).commit();
                    }
                }

                if (!hasBook) {
                    Toast.makeText(getContext(), "Books could not be loaded", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_delete_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firestore.document("/books/" + userProfileBook.getParentBookId() + "/users/" + userProfileBook.getBookId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("bookDelete", "Successful");
                            Toast.makeText(getContext(), "Book Deleted", Toast.LENGTH_SHORT).show();

                            HashMap<String, UserProfileBook> books = user.getBooks();
                            for (int i = 0; i < books.size() && books.get(String.valueOf(i)) != null; i++) {
                                if (books.get(String.valueOf(i)) == userProfileBook) {
                                    user.deleteBook(String.valueOf(i));
                                }
                            }

                            ((MainActivity) getActivity()).getUser().setBooksFirebase();
                            getParentFragmentManager().popBackStack();
                            getParentFragmentManager().beginTransaction().replace(R.id.frame_container, new AccountFragment()).commit();
                        } else {
                            Log.d("bookDelete", "Failed");
                            Toast.makeText(getContext(), "Could not delete book", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void addImageToScroll(Uri imgUri, int i) {
        View image = getLayoutInflater().inflate(R.layout.offer_book_image_view, null);
        ImageView book_image = image.findViewById(R.id.imgV_image);

        Glide.with(getContext()).load(imgUri).listener(new RequestListener<Drawable>() {
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

        image.setPadding(0, 0, 20, 0);
        linlayout_image_scroll.addView(image, i);

    }
}