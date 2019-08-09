package com.example.instagram;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class SendFriendRequests extends AppCompatActivity {

    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_friend_requests);
        editText = findViewById(R.id.tg_id);
    }

    public void onBt(View view){
        String stID = editText.getText().toString();
        Long id = Long.parseLong(stID);
        Log.d("communication", Long.toString(id));
        Request request = new Request(Request.actionEnum.SEND_FRIEND_REQUEST, id);
        Communication communication = new Communication(request);
        communication.start();
    }
}
