package com.example.bonfirebooks;

import android.app.ProgressDialog;
import android.net.Uri;
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
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookOffersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookOffersFragment extends Fragment {

    Book book;

    public BookOffersFragment() {
        // Required empty public constructor
    }

    public BookOffersFragment(Book book) {
        this.book = book;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BookOffersFragment.
     */
    public static BookOffersFragment newInstance() {
        BookOffersFragment fragment = new BookOffersFragment();
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
        return inflater.inflate(R.layout.fragment_book_offers, container, false);
    }

    FirebaseFirestore firestore;
    FirebaseStorage firebaseStorage;

    TextView txtV_book_offers;
    GridLayout gridL_books;

    HashMap<Integer, UserBook> matchingBooks = new HashMap<>();

    ProgressDialog progressDialog;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        txtV_book_offers = view.findViewById(R.id.txtV_book_offers);
        gridL_books = view.findViewById(R.id.gridL_books);

        txtV_book_offers.setText(book.getTitle());

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading Offers...");
        progressDialog.show();

        getMatchingUserBooks();
    }

    private void getMatchingUserBooks() {

        firestore.collection("books").whereEqualTo("isbn10", book.getIsbn10()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                String path = task.getResult().getDocuments().get(0).getReference().getPath();

                firestore.document(path).collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            Log.d("getDocument", "Successful");

                            int i = 0;
                            for(DocumentSnapshot doc : task.getResult().getDocuments()) {
                                UserBook book = new UserBook(doc.getDouble("price"), doc.getId(), doc.getString("email"), doc.getString("userName"), doc.getString("condition"),  doc.getTimestamp("time"), (HashMap<String, String>) doc.get("images"));
                                matchingBooks.put(i, book);

                                i++;
                            }

                            populateGrid(gridL_books);

                        } else {
                            Log.d("getDocument", "Failed");
                        }
                    }
                });
            }
        });
    }

    private void populateGrid(GridLayout gridL_books) {
        for (int i = 0; i < matchingBooks.size(); i++) {
            UserBook currBook = matchingBooks.get(i);

            View bookView = getLayoutInflater().inflate(R.layout.offer_book_fragment, null);

            // add some spacing between the grid items
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.setMargins(10, 10, 10, 10);

            bookView.setLayoutParams(params);

            // new book view details
            ImageView book_image = bookView.findViewById(R.id.imgV_coverImage);
            TextView book_condition = bookView.findViewById(R.id.txtV_book_condition);
            TextView book_price = bookView.findViewById(R.id.txtV_book_price);

            if(currBook.getPathsToImages() != null) {
                if(currBook.getPathsToImages().size() > 0) {
                    firebaseStorage.getReference().child(currBook.getPathsToImages().get("0")).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Log.d("getImage", "Successful");
                            } else {
                                Log.d("getImage", "Failed");
                            }

                            Picasso.get().load(task.getResult()).into(book_image, new Callback() {
                                @Override
                                public void onSuccess() {
                                    book_image.setBackground(null);
                                }

                                @Override
                                public void onError(Exception e) {
                                    // do nothing -- keep image not found background
                                }
                            });
                        }
                    });
                } else {
                    // set book views image
                    Picasso.get().load(book.getCoverImgUrl()).into(book_image, new Callback() {
                        @Override
                        public void onSuccess() {
                            book_image.setBackground(null);
                        }

                        @Override
                        public void onError(Exception e) {
                            // do nothing -- keep image not found background
                        }
                    });
                }
            } else {
                // set book views image
                Picasso.get().load(book.getCoverImgUrl()).into(book_image, new Callback() {
                    @Override
                    public void onSuccess() {
                        book_image.setBackground(null);
                    }

                    @Override
                    public void onError(Exception e) {
                        // do nothing -- keep image not found background
                    }
                });
            }

            // set other book view details
            book_condition.setText(currBook.getCondition());
            book_price.setText("$ " + currBook.getPrice());

            bookView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("book", currBook.toString());
                    getParentFragmentManager().beginTransaction().replace(R.id.frame_container, new BookOfferDetailsFragment(book, currBook)).addToBackStack(null).commit();
                }
            });

            // add the book to the layout
            gridL_books.addView(bookView);
        }

        progressDialog.dismiss();;
    }
}