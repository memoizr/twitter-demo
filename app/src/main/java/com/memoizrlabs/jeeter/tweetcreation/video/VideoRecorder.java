package com.memoizrlabs.jeeter.tweetcreation.video;

import android.hardware.Camera;
import android.media.MediaRecorder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.memoizrlabs.Shank;

import java.io.IOException;

final class VideoRecorder {

    private static final String TEMP_FILE_NAME = "/temp_video_raw.mp4";

    private static final int VIDEO_FRAME_RATE = 15;
    private static final int WIDTH = 640;
    private static final int HEIGHT = 480;
    private static final int BIT_RATE = 512 * 1000;
    private static final int VIDEO_ENCODING_BIT_RATE = BIT_RATE;
    private static final int CAMERA_ORIENTATION_DEGREES = 90;
    private static final int AUDIO_ENCODING_BIT_RATE = 32000;
    private final String cacheDir;

    private MediaRecorder mediaRecorder;

    public VideoRecorder(@NonNull String cacheDir) {
        this.cacheDir = cacheDir;
    }

    @NonNull
    String getVideoPath() {
        return getTempPath();
    }

    @NonNull
    private String getTempPath() {
        return cacheDir + TEMP_FILE_NAME;
    }

    void startRecording(@NonNull CameraWrapper cameraWrapper) {
        prepareRecorder(cameraWrapper.getCamera());
        mediaRecorder.start();
    }

    private void prepareRecorder(@Nullable Camera camera) {
        if (mediaRecorder == null) {
            mediaRecorder = Shank.provide(MediaRecorder.class);
        }

        if (camera != null) {
            camera.unlock();
            initializeRecorder(camera);
            try {
                mediaRecorder.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void initializeRecorder(@Nullable Camera camera) {
        mediaRecorder.setCamera(camera);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
        mediaRecorder.setOrientationHint(CAMERA_ORIENTATION_DEGREES);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setVideoEncodingBitRate(VIDEO_ENCODING_BIT_RATE);
        mediaRecorder.setAudioEncodingBitRate(AUDIO_ENCODING_BIT_RATE);
        mediaRecorder.setVideoFrameRate(VIDEO_FRAME_RATE);
        mediaRecorder.setVideoSize(WIDTH, HEIGHT);
        mediaRecorder.setOutputFile(getTempPath());
    }

    void stopRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            releaseRecorder();
        }
    }

    private void releaseRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    public static class CameraWrapper {

        private final Camera camera;

        public CameraWrapper(@Nullable Camera camera) {
            this.camera = camera;
        }

        @Nullable
        public Camera getCamera() {
            return camera;
        }
    }

    public static final class VideoType {

        public static final String VIDEO_MP4 = "video/mp4";
    }
}
