package com.example.chat;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.chat.databinding.ActivityChatBinding;


import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    
    private User logged;
    private AppDB db;
    private UserDao userDao;
    private ListView msgs;
    private List<String> texts;
    private Chat chat;
    private List<Message> msgList;
    private MessageAdapter adapter;
    private int chatId;

    private ActivityChatBinding binding;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        TextView chatWith = binding.chatWith;


        EditText msgContent = binding.etInput;

        Button send = binding.btnSend;
        send.setOnClickListener(view-> {
            sendMsg(msgContent.getText().toString());
        });

        db = Room.databaseBuilder(getApplicationContext(), AppDB.class, "users")
                .allowMainThreadQueries().build();
        String[] arr = getIntent().getExtras().getStringArray("chatInfo");
        String username = arr[0];
        chatId = Integer.parseInt(arr[1]);
        userDao = db.UserDao();
        logged = userDao.get(username);
        chat = getChat();

        chatWith.setText(chat.getUser().getDisplayName());

        handleMessages();

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMessages();
    }

    private void handleMessages() {
        msgs = binding.messages;

        msgList = setMsgsArray();

        adapter = new MessageAdapter(this, msgList);
        msgs.setAdapter(adapter);
    }

    private void sendMsg(String msg) {
        //server request here

        binding.etInput.setText("");
    }

    private void loadMessages() {

        // Assuming you have a list of messages called "messageList"

        msgList = setMsgsArray();
        //texts.clear();

        adapter.notifyDataSetChanged();
    }
    private ArrayList<Message> setMsgsArray() {
        ArrayList<Message> chatmsg = logged.getMessages();
        ArrayList<Message> list = new ArrayList<>();
        for(Message m: chatmsg) {
            if(m.getChatId() == chatId) {
                m.setIsSender(logged.getUsername());
                list.add(m);
            }
        }
        return list;
    }

    private Chat getChat() {
        ArrayList<Chat> cht = logged.getChats();
        for (Chat c: cht) {
            if(c.getId() == chatId)
                return c;
        }
        return null;
    }

}