package com.example.chat;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ChatActivity extends AppCompatActivity {

    private String token;
    private String messages;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
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