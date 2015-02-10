package ru.android.yellball.managers;

import ru.android.yellball.bo.AudioMessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by user on 01.02.2015.
 */
public class AudioMessageManager {
    static public List<AudioMessage> getMessages() {
        List<AudioMessage> result = new ArrayList<>(); // TODO

        AudioMessage message1 = new AudioMessage();
        message1.setTitle("title 1");
        message1.setAuthor("shdlhfsld");
        message1.setDuration(1231234);
        message1.setCreated(new Date(13131));
        result.add(message1);

        AudioMessage message2 = new AudioMessage();
        message2.setTitle("title 2");
        message2.setAuthor("cvxv");
        message2.setDuration(23000000);
        message2.setCreated(new Date());
        result.add(message2);

        AudioMessage message3 = new AudioMessage();
        message3.setTitle("title 3");
        message3.setAuthor("shdlbdfgfdhfsld");
        message3.setDuration(456346000);
        message3.setCreated(new Date(423423552));
        result.add(message3);

        AudioMessage message4 = new AudioMessage();
        message4.setTitle("title 4");
        message4.setAuthor("shdlhf    sld");
        message4.setDuration(444444444);
        message4.setCreated(new Date(1003131));
        result.add(message4);

        AudioMessage message5 = new AudioMessage();
        message5.setTitle("title 5");
        message5.setAuthor("shdlhfguksaOPpoOP  fsld");
        message5.setDuration(33344444);
        message5.setCreated(new Date(13131009));
        result.add(message5);

        AudioMessage message6 = new AudioMessage();
        message6.setTitle("title 6");
        message6.setAuthor("тирячсилоялми");
        message6.setDuration(4345);
        message6.setCreated(new Date(13445131));
        result.add(message6);

        return result;
    }

    static public List<AudioMessage> getReplies(AudioMessage message) {
        return new ArrayList<>(); // TODO
    }
}
