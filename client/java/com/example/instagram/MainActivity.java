package com.example.instagram;

import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.content.Intent;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    public Communication curr_comm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void Login(View view) {
        String username, password;
        EditText usernameView = findViewById(R.id.username_text);
        username = usernameView.getText().toString();
        EditText passwordView = findViewById(R.id.password_text);
        password = passwordView.getText().toString();
        Request request = new Request(Request.actionEnum.LOGIN, username, password);
        Communication communication = new Communication(request);
        communication.start();
        try{
            communication.join();
        }
        catch (InterruptedException e){
            Log.d("communication", e.toString());
        }
        if(request.response.equals("ok")){
            this.curr_comm.isListening = false;
            Intent intent = new Intent(this, ScrollPostsActivity.class);
            intent.putExtra("post_number", 1);
            startActivity(intent);
        }
        else{
            //TODO notify error
        }
    }

    public void Register(View view){
        String username, password;
        EditText usernameView = findViewById(R.id.username_text);
        username = usernameView.getText().toString();
        EditText passwordView = findViewById(R.id.password_text);
        password = passwordView.getText().toString();
        Request request = new Request(Request.actionEnum.REGISTER, username, password);
        Communication communication = new Communication(request);
        communication.start();
        try {
            communication.join();
        }
        catch (InterruptedException e){
            Log.d("communication", e.toString());
        }
        Log.d("communication", request.response);
        if(!request.response.equals("ok")){
            //TODO notify error
        }
        else {
            //TODO ask user to login
        }
    }
}
