package ru.android.yellball.fragments.recorder;

import ru.android.yellball.bo.AudioMessage;

/**
 * Created by user on 14.01.2015.
 */
public interface AudioPlayerListener {
    void onWriteDataChunk(AudioMessage audioMessage, byte[] data, int size, long totalPlayedMs);

    void onStopPlaying(AudioMessage audioMessage);
}
