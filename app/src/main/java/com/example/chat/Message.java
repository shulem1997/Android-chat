package com.example.chat;

import android.os.Build;

import java.time.LocalDate;


public class Message {
    private int id;
    private User sender;
    private LocalDate created;
    private String content;

    private boolean isSender;

    public Message(String content, User sender) {
        this.sender = sender;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.created = LocalDate.now();
        }
        this.content = content;
    }
    public String getContent() {
        return this.content;
    }

    public int getId() {
        return this.id;
    }

    public User getSender() {
        return this.sender;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

