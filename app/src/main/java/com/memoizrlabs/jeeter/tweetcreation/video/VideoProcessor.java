package com.memoizrlabs.jeeter.tweetcreation.video;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import retrofit.mime.TypedFile;

final class VideoProcessor {

    private static final String TEMP_FILE_NAME = "/temp_video_edited.mp4";
    private static final String TAG = VideoProcessor.class.getCanonicalName();

    private final String cacheDir;

    public VideoProcessor(@NonNull String cacheDir) {
        this.cacheDir = cacheDir;
    }

    @NonNull
    public String getFileSize() {
        return String.valueOf(getFile().length());
    }

    @NonNull
    private File getFile() {
        return new File(getTempPath());
    }

    @NonNull
    private String getTempPath() {
        return cacheDir + TEMP_FILE_NAME;
    }

    @NonNull
    public TypedFile getTypedFile() {
        return new TypedFile(VideoRecorder.VideoType.VIDEO_MP4, getFile());
    }

    void processVideo(@NonNull String inputPath) {
        try {
            final String executable = cacheDir + "/ffmpeg";

            final String[] command = {executable,
                    "-i", inputPath,
                    "-y",
                    "-strict", "-2",
                    "-b:a", "32k",
                    "-vf", "crop=480:480:0:0",
                    "-r", "15",
                    getTempPath()};

            final ProcessBuilder processBuilder = new ProcessBuilder(command);

            processBuilder.redirectErrorStream(true);

            final Process process = processBuilder.start();
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                Log.d(TAG, line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
