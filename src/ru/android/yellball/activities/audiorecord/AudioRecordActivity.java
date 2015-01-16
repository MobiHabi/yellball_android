package ru.android.yellball.activities.audiorecord;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;
import ru.android.yellball.bo.AudioMessage;

/**
 * Activity, used to record and play audio messages.
 * <p/>
 * Created by user on 22.12.2014.
 */
public class AudioRecordActivity extends Activity implements AudioRecordListener, AudioInfoListener {
//    // Parameter keys for passing audio message data
//    static public final String RECORDED_MEDIA_DATA_PARAMETER_KEY = "recordedMediaData";
//    static public final String RECORDED_MEDIA_TITLE_PARAMETER_KEY = "recordedMediaTitle";
//    static public final String RECORDED_MEDIA_DESCRIPTION_PARAMETER_KEY = "recordedMediaDescr";

    private ViewFlipper viewFlipper;
    private AudioInfoView audioInfoView;
    private AudioRecordView audioRecordView;

    /**
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (savedInstanceState != null) {
//            message = new AudioMessage();
//            message.setData(savedInstanceState.getByteArray(RECORDED_MEDIA_DATA_PARAMETER_KEY));
//            message.setTitle(savedInstanceState.getString(RECORDED_MEDIA_TITLE_PARAMETER_KEY));
//            message.setDescription(savedInstanceState.getString(RECORDED_MEDIA_DESCRIPTION_PARAMETER_KEY));
//        }
//        initiallyEmpty = (message == null || message.getData() == null || message.getTitle() == null);

        audioInfoView = new AudioInfoView(this, this);
        audioInfoView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
        audioInfoView.setAudioMessage(new AudioMessage());

        audioRecordView = new AudioRecordView(this, this);
        audioRecordView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));

        viewFlipper = new ViewFlipper(this);
        viewFlipper.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
        viewFlipper.setAutoStart(false);
        viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left));
        viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right));
        viewFlipper.addView(audioInfoView);
        viewFlipper.addView(audioRecordView);
        viewFlipper.setDisplayedChild(0);

        setContentView(viewFlipper);
    }

    /**
     * @see android.app.Activity#onStop()
     */
    @Override
    protected void onStop() {
        super.onStop();
        disposeAll();
    }

    /**
     * @see android.app.Activity#onBackPressed()
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        disposeAll();
    }

    /**
     * Method clears all data and closes all resources correctly.
     * Note: Can be called many times simultaneously and work correctly.
     */
    private void disposeAll() {
        audioRecordView.disposeAll();
    }

    @Override
    public void onShowAudioMessageInfo(AudioMessage audioMessage) {
        audioInfoView.setAudioMessage(audioMessage);
        viewFlipper.showPrevious();
    }

    @Override
    public void onReturnToAudioRecord(AudioMessage audioMessage) {
        audioRecordView.setAudioMessage(audioMessage);
        viewFlipper.showNext();
    }
}
