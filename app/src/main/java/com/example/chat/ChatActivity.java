package com.example.chat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.chat.databinding.ActivityChatBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.util.ArrayList;
import java.util.List;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

public class ChatActivity extends AppCompatActivity {
    
    private User logged;
    private AppDB db;
    private UserDao userDao;
    private RecyclerView msgs;
    private ArrayList<Message> tmp;
    private Chat chat;
    private ArrayList<Message> msgList;
    private MessageAdapter adapter;
    private int chatId;


    private ActivityChatBinding binding;
    private String token;
    private String messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        TextView chatWith = binding.chatWith;


        Button send = binding.btnSend;
        send.setOnClickListener(view-> {
            sendMsg();
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
        //getMessagesFromServer(logged.getUsername(), logged.getPassword());

        binding.btnBack.setOnClickListener(View-> {
            Intent intent = new Intent(this, ContactsActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        loadMessages();
    }

    private void handleMessages() {
        msgs = binding.messages;

        msgList = setMsgsArray();

        adapter = new MessageAdapter(msgList, logged.getUsername());
        msgs.setAdapter(adapter);
    }

    private void sendMsg() {
        //server request here
        AddMessagesToServer(logged.getUsername(), logged.getPassword(), binding.etInput.getText().toString());
        binding.etInput.setText("");
        getMessagesFromServer(logged.getUsername(), logged.getPassword());
    }

    private void loadMessages() {

        // Assuming you have a list of messages called "messageList"

        getMessagesFromServer(logged.getUsername(),logged.getPassword());
        adapter = new MessageAdapter(msgList,logged.getUsername());
        msgs.setAdapter(adapter);
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


    private void getMessagesFromServer(String username, String password){
        getToken(username, password);
        AtomicInteger responseCode = new AtomicInteger();
        final StringBuilder[] responseBody = {new StringBuilder()};
        Thread thread = new Thread(new Runnable() {
            // Variable to hold the response body


            @Override
            public void run() {
                try {
                    URL url = new URL("http://10.0.2.2:5000/api/Chats/" + chatId + "/Messages"); // Replace with your API endpoint

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Authorization", "bearer " + token);
                    responseCode.set(connection.getResponseCode());




                    StringBuilder res;
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                        String line;

                        StringBuilder response = new StringBuilder();
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        res = new StringBuilder(response.toString()); // Assign the response to the variable
                    }
                    responseBody[0] = res;
                    connection.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
        });

// Start the thread
        thread.start();

        try {
            // Wait for the thread to finish
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(responseCode.get()==200) {
            Gson gson = new Gson();
            ArrayList<Message> msg = gson.fromJson(responseBody[0].toString(), new TypeToken<ArrayList<Message>>() {}.getType());;
            for(Message m: msg) {
                m.setChatId(chatId);
            }

            msgList = new ArrayList<>(msg);
            //adapter.notifyDataSetChanged();


        }
        else{
            Toast.makeText(getApplicationContext(), "Wrong Messages", Toast.LENGTH_LONG).show();
        }
    }
    private void AddMessagesToServer(String username, String password, String message){
        getToken(username, password);
        Thread thread = new Thread(new Runnable() {
            private StringBuilder responseBody; // Variable to hold the response body

            public StringBuilder getResponseBody() {
                return responseBody;
            }

            @Override
            public void run() {
                try {
                    URL url = new URL("http://10.0.2.2:5000/api/Chats/"+chatId+"/Messages"); // Replace with your API endpoint

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Authorization", "Bearer " + token);
                    connection.setDoOutput(true);

                    String requestBody = "{\"msg\": \"" + message + "\"}";

                    try {
                        DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                        outputStream.writeBytes(requestBody);
                        outputStream.flush();
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    StringBuilder responseBody;
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                        String line;

                        StringBuilder response = new StringBuilder();
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        responseBody = new StringBuilder(response.toString()); // Assign the response to the variable
                    }

                    connection.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
        });

// Start the thread
        thread.start();

        try {
            // Wait for the thread to finish
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void getToken(String username, String password) {
        AtomicInteger responseCode = new AtomicInteger();
        Thread thread = new Thread(new Runnable() {
            private StringBuilder responseBody; // Variable to hold the response body

            public StringBuilder getResponseBody() {
                return responseBody;
            }

            @Override
            public void run() {
                try {
                    URL url = new URL(Settings.getServer() + "/api/Tokens/"); // Replace with your API endpoint

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setDoOutput(true);

                    String requestBody = "{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}";

                    try {
                        DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                        outputStream.writeBytes(requestBody);
                        outputStream.flush();
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    responseCode.set(connection.getResponseCode());

                    StringBuilder responseBody;
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                        String line;

                        StringBuilder response = new StringBuilder();
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        responseBody = new StringBuilder(response.toString()); // Assign the response to the variable
                    }
                    token = responseBody.toString();

                    connection.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
        });

// Start the thread
        thread.start();

        try {
            // Wait for the thread to finish
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (responseCode.get() == 200) {

        } else {
            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
        }
    }
}