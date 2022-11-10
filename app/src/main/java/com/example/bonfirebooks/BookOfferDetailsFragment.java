package com.example.bonfirebooks;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;

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

    HorizontalScrollView horizScrollV_images;
    LinearLayout linlayout_image_scroll;

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

        horizScrollV_images = view.findViewById(R.id.horizScrollV_images);
        linlayout_image_scroll = view.findViewById(R.id.linlayout_image_scroll);
        txtV_book_title = view.findViewById(R.id.txtV_book_title);
        txtV_book_price = view.findViewById(R.id.txtV_book_price);
        txtV_seller_edit = view.findViewById(R.id.txtV_seller_edit);
        txtV_condition_edit = view.findViewById(R.id.txtV_condition_edit);
        btn_back = view.findViewById(R.id.btn_back);
        btn_email_seller = view.findViewById(R.id.btn_email_seller);
        btn_message_seller = view.findViewById(R.id.btn_message_seller);


        if (userBook.getPathsToImages() == null) {
            addImageToScroll(book.getCoverImgUrl(), 0);
        } else {
            HashMap<String, String> paths = userBook.getPathsToImages();
            for (int i = 0; i < paths.size(); i++) {
                addImageToScroll(paths.get(String.valueOf(i)), i);
            }
        }

        txtV_book_title.setText(book.getTitle());
        txtV_book_price.setText("$ " + userBook.getPrice());
        txtV_seller_edit.setText(userBook.getUserName());
        txtV_condition_edit.setText(userBook.getCondition());

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).onBackPressed();
            }
        });

        btn_email_seller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] { userBook.getEmail() });
                intent.putExtra(Intent.EXTRA_SUBJECT, "Regarding Your " + book.getTitle() + " Posting On Bonfire");

                Intent.createChooser(intent, "Choose an e-mail app");
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                }

            }
        });
    }

    private void addImageToScroll(String imgLink, int i) {
        View image = getLayoutInflater().inflate(R.layout.book_image_view, null);
        ImageView book_image = image.findViewById(R.id.imgV_image);

        Picasso.get().load(imgLink).into(book_image, new Callback() {
            @Override
            public void onSuccess() {
                book_image.setBackground(null);
            }

            @Override
            public void onError(Exception e) {
                // do nothing -- keep image not found background
            }
        });

        image.setPadding(0, 0, 20, 0);
        linlayout_image_scroll.addView(image, i);

    }

}