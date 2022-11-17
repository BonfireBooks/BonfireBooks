package com.example.bonfirebooks;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserBookDetailsEditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserBookDetailsEditFragment extends Fragment {

    public UserBookDetailsEditFragment() {
        // Required empty public constructor
    }

    UserProfileBook userProfileBook;

    public UserBookDetailsEditFragment(UserProfileBook userProfileBook) {
        this.userProfileBook = userProfileBook;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UserBookDetailsEditFragment.
     */
    public static UserBookDetailsEditFragment newInstance() {
        UserBookDetailsEditFragment fragment = new UserBookDetailsEditFragment();
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
        return inflater.inflate(R.layout.fragment_user_book_details_edit, container, false);
    }

    User user;

    FirebaseFirestore firestore;

    Spinner spinner_condition;

    EditText txtE_price;

    Button btn_change_photos;
    Button btn_save_changes;
    Button btn_back;

    Switch switch_isPublic;

    LinearLayout linlayout_image_scroll;

    HashMap<String, String> imgPaths = new HashMap<>();

    ProgressDialog progressDialog;

    // images
    private ArrayList<Uri> imageUris = new ArrayList<>();
    private static final int PICK_IMAGE_MULTIPLE = 1;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        user = ((MainActivity)getActivity()).getUser();

        firestore = FirebaseFirestore.getInstance();

        linlayout_image_scroll = view.findViewById(R.id.linlayout_image_scroll);
        spinner_condition = view.findViewById(R.id.spinner_condition);
        txtE_price = view.findViewById(R.id.txtE_price);
        btn_change_photos = view.findViewById(R.id.btn_change_photos);
        btn_save_changes = view.findViewById(R.id.btn_save_changes);
        switch_isPublic = view.findViewById(R.id.switch_isPublic);
        btn_back = view.findViewById(R.id.btn_back);

        progressDialog = new ProgressDialog(getContext());

        // set options for the spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.book_conditions, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_condition.setAdapter(adapter);

        // set formatting and filter for price
        txtE_price.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 2)});

        if(userProfileBook.getIsPublic()) {
            switch_isPublic.setChecked(true);
        }

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).onBackPressed();
            }
        });

        btn_change_photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        btn_save_changes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // check user price is < the book price'
                if (isValidInput()) {

                    // start the dialog here and end when book has finished uploading
                    progressDialog.setMessage("Updating...");
                    progressDialog.show();

                    if(imageUris.size() > 0) {
                        deleteCurrentImages();
                    } else {
                        updateUserBook();
                    }
                }
            }
        });
    }

    private void updateUserBook() {
        HashMap<String, Object> updatedData = new HashMap<>();
        updatedData.put("condition", spinner_condition.getSelectedItem().toString().toLowerCase());
        updatedData.put("isPublic", switch_isPublic.isChecked());

        if(!TextUtils.isEmpty(txtE_price.getText())) {
            updatedData.put("price", Double.valueOf(txtE_price.getText().toString()));
        }

        if(imgPaths != null) {
            if(!imgPaths.isEmpty()) {
                updatedData.put("images", imgPaths);
            }
        }

        firestore.collection("books").document().collection("users").document(userProfileBook.getBookId()).set(updatedData, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Log.d("updateBook", "Successful");

                    firestore.collection("users").document(user.getUid()).collection("books").document(userProfileBook.getBookId()).set(updatedData, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                progressDialog.dismiss();
//                                Toast.makeText(getContext(), "Book Updated Successfully", Toast.LENGTH_SHORT).show();
                            } else {

                            }

                            ((MainActivity)getActivity()).onBackPressed();
                        }
                    });
                } else {
                    Log.d("updateBook", "Failed");
                    Toast.makeText(getContext(), "Book Updated Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void deleteCurrentImages() {
        // get a storage reference
        StorageReference folderRef = FirebaseStorage.getInstance().getReference().child("User Images").child(userProfileBook.getBookId());

        folderRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Log.d("removeImages", "Successful");
                } else {
                    Log.d("removeImages", "Failed");
                }

                addUserImagesFirebase();
            }
        });
    }


    private void addUserImagesFirebase() {

        // get a storage reference
        StorageReference folderRef = FirebaseStorage.getInstance().getReference().child("User Images").child(userProfileBook.getBookId());

        // store all images
        for (int i = 0; i < imageUris.size(); i++) {
            Uri imageUri = imageUris.get(i);

            // need string value of i to store the image paths in the hashmap
            String strI = String.valueOf(i);

            // create a reference of the image file
            StorageReference imgRef = folderRef.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            // store the image file
            imgRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        Log.d("imageUpload", "Succeess");
                        imgPaths.put(strI, imgRef.getPath());
                    } else {
                        Log.d("imageUpload", "Failed");
                    }

                    // if the number of images stored matches the number of uris that
                    // need to be stored we can put the user in firebase
                    if (imgPaths.size() == imageUris.size()) {
                        updateUserBook();
                    } else {
                        // todo -- create an else case
                    }
                }
            });
        }
    }

    private String getFileExtension(Uri imageUri) {
        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap mtp = MimeTypeMap.getSingleton();
        return mtp.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // set intent type to select images
        intent.setType("image/");

        // allow multiple image to be selected
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

        // start the intent
        startForResult.launch(intent);
    }

    ActivityResultLauncher<Intent> startForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result != null && result.getResultCode() == Activity.RESULT_OK) {

                Log.d("clipLength", String.valueOf(result.getData().getClipData().getItemCount()));

                // clip data holds uris
                ClipData clipData = result.getData().getClipData();

                // reset the list and the linear layout
                for (int i = 0; i < imageUris.size(); i++) {
                    imageUris.remove(i);
                    linlayout_image_scroll.removeAllViews();
                }

                // get uri data
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    imageUris.add(clipData.getItemAt(i).getUri());

                    ImageView imageView = new ImageView(getContext());
                    imageView.setImageURI(clipData.getItemAt(i).getUri());

                    // create the linear layout to hold an image
                    LinearLayout linWrapper = new LinearLayout(getContext());
                    linWrapper.addView(imageView);
                    linWrapper.setPadding(0, 0, 20, 0);

                    // add it to the parent layout
                    linlayout_image_scroll.addView(linWrapper, i);

                    Log.d("uri", imageUris.get(i).toString());
                }
            }
        }
    });

    private boolean isValidInput() {
        return true;
    }
}