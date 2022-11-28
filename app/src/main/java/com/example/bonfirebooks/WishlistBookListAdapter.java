package com.example.bonfirebooks;

import android.app.Activity;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;

import java.text.DecimalFormat;

public class WishlistBookListAdapter extends ArrayAdapter<WishlistBook> {

    Activity context;
    WishlistBook[] wishlistBooks;

    public WishlistBookListAdapter(Activity context, WishlistBook[] wishlistBooks) {
        super(context, R.layout.offer_book_list_item, wishlistBooks);
        this.context = context;
        this.wishlistBooks = wishlistBooks;
    }

    public View getView(int position, View view, ViewGroup viewgroup) {
        LayoutInflater inflater = context.getLayoutInflater();
        View bookListItem = inflater.inflate(R.layout.wishlist_book_list_item, null, true);

        ImageView imgV_book_cover = bookListItem.findViewById(R.id.imgV_book_cover);
        TextView txtV_book_title = bookListItem.findViewById(R.id.txtV_book_title);
        TextView txtV_book_condition = bookListItem.findViewById(R.id.txtV_book_condition);
        TextView txtV_book_price = bookListItem.findViewById(R.id.txtV_book_price);

        if(wishlistBooks[position].getImages().size() > 0) {
            FirebaseStorage.getInstance().getReference().child(wishlistBooks[position].getImages().get(String.valueOf(0))).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Glide.with(getContext()).load(task.getResult()).into(imgV_book_cover);
                    } else {
                        Glide.with(getContext()).load(wishlistBooks[position].getCoverImgUrl()).into(imgV_book_cover);
                    }
                }
            });
        } else {
            Glide.with(getContext()).load(wishlistBooks[position].getCoverImgUrl()).into(imgV_book_cover);
        }

        txtV_book_title.setText(wishlistBooks[position].getTitle());
        txtV_book_condition.setText(wishlistBooks[position].getCondition());

        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        txtV_book_price.setText("$ " + decimalFormat.format(wishlistBooks[position].getPrice()));

        return bookListItem;
    }
}
