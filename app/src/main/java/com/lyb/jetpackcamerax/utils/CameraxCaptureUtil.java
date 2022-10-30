package com.lyb.jetpackcamerax.utils;

import android.content.ContentValues;
import android.content.Context;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageInfo;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class CameraxCaptureUtil {


    public static void takePhoto(Context context, PreviewView viewFinder, boolean isRatation) {

        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(context);
        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {

                try {
                    // Used to bind the lifecycle of cameras to the lifecycle owner


                } catch (Exception e) {
                    e.printStackTrace();
                    Log.v("camerax_lyb", "Exception = " + e.getMessage());
                }

            }


        }, ContextCompat.getMainExecutor(context));


    }

    public static void doCamera(Context context, PreviewView viewFinder, boolean isRatation) {
        ProcessCameraProvider processCameraProvider = null;
        try {
            ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(context);
            processCameraProvider = cameraProviderFuture.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Preview preview = new Preview.Builder()
                .build();

        ImageCapture imageCapture =
                new ImageCapture.Builder().build();
        CameraSelector cameraSelector = null;
        if (isRatation) {
            cameraSelector = new CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build();

            imageCapture.setTargetRotation(viewFinder.getDisplay().getRotation());
        } else {
            cameraSelector = new CameraSelector.Builder().build();

        }


        ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder()
                        // enable the following line if RGBA output is needed.
                        //.setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                        .setTargetResolution(new Size(1280, 720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

        processCameraProvider.unbindAll();
        processCameraProvider.bindToLifecycle((LifecycleOwner) context, cameraSelector, imageAnalysis, preview, imageCapture);


        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String name = dateFormat.format(System.currentTimeMillis()) + ".jpg";

        Log.v("camerax_lyb", "fileName =  " + name);

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image");
        }

        ImageCapture.OutputFileOptions outputFileOptions =
                new ImageCapture.OutputFileOptions.Builder(context.getContentResolver(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        contentValues).build();


        File file = new File(context.getExternalCacheDir().getAbsolutePath() + name);

        ImageCapture.OutputFileOptions options = new ImageCapture.OutputFileOptions.Builder(file).build();

        imageCapture.takePicture(options, ContextCompat.getMainExecutor(context),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(ImageCapture.OutputFileResults outputFileResults) {
                        // insert your code here.
                        if (outputFileResults != null) {
                            String saveUrl = outputFileResults.getSavedUri().getPath();
                            Log.i("camerax_lyb", "saveUrl = " + saveUrl);
                        }
                        Log.v("camerax_lyb", "onImageSaved ");

                    }

                    @Override
                    public void onError(ImageCaptureException error) {
                        // insert your code here.
                        Log.i("camerax_lyb", "onError = " + error.getMessage());
                    }


                }
        );

//                    imageCapture.takePicture(Executors.newSingleThreadExecutor(), new ImageCapture.OnImageCapturedCallback() {
//                        @Override
//                        public void onCaptureSuccess(@NonNull ImageProxy image) {
//                            super.onCaptureSuccess(image);
//                            Log.i("camerax_lyb", "onCaptureSuccess " );
//                            if(image != null) {
//                                ImageInfo imageInfo = image.getImageInfo();
//                                if(imageInfo != null) {
//                                    Log.i("camerax_lyb", "onCaptureSuccess = " + imageInfo.getRotationDegrees());
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onError(@NonNull ImageCaptureException exception) {
//                            super.onError(exception);
//                        }
//                    });
    }


}
