package com.memoizrlabs.jeeter.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.memoizrlabs.jeeter.R;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class FileUtil {

    private static final int IO_BUFFER_SIZE = 4096;

    private FileUtil() {
    }

    public static void installFfmpeg(@NonNull Context context, @NonNull String cachedir) {
        final File ffmpegFile = new File(cachedir, "ffmpeg");
        final String mFfmpegInstallPath = ffmpegFile.toString();
        Logger.d("ffmpeg install path: " + mFfmpegInstallPath);

        if (!ffmpegFile.exists()) {
            try {
                ffmpegFile.createNewFile();
            } catch (IOException e) {
                Logger.d("Failed to create new file!", e);
            }
            FileUtil.installBinaryFromRaw(context, R.raw.ffmpeg, ffmpegFile);
        } else {
            Logger.d("It was already installed");
        }

        ffmpegFile.setExecutable(true);
        FileUtil.doChmod(ffmpegFile, 777);
    }

    public static void installBinaryFromRaw(@NonNull Context context, int resId, @NonNull File file) {
        final InputStream rawStream = context.getResources()
                                             .openRawResource(resId);

        final OutputStream binStream = getFileOutputStream(file);

        if (rawStream != null && binStream != null) {
            pipeStreams(rawStream, binStream);

            try {
                rawStream.close();
                binStream.close();
            } catch (IOException e) {
                Logger.d("Failed to close streams!", e);
            }

            doChmod(file, 777);
        }
    }

    public static void doChmod(@NonNull File file, int chmodValue) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("chmod");
        stringBuilder.append(' ');
        stringBuilder.append(chmodValue);
        stringBuilder.append(' ');
        stringBuilder.append(file.getAbsolutePath());

        try {
            Runtime.getRuntime()
                   .exec(stringBuilder.toString());
        } catch (IOException e) {
            Logger.d("Error performing chmod", e);
        }
    }

    @Nullable
    public static OutputStream getFileOutputStream(@NonNull File file) {
        try {
            return new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            Logger.d("File not found.", e);
        }
        return null;
    }

    public static void pipeStreams(@NonNull InputStream inputStream, @NonNull OutputStream outputStream) {
        final byte[] buffer = new byte[IO_BUFFER_SIZE];
        int count;

        try {
            while ((count = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, count);
            }
        } catch (IOException e) {
            Logger.d("Error writing stream.", e);
        }
    }
}
