package com.example.bonfirebooks;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UploadBookSearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UploadBookSearchFragment extends Fragment {

    public UploadBookSearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UploadBookSearchFragment.
     */
    public static UploadBookSearchFragment newInstance() {
        UploadBookSearchFragment fragment = new UploadBookSearchFragment();
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
        return inflater.inflate(R.layout.fragment_upload_book_search, container, false);
    }

    User user;

    // firebase
    FirebaseFirestore firestore;
    FirebaseStorage storage;
    FirebaseFunctions firebaseFunctions;

    // layout items
    EditText txtE_ISBN;
    TextView txtV_book_title;
    TextView txtV_book_description;
    TextView txtV_book_authors;
    ImageView imgV_coverImage;
    Button btn_search_isbn;
    Button btn_finish_upload;

    // book details
    Book book = new Book();
    String isbn;
    String firebaseBookId = "";

    ProgressDialog progressDialog;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        user = ((MainActivity) getActivity()).getUser();

        txtE_ISBN = view.findViewById(R.id.txtE_isbn);
        txtV_book_title = view.findViewById(R.id.txtV_book_title);
        txtV_book_description = view.findViewById(R.id.txtV_book_description);
        txtV_book_authors = view.findViewById(R.id.txtV_book_authors);
        btn_search_isbn = view.findViewById(R.id.btn_search_isbn);
        btn_finish_upload = view.findViewById(R.id.btn_finish_upload);
        imgV_coverImage = view.findViewById(R.id.imgV_coverImage);

        progressDialog = new ProgressDialog(getContext());

        // get firebase instances
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        firebaseFunctions = FirebaseFunctions.getInstance();

        // insert the book into the firebase collection
        btn_search_isbn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // reset any errors & text views holding details
                txtE_ISBN.setError(null);
                txtV_book_authors.setText(null);
                txtV_book_description.setText(null);
                txtV_book_title.setText(null);
                imgV_coverImage.setVisibility(View.GONE);

                btn_finish_upload.setVisibility(View.GONE);

                progressDialog.setMessage("Searching...");
                progressDialog.show();

                isbn = txtE_ISBN.getText().toString();

                // begin the search process
                if (isbn.length() == 13 || isbn.length() == 10) {
                    handleSearch();
                } else {
                    progressDialog.dismiss();
                    txtE_ISBN.setError("You Must Enter The ISBN 10 or 13 For Your Book");
                }
            }
        });
    }

    private void handleSearch() {
        String strISBN = isbn.length() == 13 ? "isbn13" : "isbn10";

        // check if the book exists in firebase
        firestore.collection("books").whereEqualTo(strISBN, isbn).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                List<com.google.firebase.firestore.DocumentSnapshot> documents = task.getResult().getDocuments();

                Log.d("task", task.getResult().getDocuments().toString());
                if (task.getResult().getDocuments().isEmpty()) {
                    Log.d("firestoreHasBook", "false");

                    HashMap<String, String> data = new HashMap<>();
                    data.put("isbn", isbn);

                    callAddBookUsingApi(data);

                } else {
                    Log.d("firestoreHasBook", "true");
                    DocumentSnapshot taskResult = documents.get(0);
                    firebaseBookId = taskResult.getId();

                    book = new Book(taskResult.getDouble("price"), taskResult.getId(), taskResult.getString("title"), taskResult.getString("isbn10"), taskResult.getString("isbn13"), taskResult.getString("description"), taskResult.getString("coverImgUrl"), (HashMap<String, String>) taskResult.get("authors"), (HashMap<String, String>) taskResult.get("categories"), taskResult.getTimestamp("time"));
                    updateUIElements();
                }
            }
        });
    }

    private void callAddBookUsingApi(HashMap<String, String> data) {
        firebaseFunctions.getHttpsCallable("addBookUsingApi").call(data).continueWith(new Continuation<HttpsCallableResult, HashMap<String, Object>>() {
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
                        Toast.makeText(getContext(), taskResult.get("message").toString(), Toast.LENGTH_SHORT).show();
                        Log.d("addBookUsingApi", "Failed " + taskResult.get("message"));
                    } else {
                        Log.d("addBookUsingApi", "Success " + taskResult.get("message"));

                        // add data to book object here
                        book.setTitle((String) taskResult.get("title"));
                        book.setPrice(Double.valueOf(String.valueOf(taskResult.get("price"))));
                        book.setDescription((String) taskResult.get("description"));
                        book.setcoverImgUrl((String) taskResult.get("coverImgUrl"));
                        book.setIsbn10((String) taskResult.get("isbn10"));
                        book.setIsbn13((String) taskResult.get("isbn13"));
                        book.setCategories((HashMap<String, String>) taskResult.get("categories"));
                        book.setAuthors((HashMap<String, String>) taskResult.get("authors"));

                        updateUIElements();
                    }
                } else {
                    progressDialog.dismiss();
                    Exception e = task.getException();
                    if (e instanceof FirebaseFunctionsException) {
                        FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                        FirebaseFunctionsException.Code code = ffe.getCode();
                        Object details = ffe.getDetails();
                    }
                    Toast.makeText(getContext(), "Server Error! Could Not Find Book.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateUIElements() {
        progressDialog.dismiss();

        // create a string of authors
        StringBuilder strAuthors = new StringBuilder();
        HashMap<String, String> authors = book.getAuthors();
        if (book.getAuthors() != null) {
            for (int i = 0; i < authors.size(); i++) {
                strAuthors.append(authors.get(String.valueOf(i)));
                if (i != authors.size() - 1)
                    strAuthors.append("\n");
            }
        }

        // update TextViews
        txtV_book_authors.setText(strAuthors.toString());
        txtV_book_title.setText(book.getTitle());
        txtV_book_description.setText(book.getDescription());

        // update the ImageView
        imgV_coverImage.setVisibility(View.VISIBLE);
        Picasso.get().load(book.getCoverImgUrl()).into(imgV_coverImage);
        imgV_coverImage.setMinimumHeight(500);

        // make the finish upload button visible and clickable
        btn_finish_upload.setVisibility(View.VISIBLE);
        btn_finish_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // switch to the uploadBookFragment
                getParentFragmentManager().beginTransaction().replace(R.id.frame_container, new UploadBookFragment(book, firebaseBookId)).addToBackStack(null).commit();
            }
        });
    }
}