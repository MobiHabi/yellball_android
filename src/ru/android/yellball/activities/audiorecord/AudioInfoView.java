package ru.android.yellball.activities.audiorecord;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import ru.android.yellball.R;
import ru.android.yellball.bo.AudioMessage;
import ru.android.yellball.utils.AudioMessageUtil;

/**
 * Created by user on 08.01.2015.
 */
public class AudioInfoView extends FrameLayout {
    private AudioMessage audioMessage;

    private EditText messageTitle;
    private EditText messageDescription;
    private TextView messageDuration;
    private TextView messageAuthor;
    private TextView messageCreated;
    private TextView replyToLabel;
    private TextView messageReplyTo;
    private Button sendOrReplyButton;
    private Button backToRecordButton;

    private AudioInfoListener audioInfoListener;

    public AudioInfoView(Context context, AudioInfoListener audioInfoListener) {
        super(context);
        this.audioInfoListener = audioInfoListener;
        construct();
    }

    public AudioInfoView(Context context, AttributeSet attrs, AudioInfoListener audioInfoListener) {
        super(context, attrs);
        this.audioInfoListener = audioInfoListener;
        construct();
    }

    public AudioInfoView(Context context, AttributeSet attrs, int defStyle, AudioInfoListener audioInfoListener) {
        super(context, attrs, defStyle);
        this.audioInfoListener = audioInfoListener;
        construct();
    }

    private void construct() {
        inflate(getContext(), R.layout.audioinfo, this);

        messageTitle = (EditText) findViewById(R.id.messageTitle);
        messageDescription = (EditText) findViewById(R.id.messageDescription);
        messageDuration = (TextView) findViewById(R.id.messageDuration);
        messageAuthor = (TextView) findViewById(R.id.messageAuthor);
        replyToLabel = (TextView) findViewById(R.id.replyToLabel);
        messageCreated = (TextView) findViewById(R.id.messageCreated);
        messageReplyTo = (TextView) findViewById(R.id.replyTo);

        sendOrReplyButton = (Button) findViewById(R.id.sendOrReplyMessage);
        sendOrReplyButton.setOnClickListener(new SendOrReplyButtonListener());
        backToRecordButton = (Button) findViewById(R.id.backToRecord);
        backToRecordButton.setOnClickListener(new BackToRecordButtonListener());
    }


    public AudioMessage getAudioMessage() {
        return audioMessage;
    }

    public void setAudioMessage(AudioMessage audioMessage) {
        this.audioMessage = audioMessage;

        writeMessageData();
    }

    private void readMessageData() {
        if (!AudioMessageUtil.isPersisted(audioMessage)) {
            audioMessage.setTitle(messageTitle.getText().toString());
            audioMessage.setDescription(messageDescription.getText().toString());
        }
    }

    private void writeMessageData() {
        messageTitle.setText(audioMessage.getTitle());
        messageDescription.setText(audioMessage.getDescription());
        messageDuration.setText(audioMessage.getDuration() > 0 ? FormatterUtil.formatDuration(audioMessage.getDuration()) : "");
        messageAuthor.setText(audioMessage.getAuthor());
        messageCreated.setText(FormatterUtil.formatDate(audioMessage.getCreated()));

        replyToLabel.setVisibility(audioMessage.getReplyTo() != null ? View.VISIBLE : View.GONE);
        messageReplyTo.setVisibility(audioMessage.getReplyTo() != null ? View.VISIBLE : View.GONE);

        boolean persisted = AudioMessageUtil.isPersisted(audioMessage);
        messageTitle.setEnabled(!persisted);
        messageDescription.setEnabled(!persisted);
    }

    /**
     * Listener, which handles click on button 'Send' or 'Reply'.
     */
    private class SendOrReplyButtonListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            readMessageData();

            // TODO
        }
    }

    /**
     * Listener, which handles 'Back to record' button click.
     */
    private class BackToRecordButtonListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            readMessageData();
            audioInfoListener.onReturnToAudioRecord(audioMessage);
        }
    }
}
