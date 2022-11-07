package com.example.bonfirebooks;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
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

    User user;
    HashMap<Integer, Book> booksByTitle = new HashMap<>();
    HashMap<Integer, Book> booksByTime = new HashMap<>();

    FirebaseFirestore firestore;
    FirebaseUser currUser;
    FirebaseStorage storage;

    HorizontalScrollView horizontal_new_additions;
    HorizontalScrollView horizontal_all_books;

    LinearLayout linlayout_image_scroll_new;
    LinearLayout linlayout_image_scroll_all;

    ProgressDialog progressDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        user = ((MainActivity)getActivity()).getUser();
        booksByTitle = ((MainActivity)getActivity()).getBooksByTitle();
        booksByTime = ((MainActivity)getActivity()).getBooksByTime();

        horizontal_new_additions = view.findViewById(R.id.horizontal_new_additions);
        horizontal_all_books = view.findViewById(R.id.horizontal_all_books);
        linlayout_image_scroll_new = view.findViewById(R.id.linlayout_image_scroll_new);
        linlayout_image_scroll_all = view.findViewById(R.id.linlayout_image_scroll_all);

        // loading indicator
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading Books...");
        progressDialog.show();
        progressDialog.setCancelable(false);

        // firebase initializations
        firestore = FirebaseFirestore.getInstance();
        currUser = FirebaseAuth.getInstance().getCurrentUser();
        storage = FirebaseStorage.getInstance();

        if(booksByTitle.size() == 0 || booksByTime.size() == 0) {
            getBooksFirebase("time");
            getBooksFirebase("title");

        } else {
            populateScrollViewWithFirebase(linlayout_image_scroll_new, booksByTime);
            populateScrollViewWithFirebase(linlayout_image_scroll_all, booksByTitle);
        }

    }

    private void getBooksFirebase(String order) {
        firestore.collection("books").orderBy(order).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    Log.d("RetrieveBooks", "Success");

                    int i = 0;
                    for (DocumentSnapshot taskDoc : task.getResult().getDocuments()) {
                        Book book = new Book(taskDoc.getDouble("price"), taskDoc.getString("title"), taskDoc.getString("isbn10"), taskDoc.getString("isbn13"), taskDoc.getString("description"), taskDoc.getString("coverImgUrl"), (HashMap<String, String>) taskDoc.get("authors"), (HashMap<String, String>) taskDoc.get("categories"));

                        if(order.equals("time")) {
                            booksByTime.put(i, book);
                        } else if(order.equals("title")) {
                            booksByTitle.put(i, book);
                        }

                        i++;
                    }

                    // set the books in the main activity-
                    if(order.equals("time")) {
                        ((MainActivity)getActivity()).setBooksByTime(booksByTime);
                        populateScrollViewWithFirebase(linlayout_image_scroll_new, booksByTime);
                    } else if(order.equals("title")) {
                        ((MainActivity)getActivity()).setBooksByTitle(booksByTitle);
                        populateScrollViewWithFirebase(linlayout_image_scroll_all, booksByTitle);

                        // dismiss loading
                        progressDialog.dismiss();
                    }


                } else {
                    Log.d("RetrieveBooks", "Failed");
                }
            }
        });
    }

    private void populateScrollViewWithFirebase(LinearLayout linearLayout, HashMap<Integer, Book> books) {
        for(int i = 0; i < 20; i++) {
            Book currBook = books.get(i);

            View bookView = getLayoutInflater().inflate(R.layout.home_book_item, null);
            bookView.setPadding(30, 0, 0, 0);

            // new book view details
            ImageView book_image = bookView.findViewById(R.id.imgV_coverImage);
            TextView book_title = bookView.findViewById(R.id.txtV_book_title);
            TextView book_price = bookView.findViewById(R.id.txtV_book_price);

            // set book views image
            Picasso.get().load(currBook.getCoverImgUrl()).into(book_image, new Callback() {
                @Override
                public void onSuccess() {
                    book_image.setBackground(null);
                }

                @Override
                public void onError(Exception e) {
                    // do nothing -- keep image not found background
                }
            });

            // set other book view details
            book_title.setText(currBook.getTitle());
            book_price.setText(String.valueOf(currBook.getPrice()));

            bookView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("book" , currBook.toString());
                    getParentFragmentManager().beginTransaction().replace(R.id.frame_container, new BookDetailsFragment(currBook)).commit();
                }
            });

            // add the book to the layout
            linearLayout.addView(bookView, i);
        }
    }
}
