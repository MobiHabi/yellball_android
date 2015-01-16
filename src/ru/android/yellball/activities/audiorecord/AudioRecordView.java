package ru.android.yellball.activities.audiorecord;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.*;
import ru.android.yellball.R;
import ru.android.yellball.bo.AudioMessage;
import ru.android.yellball.utils.AudioMessageUtil;
import ru.android.yellball.utils.ContextParamsUtil;

/**
 * Created by user on 03.01.2015.
 */
public class AudioRecordView extends FrameLayout implements AudioRecorderListener, AudioPlayerListener {
    static private final int RECORD_DATA_PART_SIZE = PCMUtil.BYTES_PER_FRAME * 500;

    private AudioRecorder audioRecorder;
    private AudioPlayer audioPlayer;

    // Could be volatile, but its state will be changed ONLY in the UI thread
    private AudioRecordState state;

    private HistogramView histogramView;
    private TextSwitcher countdownSwitcher;
    private ImageSwitcher playerButton;
    private ImageView infoButton;
    private CountdownTask countdownTask;
    private View timingView;
    private ImageView timingIcon;
    private TextView timingLabel;
    private ImageView menuButton;

    private AudioMessage audioMessage;

    private AudioRecordListener audioRecordListener;

    public AudioRecordView(Context context, AudioRecordListener audioRecordListener) {
        super(context);
        this.audioRecordListener = audioRecordListener;
        construct();
    }

    public AudioRecordView(Context context, AttributeSet attrs, AudioRecordListener audioRecordListener) {
        super(context, attrs);
        this.audioRecordListener = audioRecordListener;
        construct();
    }

    public AudioRecordView(Context context, AttributeSet attrs, int defStyle, AudioRecordListener audioRecordListener) {
        super(context, attrs, defStyle);
        this.audioRecordListener = audioRecordListener;
        construct();
    }

    /**
     * Changes the current state of the activity.
     * Note: This method has side effects. For instance, calling this method with a parameter {@link ru.android.yellball.activities.audiorecord.AudioRecordState#COUNTDOWN_TO_RECORD} will make an activity to launch a countdown timer and after that start a record (if not yet recorded).
     *
     * @param state
     */
    public void setState(AudioRecordState state) {
        switch (state) {
            case COUNTDOWN_TO_RECORD:
                startCountdown();
                break;
            case PAUSED:
                pause();
                break;
            case PLAYING:
                play();
                break;
            case RECORDING:
                startRecord();
                break;
            case STOPPED:
                stop();
                break;
        }
    }

    public AudioRecordState getState() {
        return state;
    }

    /**
     * Method clears all data and closes all resources correctly.
     * Note: Can be called many times simultaneously and work correctly.
     */
    public void disposeAll() {
        disposeCountdown();
        disposeAudio();
    }

    public AudioMessage getAudioMessage() {
        return audioMessage;
    }

    public void setAudioMessage(AudioMessage audioMessage) {
        this.audioMessage = audioMessage;
    }

    @Override
    public void onGetDataChunk(byte[] data, int size, long totalRecordedMs) {
        histogramView.setBuffer(data, size);
        writeFormattedTiming(totalRecordedMs, ContextParamsUtil.getMaxRecordDurationInMs(getContext()));
    }

    @Override
    public void onStopRecording(AudioMessage audioMessage) {
        setState(AudioRecordState.STOPPED);
    }

    @Override
    public void onWriteDataChunk(AudioMessage audioMessage, byte[] data, int size, long totalPlayedMs) {
        histogramView.setBuffer(data, size);
        writeFormattedTiming(totalPlayedMs, this.audioMessage.getDuration());
    }

    @Override
    public void onStopPlaying(AudioMessage audioMessage) {
        setState(AudioRecordState.STOPPED);
    }

