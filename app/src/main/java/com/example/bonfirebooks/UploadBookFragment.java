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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UploadBookFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UploadBookFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";

    private Book book;

    public UploadBookFragment() {
        // Required empty public constructor
    }


    public UploadBookFragment(Book book) {
        this.book = book;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param book the book.
     * @return A new instance of fragment UploadBookFragment.
     */
    public static UploadBookFragment newInstance(Book book) {
        UploadBookFragment fragment = new UploadBookFragment(book);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            book = (Book) getArguments().get(ARG_PARAM1);
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
    FirebaseFunctions firebaseFunctions;

    // layout items
    TextView txtE_title_edit;
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
    HashMap<String, String> imgPaths = new HashMap<>();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        user = ((MainActivity) getActivity()).getUser();

        txtE_title_edit = view.findViewById(R.id.txtE_title_edit);
        txtE_price = view.findViewById(R.id.txtE_price);
        spinner_condition = view.findViewById(R.id.spinner_condition);
        btn_publish_book = view.findViewById(R.id.btn_publish_book);
        btn_add_photo = view.findViewById(R.id.btn_add_photo);
        horizontal_scroll_view_images = view.findViewById(R.id.horizontal_scroll_view_images);
        linlayout_image_scroll = view.findViewById(R.id.linlayout_image_scroll);

        progressDialog = new ProgressDialog(getContext());

        // get firebase instances
        firestore = FirebaseFirestore.getInstance();
        currUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFunctions = FirebaseFunctions.getInstance();

        // user book path and id
        userBookPath = firestore.collection("books").document(book.getBookId()).collection("users").document().getPath();
        userBookID = firestore.document(userBookPath).getId();

        // set options for the spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.book_conditions, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_condition.setAdapter(adapter);

        txtE_title_edit.setText(book.getTitle());

        // set formatting and filter for price
        txtE_price.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 2)});

        // set the price hint based on the books price
        if(book.getPrice() == 0) {
            txtE_price.setHint("Enter a Price");
        } else {
            txtE_price.setHint("Enter a Price (" + book.getPrice() + ") or less");
        }

        // insert the book into the firebase collection
        btn_publish_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // check user price is < the book price'
                if (isValidInput()) {

                    // start the dialog here and end when book has finished uploading
                    progressDialog.setMessage("Uploading");
                    progressDialog.show();

                    if(imageUris.size() > 0) {
                        addUserImagesFirebase();
                    } else {
                        callAddUserBook();
                    }
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

            // get a storage reference
            StorageReference folderRef = FirebaseStorage.getInstance().getReference().child("User Images").child(userBookID);

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
                            callAddUserBook();
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

    private void callAddUserBook() {
        firestore.collection("users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                // values for the user book
                String name = task.getResult().getString("name");

                // book data to used in the firebase function
                HashMap<String, Object> data = new HashMap<>();
                data.put("bookId", book.getBookId());
                data.put("condition", spinner_condition.getSelectedItem().toString().toLowerCase());
                data.put("name", name);
                data.put("price", Double.valueOf(txtE_price.getText().toString()));
                data.put("images", imgPaths);
                data.put("uBookId", userBookID);

                firebaseFunctions.getHttpsCallable("addUserBook").call(data).continueWith(new Continuation<HttpsCallableResult, HashMap<String, Object>>() {
                    @Override
                    public HashMap<String, Object> then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        return (HashMap<String, Object>) task.getResult().getData();
                    }
                }).addOnCompleteListener(new OnCompleteListener<HashMap<String, Object>>() {
                    @Override
                    public void onComplete(@NonNull Task<HashMap<String, Object>> task) {
                        if (task.isSuccessful()) {
                            HashMap<String, Object> taskResult = task.getResult();
                            // task failed to insert data into firebase
                            if ((Integer) taskResult.get("status") == 400) {
                                progressDialog.dismiss();
                                // notify the user that the operation failed
                                Log.d("addBookUsingApi", "Failed\nmessage:" + taskResult.get("message"));
                                Toast.makeText(getContext(), taskResult.get("message").toString(), Toast.LENGTH_SHORT).show();
                            } else {
                                progressDialog.dismiss();
                                // notify user the book was uploaded
                                Log.d("addBookUsingApi", "Success " + taskResult.get("message"));
                                Toast.makeText(getContext(), "Book Uploaded Successfully", Toast.LENGTH_SHORT).show();

                                // switch fragments
                                getParentFragmentManager().beginTransaction().replace(R.id.frame_container, new AccountFragment()).commit();
                            }
                        } else {
                            progressDialog.dismiss();
                            Exception e = task.getException();
                            if (e instanceof FirebaseFunctionsException) {
                                FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                FirebaseFunctionsException.Code code = ffe.getCode();
                                Object details = ffe.getDetails();

                                Log.e("code ", code.toString());

                                if(details != null) {
                                    Log.e("error ", details.toString());
                                }
                            }
                            Toast.makeText(getContext(), "Server Error! Could not upload book.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private boolean isValidInput() {
        boolean isValid = true;

        // price does not exceed booksrun price if it exists
        if (txtE_price.getText().toString().isEmpty()) {
            txtE_price.setError("You must enter a price");
            isValid = false;
        } else if (Double.valueOf(txtE_price.getText().toString()) > book.getPrice() && book.getPrice() != 0.0) {
            txtE_price.setError("The price must be less than or equal to the price of the book: " + book.getPrice());
            isValid = false;
        }

        return isValid;
    }

}