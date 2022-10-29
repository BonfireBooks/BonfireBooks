package com.example.bonfirebooks;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

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
    private String mParam1;
    private String mParam2;

    public WishlistFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WishlistFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WishlistFragment newInstance(String param1, String param2) {
        WishlistFragment fragment = new WishlistFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
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
    FirebaseUser user;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        layout_wishlist_empty = view.findViewById(R.id.layout_wishlist_empty);
        scrollV_wishlist = view.findViewById(R.id.scrollV_wishlist);
        linlayout_wishlist = view.findViewById(R.id.linlayout_wishlist);

        // firestore
        firestore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        populateScrollViewWithFirebase(linlayout_wishlist);

    }

    private void populateScrollViewWithFirebase(LinearLayout linearLayout) {
        firestore.collection("users").document(user.getUid()).collection("wishlist").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    Log.d("getUserWishlist", "Successful");
                    QuerySnapshot taskResults = task.getResult();
                    if(taskResults.size() < 1) {
                        layout_wishlist_empty.setVisibility(View.VISIBLE);

                        int i = 0;
                        // add all the docs into the vertical scroll
                        for (DocumentSnapshot taskDoc : task.getResult().getDocuments()) {

                            View bookView = getLayoutInflater().inflate(R.layout.wishlist_book_item, null);
                            bookView.setPadding(30, 0, 0, 0);

                            // new book view details
                            ImageView book_image = bookView.findViewById(R.id.imgV_coverImage);
                            TextView book_title = bookView.findViewById(R.id.txtV_book_title);
                            TextView book_price = bookView.findViewById(R.id.txtV_book_price);

                            // set book views image
                            Picasso.get().load(taskDoc.getString("coverImgUrl")).into(book_image, new Callback() {
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
                            book_title.setText(taskDoc.getString("title"));
                            book_price.setText(taskDoc.getDouble("price").toString());

                            // add the book to the layout
                            linearLayout.addView(bookView, i);

                            i++;
                        }
                    }
                } else {
                    Log.d("getUserWishlist", "Failed");
                }
            }
        });
    }
}