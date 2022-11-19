package com.example.bonfirebooks;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

public class MessageListAdapter extends ArrayAdapter<String> {

    Activity context;
    User user;
    String[] content;
    String[] time;
    String[] authors;

    public MessageListAdapter(Activity context, User user, String[] content, String[] time, String[] authors) {
        super(context, R.layout.message_list, content);
        this.context = context;
        this.user = user;
        this.content = content;
        this.time = time;
        this.authors = authors;
    }

    public View getView(int position, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = context.getLayoutInflater();
        View messageView = inflater.inflate(R.layout.message_list, null, true);

        ConstraintLayout layout_message_wrapper = messageView.findViewById(R.id.layout_message_wrapper);
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) layout_message_wrapper.getLayoutParams();

        // set layout params and background based on sender
        if (authors[position].equals(user.getUid())) { // outgoing
            layout_message_wrapper.setBackgroundResource(R.drawable.message_bubble_outgoing);
            layoutParams.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
        } else { // incoming
            layout_message_wrapper.setBackgroundResource(R.drawable.message_bubble_incoming);
            layoutParams.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
        }

        // set layout position
        layout_message_wrapper.setLayoutParams(layoutParams);

        // set message text
        TextView txtV_message_content = messageView.findViewById(R.id.txtV_message_content);
        txtV_message_content.setText(content[position]);

        return messageView;
    }
}
