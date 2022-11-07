package com.example.bonfirebooks;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment {

    public AccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountFragment newInstance() {
        AccountFragment fragment = new AccountFragment();
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
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    User user;

    Button logout;
    TextView txtV_upload_book;
    TextView txtV_view_books;
    TextView txtV_edit_profile;
    TextView txtV_order_history;
    TextView txtV_notification;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        user = ((MainActivity)getActivity()).getUser();

        Log.d("account", user.toString());

        logout = view.findViewById(R.id.btn_logout);
        txtV_edit_profile = view.findViewById(R.id.txtV_edit_profile);
        txtV_view_books = view.findViewById(R.id.txtV_view_books);
        txtV_upload_book = view.findViewById(R.id.txtV_upload_book);
        txtV_order_history = view.findViewById(R.id.txtV_order_history);
        txtV_notification = view.findViewById(R.id.txtV_notification);

        setViewListeners();
    }

    private void setViewListeners() {
        // user sign out
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), StartActivity.class));
                getActivity().finish();
            }
        });

        txtV_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // move fragments here
            }
        });

        txtV_view_books.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // move fragments here
            }
        });

        txtV_upload_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction().replace(R.id.frame_container, new UploadBookSearchFragment()).addToBackStack(null).commit();
            }
        });

        txtV_order_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // move fragments here
            }
        });

        txtV_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // move fragments here
            }
        });

    }
}