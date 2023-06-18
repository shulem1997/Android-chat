package com.example.chat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.chat.databinding.ActivityContactsBinding;


import java.util.ArrayList;
import java.util.List;


public class ContactsActivity extends AppCompatActivity {
    private ActivityContactsBinding binding;
    private AppDB db;
    private ListView lvChats;
    private List<String> contacts;
    private List<Chat> chatList;
    private ArrayAdapter<String> adapter;
    private UserDao userDao;
    private User logged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContactsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = Room.databaseBuilder(getApplicationContext(), AppDB.class, "users")
                .allowMainThreadQueries().build();

        userDao = db.UserDao();
        logged = userDao.get(getIntent().getExtras().getString("username"));
        handlePosts();
        binding.btnAdd.setOnClickListener(view -> {
            Intent intent = new Intent(this, FormActivity.class);
            intent.putExtra("username", logged.getUsername());
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadChats(logged);
    }


    private void handlePosts() {
        lvChats = binding.lvChats;
        contacts = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, contacts);

        loadChats(logged);

        lvChats.setAdapter(adapter);
        lvChats.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(this, ChatActivity.class);
            String[] arr = new String[] {logged.getUsername(), chatList.get(i).getIdStr() };
            intent.putExtra("chatInfo", arr);
                startActivity(intent);
        });

    }

    private void loadChats(User logged) {
        contacts.clear();
        chatList = userDao.get(logged.getUsername()).getChats();
        for (Chat chat : chatList) {
            contacts.add(chat.getUser().getDisplayName());
            //Triple t = new Triple();
            //t.setImg(chat.getUser().getProfilePic());
            //t.setName(chat.getUser().getDisplayName());

        }

        adapter.notifyDataSetChanged();
    }
}
