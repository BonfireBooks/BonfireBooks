package com.example.bonfirebooks;

import android.graphics.drawable.Drawable;
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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

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
    ListView listV_books;
    Button btn_explore;
    BottomNavigationView bottomNavigationView;
    RelativeLayout layout_wrapper;

    // Firebase
    FirebaseFirestore firestore;
    FirebaseUser currUser;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        user = ((MainActivity) getActivity()).getUser();

        layout_wishlist_empty = view.findViewById(R.id.layout_wishlist_empty);
        listV_books = view.findViewById(R.id.listV_books);
        btn_explore = view.findViewById(R.id.btn_explore);
        bottomNavigationView = getActivity().findViewById(R.id.bottomNavView);
        layout_wrapper = view.findViewById(R.id.layout_wrapper);

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

        getWishlistFromFirebase();

        // handle swipe to refresh
        final SwipeRefreshLayout pullToRefresh = view.findViewById(R.id.swipe_refresh_wishlist);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getWishlistFromFirebase();
                pullToRefresh.setRefreshing(false);
            }
        });

        listV_books.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView listView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(listV_books.getTop() == 0 && firstVisibleItem == 0) {
                    pullToRefresh.setEnabled(true);
                } else {
                    pullToRefresh.setEnabled(false);
                }

            }
        });

        listV_books.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                WishlistBook currBook = user.getWishlist().get(String.valueOf(i));

                HashMap<Integer, Book> books = ((MainActivity) getActivity()).getBooksByTime();
                Book book = null;

                // find other details on book
                for (int j = 0; j < books.size(); j++) {
                    if (books.get(j).getTitle().equals(currBook.getTitle())) {
                        book = books.get(j);
                    }
                }

                // get other book details
                Book finalBook = book;
                firestore.document("/books/" + currBook.getParentBookId() + "/users/" + currBook.getBookId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
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
        });

    }

    private void getWishlistFromFirebase() {

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
                                doc.getString("condition"), doc.getString("coverImgUrl"), doc.getString("parentBookId"), doc.getDouble("price"), (HashMap<String, String>) doc.get("imagePaths"));

                        // add the book to the users wishlist
                        wishlist.put(String.valueOf(i), wBook);

                        i++;
                    }

                    user.setWishlist(wishlist);
                    populateScrollView();

                } else {
                    Log.d("loadWishlistFirebase", "Failed");
                }
            }
        });
    }

    private void populateScrollView() {

        HashMap<String, WishlistBook> wishlist = user.getWishlist();
        if (wishlist.size() != 0) {
            // change the visibilty of the views
            layout_wishlist_empty.setVisibility(View.GONE);
            layout_wrapper.setVisibility(View.VISIBLE);

            WishlistBook[] wishlistBooks = new WishlistBook[wishlist.size()];

            for (int i = 0; i < wishlist.size(); i++) {
                wishlistBooks[i] = wishlist.get(String.valueOf(i));
            }

            // create and set the adapter for the list
            WishlistBookListAdapter bookOfferAdapter = new WishlistBookListAdapter(getActivity(), wishlistBooks);
            listV_books.setAdapter(bookOfferAdapter);
        } else {
            layout_wishlist_empty.setVisibility(View.VISIBLE);
            layout_wrapper.setVisibility(View.GONE);
        }
    }
}