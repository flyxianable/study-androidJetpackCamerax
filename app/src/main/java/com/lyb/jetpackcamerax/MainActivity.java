package com.lyb.jetpackcamerax;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
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

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageCapture imageCapture;
    private VideoCapture videoCapture;
    private Recording recording;
    private ExecutorService cameraExecutor;

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
            startCamera();
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
                startCamera();
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
                takePhoto();
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

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {

                try {
                    ProcessCameraProvider processCameraProvider = cameraProviderFuture.get();
                    bindPreview(processCameraProvider);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }


        }, ContextCompat.getMainExecutor(this));


    }

    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(viewFinder.getSurfaceProvider());

        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview);
    }

    private void takePhoto() {

        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {

                try {
                    ProcessCameraProvider processCameraProvider = cameraProviderFuture.get();
                    Preview preview = new Preview.Builder()
                            .build();

                    CameraSelector cameraSelector = new CameraSelector.Builder()
                            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                            .build();

                    ImageCapture imageCapture =
                            new ImageCapture.Builder()
                                    .setTargetRotation(viewFinder.getDisplay().getRotation())
                                    .build();

                    processCameraProvider.bindToLifecycle((LifecycleOwner) MainActivity.this, cameraSelector, imageCapture, preview, imageCapture);


                    SimpleDateFormat dateFormat = new SimpleDateFormat("", Locale.US);
                    String name = dateFormat.format(System.currentTimeMillis());

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
                    contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
                    if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                        contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image");
                    }

                    ImageCapture.OutputFileOptions outputFileOptions =
                            new ImageCapture.OutputFileOptions.Builder(getContentResolver(),
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    contentValues).build();

                    imageCapture.takePicture(outputFileOptions, cameraExecutor,
                            new ImageCapture.OnImageSavedCallback() {
                                @Override
                                public void onImageSaved(ImageCapture.OutputFileResults outputFileResults) {
                                    // insert your code here.
                                }

                                @Override
                                public void onError(ImageCaptureException error) {
                                    // insert your code here.
                                }
                            }
                    );
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }


        }, ContextCompat.getMainExecutor(this));



    }



}