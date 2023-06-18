package com.example.chat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;
import java.util.concurrent.atomic.AtomicInteger;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class LoginActivity extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText passwordEditText;
    private String token;
    private User logged;
    private UserDao userDao;
    private AppDB db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);

        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickLogin();
            }
        });

        TextView link = findViewById(R.id.linkToRegister);
        link.setOnClickListener(view-> {
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
        });


    }

    private void clickLogin() {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        AtomicInteger responseCode = new AtomicInteger();
        Thread thread = new Thread(new Runnable() {
            private StringBuilder responseBody; // Variable to hold the response body

            public StringBuilder getResponseBody() {
                return responseBody;
            }

            @Override
            public void run() {
                try {
                    URL url = new URL("http://10.0.2.2:5000/api/Tokens/"); // Replace with your API endpoint

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
                    token=responseBody.toString();

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

            Intent intent = new Intent(this, ContactsActivity.class);
            //String[] arr = new String[] {username, password, token};
            getLogged(username, password);
            getChats();
            getMessages();
            saveLogged(logged);
            intent.putExtra("username", username);
            startActivity(intent);
        }
        else{
            Toast.makeText(getApplicationContext(), "Wrong username or password", Toast.LENGTH_LONG).show();
        }
    }


    private void getLogged(String username, String password) {
        AtomicInteger responseCode = new AtomicInteger();
        final StringBuilder[] responseBody = {new StringBuilder()};
        Thread thread = new Thread(new Runnable() {
             // Variable to hold the response body


            @Override
            public void run() {
                try {
                    URL url = new URL("http://10.0.2.2:5000/api/Users/" + username); // Replace with your API endpoint

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
            UserJson userJson = gson.fromJson(responseBody[0].toString(), UserJson.class);
            logged = new User(username, password, userJson.getDisplayName());
            logged.setProfilePic(userJson.getProfilePic());
            //Toast.makeText(getApplicationContext(), responseBody[0].toString(), Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(getApplicationContext(), "Wrong getLogged", Toast.LENGTH_LONG).show();
        }
    }

    private void getChats() {
        AtomicInteger responseCode = new AtomicInteger();
        final StringBuilder[] responseBody = {new StringBuilder()};
        Thread thread = new Thread(new Runnable() {
            // Variable to hold the response body


            @Override
            public void run() {
                try {
                    URL url = new URL("http://10.0.2.2:5000/api/Chats");

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

            Type arrayListType = new TypeToken<ArrayList<Chat>>() {}.getType();

            ArrayList<Chat> chats = gson.fromJson(responseBody[0].toString(), arrayListType);

            logged.setChats(chats);

        }
        else{
            Toast.makeText(getApplicationContext(), "Wrong getChats", Toast.LENGTH_LONG).show();
        }
    }

    private void getMessages() {
        ArrayList<Message> msgs = new ArrayList<>();
        ArrayList<Chat> chats = logged.getChats();
        for(Chat cht: chats) {
            getMessagesPerChat(msgs, cht.getId());
        }
        logged.setMessages(msgs);
    }

    private void getMessagesPerChat(ArrayList<Message> msgs, int chat) {
        AtomicInteger responseCode = new AtomicInteger();
        final StringBuilder[] responseBody = {new StringBuilder()};
        Thread thread = new Thread(new Runnable() {
            // Variable to hold the response body


            @Override
            public void run() {
                try {
                    URL url = new URL("http://10.0.2.2:5000/api/Chats/" + chat + "/Messages"); // Replace with your API endpoint

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
                m.setChatId(chat);
            }
            msgs.addAll(msg);

        }
        else{
            Toast.makeText(getApplicationContext(), "Wrong Messages", Toast.LENGTH_LONG).show();
        }
    }

    private void saveLogged(User user) {
        db = Room.databaseBuilder(getApplicationContext(), AppDB.class, "users")
                .allowMainThreadQueries().build();
        userDao = db.UserDao();
        userDao.deleteAll();
        userDao.insert(logged);
    }
}
