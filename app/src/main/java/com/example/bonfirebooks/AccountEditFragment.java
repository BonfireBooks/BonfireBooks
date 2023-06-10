package com.example.bonfirebooks;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountEditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountEditFragment extends Fragment {

    public AccountEditFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AccountEditFragment.
     */
    public static AccountEditFragment newInstance(String param1, String param2) {
        AccountEditFragment fragment = new AccountEditFragment();
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
        return inflater.inflate(R.layout.fragment_account_edit, container, false);
    }

    FirebaseFirestore firestore;
    FirebaseStorage storage;
    FirebaseUser firebaseUser;

    User user;

    Button btn_profile_picture;
    Button btn_save;
    Button btn_password;

    TextView txtV_user_name;
    TextView txtE_user_name;
    TextView txtE_phone_number;

    ImageView img_profile;

    Uri userProfileImage;
    String phoneNumber;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        user = ((MainActivity)getActivity()).getUser();

        btn_profile_picture = view.findViewById(R.id.btn_profile_picture);

        img_profile = view.findViewById(R.id.img_profile);
        btn_save = view.findViewById(R.id.btn_save);
        btn_profile_picture = view.findViewById(R.id.btn_profile_picture);
        btn_password = view.findViewById(R.id.btn_password);
        txtV_user_name = view.findViewById(R.id.txtV_user_name);
        txtE_user_name = view.findViewById(R.id.txtE_user_name);
        txtE_phone_number = view.findViewById(R.id.txtE_phone_number);

        if (user.getProfileUri() != null) {
            Glide.with(getContext()).load(Uri.parse(user.getProfileUri())).error(R.drawable.stock_user).into(img_profile);
        }

        if(user.getPhoneNumber() != null) {
            txtE_phone_number.setHint(PhoneNumberUtils.formatNumber(user.getPhoneNumber(), Locale.getDefault().getCountry()));
        }

        txtE_phone_number.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        txtV_user_name.setText(user.getName());

        // set edit text hints
        txtE_user_name.setHint(user.getName());
        // txtE_phone_number.setHint(user.getPhoneNumber());

        btn_profile_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImageChooser();
            }
        });

        btn_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(user.getEmail()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Log.d("sendResetEmail", "Successful");
                            Toast.makeText(getContext(), "Reset Password Email Sent", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("sendResetEmail", "Successful");
                            Toast.makeText(getContext(), "Could Not Send Reset Password Email", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String phoneNumClean = txtE_phone_number.getText().toString();
                phoneNumClean = phoneNumClean.replace("(", "");
                phoneNumClean = phoneNumClean.replace(")", "");
                phoneNumClean = phoneNumClean.replace("-", "");
                phoneNumClean = phoneNumClean.replace(" ", "");
                phoneNumber = phoneNumClean;

                if (!TextUtils.isEmpty(phoneNumber) && phoneNumber.length() != 10) {
                    txtE_phone_number.setError("Please enter a valid 10 digit phone number");
                } else {

                    if (userProfileImage != null) {
                        user.setProfileUri(userProfileImage.toString());
                        Log.d("userProfileImage", userProfileImage.toString());

                        StorageReference storageReference = storage.getReference().child("User Images").child(user.getUid());
                        storageReference.putFile(userProfileImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    Log.d("uploadUserProfile", "Successful");
                                    firebaseUser.reload();
                                } else {
                                    Log.d("uploadUserProfile", "Failed");
                                }
                            }
                        });
                    }

                    if (!TextUtils.isEmpty(txtE_user_name.getText()) || !TextUtils.isEmpty(txtE_phone_number.getText())) {

                        HashMap<String, Object> changes = new HashMap<>();

                        if (!TextUtils.isEmpty(txtE_user_name.getText())) {
                            user.setName(txtE_user_name.getText().toString());
                            changes.put("name", txtE_user_name.getText().toString());
                        }

                        if (!phoneNumber.isEmpty()) {
                            user.setPhoneNumber(phoneNumber);
                            changes.put("phoneNumber", phoneNumber);
                        }

                        // update username
                        firestore.collection("users").document(user.getUid()).update(changes).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("updateName", "Successful");
                                } else {
                                    Log.d("updateName", "Failed");
                                }
                                firebaseUser.reload();
                            }
                        });
                    }

                    getParentFragmentManager().popBackStack();
                    getParentFragmentManager().popBackStack();
                    getParentFragmentManager().beginTransaction().replace(R.id.frame_container, new AccountFragment()).addToBackStack(null).commit();
                }
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

                Glide.with(getContext()).load(userProfileImage).error(R.drawable.stock_user).into(img_profile);
            }
        }
    });
}