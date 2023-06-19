package com.example.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.List;

public class MessageAdapter extends ArrayAdapter<Message> {

    public MessageAdapter(Context context, List<Message> messages) {
        super(context, 0, messages);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_message, parent, false);
        }

        // Get the current message
        Message message = getItem(position);

        // Get the views from the layout
        ImageView bubbleImageView = convertView.findViewById(R.id.bubble);
        TextView contentTextView = convertView.findViewById(R.id.messageContent);

        // Set the bubble background drawable and message content
        bubbleImageView.setBackground(ContextCompat.getDrawable(getContext(),message.getDraw()));
        contentTextView.setText(message.getContent());

        return convertView;
    }
}


