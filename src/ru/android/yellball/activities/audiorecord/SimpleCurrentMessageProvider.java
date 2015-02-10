package ru.android.yellball.activities.audiorecord;

import ru.android.yellball.bo.AudioMessage;
import ru.android.yellball.managers.CurrentMessageProvider;

/**
 * Created by user on 11.02.2015.
 */
public class SimpleCurrentMessageProvider implements CurrentMessageProvider {
    private AudioMessage currentMessage;

    public void setCurrentMessage(AudioMessage currentMessage) {
        this.currentMessage = currentMessage;
    }

    @Override
    public AudioMessage getCurrentMessage() {
        return currentMessage;
    }
}
