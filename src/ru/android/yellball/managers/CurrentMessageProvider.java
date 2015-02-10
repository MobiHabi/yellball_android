package ru.android.yellball.managers;

import ru.android.yellball.bo.AudioMessage;

/**
 * Created by user on 11.02.2015.
 */
public interface CurrentMessageProvider {
    AudioMessage getCurrentMessage();
}
