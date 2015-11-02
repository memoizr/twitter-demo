package com.memoizrlabs.jeeter.tweetcreation.video;

import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.memoizrlabs.Shank;
import com.memoizrlabs.jeeter.R;
import com.memoizrlabs.jeeter.api.model.Tweet;
import com.memoizrlabs.jeeter.common.BaseActivity;
import com.memoizrlabs.jeeter.common.BasePresenter;
import com.memoizrlabs.jeeter.common.Validator;
import com.memoizrlabs.jeeter.tweetcreation.TweetCreationHelper;

import rx.Observable;
import rx.subjects.PublishSubject;

public class VideoTweetCreationActivity extends BaseActivity<VideoTweetCreationPresenter.View>
        implements VideoTweetCreationPresenter.View {

    private static final int PROCESSING_DELAY = 10000;

    private final PublishSubject<Object> activityStartedSubject = PublishSubject.create();
    private final PublishSubject<Object> activityPausedSubject = PublishSubject.create();

    private Camera camera;
    private View recordingButton;
    private EditText tweetContentEditText;
    private View tweetContentViewGroup;
    private TextView remainingTimeTextView;
    private View progress;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        super.onCreateView(savedInstanceState);
        setContentView(R.layout.activity_video_tweet_creation);

        recordingButton = findViewById(R.id.button_capture);
        tweetContentViewGroup = findViewById(R.id.viewgroup_tweet_content_container);

        final TextInputLayout textInputLayout = (TextInputLayout) findViewById(R.id.textinputlayout_tweet_content);
        tweetContentEditText = textInputLayout.getEditText();
        remainingTimeTextView = (TextView) findViewById(R.id.textview_remaining_time);
        progress = findViewById(R.id.progressbar);
    }

    @Override
    protected void onPause() {
        super.onPause();
        activityPausedSubject.onNext(new Object());
    }

    @Override
    protected void onStart() {
        super.onStart();
        activityStartedSubject.onNext(new Object());
    }

    @NonNull
    @Override
    protected BasePresenter<VideoTweetCreationPresenter.View> getPresenter() {
        return Shank.withBoundScope(VideoTweetCreationActivity.class, whenViewIsDestroyed)
                    .provide(VideoTweetCreationPresenter.class);
    }

    @NonNull
    @Override
    protected VideoTweetCreationPresenter.View getView() {
        return this;
    }

    @NonNull
    @Override
    public Observable<Object> whenStartRecording() {
        return RxView.clicks(recordingButton);
    }

    @NonNull
    @Override
    public Observable<Object> whenViewStarted() {
        return activityStartedSubject;
    }

    @NonNull
    @Override
    public Observable<Object> whenViewPaused() {
        return activityPausedSubject;
    }

    @Override
    public void showRemainingTime(long remainingSeconds) {
        remainingTimeTextView.setText(String.valueOf(remainingSeconds));
    }

    @NonNull
    @Override
    public Observable<String> showTweetTextEntryField() {
        return RxView.clicks(findViewById(R.id.button_send))
                     .map(x -> tweetContentEditText.getText()
                                                   .toString());
    }

    @NonNull
    @Override
    public VideoRecorder.CameraWrapper startCamera() {
        camera = Camera.open();

        final CameraPreview cameraPreview = new CameraPreview(getApplicationContext(), camera);
        final FrameLayout preview = (FrameLayout) findViewById(R.id.view_camera_preview);
        preview.addView(cameraPreview);
        return new VideoRecorder.CameraWrapper(camera);
    }

    @Override
    public void stopCamera() {
        if (camera != null) {
            camera.lock();
            camera.release();
            camera = null;
        }
    }

    @Override
    public void showRecordingStarted() {
        recordingButton.setVisibility(View.GONE);
        remainingTimeTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showRecordingStopped() {
        remainingTimeTextView.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
        tweetContentViewGroup.postDelayed(() -> {
            progress.setVisibility(View.GONE);
            tweetContentViewGroup.setVisibility(View.VISIBLE);
        }, PROCESSING_DELAY);
    }

    @Override
    public void showTweetInTimeline(Tweet tweet) {
        final Intent intent = new Intent();
        intent.putExtra(TweetCreationHelper.TWEET, tweet);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void showError() {
        Toast.makeText(this, R.string.generic_error, Toast.LENGTH_LONG)
             .show();
    }

    @Override
    public void showError(@NonNull Validator.ValidityState invalidState) {
        Toast.makeText(this, invalidState.getMessage(), Toast.LENGTH_LONG)
             .show();
    }
}
