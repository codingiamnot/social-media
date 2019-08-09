package com.example.instagram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import static java.lang.Math.abs;

public class FriendRequestsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private MyAdapterFriendRequests mAdapter;
    private GestureDetectorCompat mDetector;

    class MyGestureListenerFriendRequests extends GestureDetector.SimpleOnGestureListener {

        public boolean onDown(MotionEvent event){
            return true;
        }

        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY){
            float relX = event2.getX() - event1.getX();
            float relY = event2.getY() - event1.getY();
            if(relX > 0 && relX > abs(relY)){
                Log.d("gestures", "scroll");
                goToScroll();
            }
            if(relX < 0 && abs(relX) > abs(relY)){
                Log.d("gestures", "send friends requests");
                sendFriendRequests();
            }
            return true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_requests);


        mDetector = new GestureDetectorCompat(this, new MyGestureListenerFriendRequests());

        recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new MyAdapterFriendRequests(this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mDetector.onTouchEvent(motionEvent);
                return FriendRequestsActivity.super.onTouchEvent(motionEvent);
            }
        });
    }


    private void goToScroll(){
        this.finish();
    }

    private void sendFriendRequests(){
        Intent intent = new Intent(this, SendFriendRequests.class);
        startActivity(intent);
    }
}
