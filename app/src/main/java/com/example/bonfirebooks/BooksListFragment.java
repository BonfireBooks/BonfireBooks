package com.example.bonfirebooks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BooksListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BooksListFragment extends Fragment {

    public BooksListFragment() {
        // Required empty public constructor
    }

    HashMap<Integer, Book> books = new HashMap<>();
    String header;

    public BooksListFragment(HashMap<Integer, Book> books, String header) {
        this.books = books;
        this.header = header;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BooksListFragment.
     */
    public static BooksListFragment newInstance() {
        BooksListFragment fragment = new BooksListFragment();
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
        return inflater.inflate(R.layout.fragment_books_list, container, false);
    }

    ListView listV_books;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listV_books = view.findViewById(R.id.listV_books);

        // set the adapter for the list view
        Book[] booksArr = books.values().toArray(new Book[0]);
        BookListAdapter bookListAdapter = new BookListAdapter(getActivity(), getParentFragmentManager(), booksArr);
        listV_books.setAdapter(bookListAdapter);

        // navigate when the user clicks on book
        listV_books.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                getParentFragmentManager().beginTransaction().replace(R.id.frame_container, new BookDetailsFragment(books.get(i))).addToBackStack(null).commit();
            }
        });
    }

}