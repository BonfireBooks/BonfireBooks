package com.example.bonfirebooks;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.text.DecimalFormat;
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

    Button btn_view_new;
    Button btn_view_all;

    SearchView search_bar;

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

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        // data from main activity
        user = ((MainActivity) getActivity()).getUser();
        booksByTitle = ((MainActivity) getActivity()).getBooksByTitle();
        booksByTime = ((MainActivity) getActivity()).getBooksByTime();

        // views from layout
        horizontal_new_additions = view.findViewById(R.id.horizontal_new_additions);
        horizontal_all_books = view.findViewById(R.id.horizontal_all_books);
        linlayout_image_scroll_new = view.findViewById(R.id.linlayout_image_scroll_new);
        linlayout_image_scroll_all = view.findViewById(R.id.linlayout_image_scroll_all);
        btn_view_new = view.findViewById(R.id.btn_view_new);
        btn_view_all = view.findViewById(R.id.btn_view_all);
        search_bar = view.findViewById(R.id.search_bar);

        // loading indicator
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading Books...");
        progressDialog.show();
        progressDialog.setCancelable(false);

        // firebase initializations
        firestore = FirebaseFirestore.getInstance();
        currUser = FirebaseAuth.getInstance().getCurrentUser();
        storage = FirebaseStorage.getInstance();

        if (booksByTitle.size() == 0 || booksByTime.size() == 0) {
            getBooksFirebase("time");
            getBooksFirebase("title");
        } else {
            populateScrollViewWithFirebase(linlayout_image_scroll_new, booksByTime);
            populateScrollViewWithFirebase(linlayout_image_scroll_all, booksByTitle);
            // dismiss loading
            progressDialog.dismiss();
        }

        btn_view_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction().replace(R.id.frame_container, new BooksListFragment(booksByTime, "New Books")).addToBackStack(null).commit();
            }
        });

        btn_view_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction().replace(R.id.frame_container, new BooksListFragment(booksByTitle, "All Books")).addToBackStack(null).commit();
            }
        });

        search_bar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                getParentFragmentManager().beginTransaction().replace(R.id.frame_container, new SearchFragment(s)).addToBackStack(null).commit();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                return false;
            }
        });
    }

    private void getBooksFirebase(String order) {
        firestore.collection("books").orderBy(order).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d("RetrieveBooks", "Success");

                    int i = 0;
                    boolean reverse = false;

                    if (order.equals("time")) {
                        i = task.getResult().getDocuments().size() - 1;
                        reverse = true;
                    }

                    for (DocumentSnapshot taskDoc : task.getResult().getDocuments()) {
                        Book book = new Book(taskDoc.getDouble("price"), taskDoc.getId(), taskDoc.getString("title"), taskDoc.getString("isbn10"), taskDoc.getString("isbn13"), taskDoc.getString("description"), taskDoc.getString("coverImgUrl"), (HashMap<String, String>) taskDoc.get("authors"), (HashMap<String, String>) taskDoc.get("categories"), taskDoc.getTimestamp("time"));

                        if (taskDoc.contains("cheapestPrice")) {
                            book.setCheapestPrice(taskDoc.getDouble("cheapestPrice"));
                        }

                        if (taskDoc.contains("cheapestCondition")) {
                            book.setCheapestCondition(taskDoc.getString("cheapestCondition"));
                        }

                        if (order.equals("time")) {
                            booksByTime.put(i, book);
                        } else if (order.equals("title")) {
                            booksByTitle.put(i, book);
                        }

                        i = reverse ? i - 1 : i + 1;
                    }

                    // set the books in the main activity-
                    if (order.equals("time")) {
                        ((MainActivity) getActivity()).setBooksByTime(booksByTime);
                        populateScrollViewWithFirebase(linlayout_image_scroll_new, booksByTime);
                    } else if (order.equals("title")) {
                        ((MainActivity) getActivity()).setBooksByTitle(booksByTitle);
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
        for (int i = 0; i < 20; i++) {
            Book currBook = books.get(i);

            View bookView = getLayoutInflater().inflate(R.layout.home_book_item, null);
            bookView.setPadding(30, 0, 0, 0);

            // new book view details
            ImageView book_image = bookView.findViewById(R.id.imgV_coverImage);
            TextView book_title = bookView.findViewById(R.id.txtV_book_title);
            TextView book_price = bookView.findViewById(R.id.txtV_book_price);

            // set book views image
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
            book_title.setText(currBook.getTitle());

            DecimalFormat decimalFormat = new DecimalFormat("0.00");

            if(currBook.getCheapestPrice() != null) {
                if (currBook.getCheapestPrice() > 0) {
                    book_price.setText("$ " + decimalFormat.format(currBook.getCheapestPrice()));
                } else if (currBook.getPrice() > 0){
                    book_price.setText("$ " + decimalFormat.format(currBook.getPrice()));
                } else {
                    book_price.setText("No Price On File");
                }
            } else if (currBook.getPrice() > 0){
                book_price.setText("$ " + decimalFormat.format(currBook.getPrice()));
            } else {
                book_price.setText("No Price On File");
            }

            bookView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getParentFragmentManager().beginTransaction().replace(R.id.frame_container, new BookDetailsFragment(currBook)).addToBackStack(null).commit();
                }
            });

            // add the book to the layout
            linearLayout.addView(bookView, i);
        }
    }
}
