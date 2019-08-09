package com.example.instagram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import android.content.Intent;
import android.graphics.ImageFormat;
import android.hardware.camera2.*;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

public class CameraActivity extends AppCompatActivity {

    private SurfaceView surfaceView;
    private ImageReader imageReader;
    private List<Surface> outputs = new ArrayList<>();
    private CameraManager cameraManager;
    private CameraDevice cameraDevice;
    private CameraCaptureSession captureSession;
    private boolean takingPhoto = false;
    private static String debugTag = "camera_tag";
    private GestureDetectorCompat mDetector;

    protected ImageReader.OnImageAvailableListener imageReadyCallback = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader imageReader) {
            Image image;
            image = imageReader.acquireLatestImage();
            if(takingPhoto){
                processImage(image);
                takingPhoto = false;
            }
            image.close();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        surfaceView = findViewById(R.id.surfaceView);

        //camera setup
        cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
        try {
            cameraManager.openCamera("0", new CameraStateCallBack(this), null);
        }
        catch (SecurityException e){
            Log.d(debugTag, e.toString());
            goToScroll();
        }
        catch (CameraAccessException e){
            Log.d(debugTag, e.toString());
            goToScroll();
        }

        //gesture setup
        mDetector = new GestureDetectorCompat(this, new MyGestureListener());
        surfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.d("gestures", "touch");
                mDetector.onTouchEvent(motionEvent);
                return true;
            }
        });
    }

    private void goToScroll(){
        this.finish();
    }

    private CaptureRequest createCaptureRequest(){
        try {
            CaptureRequest.Builder builder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            builder.addTarget(imageReader.getSurface());
            builder.addTarget(surfaceView.getHolder().getSurface());
            return builder.build();
        }
        catch (CameraAccessException e) {
            Log.e(debugTag, e.getMessage());
            return null;
        }
    }

    private void processImage(Image image){
        Log.d(debugTag, "processing image");
        ByteBuffer buffer;
        byte[] bytes;
        if(image.getFormat() == ImageFormat.JPEG){
            buffer = image.getPlanes()[0].getBuffer();
            bytes = new byte[buffer.remaining()]; // makes byte array large enough to hold image
            buffer.get(bytes);
            Log.d(debugTag, "sending img");
            Request request = new Request(Request.actionEnum.POST, "", "jpeg", bytes);
            Communication communication = new Communication(request);
            communication.start();
        }
        image.close();
    }

    public void onCameraStart(CameraDevice device){
        cameraDevice = device;
        imageReader = ImageReader.newInstance(1920, 1080, ImageFormat.JPEG, 2 /* images buffered*/);
        imageReader.setOnImageAvailableListener(imageReadyCallback, null);
        outputs.add(surfaceView.getHolder().getSurface());
        outputs.add(imageReader.getSurface());
        try {
             cameraDevice.createCaptureSession(outputs, new MyCaptureSessionStateCallback(this), null);
        }
        catch (CameraAccessException e){
            Log.d(debugTag, e.toString());
            goToScroll();
        }
    }

    public void onCaptureSessionStart(CameraCaptureSession cameraCaptureSession){
        captureSession = cameraCaptureSession;
        try {
            CaptureRequest captureRequest = createCaptureRequest();
            if(captureRequest == null) {
                Log.d(debugTag, "no request");
                goToScroll();
            }
            else {
                Log.d(debugTag, "request");
                captureSession.setRepeatingRequest(captureRequest, null, null);
            }
        }
        catch (CameraAccessException e){
            Log.d(debugTag, e.toString());
            goToScroll();
        }
    }

    public void takePhoto(View view){
        takingPhoto = true;
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        public boolean onDown(MotionEvent event){
            return true;
        }

        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY){
            Log.d("gestures", "fling");
            float relX = event2.getX() - event1.getX();
            float relY = event2.getY() - event1.getY();
            if(relX < 0 && abs(relX) > abs(relY)){
                Log.d("gestures", "scroll");
                goToScroll();
            }
            return true;
        }
    }

}
