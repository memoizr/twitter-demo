package com.memoizrlabs.jeeter.tweetcreation.video;

import android.content.Context;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

final class CameraPreview extends SurfaceView {

    public CameraPreview(@NonNull Context context, @NonNull Camera camera) {
        super(context);
        final SurfaceHolder holder = getHolder();
        holder.addCallback(new SurfaceHolderCallback(camera));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int height = 4 * widthMeasureSpec / 3;
        super.onMeasure(widthMeasureSpec, height);
        setMeasuredDimension(widthMeasureSpec, height);
    }

    private static final class SurfaceHolderCallback implements SurfaceHolder.Callback {

        private static final int DISPLAY_ORIENTATION = 90;
        private final Camera camera;

        public SurfaceHolderCallback(@NonNull Camera camera) {
            this.camera = camera;
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                camera.setPreviewDisplay(holder);
                camera.setDisplayOrientation(DISPLAY_ORIENTATION);
                camera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
        }
    }
}
