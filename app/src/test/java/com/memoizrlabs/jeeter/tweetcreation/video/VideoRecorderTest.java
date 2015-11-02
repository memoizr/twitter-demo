package com.memoizrlabs.jeeter.tweetcreation.video;

import android.hardware.Camera;
import android.media.MediaRecorder;

import com.memoizrlabs.Shank;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class VideoRecorderTest {

    @Mock private Camera camera;
    @Mock private MediaRecorder mediaRecorder;

    private VideoRecorder videoRecorder;
    private VideoRecorder.CameraWrapper cameraWrapper;

    @Before
    public void before() {
        cameraWrapper = new VideoRecorder.CameraWrapper(camera);
        videoRecorder = new VideoRecorder("/mnt/sdcard");

        Shank.clearAll();
        Shank.registerFactory(MediaRecorder.class, () -> mediaRecorder);
    }

    @Test
    public void when_recordingStarts_then_startRecording() throws Exception {
        videoRecorder.startRecording(cameraWrapper);
        verify(mediaRecorder).start();
    }

    @Test
    public void when_recordingStartsAndStops_then_stopRecording() throws Exception {
        videoRecorder.startRecording(cameraWrapper);
        videoRecorder.stopRecording();
        verify(mediaRecorder).stop();
    }
}