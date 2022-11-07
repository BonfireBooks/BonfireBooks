package com.example.bonfirebooks;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookOfferDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookOfferDetailsFragment extends Fragment {

    Book book;
    UserBook userBook;

    public BookOfferDetailsFragment() {
        // Required empty public constructor
    }

    public BookOfferDetailsFragment(Book book, UserBook userBook) {
        this.book = book;
        this.userBook = userBook;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BookOfferDetailsFragment.
     */
    public static BookOfferDetailsFragment newInstance() {
        BookOfferDetailsFragment fragment = new BookOfferDetailsFragment();
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
        return inflater.inflate(R.layout.fragment_book_offer_details, container, false);
    }

    ImageSwitcher imgS_book_images;

    TextView txtV_book_title;
    TextView txtV_book_price;
    TextView txtV_seller_edit;
    TextView txtV_condition_edit;

    Button btn_back;
    Button btn_email_seller;
    Button btn_message_seller;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imgS_book_images = view.findViewById(R.id.imgS_book_images);
        txtV_book_title = view.findViewById(R.id.txtV_book_title);
        txtV_book_price = view.findViewById(R.id.txtV_book_price);
        txtV_seller_edit = view.findViewById(R.id.txtV_seller_edit);
        txtV_condition_edit = view.findViewById(R.id.txtV_condition_edit);
        btn_back = view.findViewById(R.id.btn_back);
        btn_email_seller = view.findViewById(R.id.btn_email_seller);
        btn_message_seller = view.findViewById(R.id.btn_message_seller);

        txtV_book_title.setText(book.getTitle());
        txtV_book_price.setText("$ " + userBook.getPrice());
        txtV_seller_edit.setText(userBook.getUserName());
        txtV_condition_edit.setText(userBook.getCondition());
    }
}