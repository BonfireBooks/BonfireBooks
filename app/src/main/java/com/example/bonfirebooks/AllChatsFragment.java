package com.example.bonfirebooks;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AllChatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AllChatsFragment extends Fragment {

    public AllChatsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AllChatsFragment.
     */
    public static AllChatsFragment newInstance() {
        AllChatsFragment fragment = new AllChatsFragment();
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
        return inflater.inflate(R.layout.fragment_all_chats, container, false);
    }

    User user;

    // layout items
    ConstraintLayout layout_chats_empty;
    ListView listV_chats;
    TextView txtV_no_chats;

    // Firebase
    FirebaseFirestore firestore;
    FirebaseUser currUser;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        user = ((MainActivity) getActivity()).getUser();

        layout_chats_empty = view.findViewById(R.id.layout_chats_empty);
        txtV_no_chats = view.findViewById(R.id.txtV_no_chats);
        listV_chats = view.findViewById(R.id.listV_chats);

        // firestore
        firestore = FirebaseFirestore.getInstance();
        currUser = FirebaseAuth.getInstance().getCurrentUser();

        listV_chats.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                UserProfileChat userProfileChat = ((MainActivity)getActivity()).getUser().getChats().get(String.valueOf(i));
                getParentFragmentManager().beginTransaction().replace(R.id.frame_container, new ChatFragment(userProfileChat)).addToBackStack(null).commit();
            }
        });

        final SwipeRefreshLayout pullToRefresh = view.findViewById(R.id.swipe_refresh_chats);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshChats();
                pullToRefresh.setRefreshing(false);
                populateChatList();
            }
        });

        populateChatList();
    }

    private void refreshChats() {
        Log.d("wishlistChats", "Triggered");

        HashMap<String, UserProfileChat> chats = new HashMap<>();

        firestore.collection("users").document(user.getUid()).collection("chats").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // add log d
                    int i = 0;
                    for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                        // create a new chat book with the document data
                        UserProfileChat chat = new UserProfileChat(doc.getId(), doc.getString("otherUserName"), doc.getString("content"), doc.getTimestamp("time").toDate());

                        // add the book to the users wishlist
                        chats.put(String.valueOf(i), chat);

                        i++;
                    }

                    user.setChats(chats);
                } else {
                    // add log d
                }
            }
        });
    }

    private void populateChatList() {
        HashMap<String, UserProfileChat> chats = user.getChats();

        if (chats != null && chats.size() != 0) {

            txtV_no_chats.setVisibility(View.GONE);
            listV_chats.setVisibility(View.VISIBLE);

            String[] names = new String[chats.size()];
            String[] contents = new String[chats.size()];
            Date[] times = new Date[chats.size()];

            for (int i = 0; i < chats.size(); i++) {
                UserProfileChat chat = chats.get(String.valueOf(i));
                names[i] = chat.getOtherUserName();
                contents[i] = chat.getContent();
                times[i] = chat.getTime();
            }

            ChatListAdapter chatAdapter = new ChatListAdapter(getActivity(), names, contents, times);
            listV_chats.setAdapter(chatAdapter);

        } else {
            txtV_no_chats.setVisibility(View.VISIBLE);
            listV_chats.setVisibility(View.GONE);
        }
    }
}