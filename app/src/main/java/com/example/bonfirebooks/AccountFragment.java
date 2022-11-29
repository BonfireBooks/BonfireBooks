package com.example.bonfirebooks;

import android.app.Activity;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment {

    public AccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountFragment newInstance() {
        AccountFragment fragment = new AccountFragment();
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
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    FirebaseFirestore firestore;
    FirebaseStorage storage;
    FirebaseUser firebaseUser;

    User user;

    ImageView img_profile;

    View drawer_account;

    TextView logout;
    TextView txtV_upload_book;
    TextView txtV_view_books;
    TextView txtV_edit_profile;
//    TextView txtV_order_history;
//    TextView txtV_notification;
    TextView txtV_user_name;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        user = ((MainActivity) getActivity()).getUser();

        img_profile = view.findViewById(R.id.img_profile);
        drawer_account = view.findViewById(R.id.drawer_account);
        logout = view.findViewById(R.id.txtV_logout);
        txtV_user_name = view.findViewById(R.id.txtV_user_name);
        txtV_edit_profile = view.findViewById(R.id.txtV_edit_profile);
        txtV_view_books = view.findViewById(R.id.txtV_view_books);
        txtV_upload_book = view.findViewById(R.id.txtV_upload_book);
//        txtV_order_history = view.findViewById(R.id.txtV_order_history);
//        txtV_notification = view.findViewById(R.id.txtV_notification);

        if (user.getProfileUri() != null) {
            Glide.with(getContext()).load(Uri.parse(user.getProfileUri())).error(R.drawable.stock_user).into(img_profile);
        }

        txtV_user_name.setText(user.getName());

        setViewListeners();
    }

    private void setViewListeners() {

        // user sign out
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), StartActivity.class));
                getActivity().finish();
            }
        });

        txtV_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction().replace(R.id.frame_container, new AccountEditFragment()).addToBackStack(null).commit();
            }
        });

        txtV_view_books.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction().replace(R.id.frame_container, new UserBooksFragment()).addToBackStack(null).commit();
            }
        });

        txtV_upload_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction().replace(R.id.frame_container, new UploadBookSearchFragment()).addToBackStack(null).commit();
            }
        });

//        txtV_order_history.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // move fragments here
//            }
//        });
//
//        txtV_notification.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // move fragments here
//            }
//        });
    }

}