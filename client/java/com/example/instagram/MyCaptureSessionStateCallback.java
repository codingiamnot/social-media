package com.example.instagram;

import android.hardware.camera2.CameraCaptureSession;
import android.util.Log;

public class MyCaptureSessionStateCallback extends CameraCaptureSession.StateCallback {

    private final String debugTag = "camera_tag";
    private CameraActivity parent;

    public MyCaptureSessionStateCallback(CameraActivity parent){
        this.parent = parent;
    }

    public void onActive(CameraCaptureSession cameraCaptureSession){
        Log.d(debugTag, "cappture session active");
    }

    public void onConfigured(CameraCaptureSession cameraCaptureSession){
        Log.d(debugTag, "cappture session configured");
    }

    public void onConfigureFailed(CameraCaptureSession cameraCaptureSession){
        Log.d(debugTag, "cappture session configure failed");
    }

    public void onReady(CameraCaptureSession cameraCaptureSession){
        Log.d(debugTag, "capture session ready");
        parent.onCaptureSessionStart(cameraCaptureSession);
    }
}
