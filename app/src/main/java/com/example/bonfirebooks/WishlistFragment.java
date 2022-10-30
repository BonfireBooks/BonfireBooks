package com.example.bonfirebooks;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WishlistFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WishlistFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private User user;

    public WishlistFragment(User user) {
        // Required empty public constructor
        this.user = user;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param user User;
     * @return A new instance of fragment WishlistFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WishlistFragment newInstance(User user) {
        WishlistFragment fragment = new WishlistFragment(user);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = (User) getArguments().get(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wishlist, container, false);
    }

    // layout items
    ConstraintLayout layout_wishlist_empty;
    ScrollView scrollV_wishlist;
    LinearLayout linlayout_wishlist;

    // Firebase
    FirebaseFirestore firestore;
    FirebaseUser currUser;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        layout_wishlist_empty = view.findViewById(R.id.layout_wishlist_empty);
        scrollV_wishlist = view.findViewById(R.id.scrollV_wishlist);
        linlayout_wishlist = view.findViewById(R.id.linlayout_wishlist);

        // firestore
        firestore = FirebaseFirestore.getInstance();
        currUser = FirebaseAuth.getInstance().getCurrentUser();

        // handle swipe to refresh
        final SwipeRefreshLayout pullToRefresh = view.findViewById(R.id.swipe_refresh_wishlist);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshWishlist();
                pullToRefresh.setRefreshing(false);
                populateScrollViewWithFirebase(linlayout_wishlist);
            }
        });
        populateScrollViewWithFirebase(linlayout_wishlist);
    }

    private void refreshWishlist() {

        Log.d("wishlistRefresh", "Triggered");

        HashMap<String, WishlistBook> wishlist = new HashMap<>();

        // get wishlist from firebase
        firestore.collection("users").document(user.getUid()).collection("wishlist").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    Log.d("loadWishlistFirebase", "Successful");

                    int i = 0;
                    for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                        // create a new wishlist book with the document data
                        WishlistBook wBook = new WishlistBook(doc.getString("path"), doc.getString("title"),
                                doc.getString("coverImgUrl"), doc.getDouble("price"));

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

    private void populateScrollViewWithFirebase(LinearLayout linearLayout) {

        HashMap<String, WishlistBook> wishlist = user.getWishlist();
        if (wishlist != null && wishlist.size() != 0) {
            // change the visibilty of the views
            layout_wishlist_empty.setVisibility(View.GONE);
            scrollV_wishlist.setVisibility(View.VISIBLE);

            Log.d("wishlist", wishlist.toString());
            for (int i = 0; i < wishlist.size(); i++) {
                View bookView = getLayoutInflater().inflate(R.layout.wishlist_book_item, null);
                bookView.setPadding(30, 0, 0, 0);

                // new book view details
                ImageView book_image = bookView.findViewById(R.id.imgV_coverImage);
                TextView book_title = bookView.findViewById(R.id.txtV_book_title);
                TextView book_price = bookView.findViewById(R.id.txtV_book_price);

                String strI = String.valueOf(i);

                // set book views image
                Picasso.get().load(wishlist.get(strI).getCoverImgUrl()).into(book_image, new Callback() {
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
                book_title.setText(wishlist.get(strI).getTitle());
                book_price.setText(wishlist.get(strI).getPrice().toString());

                // add the book to the layout
                linearLayout.addView(bookView, i);
            }
        } else {
            // change the visibility of the views
            layout_wishlist_empty.setVisibility(View.VISIBLE);
            scrollV_wishlist.setVisibility(View.GONE);
        }

    }
}