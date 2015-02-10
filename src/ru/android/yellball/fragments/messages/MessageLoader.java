package ru.android.yellball.fragments.messages;

import android.content.AsyncTaskLoader;
import android.content.Context;
import ru.android.yellball.bo.AudioMessage;
import ru.android.yellball.managers.AudioMessageManager;

import java.util.List;

/**
 * Created by user on 01.02.2015.
 */
public class MessageLoader extends AsyncTaskLoader<List<AudioMessage>> {
    public MessageLoader(Context context) {
        super(context);
    }

    @Override
    public List<AudioMessage> loadInBackground() {
        List<AudioMessage> result = AudioMessageManager.getMessages();
        return result;
    }
}
