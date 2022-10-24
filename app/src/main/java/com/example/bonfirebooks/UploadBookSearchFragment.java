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

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UploadBookSearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UploadBookSearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UploadBookSearchFragment newInstance(String param1, String param2) {
        UploadBookSearchFragment fragment = new UploadBookSearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upload_book_search, container, false);
    }

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
    HashMap<String, String> authors = new HashMap<>();
    String isbn;
    String firebaseBookPath = "";


    ProgressDialog progressDialog;

    // api calls
    private RequestQueue mQueue;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
        // check if the book exists in firebase

        String strISBN = isbn.length() == 13 ? "isbn13" : "isbn10";

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
                    firebaseBookPath = documents.get(0).getReference().getPath();
                    updateUIFirestore();
                }
            }
        });
    }

    private void updateUIFirestore() {

        String strISBN = isbn.length() == 13 ? "isbn13" : "isbn10";

        // get the book with the matching isbn
        firestore.document(firebaseBookPath).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Log.d("updateUIFirestore", "called");

                // there should only ever be one occurance of a book with matching isbn - so grab the first one
                DocumentSnapshot taskResult = task.getResult();

                // get the authors map from firebase
                HashMap<String, String> firebaseAuthors = (HashMap<String, String>) taskResult.get("authors");

                // iterate through the authors map from firebase creating a string with the data
                StringBuilder strAuthors = new StringBuilder();
                for (int i = 0; i < firebaseAuthors.size(); i++) {
                    strAuthors.append(firebaseAuthors.get(String.valueOf(i)));
                    if (i != firebaseAuthors.size() - 1)
                        strAuthors.append("\n");
                }

                // update the text views
                txtV_book_authors.setText(strAuthors.toString());
                txtV_book_title.setText(taskResult.getString("title"));
                txtV_book_description.setText(taskResult.getString("description"));
                imgV_coverImage.setVisibility(View.VISIBLE);
                Picasso.get().load(taskResult.getString("coverImgUrl")).into(imgV_coverImage);
                imgV_coverImage.setMinimumHeight(500);

                book = new Book((Double) taskResult.get("price"), (String) taskResult.get("title"), (String) taskResult.get("isbn10"), (String) taskResult.get("isbn13"), (String) taskResult.get("description"), (String) taskResult.get("coverImgUrl") , (HashMap<String, String>) taskResult.get("authors"));

                updateOtherUIElements();
            }
        });
    }

    private void updateOtherUIElements() {

        progressDialog.dismiss();

        btn_finish_upload.setVisibility(View.VISIBLE);
        btn_finish_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction().replace(R.id.frame_container, new UploadBookFragment(book, firebaseBookPath)).commit();
            }
        });
    }

    private void updateFirebase() {
        DocumentReference newBookRef = firestore.collection("books").document();
        firebaseBookPath = newBookRef.getPath();

        //  store the book details (title, description, isbn10, isbn13)
        newBookRef.set(book).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("addToFirebase", "complete");
                //  string of authors
                StringBuilder strAuthors = new StringBuilder();
                for (int i = 0; i < authors.size(); i++) {
                    strAuthors.append(authors.get(String.valueOf(i)));
                    if (i != authors.size() - 1)
                        strAuthors.append("\n");
                }

                // update the ui
                txtV_book_authors.setText(strAuthors);
                txtV_book_title.setText(book.getTitle());
                txtV_book_description.setText(book.getDescription());
                imgV_coverImage.setVisibility(View.VISIBLE);
                Picasso.get().load(book.getCoverImgUrl()).into(imgV_coverImage);

                updateOtherUIElements();
            }
        });
    }

    private void callBookrunApi() {
        String url = "https://booksrun.com/api/v3/price/buy/" + isbn + "?key=t2xlhotmhy246sby0zvu"; //Book Runs API

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            // get json information
                            JSONObject jsonObject = response.getJSONObject("result");
                            String status = jsonObject.getString("status");
                            JSONObject offers = jsonObject.getJSONObject("offers");
                            JSONObject bookrs = offers.getJSONObject("booksrun");

                            // check if bookrun has a price for the book
                            JSONObject new_price = bookrs.getJSONObject("new");

                            Log.d("new Price", new_price.toString());

                            // set the price of the book object
                            if (new_price.equals("none")) {
                                book.setPrice(0);
                            } else if (!status.equals("error")) {
                                Log.d("HERE", "properly set");
                                Double brPrice = new_price.getDouble("price"); //price of new
                                book.setPrice(brPrice);
                            }

                            Log.d("bookDetails", book.toString());

                            // update firestore with the book data
                            updateFirebase();

                        } catch (JSONException e) {

                            // typically JSONExceptions are thrown since booksrun api
                            // doesnt always format properly
                            book.setPrice(0);
                            Log.d("bookDetails", book.toString());

                            // update firestore with the book data
                            updateFirebase();


                            // Toast.makeText(getActivity(), "Sorry! We couldn't gather enough info on this book.", Toast.LENGTH_SHORT).show();
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

                            // get book info from the json
                            JSONObject volumeObj = itemsObj.getJSONObject("volumeInfo");
                            String title = volumeObj.optString("title");
                            JSONArray authorsArray = volumeObj.getJSONArray("authors");
                            String description = volumeObj.optString("description");
                            JSONArray isbnNums = volumeObj.getJSONArray("industryIdentifiers");
                            String coverImgUrl = volumeObj.getJSONObject("imageLinks").optString("thumbnail");

                            // unprotected traffic not allowed when making requests to google books servers
                            // must make sure the image link uses https
                            if(!coverImgUrl.substring(0, 5).equals("https")) {
                                coverImgUrl = "https:" + coverImgUrl.substring(5);
                            }

                            //  add authors to map
                            for (int i = 0; i < authorsArray.length(); i++) {
                                authors.put(String.valueOf(i), authorsArray.optString(i));
                            }

                            // add book details to the book object
                            book.setTitle(title);
                            book.setDescription(description);
                            book.setAuthors(authors);
                            book.setcoverImgUrl(coverImgUrl);

                            // add the isbns to the book object
                            for (int i = 0; i < isbnNums.length(); i++) {
                                JSONObject currIsbn = isbnNums.getJSONObject(i);
                                if (currIsbn.optString("type").equals("ISBN_10")) {
                                    book.setIsbn10(currIsbn.optString("identifier"));
                                } else if (currIsbn.optString("type").equals("ISBN_13")) {
                                    book.setIsbn13(currIsbn.optString("identifier"));
                                }
                            }

                            // call the bookrun api after the google api is done being called
                            callBookrunApi();

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