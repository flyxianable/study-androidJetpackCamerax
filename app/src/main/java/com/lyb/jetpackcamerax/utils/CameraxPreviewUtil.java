package com.lyb.jetpackcamerax.utils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

public class CameraxPreviewUtil {

    /**
     * 实现预览
     */
    public static void startCamera(Context context, PreviewView viewFinder) {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(context);
        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {

                try {
                    ProcessCameraProvider processCameraProvider = cameraProviderFuture.get();
                    bindPreview(context, processCameraProvider, viewFinder);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }


        }, ContextCompat.getMainExecutor(context));

    }

    private static void bindPreview(Context context, @NonNull ProcessCameraProvider cameraProvider,  PreviewView viewFinder) {
        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(viewFinder.getSurfaceProvider());

        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner) context, cameraSelector, preview);
    }
}
