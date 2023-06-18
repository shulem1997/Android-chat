package com.example.chat;

import android.os.Bundle;
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
    private ArrayAdapter<String> adapter;
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
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, texts);
        texts = new ArrayList<>();

        loadMessages();

        msgs.setAdapter(adapter);
    }

    private void sendMsg(String msg) {
    }

    private void loadMessages() {

        msgList = setMsgsArray();
        texts.clear();
        for(Message m: msgList) {
            texts.add(m.getContent() + " " + m.getCreated());
        }
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