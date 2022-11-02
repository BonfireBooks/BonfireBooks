package com.example.bonfirebooks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UploadBookFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UploadBookFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Book book;
    private String bookPath;

    public UploadBookFragment() {
        // Required empty public constructor
    }


    public UploadBookFragment(Book book, String path) {
        this.book = book;
        this.bookPath = path;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param book the book.
     * @return A new instance of fragment UploadBookFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UploadBookFragment newInstance(Book book, String path) {
        UploadBookFragment fragment = new UploadBookFragment(book, path);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            book = (Book) getArguments().get(ARG_PARAM1);
            bookPath = (String) getArguments().get(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upload_book, container, false);
    }

    User user;

    // firebase
    FirebaseFirestore firestore;
    FirebaseUser currUser;

    // layout items
    EditText txtE_price;
    Spinner spinner_condition;
    Button btn_publish_book;
    Button btn_add_photo;
    HorizontalScrollView horizontal_scroll_view_images;
    LinearLayout linlayout_image_scroll;

    // images
    private ArrayList<Uri> imageUris = new ArrayList<>();
    private static final int PICK_IMAGE_MULTIPLE = 1;

    ProgressDialog progressDialog;

    // firebase paths
    String userBookPath;
    String userBookID;
    String bookID;
    HashMap<String, String> imgPaths = new HashMap<>();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        user = ((MainActivity)getActivity()).getUser();

        horizontal_scroll_view_images = view.findViewById(R.id.horizontal_scroll_view_images);
        txtE_price = view.findViewById(R.id.txtE_price);
        spinner_condition = view.findViewById(R.id.spinner_condition);
        btn_publish_book = view.findViewById(R.id.btn_publish_book);
        btn_add_photo = view.findViewById(R.id.btn_add_photo);
        linlayout_image_scroll = view.findViewById(R.id.linlayout_image_scroll);

        progressDialog = new ProgressDialog(getContext());

        Log.d("book", book.toString());

        // get firebase instances
        firestore = FirebaseFirestore.getInstance();
        currUser = FirebaseAuth.getInstance().getCurrentUser();

        // user book path and id
        userBookPath = firestore.document(bookPath).collection("users").document().getPath();
        userBookID = firestore.document(userBookPath).getId();

        // set options for the spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.book_conditions, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_condition.setAdapter(adapter);

        // set formatting and filter for price
        txtE_price.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 2)});

        // set the price hint based on the books price
        txtE_price.setHint("Price <= " + book.getPrice());

        // insert the book into the firebase collection
        btn_publish_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // check user price is < the book price'
                if (isValidInput()) {

                    // start the dialog here and end when book has finished uploading
                    progressDialog.setMessage("Uploading");
                    progressDialog.show();

                    addUserImagesFirebase();
                }
            }
        });

        btn_add_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
    }

    private void addUserImagesFirebase() {

        if(imageUris.size() > 0) {
            // get a storage reference
            StorageReference folderRef = FirebaseStorage.getInstance().getReference().child("User Images").child(userBookID);

            // store all images
            for(int i = 0; i < imageUris.size(); i++) {
                Uri imageUri = imageUris.get(i);

                // need string value of i to store the image paths in the hashmap
                String strI = String.valueOf(i);

                // create a reference of the image file
                StorageReference imgRef = folderRef.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

                // store the image file
                imgRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()) {
                            Log.d("imageUpload", "Succeess");
                            imgPaths.put(strI, imgRef.getPath());
                        } else {
                            Log.d("imageUpload", "Failed");
                        }

                        // if the number of images stored matches the number of uris that
                        // need to be stored we can put the user in firebase
                        if(imgPaths.size() == imageUris.size()) {
                            addUserBookFirebase();
                        }
                    }
                });
            }
        } else {
            // if there arent any images to add we can just add the user
            addUserBookFirebase();
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
            if(result != null && result.getResultCode() == Activity.RESULT_OK) {

                Log.d("clipLength", String.valueOf(result.getData().getClipData().getItemCount()));

                // clip data holds uris
                ClipData clipData = result.getData().getClipData();

                // reset the list and the linear layout
                for(int i = 0; i < imageUris.size(); i++) {
                    imageUris.remove(i);
                    linlayout_image_scroll.removeAllViews();
                }

                // get uri data
                for(int i = 0; i < clipData.getItemCount(); i++) {
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

    private void addUserBookFirebase() {

        // get the user name then store the book
        firestore.collection("users").document(currUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                // values for the user book
                String name = task.getResult().getString("name");
                double price = Double.valueOf(txtE_price.getText().toString());

                UserBook userBook = new UserBook(price, user.getEmail(), name, spinner_condition.getSelectedItem().toString().toLowerCase(), imgPaths);
                userBook.setTime(Timestamp.now());
                Log.d("userBook", userBook.toString());

                // store the book in firebase
                firestore.document(userBookPath).set(userBook).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("addUserBookFirebase", "Success");
                            updateUserBooks();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Upload Failed. Please try again later.", Toast.LENGTH_SHORT).show();
                            Log.d("addUserBookFirebase", "Failed");
                            Log.d("reason", task.getException().toString());
                        }
                    }
                });
            }
        });
    }

    private void updateUserBooks() {

        // map with the path details
        HashMap<String, Object> userBookDetails = new HashMap<>();
        userBookDetails.put("path", userBookPath);
        userBookDetails.put("title", book.getTitle());
        userBookDetails.put("coverImgUrl", book.getCoverImgUrl());
        userBookDetails.put("price", book.getPrice());

        // add the path of the book to the users collection of books
        firestore.collection("users").document(currUser.getUid()).collection("books").add(userBookDetails).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    Log.d("updateUserBooks", "Success");
                    progressDialog.dismiss();
                    getParentFragmentManager().beginTransaction().replace(R.id.frame_container, new AccountFragment()).commit();
                } else {
                    Log.d("updateUserBooks", "Failed");
                }
            }
        });
    }

    private boolean isValidInput() {
        boolean isValid = true;

        // price does not exceed booksrun price if it exists
        if(txtE_price.getText().toString().isEmpty()) {
            txtE_price.setError("You must enter a price");
            isValid = false;
        } else if (Double.valueOf(txtE_price.getText().toString()) > book.getPrice() && book.getPrice() != 0.0) {
            txtE_price.setError("The price must be less than or equal to the price of the book: " + book.getPrice());
            isValid = false;
        }

        return isValid;
    }

}