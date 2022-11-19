package com.example.bonfirebooks;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MessageListAdapter extends ArrayAdapter<String> {

    Activity context;
    String[] content;
    String[] time;
    String[] authors;

    public MessageListAdapter(Activity context, String[] content, String[] time, String[] authors) {
        super(context, R.layout.message_list, content);
        this.context = context;
        this.content = content;
        this.time = time;
        this.authors = authors;
    }

    public View getView(int position, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = context.getLayoutInflater();
        View messageView = inflater.inflate(R.layout.message_list, null, true);

        TextView txtV_message_content = messageView.findViewById(R.id.txtV_message_content);

        txtV_message_content.setText(content[position]);

        return messageView;
    }

}
