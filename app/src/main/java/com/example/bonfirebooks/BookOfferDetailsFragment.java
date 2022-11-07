package com.example.bonfirebooks;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookOfferDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookOfferDetailsFragment extends Fragment {

    UserBook userBook;

    public BookOfferDetailsFragment() {
        // Required empty public constructor
    }

    public BookOfferDetailsFragment(UserBook userBook) {
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}