package com.example.bonfirebooks;

import android.app.Activity;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

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
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

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
    View drawer_account_edit;

    Button logout;
    Button btn_save;
    Button btn_profile_picture;

    TextView txtV_upload_book;
    TextView txtV_view_books;
    TextView txtV_edit_profile;
    TextView txtV_order_history;
    TextView txtV_notification;
    TextView txtV_user_name;
    TextView txtE_user_name;
    TextView txtE_phone_number;
    TextView txtE_password;

    Uri userProfileImage;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        user = ((MainActivity) getActivity()).getUser();

        img_profile = view.findViewById(R.id.img_profile);
        drawer_account = view.findViewById(R.id.drawer_account);
        drawer_account_edit = view.findViewById(R.id.drawer_account_edit);
        logout = view.findViewById(R.id.btn_logout);
        btn_save = view.findViewById(R.id.btn_save);
        btn_profile_picture = view.findViewById(R.id.btn_profile_picture);
        txtV_user_name = view.findViewById(R.id.txtV_user_name);
        txtV_edit_profile = view.findViewById(R.id.txtV_edit_profile);
        txtV_view_books = view.findViewById(R.id.txtV_view_books);
        txtV_upload_book = view.findViewById(R.id.txtV_upload_book);
        txtV_order_history = view.findViewById(R.id.txtV_order_history);
        txtV_notification = view.findViewById(R.id.txtV_notification);
        txtE_user_name = view.findViewById(R.id.txtE_user_name);
        txtE_phone_number = view.findViewById(R.id.txtE_phone_number);
        txtE_password = view.findViewById(R.id.txtE_password);


        if (user.getProfileUri() != null) {
            Picasso.get().load(Uri.parse(user.getProfileUri())).into(img_profile, new Callback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError(Exception e) {
                    img_profile.setBackgroundResource(R.drawable.stock_user);
                }
            });
        }


        // set edit text hints
        txtE_user_name.setHint(user.getName());
        // txtE_phone_number.setHint(user.getPhoneNumber());

        txtV_user_name.setText(user.getName());

        setViewListeners();
    }

    private void setViewListeners() {

        // drawer account views

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
                drawer_account.setVisibility(View.GONE);
                drawer_account_edit.setVisibility(View.VISIBLE);
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

        txtV_order_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // move fragments here
            }
        });

        txtV_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // move fragments here
            }
        });

        // drawer account edit views

        btn_profile_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImageChooser();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (userProfileImage != null) {
                    Log.d("userProfileImage", userProfileImage.toString());

                    StorageReference storageReference = storage.getReference().child("User Images").child(user.getUid());
                    storageReference.putFile(userProfileImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                Log.d("uploadUserProfile", "Successful");
                                user.setProfileUri(userProfileImage.toString());
                                Picasso.get().load(userProfileImage).into(img_profile, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        // do nothing -- keep image not found background
                                    }
                                });
                                firebaseUser.reload();
                            } else {
                                Log.d("uploadUserProfile", "Failed");
                            }
                        }
                    });
                }

                if (!TextUtils.isEmpty(txtE_user_name.getText())) {
                    HashMap<String, Object> changes = new HashMap<>();
                    changes.put("name", txtE_user_name.getText().toString());

                    // update username
                    firestore.collection("users").document(user.getUid()).update(changes).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("updateName", "Successful");
                                user.setName(txtE_user_name.getText().toString());
                                txtV_user_name.setText(user.getName());
                                txtE_user_name.setText(null);
                            } else {
                                Log.d("updateName", "Failed");
                            }
                            firebaseUser.reload();
                        }
                    });
                }

                // documentation for setting phone number is poor
//                String phoneNumber = txtE_phone_number.getText().toString();
//                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setPhotoUri()
//
//                firebaseUser.updatePhoneNumber(newPhoneNum).addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            Log.d("updatePhoneNumber", "Successful");
//                txtE_phone_number.setText(null);
//                        } else {
//                            Log.d("updatePhoneNumber", "Successful");
//                        }
//                    }
//                });

                if (!TextUtils.isEmpty(txtE_password.getText())) {
                    String password = txtE_password.getText().toString();
                    firebaseUser.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("updatePassword", "Successful");
                                txtE_password.setText(null);
                            } else {
                                Log.d("updatePassword", "Successful");
                            }
                            firebaseUser.reload();
                        }
                    });
                }

                drawer_account.setVisibility(View.VISIBLE);
                drawer_account_edit.setVisibility(View.GONE);

            }
        });

    }

    private void openImageChooser() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // todo -- fix camera intent not returning uri
//        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        // set intent type to select images
        galleryIntent.setType("image/");

        // allow multiple image to be selected
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);

        // create the chooser
        Intent chooser = new Intent(Intent.ACTION_CHOOSER);
        chooser.putExtra(Intent.EXTRA_INTENT, galleryIntent);
        chooser.putExtra(Intent.EXTRA_TITLE, "Select From:");

//        Intent[] intentArray = {cameraIntent};
//        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

        // start the intent
        startForResult.launch(chooser);
    }

    ActivityResultLauncher<Intent> startForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result != null && result.getResultCode() == Activity.RESULT_OK) {
                userProfileImage = result.getData().getData();
                Picasso.get().load(userProfileImage).into(img_profile);
            }
        }
    });
}