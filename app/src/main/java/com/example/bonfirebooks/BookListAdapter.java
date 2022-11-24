package com.example.bonfirebooks;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;

import java.text.DecimalFormat;
import java.util.HashMap;

public class BookListAdapter extends ArrayAdapter<Book> {

    Activity context;
    FragmentManager fragmentManager;
    Book[] books;

    public BookListAdapter(Activity context, FragmentManager fragmentManager, Book[] books) {
        super(context, R.layout.book_list_item, books);
        this.context = context;
        this.books = books;
        this.fragmentManager = fragmentManager;
    }

    public View getView(int position, View view, ViewGroup viewgroup) {
        LayoutInflater inflater = context.getLayoutInflater();
        View bookListItem = inflater.inflate(R.layout.book_list_item, null, true);

        ImageView imgV_book_cover = bookListItem.findViewById(R.id.imgV_book_cover);
        TextView txtV_book_title = bookListItem.findViewById(R.id.txtV_book_title);
        TextView txtV_book_price = bookListItem.findViewById(R.id.txtV_book_price);

        Glide.with(getContext()).load(books[position].getCoverImgUrl()).into(imgV_book_cover);
        txtV_book_title.setText(books[position].getTitle());

        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        txtV_book_price.setText("$ " + decimalFormat.format(books[position].getPrice()));

        return bookListItem;
    }

}
