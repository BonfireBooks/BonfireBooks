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

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.firebase.firestore.FirebaseFirestore;

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

    public UploadBookFragment(Book book, String path) {
        // Required empty public constructor
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upload_book, container, false);
    }

    // firebase
    FirebaseFirestore firestore;

    // layout items
    EditText txtE_price;
    Spinner spinner_condition;
    Button btn_publish_book;

    // api calls
    private RequestQueue mQueue;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtE_price = view.findViewById(R.id.txtE_price);
        spinner_condition = view.findViewById(R.id.spinner_condition);
        btn_publish_book = view.findViewById(R.id.btn_publish_book);

        Log.d("book", book.toString());

        // get firebase instances
        firestore = FirebaseFirestore.getInstance();

        // set options for the spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.book_conditions, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_condition.setAdapter(adapter);

        // todo -- set formatting for the price

        // create a volley to hold querys
        mQueue = Volley.newRequestQueue(getActivity());

        // set the price hint based on the books price
        txtE_price.setHint("Price <= " + book.getPrice());

        // insert the book into the firebase collection
        btn_publish_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

}