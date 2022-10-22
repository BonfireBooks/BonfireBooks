package com.example.bonfirebooks;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.InputFilter;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
    FirebaseUser user;

    // layout items
    EditText txtE_price;
    Spinner spinner_condition;
    Button btn_publish_book;
    Button btn_img_backward;
    Button btn_img_forward;

    // firebase paths
    String userBookPath;
    HashMap<String, String> imgPaths;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtE_price = view.findViewById(R.id.txtE_price);
        spinner_condition = view.findViewById(R.id.spinner_condition);
        btn_publish_book = view.findViewById(R.id.btn_publish_book);
        btn_img_backward = view.findViewById(R.id.btn_img_backward);
        btn_img_forward = view.findViewById(R.id.btn_img_forward);

        Log.d("book", book.toString());

        // get firebase instances
        firestore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

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
                    addUserBookFirebase();
                }
            }
        });
    }

    private void addUserBookFirebase() {

        // get the user name then store the book
        firestore.collection("users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                // new books document path
                userBookPath = firestore.document(bookPath).collection("users").document().getPath();

                // values for the user book
                String name = task.getResult().getString("name");
                double price = Double.valueOf(txtE_price.getText().toString());

                UserBook userBook = new UserBook(price, user.getEmail(), name, spinner_condition.getSelectedItem().toString().toLowerCase(), imgPaths);
                Log.d("book", userBook.toString());

                // store the book in firebase
                firestore.document(userBookPath).set(userBook).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("addUserBookFirebase", "Success");
                            updateUserBooks();
                        } else {
                            Log.d("addUserBookFirebase", "Failed");
                        }
                    }
                });
            }
        });
    }

    private void updateUserBooks() {

        // map with the path details
        HashMap<String, String> path = new HashMap<>();
        path.put("path", userBookPath);

        // add the path of the book to the users collection of books
        firestore.collection("users").document(user.getUid()).collection("books").add(path).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    Log.d("updateUserBooks", "Success");
                } else {
                    Log.d("updateUserBooks", "Failed");
                }
            }
        });
    }

    private boolean isValidInput() {
        boolean isValid = true;

        // price does not exceed booksrun price if it exists
        if (Double.valueOf(txtE_price.getText().toString()) > book.getPrice() && book.getPrice() != 0.0) {
            txtE_price.setError("The price must be less than or equal to the price of the book: " + book.getPrice());
            isValid = false;
        }

        return isValid;
    }

}