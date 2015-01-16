package ru.android.yellball.activities.audiorecord;

import ru.android.yellball.bo.AudioMessage;

/**
 * Created by user on 13.01.2015.
 */
public interface AudioRecorderListener {
    void onGetDataChunk(byte[] data, int size, long totalRecordedMs);

    void onStopRecording(AudioMessage audioMessage);
}
