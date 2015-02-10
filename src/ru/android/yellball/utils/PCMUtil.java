package ru.android.yellball.utils;

import android.media.AudioFormat;

/**
 * Created by user on 13.01.2015.
 */
public class PCMUtil {
    static public final int RATE = 44100;
    static public final int BYTES_PER_FRAME = 2;
    static public final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    static public long msToBytes(long milliseconds) {
        long result = RATE * milliseconds / 500;
        if (result % 2 == 1) --result;
        return result;
    }

    static public long bytesToMs(long bytes) {
        long result = 500 * bytes / RATE;
        return result;
    }

    static public long framesToBytes(long frames) {
        return frames * BYTES_PER_FRAME;
    }

    static public long bytesToFrames(long bytes) {
        return bytes / BYTES_PER_FRAME;
    }

    static public long framesToMs(long frames) {
        return bytesToMs(framesToBytes(frames));
    }

    static public long msToFrames(long milliseconds) {
        return bytesToFrames(msToBytes(milliseconds));
    }
}
