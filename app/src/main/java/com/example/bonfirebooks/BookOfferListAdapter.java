package com.example.bonfirebooks;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.text.DecimalFormat;
import java.util.HashMap;

public class BookOfferListAdapter extends ArrayAdapter<UserBook> {

    Activity context;
    User user;
    FragmentManager fragmentManager;
    Book book;
    UserBook[] userBook;

    public BookOfferListAdapter(Activity context, User user, Book book, UserBook[] userBook, FragmentManager fragmentManager) {
        super(context, R.layout.offer_book_list_item, userBook);
        this.context = context;
        this.user = user;
        this.book = book;
        this.userBook = userBook;
        this.fragmentManager = fragmentManager;
    }

    public View getView(int position, View view, ViewGroup viewgroup) {
        LayoutInflater inflater = context.getLayoutInflater();
        View bookListItem = inflater.inflate(R.layout.offer_book_list_item, null, true);

        // layout views
        ImageView imgV_book_cover = bookListItem.findViewById(R.id.imgV_book_cover);
        TextView txtV_book_condition = bookListItem.findViewById(R.id.txtV_book_condition);
        TextView txtV_book_price = bookListItem.findViewById(R.id.txtV_book_price);
        TextView txtV_book_seller = bookListItem.findViewById(R.id.txtV_book_seller);
        TextView txtV_book_seller_header = bookListItem.findViewById(R.id.txtV_book_seller_header);
        Button btn_message_seller = bookListItem.findViewById(R.id.btn_message_seller);
        Button btn_edit_book = bookListItem.findViewById(R.id.btn_edit_book);

        // set text views
        txtV_book_condition.setText(userBook[position].getCondition());
        txtV_book_seller.setText(userBook[position].getUserName());

        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        txtV_book_price.setText("$ " + decimalFormat.format(userBook[position].getPrice()));

        // set image view
        if (userBook[position].getPathsToImages().size() > 0) {
            // get the first book image
            String pathToImage = userBook[position].getPathsToImages().get(String.valueOf(0));
            FirebaseStorage.getInstance().getReference().child(pathToImage).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Glide.with(getContext()).load(task.getResult()).into(imgV_book_cover);
                    } else {
                        Glide.with(getContext()).load(book.getCoverImgUrl()).into(imgV_book_cover);
                    }
                }
            });
        } else {
            // set the view with the default cover image
            Glide.with(getContext()).load(book.getCoverImgUrl()).into(imgV_book_cover);
        }

        if(userBook[position].getOwner().equals(user.getUid())) {
            btn_message_seller.setVisibility(View.GONE);
            txtV_book_seller.setVisibility(View.GONE);
            txtV_book_seller_header.setVisibility(View.GONE);
            btn_edit_book.setVisibility(View.VISIBLE);
            btn_edit_book.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {;
                    UserProfileBook userProfileBook = new UserProfileBook(user.getName(), userBook[position].getBookId(), book.getTitle(), book.getCoverImgUrl(), userBook[position].getCondition(), user.getUid(), book.getBookId(), userBook[position].getPrice(), book.getPrice(), true, userBook[position].getPathsToImages());
                    fragmentManager.beginTransaction().replace(R.id.frame_container, new UserBookDetailsEditFragment(userProfileBook)).addToBackStack(null).commit();
                }
            });
       }

        // set a list item listener here -- switch to details fragment
        bookListItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentManager.beginTransaction().replace(R.id.frame_container, new BookOfferDetailsFragment(book, userBook[position])).addToBackStack(null).commit();
            }
        });

        btn_message_seller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get the chat data in a map
                HashMap<String, String> uMap = new HashMap<>();
                uMap.put(user.getUid(), user.getName());
                uMap.put(userBook[position].getOwner(), userBook[position].getName());

                HashMap<String, Object> data = new HashMap<>();
                data.put("users", uMap);
                data.put("time", Timestamp.now());

                FirebaseFirestore firestore = FirebaseFirestore.getInstance();

                // check if a chat already exists
                firestore.collection("chats").whereEqualTo("users", uMap).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d("checkChatExists", "Successful");

                            if (task.getResult().size() > 0) {
                                // switch to the AllChatsFragment if the chat exists
                                Toast.makeText(getContext(), "Chat Already Exists", Toast.LENGTH_SHORT).show();
                                fragmentManager.beginTransaction().replace(R.id.frame_container, new AllChatsFragment()).addToBackStack(null).commit();
                            } else {
                                // create a new doc with the chat data
                                DocumentReference docRef = firestore.collection("chats").document();
                                docRef.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("createChat", "Successful");

                                            // create placeholder chat
                                            UserProfileChat userProfileChat = new UserProfileChat(docRef.getId(), userBook[position].getOwner(), userBook[position].getUserName(), "", Timestamp.now().toDate());

                                            // add the chat to the chats collection
                                            HashMap<String, UserProfileChat> chats = user.getChats();
                                            chats.put(String.valueOf(chats.size()), userProfileChat);
                                            user.setChats(chats);

                                            // switch into the chat
                                            fragmentManager.beginTransaction().replace(R.id.frame_container, new ChatFragment(userProfileChat)).addToBackStack(null).commit();

                                        } else {
                                            Log.d("createChat", "Failed");
                                        }
                                    }
                                });
                            }
                        } else {
                            Log.d("checkChatExists", "Failed");
                            Toast.makeText(getContext(), "Action Could Not Be Preformed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


        return bookListItem;
    }

}