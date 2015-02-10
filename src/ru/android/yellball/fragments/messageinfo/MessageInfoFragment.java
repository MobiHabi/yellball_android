package ru.android.yellball.fragments.messageinfo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import ru.android.yellball.R;
import ru.android.yellball.bo.AudioMessage;
import ru.android.yellball.managers.CurrentMessageProvider;
import ru.android.yellball.utils.AudioMessageUtil;
import ru.android.yellball.utils.FormatterUtil;

/**
 * Created by user on 11.02.2015.
 */
public class MessageInfoFragment extends Fragment {
    private EditText messageTitle;
    private EditText messageDescription;
    private TextView messageDuration;
    private TextView messageAuthor;
    private TextView messageCreated;
    private TextView replyToLabel;
    private TextView messageReplyTo;
    private Button sendOrReplyButton;
    private Button backToRecordButton;

    private CurrentMessageProvider currentMessageProvider;

    public MessageInfoFragment(CurrentMessageProvider currentMessageProvider) {
        this.currentMessageProvider = currentMessageProvider;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.audioinfo, container, false);

        messageTitle = (EditText) view.findViewById(R.id.messageTitle);
        messageDescription = (EditText) view.findViewById(R.id.messageDescription);
        messageDuration = (TextView) view.findViewById(R.id.messageDuration);
        messageAuthor = (TextView) view.findViewById(R.id.messageAuthor);
        replyToLabel = (TextView) view.findViewById(R.id.replyToLabel);
        messageCreated = (TextView) view.findViewById(R.id.messageCreated);
        messageReplyTo = (TextView) view.findViewById(R.id.replyTo);

        sendOrReplyButton = (Button) view.findViewById(R.id.sendOrReplyMessage);
        sendOrReplyButton.setOnClickListener(new SendOrReplyButtonListener());
        backToRecordButton = (Button) view.findViewById(R.id.backToRecord);
        backToRecordButton.setOnClickListener(new BackToRecordButtonListener());

        return view;
    }

    private void readMessageData() {
        AudioMessage audioMessage = currentMessageProvider.getCurrentMessage();
        if (!AudioMessageUtil.isPersisted(audioMessage)) {
            audioMessage.setTitle(messageTitle.getText().toString());
            audioMessage.setDescription(messageDescription.getText().toString());
        }
    }

    private void writeMessageData() {
        AudioMessage audioMessage = currentMessageProvider.getCurrentMessage();

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
    private class SendOrReplyButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            readMessageData();

            // TODO
        }
    }

    /**
     * Listener, which handles 'Back to record' button click.
     */
    private class BackToRecordButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            readMessageData();// TODO
//            audioInfoListener.onReturnToAudioRecord(audioMessage);
        }
    }
}
