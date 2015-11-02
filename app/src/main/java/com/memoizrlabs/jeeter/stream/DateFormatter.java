package com.memoizrlabs.jeeter.stream;

import android.support.annotation.NonNull;
import android.text.format.DateUtils;

import com.orhanobut.logger.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

final class DateFormatter {

    private static final String TWITTER_DATE_FORMAT = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";

    private DateFormatter() {
    }

    public static String parseDate(@NonNull String formattedDate) {
        try {
            final long timeThen = getTwitterDate(formattedDate).getTime();
            final long timeNow = System.currentTimeMillis();
            return DateUtils.getRelativeTimeSpanString(timeThen, timeNow, DateUtils.MINUTE_IN_MILLIS)
                            .toString();
        } catch (ParseException e) {
            Logger.d("Could not parse date: " + formattedDate, e);
            return "";
        }
    }

    private static Date getTwitterDate(@NonNull String date) throws ParseException {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TWITTER_DATE_FORMAT);
        simpleDateFormat.setLenient(true);
        return simpleDateFormat.parse(date);
    }
}
