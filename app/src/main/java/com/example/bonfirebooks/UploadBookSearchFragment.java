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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
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
    String firebaseBookPath = "";

    ProgressDialog progressDialog;

    // api calls
    private RequestQueue mQueue;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        user = ((MainActivity)getActivity()).getUser();

        txtE_ISBN = view.findViewById(R.id.txtE_isbn);
        txtV_book_title = view.findViewById(R.id.txtV_book_title);
        txtV_book_description = view.findViewById(R.id.txtV_book_description);
        txtV_book_authors = view.findViewById(R.id.txtV_book_authors);
        btn_search_isbn = view.findViewById(R.id.btn_search_isbn);
        btn_finish_upload = view.findViewById(R.id.btn_finish_upload);
        imgV_coverImage =  view.findViewById(R.id.imgV_coverImage);


        progressDialog = new ProgressDialog(getContext());

        // get firebase instances
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        // create a volly to hold querys
        mQueue = Volley.newRequestQueue(requireActivity());

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
                    callGoogleApi();
                } else {
                    Log.d("firestoreHasBook", "true");
                    DocumentSnapshot taskResult = documents.get(0);
                    firebaseBookPath = taskResult.getReference().getPath();

                    book = new Book(taskResult.getDouble("price"), (String) taskResult.get("title"), (String) taskResult.get("isbn10"), (String) taskResult.get("isbn13"), (String) taskResult.get("description"), (String) taskResult.get("coverImgUrl") , (HashMap<String, String>) taskResult.get("authors"), (HashMap<String, String>) taskResult.get("categories"));
                    updateUIElements();
                }
            }
        });
    }

    private void updateUIElements() {
        progressDialog.dismiss();

        // create a string of authors
        StringBuilder strAuthors = new StringBuilder();
        HashMap<String, String> authors = book.getAuthors();
        for (int i = 0; i < authors.size(); i++) {
            strAuthors.append(authors.get(String.valueOf(i)));
            if (i != authors.size() - 1)
                strAuthors.append("\n");
        }

        // update testViews
        txtV_book_authors.setText(strAuthors.toString());
        txtV_book_title.setText(book.getTitle());
        txtV_book_description.setText(book.getDescription());

        // update the imageView
        imgV_coverImage.setVisibility(View.VISIBLE);
        Picasso.get().load(book.getCoverImgUrl()).into(imgV_coverImage);
        imgV_coverImage.setMinimumHeight(500);

        // make the finish upload button visible and clickable
        btn_finish_upload.setVisibility(View.VISIBLE);
        btn_finish_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // switch to the uploadBookFragment
                getParentFragmentManager().beginTransaction().replace(R.id.frame_container, new UploadBookFragment(book, firebaseBookPath)).addToBackStack(null).commit();
            }
        });
    }

    private void updateFirebase() {
        DocumentReference newBookRef = firestore.collection("books").document();
        firebaseBookPath = newBookRef.getPath();

        //  store the book
        newBookRef.set(book).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("addToFirebase", "complete");
                updateUIElements();
            }
        });
    }

    private void callGoogleApi() {

        String url = "https://www.googleapis.com/books/v1/volumes?q=" + isbn; //Google Books API

        // call google api and gather data
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("onResponse", "called");
                        try {
                            JSONArray itemsArray = response.getJSONArray("items");

                            // get the volume with the correct isbn
                            JSONObject itemsObj = null;
                            for (int i = 0; i < itemsArray.length(); i++) {

                                // some may not have both isbns -- this will have to be changed later
                                String item_isbn1 = itemsArray.getJSONObject(i).getJSONObject("volumeInfo").getJSONArray("industryIdentifiers").getJSONObject(0).optString("identifier");
                                String item_isbn2 = itemsArray.getJSONObject(i).getJSONObject("volumeInfo").getJSONArray("industryIdentifiers").getJSONObject(1).optString("identifier");

                                Log.d("isbns", isbn + " " + item_isbn1 + " " + item_isbn2);

                                if (isbn.equals(item_isbn1) || isbn.equals(item_isbn2)) {
                                    itemsObj = itemsArray.getJSONObject(i);
                                    break;
                                }
                            }

                            // a book with the isbn does not exist
                            if (itemsObj == null) {
                                throw new RuntimeException("Isbn does not exist");
                            }

                            // book volume info
                            JSONObject volumeObj = itemsObj.getJSONObject("volumeInfo");
                            String title = volumeObj.optString("title");
                            JSONArray authorsArray = volumeObj.getJSONArray("authors");
                            String description = volumeObj.optString("description");
                            JSONArray isbnNums = volumeObj.getJSONArray("industryIdentifiers");
                            String coverImgUrl = volumeObj.getJSONObject("imageLinks").optString("thumbnail");

                            // sale info data
                            JSONObject saleInfo = itemsObj.getJSONObject("saleInfo");
                            Double retailPrice = 0.0;

                            if(saleInfo.has("retailPrice")) {
                                retailPrice = saleInfo.getJSONObject("retailPrice").getDouble("amount");
                            }

                            // unprotected traffic not allowed when making requests to google books servers
                            // must make sure the image link uses https
                            if(!coverImgUrl.substring(0, 5).equals("https")) {
                                coverImgUrl = "https:" + coverImgUrl.substring(5);
                            }

                            HashMap<String, String> authors = new HashMap<>();
                            HashMap<String, String> categories = new HashMap<>();

                            //  add authors to map
                            for (int i = 0; i < authorsArray.length(); i++) {
                                authors.put(String.valueOf(i), authorsArray.optString(i));
                            }

                            // categories are not always required/included in JSON
                            if(volumeObj.has("categories")) {
                                JSONArray categoriesArray = volumeObj.getJSONArray("categories");

                                // add categories to map
                                for(int i = 0; i < categoriesArray.length(); i++) {
                                    categories.put(String.valueOf(i), categoriesArray.optString(i));
                                }
                            }

                            // add book details to the book object
                            book.setTitle(title);
                            book.setDescription(description);
                            book.setAuthors(authors);
                            book.setCategories(categories);
                            book.setcoverImgUrl(coverImgUrl);
                            book.setPrice(retailPrice);

                            // add the isbns to the book object
                            for (int i = 0; i < isbnNums.length(); i++) {
                                JSONObject currIsbn = isbnNums.getJSONObject(i);
                                if (currIsbn.optString("type").equals("ISBN_10")) {
                                    book.setIsbn10(currIsbn.optString("identifier"));
                                } else if (currIsbn.optString("type").equals("ISBN_13")) {
                                    book.setIsbn13(currIsbn.optString("identifier"));
                                }
                            }

                            // update firestore with the book data
                            updateFirebase();

                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Sorry! We couldn't gather enough info on this book.", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        } catch (RuntimeException e) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Sorry! A book with that isbn could not be found", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Sorry! We couldn't gather enough info on this book.", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                });

        mQueue.add(request);
    }
}