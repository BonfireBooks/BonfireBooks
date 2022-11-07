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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

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
    // TODO: Rename and change types and number of parameters
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
    ScrollView scrollV_wishlist;
    LinearLayout linlayout_wishlist;
    Button btn_explore;
    BottomNavigationView bottomNavigationView;

    // Firebase
    FirebaseFirestore firestore;
    FirebaseUser currUser;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        user = ((MainActivity)getActivity()).getUser();

        layout_wishlist_empty = view.findViewById(R.id.layout_wishlist_empty);
        scrollV_wishlist = view.findViewById(R.id.scrollV_wishlist);
        linlayout_wishlist = view.findViewById(R.id.linlayout_wishlist);
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