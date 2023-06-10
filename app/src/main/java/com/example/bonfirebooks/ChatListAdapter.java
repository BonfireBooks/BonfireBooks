package com.example.bonfirebooks;

import android.app.Activity;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;

import java.time.Duration;
import java.util.Date;

public class ChatListAdapter extends ArrayAdapter<String> {

    Activity context;
    String[] ids;
    String[] userName;
    String[] messageContents;
    Date[] times;

    public ChatListAdapter(Activity context, String[] ids, String[] userNames, String[] messageContents, Date[] times) {
        super(context, R.layout.chat_list, userNames);
        this.context = context;
        this.ids = ids;
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
        ImageView img_profile = rowView.findViewById(R.id.img_profile);

        txtV_name.setText(userName[position]);
        txtV_content.setText(messageContents[position]);

        FirebaseStorage.getInstance().getReference().child("User Images").child(ids[position]).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()){
                    Glide.with(rowView.getContext()).load(task.getResult()).into(img_profile);
                }
            }
        });

        if(times[position] != null) {
            Date date1 = times[position];
            Date date2 = new Date(System.currentTimeMillis());

            Duration duration = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                duration = Duration.between(date1.toInstant(), date2.toInstant());
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if(duration.getSeconds() < 60) {
                    txtV_time.setText(duration.getSeconds() + "s");
                } else if(duration.toMinutes() < 60) {
                    txtV_time.setText(duration.toMinutes() + "m");
                } else if(duration.toHours() < 24) {
                    txtV_time.setText(duration.toHours() + "h");
                } else if(duration.toDays() < 30) {
                    txtV_time.setText(duration.toDays() + "d");
                }
            }

        } else {
            txtV_time.setText("");
        }

        return rowView;
    }


}
