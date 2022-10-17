package com.example.bonfirebooks;

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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

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

    // layout items
    EditText txtE_ISBN;
    TextView txtV_book_title;
    TextView txtV_book_description;
    TextView txtV_book_authors;
    Button btn_search_isbn;

    // book details
    HashMap<String, String> bookDetails = new HashMap<String, String>();
    HashMap<String, String> authors = new HashMap<String, String>();
    Double price;
    String isbn;

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

        // get firebase instances
        firestore = FirebaseFirestore.getInstance();

        // create a volly to hold querys
        mQueue = Volley.newRequestQueue(requireActivity());

        // insert the book into the firebase collection
        btn_search_isbn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // reset any errors
                txtE_ISBN.setError(null);

                isbn = txtE_ISBN.getText().toString();

                // begin the search process
                if(isbn.length() == 13) {
                    handleSearch();
                } else {
                    txtE_ISBN.setError("You Must Enter The ISBN 13 For Your Book");
                }
            }
        });
    }

    private void handleSearch() {
        // check if the book exists in firebase
        firestore.collection("books").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<DocumentSnapshot> taskDocs = task.getResult().getDocuments();

                // check if a document with the isbn exists
                boolean hasBook = false;
                for(int i = 0; i < taskDocs.size(); i++) {
                    if(taskDocs.get(i).getId().equals(isbn)) {
                        hasBook = true;
                    }
                }

                Log.d("FirestoreHasBook", Boolean.toString(hasBook));

                // being the api call process if the book does not exist in firebase
                // otherwise just update the ui with the information from firebase
                if(!hasBook) {
                    // this process will call  the bookrun api, update firestore and updateUI methods
                    callGoogleApi();
                } else {
                    UpdateUIFirestore();
                }
            }
        });
    }

    private void UpdateUIFirestore() {
        // grab all the book data from firestore
        firestore.collection("books").document(isbn).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Log.d("updateUIFirestore", "called");

                DocumentSnapshot taskResult = task.getResult();

                // get the authors map from fireabse
                HashMap<String, String> firebaseAuthors = (HashMap<String, String>) taskResult.get("authors");

                // iterate through the authors map from firebase creating a string with the data
                StringBuilder strAuthors = new StringBuilder();
                for(int i = 0; i < firebaseAuthors.size(); i++) {
                    strAuthors.append(firebaseAuthors.get(String.valueOf(i)));
                    if(i != firebaseAuthors.size()-1)
                        strAuthors.append("\n");
                }

                // update the text views
                txtV_book_authors.setText(strAuthors.toString());
                txtV_book_title.setText(taskResult.getString("title"));
                txtV_book_description.setText(taskResult.getString("description"));
            }
        });
    }

    private void updateFirebase() {
        //  store the book details (title, description)
        firestore.collection("books").document(isbn).set(bookDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("addDetails", "Complete");

                // store the authors
                firestore.collection("books").document(isbn).update("authors", authors).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("addAuthors", "Complete");

                        // store the price
                        firestore.collection("books").document(isbn).update("price", price).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d("addPrice", "Complete");

                                // update the ui
                                txtV_book_title.setText(bookDetails.get("title"));
                                txtV_book_description.setText(bookDetails.get("description"));

                                //
                                StringBuilder strAuthors = new StringBuilder();
                                for(int i = 0; i < authors.size(); i++) {
                                    strAuthors.append(authors.get(String.valueOf(i)));
                                    if(i != authors.size()-1)
                                        strAuthors.append("\n");
                                }

                                txtV_book_authors.setText(strAuthors);
                            }
                        });
                    }
                });
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

                            JSONObject jsonObject = response.getJSONObject("result");
                            String status = jsonObject.getString("status");
                            JSONObject offers = jsonObject.getJSONObject("offers");
                            JSONObject bookrs = offers.getJSONObject("booksrun");
                            JSONObject new_price = bookrs.getJSONObject("new");
                            Double brPrice = new_price.getDouble("price"); //price of new

                            // set the price
                            if(!status.equals("error")) {
                                price = brPrice;
                                updateFirebase();
                            } else {
                                throw new RuntimeException("status error");
                            }

                        }
                        catch (JSONException e) {
                            Toast.makeText(getActivity(), "Sorry! We couldn't gather enough info on this book.", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                        catch (RuntimeException e) {
                            Toast.makeText(getActivity(), "Sorry! We couldn't gather enough info on this book.", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "Sorry! We couldn't gather enough info on this book.", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                });

        mQueue.add(request);
    }

    private void callGoogleApi() {

        String url = "https://www.googleapis.com/books/v1/volumes?q=" + isbn; //Google Books API
        String bk_price = String.valueOf(isbn);

        // call google api and gather data
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("onResponse", "called");
                        try {
                            JSONArray itemsArray = response.getJSONArray("items");

                            JSONObject itemsObj = null;
                            for(int i = 0; i < itemsArray.length(); i++) {
                                itemsObj = itemsArray.getJSONObject(i);
                                String item_isbn = itemsObj.getJSONObject("volumeInfo").getJSONArray("industryIdentifiers").getJSONObject(0).optString("identifier");

                                if(isbn.equals(item_isbn)) {
                                    break;
                                }
                            }

                            // a book with the isbn does not exist
                            if(itemsObj == null) {
                                return;
                            }

                            JSONObject volumeObj = itemsObj.getJSONObject("volumeInfo");
                            String title = volumeObj.optString("title");
                            JSONArray authorsArray = volumeObj.getJSONArray("authors");
                            String description = volumeObj.optString("description");
                            int pageCount = volumeObj.optInt("pageCount");

                            if (authorsArray.length() != 0) {

                                // add book details to map
                                bookDetails.put("title", title);
                                bookDetails.put("description", description);

                                //  add authors to map
                                for (int i = 0; i < authorsArray.length(); i++) {
                                    authors.put(String.valueOf(i), authorsArray.optString(i));
                                }

                                Log.d("book details", title + " " + description + " " +  authors.toString());

                                callBookrunApi();
                            }
                        }catch (JSONException e) {
                            Toast.makeText(getActivity(), "Sorry! We couldn't gather enough info on this book.", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "Sorry! We couldn't gather enough info on this book.", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                });

        mQueue.add(request);
    }
}