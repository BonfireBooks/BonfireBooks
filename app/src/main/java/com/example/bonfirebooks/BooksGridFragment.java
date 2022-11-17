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
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BooksGridFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BooksGridFragment extends Fragment {

    public BooksGridFragment() {
        // Required empty public constructor
    }

    HashMap<Integer, Book> books = new HashMap<>();
    String header;

    public BooksGridFragment(HashMap<Integer, Book> books, String header) {
        this.books = books;
        this.header = header;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BooksGridFragment.
     */
    public static BooksGridFragment newInstance() {
        BooksGridFragment fragment = new BooksGridFragment();
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
        return inflater.inflate(R.layout.fragment_books_grid, container, false);
    }

    GridLayout gridL_books;
    TextView txtV_all_books;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        gridL_books = view.findViewById(R.id.gridL_books);
        txtV_all_books = view.findViewById(R.id.txtV_all_books);

        txtV_all_books.setText(header);

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
                    getParentFragmentManager().beginTransaction().replace(R.id.frame_container, new BookDetailsFragment(currBook)).addToBackStack(null).commit();
                }
            });

            // add the book to the layout
            gridL_books.addView(bookView);
        }
    }
}