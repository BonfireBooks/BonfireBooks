package com.example.bonfirebooks;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
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

    ListView listV_books;

    SearchView search_bar;

    HashMap<Integer, Book> books = new HashMap<>();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        books = ((MainActivity) getActivity()).getBooksByTitle();

        listV_books = view.findViewById(R.id.listV_books);
        search_bar = view.findViewById(R.id.search_bar);

        search_bar.setQuery(query, false);
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

        Book[] booksArr = getBooksMatchingQuery(books, query).values().toArray(new Book[0]);
        BookListAdapter bookListAdapter = new BookListAdapter(getActivity(), getParentFragmentManager(), booksArr);
        listV_books.setAdapter(bookListAdapter);

        // navigate when the user clicks on book
        listV_books.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                getParentFragmentManager().beginTransaction().replace(R.id.frame_container, new BookDetailsFragment(booksArr[i])).addToBackStack(null).commit();
            }
        });
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