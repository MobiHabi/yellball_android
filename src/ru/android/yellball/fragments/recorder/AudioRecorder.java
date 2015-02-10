package ru.android.yellball.fragments.recorder;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.util.Log;
import ru.android.yellball.bo.AudioMessage;
import ru.android.yellball.utils.PCMUtil;

import java.io.ByteArrayOutputStream;
import java.util.Date;

/**
 * Created by user on 13.01.2015.
 */
public class AudioRecorder {
    // Audio record parameters
    static private final int CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    static private final int RECORD_BUFFER_SIZE = AudioRecord.getMinBufferSize(PCMUtil.RATE, CHANNELS, PCMUtil.ENCODING);

    private AudioRecord audioRecord;
    private RecordTask recordTask;
    private AudioRecorderListener listener;

    private boolean recording;

    public AudioRecorder(AudioRecorderListener listener) {
        this.listener = listener;
    }

    public void record(AudioMessage audioMessage, int maxRecordSizeInMs, int dataPartSize) {
        if (!recording) {
            createAudioIfNeed();
            audioRecord.startRecording();

            recordTask = new RecordTask(audioMessage, maxRecordSizeInMs, dataPartSize);
            recordTask.execute();

            recording = true;
        }
    }

    public void stop() {
        try {
            recordTask.stopRecording();
            recordTask.get();
            recordTask = null;

            recording = false;
        } catch (Exception ex) {
            Log.e("stop failure", ex.getLocalizedMessage(), ex);
        }
    }

    public void release() {
        if (recordTask != null) {
            recordTask.cancel(true);
            recordTask = null;
        }

        if (audioRecord != null) {
            audioRecord.release();
            audioRecord = null;
        }
    }

    private void createAudioIfNeed() {
        if (audioRecord == null) {
            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, PCMUtil.RATE, CHANNELS, PCMUtil.ENCODING, RECORD_BUFFER_SIZE);
        }
    }


    /**
     * Task, which gets a raw data from device and draws a histogram.
     */
    private class RecordTask extends AsyncTask<Void, Object, Void> {
        private AudioMessage audioMessage;
        private int maxRecordDurationInMs;
        private int dataPartSize;

        private volatile long totalBytesCount; // This needed in both threads

        private long maxRecordDurationInBytes;
        private volatile ByteArrayOutputStream rawData = new ByteArrayOutputStream(); // This needed in both threads
        private volatile boolean recording = false; //This value is shared between UI and background threads, therefore volatile

        public RecordTask(AudioMessage audioMessage, int maxRecordDurationInMs, int dataPartSize) {
            this.audioMessage = audioMessage;
            this.maxRecordDurationInMs = maxRecordDurationInMs;
            this.dataPartSize = dataPartSize;
        }

        @Override
        protected Void doInBackground(Void... params) {
            totalBytesCount = 0;
            maxRecordDurationInBytes = PCMUtil.msToBytes(maxRecordDurationInMs);

            recording = true;
            byte[] buffer = new byte[dataPartSize];
            try {
                boolean timeout = false;
                while (!isCancelled() && recording && !timeout) {
                    int bytesRead = audioRecord.read(buffer, 0, buffer.length);
                    if (bytesRead < 0) {
                        throw new IllegalStateException("Read returned an error code: " + bytesRead);
                    }

                    if (totalBytesCount + bytesRead >= maxRecordDurationInBytes) {
                        bytesRead = (int) (maxRecordDurationInBytes - totalBytesCount);
                        timeout = true;
                    }

                    rawData.write(buffer, 0, bytesRead);

                    long durationInMs = PCMUtil.bytesToMs(totalBytesCount);
                    publishProgress(buffer, bytesRead, durationInMs);
                    totalBytesCount += bytesRead;
                }
            } catch (Exception ex) {
                Log.e("audio", ex.getLocalizedMessage(), ex);
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);

            if (recording) {
                byte[] dataChunk = (byte[]) values[0];
                int size = (Integer) values[1];
                if (size > 0) {
                    listener.onGetDataChunk(dataChunk, size, PCMUtil.bytesToMs(totalBytesCount));
                }
            }
        }

        @Override
        protected void onPostExecute(Void dummy) {
            super.onPostExecute(dummy);

            byte[] wholeData = rawData.toByteArray();
            long duration = PCMUtil.bytesToMs(totalBytesCount);
            audioMessage.setData(wholeData);
            audioMessage.setDuration(duration);
            audioMessage.setCreated(new Date());

            stop();

            listener.onStopRecording(audioMessage);
        }

        public void stopRecording() {
            recording = false;
        }
    }
}
