package com.example.bonfirebooks;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.Timestamp;

public class ChatListAdapter extends ArrayAdapter<String> {

    Activity context;
    String[] userName;
    String[] messageContents;
    Timestamp[] times;

    public ChatListAdapter(Activity context, String[] userNames, String[] messageContents, Timestamp[] times) {
        super(context, R.layout.chat_list, userNames);
        this.context = context;
        this.userName = userNames;
        this.messageContents = messageContents;
        this.times = times;
    }

    public View getView(int position, View view, ViewGroup viewgroup) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.chat_list, null, true);

        TextView txtV_name = rowView.findViewById(R.id.txtV_name);
        TextView txtV_content = rowView.findViewById(R.id.txtV_content);
        TextView txtV_time = rowView.findViewById(R.id.txtV_time);

        txtV_name.setText(userName[position]);
        txtV_content.setText(messageContents[position]);
        txtV_time.setText(String.valueOf(times[position].toDate().getTime()));

        return rowView;
    }


}
