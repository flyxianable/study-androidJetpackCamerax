package com.lyb.jetpackcamerax.utils;

import android.content.Context;
import android.util.Size;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraProvider;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

public class CameraxAnalysisUtil {

    public void doImageAnlysis(Context context, ProcessCameraProvider cameraProvider,
                               CameraSelector cameraSelector, Preview preview){

        ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder()
                        // enable the following line if RGBA output is needed.
                        //.setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                        .setTargetResolution(new Size(1280, 720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(context), new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(@NonNull ImageProxy imageProxy) {
                int rotationDegrees = imageProxy.getImageInfo().getRotationDegrees();
                // insert your code here.

                // after done, release the ImageProxy object
                imageProxy.close();
            }
        });

        cameraProvider.bindToLifecycle((LifecycleOwner) context, cameraSelector, imageAnalysis, preview);
    }
}
