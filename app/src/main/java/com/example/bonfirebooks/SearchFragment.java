package com.example.bonfirebooks;

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
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    public SearchFragment() {
        // Required empty public constructor
    }

    String query;

    public SearchFragment(String query) {
        this.query = query.toLowerCase();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SearchFragment.
     */
    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
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
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    GridLayout gridL_books;

    SearchView search_bar;

    HashMap<Integer, Book> books = new HashMap<>();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        books = ((MainActivity) getActivity()).getBooksByTitle();

        gridL_books = view.findViewById(R.id.gridL_books);
        search_bar = view.findViewById(R.id.search_bar);

        search_bar.setQuery(query, false);

        books = getBooksMatchingQuery(books, query);
        Log.d("matchingBooks", books.toString());

        populateGridView(books, gridL_books);
    }

    private void populateGridView(HashMap<Integer, Book> books, GridLayout gridL_books) {

        for (int i = 0; i < books.size(); i++) {
            Book currBook = books.get(i);

            View bookView = getLayoutInflater().inflate(R.layout.home_book_item, null);

            // add some spacing between the grid items
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.setMargins(10, 10, 10, 10);

            bookView.setLayoutParams(params);

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
            book_price.setText("$ " + currBook.getPrice());

            bookView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("book", currBook.toString());
                    getParentFragmentManager().beginTransaction().replace(R.id.frame_container, new BookDetailsFragment(currBook, SearchFragment.this)).addToBackStack(null).commit();
                }
            });

            // add the book to the layout
            gridL_books.addView(bookView);

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
    }

    private HashMap<Integer, Book> getBooksMatchingQuery(HashMap<Integer, Book> books, String query) {
        HashMap<Integer, Book> matchingBooks = new HashMap<>();

        int index = 0;
        for (int i = 0; i < books.size(); i++) {
            Book currBook = books.get(i);

            // compare the query to isbns, title
            if (currBook.getIsbn10().contains(query) || currBook.getIsbn13().contains(query) || currBook.getTitle().toLowerCase().contains(query)) {
                matchingBooks.put(index, currBook);
                index++;
            } else {
                boolean placed = false;

                // compare query against authors
                HashMap<String, String> authors = currBook.getAuthors();
                for (int j = 0; j < authors.size(); j++) {
                    if (authors.get(String.valueOf(j)).toLowerCase().contains(query)) {
                        matchingBooks.put(index, currBook);
                        index++;
                        placed = true;
                        break;
                    }
                }

                // compare query against categories
                HashMap<String, String> categories = currBook.getCategories();
                for (int j = 0; j < categories.size() && !placed; j++) {
                    if (categories.get(String.valueOf(j)).toLowerCase().contains(query)) {
                        matchingBooks.put(index, currBook);
                        index++;
                        break;
                    }
                }
            }
        }

        return matchingBooks;
    }

}