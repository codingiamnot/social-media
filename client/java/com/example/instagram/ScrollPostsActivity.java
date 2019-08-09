package com.example.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;

import static java.lang.Math.abs;

public class ScrollPostsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private GestureDetectorCompat mDetector;
    public File postDir;
    private final int CAMERA_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll_posts);
        deleteFile("app_posts_dir");
        deleteFile("posts_dir");
        this.postDir = getDir("posts_dir", MODE_PRIVATE);

        //setting up the gesture detector
        mDetector = new GestureDetectorCompat(this, new MyGestureListener());

        //setting up the recycler view

        recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new MyAdapter(this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mDetector.onTouchEvent(motionEvent);
                return ScrollPostsActivity.super.onTouchEvent(motionEvent);
            }
        });

    }

    public boolean onTouchEvent(MotionEvent event){
        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private void startCamera(){
        Intent intent = new Intent(this, CameraActivity.class);

        //check camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);
        }
        else{
            startActivity(intent);
        }
    }

    private void friendRequests(){
        Intent intent = new Intent(this, FriendRequestsActivity.class);
        startActivity(intent);
    }

    public void  onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,@NonNull int[] grantResults){
        if(requestCode == CAMERA_REQUEST){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startCamera();
            }
            else{
                //TODO request camera permission
            }
        }
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        public boolean onDown(MotionEvent event){
            return true;
        }

        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY){
            float relX = event2.getX() - event1.getX();
            float relY = event2.getY() - event1.getY();
            if(relX > 0 && relX > abs(relY)){
                Log.d("gestures", "camera");
                startCamera();
            }
            if(relX < 0 && abs(relX) > abs(relY)){
                Log.d("gestures", "friends requests");
                friendRequests();
            }
            return true;
        }
    }

}
