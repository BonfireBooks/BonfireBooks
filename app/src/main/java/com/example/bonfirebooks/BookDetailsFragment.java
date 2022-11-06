package com.example.bonfirebooks;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookDetailsFragment extends Fragment {

    public BookDetailsFragment() {
        // Required empty public constructor
    }

    Book book;

    public BookDetailsFragment(Book book) {
        this.book = book;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BookDetailsFragment.
     */
    public static BookDetailsFragment newInstance() {
        BookDetailsFragment fragment = new BookDetailsFragment();
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
        return inflater.inflate(R.layout.fragment_book_details, container, false);
    }

    User user;

    TextView txtV_book_title;
    TextView txtV_book_price;
    TextView txtV_author_edit;
    TextView txtV_isbn10_edit;
    TextView txtV_isbn13_edit;
    TextView txtV_book_description_edit;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        user = ((MainActivity)getActivity()).getUser();

        txtV_book_title = view.findViewById(R.id.txtV_book_title);
        txtV_book_price = view.findViewById(R.id.txtV_book_price);
        txtV_author_edit = view.findViewById(R.id.txtV_author_edit);
        txtV_isbn10_edit = view.findViewById(R.id.txtV_isbn10_edit);
        txtV_isbn13_edit = view.findViewById(R.id.txtV_isbn13_edit);
        txtV_book_description_edit = view.findViewById(R.id.txtV_book_description_edit);

        txtV_book_title.setText(book.getTitle());
        txtV_book_price.setText("$ " + String.valueOf(book.getPrice()));
        txtV_author_edit.setText(book.getAuthors().toString());
        txtV_isbn10_edit.setText(book.getIsbn10());
        txtV_isbn13_edit.setText(book.getIsbn13());
        txtV_book_description_edit.setText(book.getDescription());


    }
}