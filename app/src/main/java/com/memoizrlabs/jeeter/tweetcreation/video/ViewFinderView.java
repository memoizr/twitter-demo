package com.memoizrlabs.jeeter.tweetcreation.video;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public final class ViewFinderView extends FrameLayout {

    public ViewFinderView(Context context) {
        super(context);
    }

    public ViewFinderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ViewFinderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
