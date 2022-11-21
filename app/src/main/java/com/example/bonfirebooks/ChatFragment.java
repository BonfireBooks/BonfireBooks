package com.example.bonfirebooks;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import org.checkerframework.checker.units.qual.A;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {

    public ChatFragment() {
        // Required empty public constructor
    }

    UserProfileChat userProfileChat;

    public ChatFragment(UserProfileChat userProfileChat) {
        this.userProfileChat = userProfileChat;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ChatFragment.
     */
    public static ChatFragment newInstance() {
        ChatFragment fragment = new ChatFragment();
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
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    User user;

    FirebaseFirestore firestore;
    FirebaseStorage firebaseStorage;

    ConstraintLayout layout_header;
    ConstraintLayout layout_send_bar;
    ConstraintLayout layout_chats_empty;

    TextView txtV_user_name;
    TextView txtV_no_chats;

    ImageView img_profile;

    EditText txtE_message_content;
    Button btn_send;

    ListView listV_chats;

    ProgressDialog progressDialog;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BottomNavigationView navBar = getActivity().findViewById(R.id.bottomNavView);
        navBar.setVisibility(View.GONE);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        user = ((MainActivity) getActivity()).getUser();

        firestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        layout_header = view.findViewById(R.id.layout_header);
        layout_send_bar = view.findViewById(R.id.layout_send_bar);
        layout_chats_empty = view.findViewById(R.id.layout_chats_empty);
        listV_chats = view.findViewById(R.id.listV_chats);
        txtV_user_name = view.findViewById(R.id.txtV_user_name);
        txtV_no_chats = view.findViewById(R.id.txtV_no_chats);
        img_profile = view.findViewById(R.id.img_profile);
        txtE_message_content = view.findViewById(R.id.txtE_message_content);
        btn_send = view.findViewById(R.id.btn_send);

        txtV_user_name.setText(userProfileChat.getOtherUserName());

        // get user profile image
        firebaseStorage.getReference().child("User Images").child(userProfileChat.getOtherUserId()).getDownloadUrl().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("chatGetProfileImg", "Failed");
            }
        }).addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d("chatGetProfileImg", "Success");
                Glide.with(getContext()).load(uri).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                }).into(img_profile);
            }
        });

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading Chats...");
        progressDialog.show();

        firestore.collection("chats").document(userProfileChat.getChatId()).collection("messages").orderBy("time").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error == null) {
                    if (value.getDocuments().size() > 0) {
                        Log.d("hasMessages", "true");

                        List<DocumentSnapshot> docs = value.getDocuments();
                        HashMap<String, UserMessage> messages = new HashMap<>();

                        for (int i = 0; i < docs.size(); i++) {
                            DocumentSnapshot doc = docs.get(i);
                            messages.put(String.valueOf(i), new UserMessage(doc.getId(), doc.getString("content"), doc.getString("sender"), doc.getTimestamp("time").toDate()));
                        }

                        updateMessageList(messages);
                    } else {
                        Log.d("hasMessages", "false");
                        txtV_no_chats.setVisibility(View.VISIBLE);
                        progressDialog.dismiss();
                    }
                } else {
                    Log.d("loadMessages", "Failed, code:" + error.getCode() + "message: " + error.getMessage());
                    Toast.makeText(getContext(), "Could Not Load Messages", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidInput()) {
                    HashMap<String, Object> data = new HashMap<>();
                    data.put("content", txtE_message_content.getText().toString());
                    data.put("time", Timestamp.now());
                    data.put("sender", user.getUid());

                    firestore.collection("chats").document(userProfileChat.getChatId()).collection("messages").document().set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("sendMessage", "Successful");
                                txtE_message_content.setText("");
                            } else {
                                Toast.makeText(getContext(), "Could Not Send Message", Toast.LENGTH_SHORT).show();
                                Log.d("sendMessage", "Failed");
                            }
                        }
                    });
                }
            }
        });

    }

    private boolean isValidInput() {
        // if the message content is empty -> input invalid
        return !TextUtils.isEmpty(txtE_message_content.getText());
    }

    private void updateMessageList(HashMap<String, UserMessage> messages) {

        if (messages != null && messages.size() != 0) {

            listV_chats.setVisibility(View.VISIBLE);

            String[] content = new String[messages.size()];
            String[] time = new String[messages.size()];
            String[] sender = new String[messages.size()];

            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy\nhh:mm aa");
            sdf.setTimeZone(TimeZone.getDefault());

            for (int i = messages.size() - 1; i >= 0; i--) {
                UserMessage userMessage = messages.get(String.valueOf(i));
                content[i] = userMessage.getContent();
                sender[i] = userMessage.getSenderId();
                time[i] = sdf.format(userMessage.getTime());
            }

            MessageListAdapter messageListAdapter = new MessageListAdapter(getActivity(), user, content, time, sender);
            listV_chats.setAdapter(messageListAdapter);
            progressDialog.dismiss();
        }

    }
}