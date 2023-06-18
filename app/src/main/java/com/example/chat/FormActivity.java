package com.example.chat;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.chat.databinding.ActivityFormBinding;

public class FormActivity extends AppCompatActivity {
    private AppDB db;
    private ActivityFormBinding binding;

    private User loggeduser;
    private User contact;

    UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFormBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = Room.databaseBuilder(getApplicationContext(), AppDB.class, "users")
                .allowMainThreadQueries().build();
        userDao = db.UserDao();
        loggeduser = userDao.get(getIntent().getExtras().getString("username"));
        handleSave();

//        if (getIntent().getExtras() != null) {
//            int id = getIntent().getExtras().getInt("id");
//            user = userDao.get(id);
//
//            binding.etContent.setText(user.getContent());
//        }
    }

    private void handleSave() {
        binding.btnSave.setOnClickListener(view -> {
            contact = userDao.get(binding.etContent.getText().toString());
            Chat chat1 = new Chat(contact);
            Chat chat2 = new Chat(loggeduser);
            loggeduser.addChat(chat1);
            contact.addChat(chat2);
            userDao.update(loggeduser);
            userDao.update(contact);
            finish();
        });
    }
}