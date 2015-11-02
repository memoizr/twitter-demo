package com.memoizrlabs.jeeter.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

public final class ActivityStarter {

    private ActivityStarter() {
    }

    @NonNull
    public static Builder with(@NonNull Context context) {
        return new Builder(context);
    }

    public static final class Builder {

        private final Context context;
        private int result;

        public Builder(@NonNull Context context) {
            this.context = context;
        }

        @NonNull
        public Builder forResult(@IntRange(from = 1) int result) {
            this.result = result;
            return this;
        }

        public void start(@NonNull Class<?> activityClass) {
            final Intent intent = createIntent(context, activityClass);
            if (context instanceof Activity) {
                if (result != 0) {
                    ActivityCompat.startActivityForResult((Activity) context,
                                                          intent,
                                                          result,
                                                          Bundle.EMPTY);
                } else {
                    ActivityCompat.startActivity((Activity) context, intent, Bundle.EMPTY);
                }
            } else {
                context.startActivity(intent);
            }
        }

        @NonNull
        private static Intent createIntent(@NonNull Context context,
                                           @NonNull Class<?> activityClass) {
            return new Intent(context, activityClass);
        }
    }
}
