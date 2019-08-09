package com.example.instagram;

import android.hardware.camera2.CameraDevice;
import android.util.Log;

public class CameraStateCallBack extends CameraDevice.StateCallback {

    private String debugTag = "camera_tag";
    private CameraActivity parent;

    public CameraStateCallBack(CameraActivity parent){
        this.parent = parent;
    }

    public void onOpened(CameraDevice cameraDevice){
        Log.d(debugTag, "open camera");
        parent.onCameraStart(cameraDevice);
    }

    public void onDisconnected(CameraDevice cameraDevice){
        Log.d(debugTag, "disconnect camera");
    }

    public void onError(CameraDevice cameraDevice, int error){
        Log.d(debugTag, "error camera");
    }

}
