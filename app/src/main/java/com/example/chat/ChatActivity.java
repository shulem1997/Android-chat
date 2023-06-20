package com.example.chat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.chat.databinding.ActivityChatBinding;


import java.util.ArrayList;
import java.util.List;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ChatActivity extends AppCompatActivity {
    
    private User logged;
    private AppDB db;
    private UserDao userDao;
    private RecyclerView msgs;
    private List<String> texts;
    private Chat chat;
    private List<Message> msgList;
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

        adapter = new MessageAdapter(msgList);
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


    private void getMessagesFromServer(String username, String password, int id){
        getToken(username, password);
        Thread thread = new Thread(new Runnable() {
            private StringBuilder responseBody; // Variable to hold the response body

            public StringBuilder getResponseBody() {
                return responseBody;
            }

            @Override
            public void run() {
                try {
                    URL url = new URL("http://10.0.2.2:5000/api/Chats/"+id);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Authorization", "Bearer " + token);

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        StringBuilder responseBody;
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                            String line;
                            StringBuilder response = new StringBuilder();
                            while ((line = reader.readLine()) != null) {
                                response.append(line);
                            }
                            messages = response.toString(); // Assign the response to the variable
                        }
                    } else {
                        // Handle the error case
                        System.out.println("HTTP GET request failed with response code: " + responseCode);
                    }

                    connection.disconnect();
                }
                catch (IOException e) {
                    e.printStackTrace();
                    System.out.println(e.toString());
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
    private void AddMessagesToServer(String username, String password,int id, String message){
        getToken(username, password);
        Thread thread = new Thread(new Runnable() {
            private StringBuilder responseBody; // Variable to hold the response body

            public StringBuilder getResponseBody() {
                return responseBody;
            }

            @Override
            public void run() {
                try {
                    URL url = new URL("http://10.0.2.2:5000/api/Chats/"+id+"/Messages"); // Replace with your API endpoint

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setDoOutput(true);

                    String requestBody = message;

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

    private void getToken(String username, String password ) {
        Thread thread = new Thread(new Runnable() {
            private StringBuilder responseBody; // Variable to hold the response body

            public StringBuilder getResponseBody() {
                return responseBody;
            }

            @Override
            public void run() {
                try {
                    URL url = new URL(Settings.getServer()+"/api/Tokens/"); // Replace with your API endpoint

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


                    String responseCode= String.valueOf(connection.getResponseCode());

                    StringBuilder responseBody;
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                        String line;

                        StringBuilder response = new StringBuilder();
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        token = String.valueOf(new StringBuilder(response.toString())); // Assign the response to the variable
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

}