    private void construct() {
        inflate(getContext(), R.layout.audiorecord, this);

        audioRecorder = new AudioRecorder(this);
        audioPlayer = new AudioPlayer(this);

        histogramView = (HistogramView) findViewById(R.id.histogram);

        countdownSwitcher = (TextSwitcher) findViewById(R.id.countdown);
        countdownSwitcher.setFactory(new CountdownViewFactory());
        countdownSwitcher.setInAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));
        countdownSwitcher.setOutAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out));

        playerButton = (ImageSwitcher) findViewById(R.id.playerButton);
        playerButton.setFactory(new RecordButtonViewFactory());
        playerButton.setInAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));
        playerButton.setOutAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out));
        playerButton.setOnClickListener(new RecordButtonListener());

        infoButton = (ImageView) findViewById(R.id.infoButton);
        infoButton.setAlpha(ContextParamsUtil.getInfoButtonAlpha(getContext()));
        infoButton.setOnClickListener(new InfoButtonListener());

        View gestureView = findViewById(R.id.gestureView);
        gestureView.setOnTouchListener(new SwipeListener());

        timingView = findViewById(R.id.timingView);
        timingIcon = (ImageView) findViewById(R.id.timingIcon);
        timingLabel = (TextView) findViewById(R.id.timingLabel);

        menuButton = (ImageView) findViewById(R.id.menuButton);
        menuButton.setAlpha(ContextParamsUtil.getMenuButtonAlpha(getContext()));
        menuButton.setOnClickListener(new MenuButtonListener());

        initialize();
    }

    /**
     * Initializes an activity. Supposed to be called only once, when creating.
     */
    private void initialize() {
        if (!AudioMessageUtil.isPersisted(audioMessage)) {
            state = AudioRecordState.INITIAL;

            if (ContextParamsUtil.isCountdownOn(getContext())) {
                setState(AudioRecordState.COUNTDOWN_TO_RECORD);
            } else {
                updateControlsState();
            }
        } else {
            state = AudioRecordState.STOPPED;
            updateControlsState();
        }
    }

    /**
     * Method launches a process of counting down to start recording. If configured to support this.
     */
    private void startCountdown() {
        if (state == AudioRecordState.INITIAL) {
            if (ContextParamsUtil.isCountdownOn(getContext())) {
                int startCountdownValue = ContextParamsUtil.getCountdownStartValue(getContext());
                countdownTask = new CountdownTask();
                countdownTask.execute(startCountdownValue);

                state = AudioRecordState.COUNTDOWN_TO_RECORD;
                updateControlsState();
            }
        }
    }

    /**
     * Method pauses audio audioMessage playing.
     */
    private void pause() {
        if (state == AudioRecordState.PLAYING) {
            audioPlayer.pause();

            state = AudioRecordState.PAUSED;
            updateControlsState();
        }
    }

    /**
     * Method starts or resumes playing audio file.
     */
    private void play() {
        if ((state == AudioRecordState.PAUSED || state == AudioRecordState.STOPPED) && AudioMessageUtil.isRecorded(audioMessage)) {
            if (state == AudioRecordState.STOPPED) {
                final int PLAY_STRIDE_IN_MS = 100;
                audioPlayer.setVolume(1, 1);
                audioPlayer.play(audioMessage, PLAY_STRIDE_IN_MS);
            }
            if (state == AudioRecordState.PAUSED) {
                audioPlayer.resume();
            }

            state = AudioRecordState.PLAYING;
            updateControlsState();
        }
    }

    /**
     * Method starts recording an audio audioMessage.
     */
    private void startRecord() {
        boolean countdownOn = ContextParamsUtil.isCountdownOn(getContext());
        if (state == AudioRecordState.COUNTDOWN_TO_RECORD && countdownOn || state == AudioRecordState.INITIAL && !countdownOn) {
            countdownTask = null;

            if (audioMessage == null) audioMessage = new AudioMessage();

            int maxRecordDurationInMs = ContextParamsUtil.getMaxRecordDurationInMs(getContext());
            audioRecorder.record(audioMessage, maxRecordDurationInMs, RECORD_DATA_PART_SIZE);

            state = AudioRecordState.RECORDING;
            updateControlsState();
        }
    }

    /**
     * Method stops recording an audio audioMessage.
     */
    private void stop() {
        writeFormattedTiming(0, audioMessage.getDuration());

        state = AudioRecordState.STOPPED;
        updateControlsState();
    }

    /**
     * Method moves to a message information.
     */
    private void navigateToInfo() {
        audioRecordListener.onShowAudioMessageInfo(audioMessage);
    }

    private void showSettings() {
        // TODO
    }

    private void leave() {
        // TODO
    }

    private void disposeCountdown() {
        if (countdownTask != null) {
            countdownTask.cancel(true);
            countdownTask = null;
        }
    }

    private void disposeAudio() {
        audioRecorder.release();
        audioPlayer.release();
    }

    /**
     * Update such mutable controls states as visibility, icons, etc.
     */
    private void updateControlsState() {
        switch (state) {
            case INITIAL:
                countdownSwitcher.setVisibility(View.GONE);
                playerButton.setImageResource(R.drawable.ic_media_record);
                playerButton.setVisibility(View.VISIBLE);
                infoButton.setVisibility(View.VISIBLE);
                timingView.setVisibility(View.GONE);
                histogramView.setVisibility(View.GONE);
                menuButton.setVisibility(View.VISIBLE);
                break;
            case COUNTDOWN_TO_RECORD:
                countdownSwitcher.setVisibility(View.VISIBLE);
                playerButton.setVisibility(View.GONE);
                infoButton.setVisibility(View.GONE);
                timingView.setVisibility(View.GONE);
                histogramView.setVisibility(View.GONE);
                menuButton.setVisibility(View.GONE);
                break;
            case RECORDING:
                countdownSwitcher.setVisibility(View.GONE);
                playerButton.setImageResource(R.drawable.ic_media_stop);
                playerButton.setVisibility(View.VISIBLE);
                infoButton.setVisibility(View.GONE);
                timingView.setVisibility(View.VISIBLE);
                timingIcon.setImageResource(R.drawable.ic_media_record);
                histogramView.setVisibility(View.VISIBLE);
                menuButton.setVisibility(View.GONE);
                break;
            case STOPPED:
                countdownSwitcher.setVisibility(View.GONE);
                playerButton.setImageResource(R.drawable.ic_media_play);
                playerButton.setVisibility(View.VISIBLE);
                infoButton.setVisibility(View.VISIBLE);
                timingView.setVisibility(View.VISIBLE);
                timingIcon.setImageResource(R.drawable.ic_media_play);
                histogramView.setVisibility(View.GONE);
                menuButton.setVisibility(View.VISIBLE);
                break;
            case PLAYING:
                countdownSwitcher.setVisibility(View.GONE);
                playerButton.setImageResource(R.drawable.ic_media_pause);
                playerButton.setVisibility(View.VISIBLE);
                infoButton.setVisibility(View.GONE);
                timingView.setVisibility(View.VISIBLE);
                timingIcon.setImageResource(R.drawable.ic_media_play);
                histogramView.setVisibility(View.VISIBLE);
                menuButton.setVisibility(View.GONE);
                break;
            case PAUSED:
                countdownSwitcher.setVisibility(View.GONE);
                playerButton.setImageResource(R.drawable.ic_media_play);
                playerButton.setVisibility(View.VISIBLE);
                infoButton.setVisibility(View.VISIBLE);
                timingView.setVisibility(View.VISIBLE);
                timingIcon.setImageResource(R.drawable.ic_media_play);
                histogramView.setVisibility(View.GONE);
                menuButton.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * Writes 2 time values: current and total values. Used by recording/playing audio.
     *
     * @param millisecondsCurrent
     * @param millisecondsTotal
     */
    private void writeFormattedTiming(long millisecondsCurrent, long millisecondsTotal) {
        String formattedValue = FormatterUtil.formatDuration(millisecondsCurrent) + " / " + FormatterUtil.formatDuration(millisecondsTotal);
        timingLabel.setText(formattedValue);
    }

    /**
     * Factory, which generates views for counter down.
     */
    private class CountdownViewFactory implements ViewSwitcher.ViewFactory {
        @Override
        public View makeView() {
            int textSize = ContextParamsUtil.getCountdownTextSize(getContext());
            int textColor = ContextParamsUtil.getCountdownTextColor(getContext());

            TextView view = new TextView(getContext());
            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            view.setTextColor(textColor);
            return view;
        }
    }

    /**
     * Factory, which generates views for image change transitions of the recorder control button.
     */
    private class RecordButtonViewFactory implements ViewSwitcher.ViewFactory {
        @Override
        public View makeView() {
            float size = ContextParamsUtil.getRecordButtonSize(getContext());
            int alpha = ContextParamsUtil.getRecordButtonAlpha(getContext());

            ImageView view = new ImageView(getContext());
            view.setAlpha(alpha);
            view.setScaleType(ImageView.ScaleType.FIT_XY);
            view.setMinimumWidth((int) size);
            view.setMinimumHeight((int) size);
            return view;
        }
    }

    /**
     * Listener, which handles click on recorded control button.
     */
    private class RecordButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (state) {
                case INITIAL:
                    if (!ContextParamsUtil.isCountdownOn(getContext())) {
                        setState(AudioRecordState.RECORDING);
                    }
                    break;
                case RECORDING:
                    audioRecorder.stop();
                    break;
                case STOPPED:
                    setState(AudioRecordState.PLAYING);
                    break;
                case PLAYING:
                    setState(AudioRecordState.PAUSED);
                    break;
                case PAUSED:
                    setState(AudioRecordState.PLAYING);
                    break;
            }
        }
    }

    /**
     * Listener, which handles click on send button.
     */
    private class InfoButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            navigateToInfo();
        }
    }

    /**
     * Listener, which handles click on menu button.
     */
    private class MenuButtonListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            if (state == AudioRecordState.INITIAL || state == AudioRecordState.PAUSED || state == AudioRecordState.STOPPED) {
                PopupMenu popupMenu;
                if (AudioMessageUtil.isPersisted(audioMessage)) {
                    popupMenu = PopupMenuFactory.create(getContext(), v, R.menu.audiorecord_stopped, new AudioRecordMenuListener());
                } else if (AudioMessageUtil.isRecorded(audioMessage)) {
                    popupMenu = PopupMenuFactory.create(getContext(), v, R.menu.audiorecord_stopped_new, new AudioRecordMenuListener());
                } else {
                    popupMenu = PopupMenuFactory.create(getContext(), v, R.menu.audiorecord_no_record, new AudioRecordMenuListener());
                }
                popupMenu.show();
            }
        }
    }

    /**
     * Listener, which handles click on popup menu item.
     */
    private class AudioRecordMenuListener implements PopupMenu.OnMenuItemClickListener {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_play: // Play
                    if (AudioMessageUtil.isRecorded(audioMessage)) {
                        play();
                    }
                    return true;
                case R.id.menu_record_again: // Record again
                    if (!AudioMessageUtil.isPersisted(audioMessage)) {
                        audioMessage.setData(null); // Cancel previously recorded data
                        initialize();
                    }
                    return true;
                case R.id.menu_settings: // Settings
                    showSettings();
                    return true;
                case R.id.menu_leave: // Leave
                    leave();
                    return true;
            }

            return false;
        }
    }

    /**
     * Listener, which handles click on popup menu item.
     */
    private class AudioRecordStoppedMenuListener implements PopupMenu.OnMenuItemClickListener {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case 0: // Play
                    play();
                    return true;
                case 1: // Settings
                    showSettings();
                    return true;
                case 2: // Leave
                    leave();
                    return true;
            }

            return false;
        }
    }

    /**
     * Listener, which handles swipe up gesture.
     */
    private class SwipeListener extends OnSwipeTouchListener {
        public SwipeListener() {
            super(getContext());
        }

        @Override
        public void onSwipeLeft() {
            super.onSwipeLeft();
            if (isSwipeDetecting()) navigateToInfo();
        }

        @Override
        public void onSwipeRight() {
            super.onSwipeRight();
            if (isSwipeDetecting()) navigateToInfo();
        }

        private boolean isSwipeDetecting() {
            return state == AudioRecordState.INITIAL || state == AudioRecordState.PAUSED || state == AudioRecordState.STOPPED;
        }
    }

    /**
     * Task, which works as a timer and shows user seconds until record start
     */
    private class CountdownTask extends AsyncTask<Integer, Integer, Void> {
        @Override
        protected Void doInBackground(Integer... params) {
            int currentValue = params[0];
            try {
                while (currentValue > 0) {
                    publishProgress(currentValue);
                    --currentValue;
                    Thread.sleep(1000);
                }
            } catch (InterruptedException ex) {
                Log.e("Countdown interrupt", "Interrupted on value " + currentValue);
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            int current = values[0];
            countdownSwitcher.setText("" + current);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setState(AudioRecordState.RECORDING);
        }
    }
}
