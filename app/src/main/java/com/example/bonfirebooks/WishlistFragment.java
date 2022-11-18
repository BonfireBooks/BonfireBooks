package com.example.bonfirebooks;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WishlistFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WishlistFragment extends Fragment {

    public WishlistFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment WishlistFragment.
     */
    public static WishlistFragment newInstance() {
        WishlistFragment fragment = new WishlistFragment();
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
        return inflater.inflate(R.layout.fragment_wishlist, container, false);
    }

    User user;

    // layout items
    ConstraintLayout layout_wishlist_empty;
    ConstraintLayout layout_wishlist_grid;
    Button btn_explore;
    BottomNavigationView bottomNavigationView;

    // Firebase
    FirebaseFirestore firestore;
    FirebaseUser currUser;

    ScrollView scrollV_books;
    GridLayout gridL_books;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        user = ((MainActivity) getActivity()).getUser();

        layout_wishlist_empty = view.findViewById(R.id.layout_wishlist_empty);

        layout_wishlist_grid = view.findViewById(R.id.layout_wishlist_grid);
        scrollV_books = view.findViewById(R.id.scrollV_books);
        gridL_books = view.findViewById(R.id.gridL_books);

        btn_explore = view.findViewById(R.id.btn_explore);
        bottomNavigationView = getActivity().findViewById(R.id.bottomNavView);

        // firestore
        firestore = FirebaseFirestore.getInstance();
        currUser = FirebaseAuth.getInstance().getCurrentUser();

        // add explore button listener
        btn_explore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // switch to the home fragment
                bottomNavigationView.getMenu().findItem(R.id.nav_home).setChecked(true);
                getParentFragmentManager().beginTransaction().replace(R.id.frame_container, new HomeFragment()).addToBackStack(null).commit();
            }
        });

        // handle swipe to refresh
        final SwipeRefreshLayout pullToRefresh = view.findViewById(R.id.swipe_refresh_wishlist);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshWishlist();
                pullToRefresh.setRefreshing(false);
                populateScrollViewWithFirebase();
            }
        });

        populateScrollViewWithFirebase();
    }

    private void refreshWishlist() {

        Log.d("wishlistRefresh", "Triggered");

        HashMap<String, WishlistBook> wishlist = new HashMap<>();

        // get wishlist from firebase
        firestore.collection("users").document(user.getUid()).collection("wishlist").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d("loadWishlistFirebase", "Successful");

                    int i = 0;
                    for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                        // create a new wishlist book with the document data
                        WishlistBook wBook = new WishlistBook(doc.getId(), doc.getString("title"),
                                doc.getString("condition"), doc.getString("coverImgUrl"), doc.getDouble("price"));

                        // add the book to the users wishlist
                        wishlist.put(String.valueOf(i), wBook);

                        i++;
                    }

                    user.setWishlist(wishlist);

                } else {
                    Log.d("loadWishlistFirebase", "Failed");
                }
            }
        });
    }

    private void populateScrollViewWithFirebase() {

        HashMap<String, WishlistBook> wishlist = user.getWishlist();
        if (wishlist.size() != 0) {

            gridL_books.removeAllViews();

            // change the visibilty of the views
            layout_wishlist_empty.setVisibility(View.GONE);
            layout_wishlist_grid.setVisibility(View.VISIBLE);

            Log.d("wishlist", wishlist.toString());
            for (int i = 0; i < wishlist.size(); i++) {

                WishlistBook currBook = wishlist.get(String.valueOf(i));

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

                DecimalFormat decimalFormat = new DecimalFormat("0.00");

                book_price.setText("$ " + decimalFormat.format(currBook.getPrice()));

                book_condition.setText(currBook.getCondition());

                bookView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //
                        HashMap<Integer, Book> books = ((MainActivity)getActivity()).getBooksByTime();

                        Book book = null;

                        // find other details on book
                        for(int i = 0; i < books.size(); i++) {
                            if(books.get(i).getTitle().equals(currBook.getTitle())) {
                                book = books.get(i);
                            }
                        }

                        if(book == null) {
                            Toast.makeText(getContext(), "Could Not Load Book", Toast.LENGTH_SHORT).show();
                        } else {
                            Book finalBook = book;
                            // get other book details
                            firestore.document("/books/" + book.getBookId() + "/users/" + currBook.getBookId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        Log.d("BookLocated", "Successful");

                                        DocumentSnapshot taskDoc = task.getResult();

                                        UserBook userBook = new UserBook(currBook.getPrice(), currBook.getBookId(), taskDoc.getString("email"), taskDoc.getString("name"), currBook.getCondition(),
                                                taskDoc.getString("owner"), taskDoc.getBoolean("isPublic"), taskDoc.getTimestamp("time"), (HashMap<String, String>) taskDoc.get("images"));

                                        getParentFragmentManager().beginTransaction().replace(R.id.frame_container, new BookOfferDetailsFragment(finalBook, userBook)).addToBackStack(null).commit();

                                    } else {
                                        Log.d("BookLocated", "Failed");
                                    }
                                }
                            });
                        }
                    }
                });

                // add the book to the layout
                gridL_books.addView(bookView);
            }
    } else {
        // change the visibility of the views
        layout_wishlist_empty.setVisibility(View.VISIBLE);
        layout_wishlist_grid.setVisibility(View.GONE);
    }

}
}