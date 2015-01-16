package ru.android.yellball.utils;

import ru.android.yellball.bo.AudioMessage;

/**
 * Created by user on 08.01.2015.
 */
public class AudioMessageUtil {
    static public boolean isPersisted(AudioMessage audioMessage) {
        return (audioMessage != null && audioMessage.getId() != null);
    }

    static public boolean isRecorded(AudioMessage audioMessage) {
        return (audioMessage.getData() != null && audioMessage.getData().length > 0);
    }
}
