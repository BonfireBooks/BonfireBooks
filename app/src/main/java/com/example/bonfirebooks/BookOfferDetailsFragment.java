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
import android.view.Gravity;
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
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
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

        // if the book being loaded is one of the users books switch to another view
        for (UserProfileBook uBook : ((MainActivity) getActivity()).getUser().getBooks().values()) {
            if (uBook.getBookId().equals(userBook.getBookId())) {
                // get this fragment off the backstack
                getParentFragmentManager().popBackStack();
                getParentFragmentManager().beginTransaction().replace(R.id.frame_container, new UserBookDetailsFragment(uBook)).addToBackStack(null).commit();
                return null;
            }
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_book_offer_details, container, false);
    }

    User user;

    FirebaseFirestore firestore;
    FirebaseStorage firebaseStorage;

    HashMap<String, WishlistBook> userWishlsit;

    ImageView imgV_coverImage;

    HorizontalScrollView horizScrollV_images;
    LinearLayout linlayout_image_scroll;

    TextView txtV_book_title;
    TextView txtV_book_price;
    TextView txtV_seller_edit;
    TextView txtV_condition_edit;

    Button btn_wishlist;
    Button btn_email_seller;
    Button btn_message_seller;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        user = ((MainActivity) getActivity()).getUser();

        firestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        userWishlsit = user.getWishlist();

        imgV_coverImage = view.findViewById(R.id.imgV_coverImage);
        horizScrollV_images = view.findViewById(R.id.horizScrollV_images);
        linlayout_image_scroll = view.findViewById(R.id.linlayout_image_scroll);
        txtV_book_title = view.findViewById(R.id.txtV_book_title);
        txtV_book_price = view.findViewById(R.id.txtV_book_price);
        txtV_seller_edit = view.findViewById(R.id.txtV_seller_edit);
        txtV_condition_edit = view.findViewById(R.id.txtV_condition_edit);
        btn_email_seller = view.findViewById(R.id.btn_email_seller);
        btn_message_seller = view.findViewById(R.id.btn_message_seller);
        btn_wishlist = view.findViewById(R.id.btn_wishlist);

        Log.d("wishlistBook", userBook.toString());

        // set the images for the book
        if (userBook.getPathsToImages().isEmpty()) {
            imgV_coverImage.setVisibility(View.VISIBLE);
            horizScrollV_images.setVisibility(View.GONE);
            Glide.with(getContext()).load(book.getCoverImgUrl()).error("").into(imgV_coverImage);
        } else {
            HashMap<String, String> paths = userBook.getPathsToImages();

            for (int i = 0; i < paths.size(); i++) {
                int finalI = linlayout_image_scroll.getChildCount();
                firebaseStorage.getReference().child(paths.get(String.valueOf(i))).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Log.d("getImage", "Successful");
                            addImageToScroll(task.getResult(), finalI);
                        } else {
                            Log.d("getImage", "Failed");
                        }
                    }
                });
            }
        }

        // set the details of the book
        txtV_book_title.setText(book.getTitle());
        txtV_book_price.setText("$ " + userBook.getPrice());
        txtV_seller_edit.setText(userBook.getUserName());
        txtV_condition_edit.setText(userBook.getCondition());

        final boolean[] inWishlist = {false};

        // set the background of the wishlist button
        if (userWishlsit != null) {
            for (int i = 0; i < userWishlsit.size(); i++) {
                if (userBook.getBookId().equals(userWishlsit.get(String.valueOf(i)).getBookId())) {
                    btn_wishlist.setBackground(getResources().getDrawable(R.drawable.ic_heart_filled));
                    inWishlist[0] = true;
                    break;
                }
            }
        }

        // wishlist toggle button click
        btn_wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // the book is wishlisted
                if (inWishlist[0]) {
                    // unwishlist
                    firestore.collection("users").document(user.getUid()).collection("wishlist").document(userBook.getBookId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("removeFromWishlist", "Successful");
                                inWishlist[0] = false;
                                btn_wishlist.setBackground(getResources().getDrawable(R.drawable.ic_heart_empty));
                            } else {
                                Log.d("removeFromWishlist", "Failed");
                            }
                        }
                    });
                } else {
                    // add to wishlist
                    WishlistBook wishBook = new WishlistBook(userBook.getBookId(), book.getTitle(), userBook.getCondition().toLowerCase(), book.getCoverImgUrl(), userBook.getPrice());
                    firestore.collection("users").document(user.getUid()).collection("wishlist").document(userBook.getBookId()).set(wishBook).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("addToWishlist", "Successful");
                                inWishlist[0] = true;
                                btn_wishlist.setBackground(getResources().getDrawable(R.drawable.ic_heart_filled));
                            } else {
                                Log.d("addToWishlist", "Failed");
                            }
                        }
                    });
                }
            }
        });

        // email seller button click
        btn_email_seller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // create the intent
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{userBook.getEmail()});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Regarding Your " + book.getTitle() + " Posting On Bonfire");

                // set up a chooser for the available e-mail apps
                Intent.createChooser(intent, "Choose an e-mail app");
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                }

            }
        });

        btn_message_seller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get the chat data in a map
                HashMap<String, String> uMap = new HashMap<>();
                uMap.put(user.getUid(), user.getName());
                uMap.put(userBook.getOwner(), userBook.getName());

                HashMap<String, Object> data = new HashMap<>();
                data.put("users", uMap);
                data.put("time", Timestamp.now());

                firestore.collection("chats").whereEqualTo("users", uMap).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d("checkChatExists", "Successful");

                            if (task.getResult().size() > 0) {
                                // switch to the AllChatsFragment if the chat exists
                                Toast.makeText(getContext(), "Chat Already Exists", Toast.LENGTH_SHORT).show();
                                getParentFragmentManager().beginTransaction().replace(R.id.frame_container, new AllChatsFragment()).addToBackStack(null).commit();
                            } else {
                                // create a new doc with the chat data
                                DocumentReference docRef = firestore.collection("chats").document();
                                docRef.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("createChat", "Successful");

                                            // create placeholder chat
                                            UserProfileChat userProfileChat = new UserProfileChat(docRef.getId(), userBook.getOwner(), userBook.getUserName(), "", Timestamp.now().toDate());

                                            // add the chat to the chats collection
                                            HashMap<String, UserProfileChat> chats = user.getChats();
                                            chats.put(String.valueOf(chats.size()), userProfileChat);
                                            user.setChats(chats);

                                            // switch into the chat
                                            getParentFragmentManager().beginTransaction().replace(R.id.frame_container, new ChatFragment(userProfileChat)).addToBackStack(null).commit();

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
    }

    private void addImageToScroll(String imgLink, int i) {
        View image = getLayoutInflater().inflate(R.layout.book_image_view, null);
        ImageView book_image = image.findViewById(R.id.imgV_image);

        Glide.with(image.getContext()).load(imgLink).error("").into(book_image);

        linlayout_image_scroll.addView(image, i);

    }

    private void addImageToScroll(Uri imgUri, int i) {
        View image = getLayoutInflater().inflate(R.layout.book_image_view, null);
        ImageView book_image = image.findViewById(R.id.imgV_image);

        Glide.with(image.getContext()).load(imgUri).error("").into(book_image);

        linlayout_image_scroll.addView(image, i);

    }

}