package com.example.bonfirebooks;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

public class MessageListAdapter extends ArrayAdapter<UserMessage> {

    Activity context;
    User user;
    UserMessage[] messages;

    public MessageListAdapter(Activity context, User user, UserMessage[] messages) {
        super(context, R.layout.message_list, messages);
        this.context = context;
        this.user = user;
        this.messages = messages;
    }

    public View getView(int position, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = context.getLayoutInflater();
        View messageView = inflater.inflate(R.layout.message_list, null, true);

        TextView txtV_message_content = messageView.findViewById(R.id.txtV_message_content);
        ConstraintLayout layout_message_wrapper = messageView.findViewById(R.id.layout_message_wrapper);
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) layout_message_wrapper.getLayoutParams();

        // set layout params and background based on sender
        if (messages[position].getSenderId().equals(user.getUid())) { // outgoing
            layout_message_wrapper.setBackgroundResource(R.drawable.message_bubble_outgoing);
            layoutParams.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
        } else { // incoming
            txtV_message_content.setTextColor(Color.parseColor("#FFFFFF"));
            layout_message_wrapper.setBackgroundResource(R.drawable.message_bubble_incoming);
            layoutParams.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
        }

        // set layout position
        layout_message_wrapper.setLayoutParams(layoutParams);

        // set message text
        txtV_message_content.setText(messages[position].getContent());

        return messageView;
    }
}
