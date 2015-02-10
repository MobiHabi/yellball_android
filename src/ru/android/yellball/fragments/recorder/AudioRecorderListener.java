package ru.android.yellball.fragments.recorder;

import ru.android.yellball.bo.AudioMessage;

/**
 * Created by user on 13.01.2015.
 */
public interface AudioRecorderListener {
    void onGetDataChunk(byte[] data, int size, long totalRecordedMs);

    void onStopRecording(AudioMessage audioMessage);
}
