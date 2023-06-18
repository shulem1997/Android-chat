package com.example.chat;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        loadMessages();
        EditText msgContent = findViewById(R.id.etInput);
        Button send = findViewById(R.id.btnSend);
        send.setOnClickListener(view-> {

        });
    }

    private void loadMessages() {
    }
}