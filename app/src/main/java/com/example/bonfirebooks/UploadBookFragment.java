package com.example.bonfirebooks;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.jar.JarInputStream;

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

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UploadBookFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UploadBookFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UploadBookFragment newInstance(String param1, String param2) {
        UploadBookFragment fragment = new UploadBookFragment();
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
        return inflater.inflate(R.layout.fragment_upload_book, container, false);
    }
//
//    // firebase
//    FirebaseFirestore firestore;
//
//    // layout items
//    EditText txtE_ISBN;
//    EditText txtE_price;
//    Spinner spinner_condition;
//    Button btn_publish_book;
//
//    // api calls
//    private RequestQueue mQueue;
//
//    // book details
//    HashMap<String, String> bookDetails = new HashMap();
//    HashMap<String, String> authors = new HashMap();
//    Double price;
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        txtE_ISBN = view.findViewById(R.id.txtE_isbn);
//        txtE_price = view.findViewById(R.id.txtE_price);
//        spinner_condition = view.findViewById(R.id.spinner_condition);
//        btn_publish_book = view.findViewById(R.id.btn_publish_book);
//
//        // get firebase instances
//        firestore = FirebaseFirestore.getInstance();
//
//        // set options for the spinner
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.book_conditions, android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner_condition.setAdapter(adapter);
//
//        // todo -- set formatting for the price
//
//        // create a volly to hold querys
//        mQueue = Volley.newRequestQueue(getActivity());
//
//        // insert the book into the firebase collection
//        btn_publish_book.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String isbn = txtE_ISBN.getText().toString();
//
//                if(isbn.length() == 10 || isbn.length() == 13) {
//                    updateFirebase(txtE_ISBN.getText().toString());
//                } else {
//
//                }
//            }
//        });
//    }
//
//    private void updateFirebase(String isbn) {
//
//        callGoogleApi(isbn);
//        callBookrunApi(isbn);
//
//        // store the book details (title, description)
////        firestore.collection("books").document(isbn).set(bookDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
////            @Override
////            public void onComplete(@NonNull Task<Void> task) {
////                Log.d("addDetails", "Complete");
////
////                // store the authors
////                firestore.collection("books").document(isbn).update("authors", authors).addOnCompleteListener(new OnCompleteListener<Void>() {
////                    @Override
////                    public void onComplete(@NonNull Task<Void> task) {
////                        Log.d("addAuthors", "Complete");
////                        firestore.collection("books").document(isbn).update("price", price).addOnCompleteListener(new OnCompleteListener<Void>() {
////                            @Override
////                            public void onComplete(@NonNull Task<Void> task) {
////                                Log.d("addPrice", "Complete");
////                            }
////                        });
////                    }
////                });
////            }
////        });
//    }
//
//    private void callBookrunApi(String isbn) {
//
//        String url = "https://booksrun.com/api/v3/price/buy/" + isbn + "?key=t2xlhotmhy246sby0zvu"; //Book Runs API
//
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//
//                            JSONObject jsonObject = response.getJSONObject("result");
//                            String status = jsonObject.getString("status");
//                            JSONObject offers = jsonObject.getJSONObject("offers");
//                            JSONObject bookrs = offers.getJSONObject("booksrun");
//                            JSONObject new_price = bookrs.getJSONObject("new");
//                            Double brPrice = bookrs.getDouble("price"); //price of new
//
//                            if(!status.equals("error")) {
//                                price = brPrice;
//                            }
//
//                        }
//                        catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        error.printStackTrace();
//                    }
//                });
//
//        mQueue.add(request);
//    }
//
//    private void callGoogleApi(String isbn) {
//
//        String url = "https://www.googleapis.com/books/v1/volumes?q=" + isbn; //Google Books API
//        String bk_price = String.valueOf(isbn);
//
//        // call google api and gather data
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.d("onResponse", "called");
//                        try {
//                            JSONArray itemsArray = response.getJSONArray("items");
//
//
//                            // make sure the isbn exists within the json
//
//
//                            JSONObject itemObj;
//                            for(int i = 0; i < itemsArray.length(); i++) {
//                                String isbn = itemsArray.getJSONObject(i).getJSONObject("volumeInfo").getJSONArray("industryIdentifiers").getJSONObject(0).optString("identifier");
//                                Log.d("ISBN", isbn);
//                            }
//
//
//                            JSONObject itemsObj = itemsArray.getJSONObject(0); //To get first index
//                            JSONObject volumeObj = itemsObj.getJSONObject("volumeInfo");
//                            String title = volumeObj.optString("title");
//                            JSONArray authorsArray = volumeObj.getJSONArray("authors");
//                            String description = volumeObj.optString("description");
//                            int pageCount = volumeObj.optInt("pageCount");
//
//                            if (authorsArray.length() != 0) {
//
//                                // add book details to map
//                                bookDetails.put("title", title);
//                                bookDetails.put("description", description);
//
//                                // add authors to map
//                                for (int i = 0; i < authorsArray.length(); i++) {
//                                    authors.put(String.valueOf(i), authorsArray.optString(i));
//                                }
//
//                            }
//                        }catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        error.printStackTrace();
//                    }
//                });
//
//        mQueue.add(request);
//    }
}