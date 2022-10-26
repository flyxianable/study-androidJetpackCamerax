package com.lyb.jetpackcamerax;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.core.VideoCapture;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.video.Recording;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;
import com.lyb.jetpackcamerax.utils.CameraxCaptureUtil;
import com.lyb.jetpackcamerax.utils.CameraxPreviewUtil;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

//    private ImageCapture imageCapture;
//    private VideoCapture videoCapture;
//    private Recording recording;
//    private ExecutorService cameraExecutor;

    String[] REQUIRED_PERMISSIONS = {Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO};

    private int REQUEST_CODE_PERMISSIONS = 10;

    private PreviewView viewFinder;
    private Button image_capture_button;
    private Button video_capture_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_kotlin);

        viewFinder = findViewById(R.id.viewFinder);
        image_capture_button = findViewById(R.id.image_capture_button);
        video_capture_button = findViewById(R.id.video_capture_button);

        image_capture_button.setOnClickListener(this);
        video_capture_button.setOnClickListener(this);

        if (allPermissionsGranted()) {
            CameraxPreviewUtil.startCamera(this, viewFinder);
        } else {
            ActivityCompat.requestPermissions(
                    this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                CameraxPreviewUtil.startCamera(this, viewFinder);
            } else {
                Toast.makeText(this,
                        "Permissions not granted by the user.",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch(id){
            case R.id.image_capture_button:
                CameraxCaptureUtil.takePhoto(MainActivity.this,  viewFinder, false);
                break;
            case R.id.video_capture_button:
                break;
        }
    }


    private boolean allPermissionsGranted() {
        for (String permiss : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permiss) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }








}