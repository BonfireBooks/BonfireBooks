package com.example.bonfirebooks;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

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

    User user;

    FirebaseFirestore firestore;
    FirebaseStorage firebaseStorage;

    View img_no_books_found;
    ListView listV_books;

    HashMap<Integer, UserBook> matchingBooks = new HashMap<>();

    ProgressDialog progressDialog;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        user = ((MainActivity)getActivity()).getUser();

        firestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        img_no_books_found = view.findViewById(R.id.img_no_books_found);
        listV_books = view.findViewById(R.id.listV_books);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading Offers...");
        progressDialog.show();

        getMatchingUserBooks();

        // despite being the same method used in the all chats fragment it does not work here
//        listV_books.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Log.d("itemClick", "true");
//                getParentFragmentManager().beginTransaction().replace(R.id.frame_container, new BookOfferDetailsFragment(book, matchingBooks.get(i))).commit();
//            }
//        });
    }

    private void getMatchingUserBooks() {

        firestore.document("/books/" + book.getBookId()).collection("/users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d("getDocument", "Successful");

                    int i = 0;
                    for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                        UserBook book = new UserBook(doc.getDouble("price"), doc.getId(), doc.getString("email"), doc.getString("name"), doc.getString("condition"), doc.getString("owner"), doc.getBoolean("isPublic"), doc.getTimestamp("time"), (HashMap<String, String>) doc.get("images"));
                        matchingBooks.put(i, book);

                        i++;
                    }

                    populateChatList();

                } else {
                    Log.d("getDocument", "Failed");
                }
            }
        });
    }

    private void populateChatList() {
        if(matchingBooks.size() > 0) {
            img_no_books_found.setVisibility(View.GONE);
            listV_books.setVisibility(View.VISIBLE);

            UserBook[] userBook = new UserBook[matchingBooks.size()];

            for (int i = 0; i < matchingBooks.size(); i++) {
                userBook[i] = matchingBooks.get(i);
            }

            // create and set the adapter for the list
            BookOfferAdapter bookOfferAdapter = new BookOfferAdapter(getActivity(), user, book, userBook, getParentFragmentManager());
            listV_books.setAdapter(bookOfferAdapter);
        } else {
            img_no_books_found.setVisibility(View.VISIBLE);
            listV_books.setVisibility(View.GONE);
        }

        progressDialog.dismiss();
    }
}