package com.example.bonfirebooks;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
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
    ListView listV_chats;
    ImageView imgV_no_chats;
    TextView txtV_chats;

    // Firebase
    FirebaseFirestore firestore;
    FirebaseUser currUser;

    ListenerRegistration listenerRegistration;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        user = ((MainActivity) getActivity()).getUser();

        listV_chats = view.findViewById(R.id.listV_chats);
        imgV_no_chats = view.findViewById(R.id.imgV_no_chats);
        txtV_chats = view.findViewById(R.id.txtV_chats);

        // firestore
        firestore = FirebaseFirestore.getInstance();
        currUser = FirebaseAuth.getInstance().getCurrentUser();

        listV_chats.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                UserProfileChat userProfileChat = ((MainActivity) getActivity()).getUser().getChats().get(String.valueOf(i));
                getParentFragmentManager().beginTransaction().replace(R.id.frame_container, new ChatFragment(userProfileChat)).addToBackStack(null).commit();
            }
        });

        getChats();

        // force get chats
        final SwipeRefreshLayout pullToRefresh = view.findViewById(R.id.swipe_refresh_chats);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getChats();
                pullToRefresh.setRefreshing(false);
            }
        });

        // set to refresh when at the top of the list
        listV_chats.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView listView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(listV_chats.getTop() == 0 && firstVisibleItem == 0) {
                    pullToRefresh.setEnabled(true);
                } else {
                    pullToRefresh.setEnabled(false);
                }

            }
        });

    }

    private void getChats() {
        Log.d("wishlistChats", "Triggered");

        HashMap<String, UserProfileChat> chats = new HashMap<>();

        listenerRegistration = firestore.collection("users").document(user.getUid()).collection("chats").orderBy("time", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error == null) {
//                     add log d

                    int i = 0;
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        // create a new chat book with the document data

                        String otherUserId = "";

                        for (String key : doc.getData().keySet()) {
                            if (!key.equals("content") && !key.equals("time")) {
                                otherUserId = key;
                            }
                        }

                        UserProfileChat chat = new UserProfileChat(doc.getId(), otherUserId, doc.getString(otherUserId), doc.getString("content"), doc.getTimestamp("time").toDate());

                        // add the book to the users wishlist
                        chats.put(String.valueOf(i), chat);

                        i++;
                    }

                    user.setChats(chats);
                    populateChatList();
                } else {
                    // add log d
                }
            }
        });

    }

    private void populateChatList() {
        HashMap<String, UserProfileChat> chats = user.getChats();

        if (chats != null && chats.size() != 0) {
            txtV_chats.setVisibility(View.VISIBLE);
            listV_chats.setVisibility(View.VISIBLE);
            imgV_no_chats.setVisibility(View.GONE);

            String[] names = new String[chats.size()];
            String[] ids = new String[chats.size()];
            String[] contents = new String[chats.size()];
            Date[] times = new Date[chats.size()];

            for (int i = 0; i < chats.size(); i++) {
                UserProfileChat chat = chats.get(String.valueOf(i));
                names[i] = chat.getOtherUserName();
                ids[i] = chat.getOtherUserId();
                contents[i] = chat.getContent();
                times[i] = chat.getTime();
            }

            ChatListAdapter chatAdapter = new ChatListAdapter(getActivity(), ids, names, contents, times);
            listV_chats.setAdapter(chatAdapter);

        } else {
            imgV_no_chats.setVisibility(View.VISIBLE);
            listV_chats.setVisibility(View.GONE);
            txtV_chats.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        listenerRegistration.remove();
    }
}