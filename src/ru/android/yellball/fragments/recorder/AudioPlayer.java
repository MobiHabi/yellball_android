package ru.android.yellball.fragments.recorder;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.util.Log;
import ru.android.yellball.bo.AudioMessage;
import ru.android.yellball.utils.PCMUtil;

/**
 * Created by user on 13.01.2015.
 */
public class AudioPlayer {
    static private final int STREAM_TYPE = AudioManager.STREAM_MUSIC;
    static private final int CHANNELS = AudioFormat.CHANNEL_OUT_MONO;
    static private final int MODE = AudioTrack.MODE_STREAM;

    private AudioTrack audioTrack;
    private State state = State.STOPPED;
    private PlayTask playTask;
    private volatile PlayStreamState playStreamState;

    private AudioPlayerListener audioPlayerListener;

    public AudioPlayer(AudioPlayerListener audioPlayerListener) {
        this.audioPlayerListener = audioPlayerListener;
    }

    public void setVolume(float left, float right) {
        if (audioTrack != null) {
            audioTrack.setStereoVolume(left, right);
        }
    }

    public void play(AudioMessage audioMessage, int durationStrideInMs) {
        if (state == State.PAUSED || state == State.STOPPED) {
            createAudio(durationStrideInMs);
            audioTrack.play();

            playStreamState = new PlayStreamState();
            playStreamState.audioMessage = audioMessage;
            playStreamState.durationStrideInMs = durationStrideInMs;
            playStreamState.totalPlayedBytes = 0;

            playTask = new PlayTask(playStreamState);
            playTask.execute();

            state = State.PLAYING;
        }
    }

    public void pause() {
        if (state == State.PLAYING) {
            if (playTask != null) {
                playTask.cancel(true);
                playTask = null;
            }

            state = State.PAUSED;
        }
    }

    public void resume() {
        if (state == State.PAUSED && playStreamState != null) {
            playTask = new PlayTask(playStreamState);
            playTask.execute();

            state = State.PLAYING;
        }
    }

    public void stop() {
        if (state == State.PLAYING) {
            try {
                playTask.stopPlaying();
                playTask.get();

                state = State.STOPPED;
            } catch (Exception ex) {
                Log.e("stop failure", ex.getLocalizedMessage(), ex);
            }
        }
    }

    public void release() {
        if (audioTrack != null) {
            audioTrack.release();
            audioTrack = null;
        }

        if (playTask != null) {
            playTask.cancel(true);
            playTask = null;
        }

        playStreamState = null;
        state = State.STOPPED;
    }

    private void createAudio(int durationStrideInMs) {
        release();

        long durationStrideInBytes = PCMUtil.msToBytes(durationStrideInMs);
        long bufferSize = Math.max(durationStrideInBytes, AudioTrack.getMinBufferSize(PCMUtil.RATE, CHANNELS, PCMUtil.ENCODING));

        audioTrack = new AudioTrack(STREAM_TYPE, PCMUtil.RATE, CHANNELS, PCMUtil.ENCODING, (int) bufferSize, MODE);
    }

    private class PlayTask extends AsyncTask<Void, Object, Void> {
        private volatile PlayStreamState playStreamState;
        private volatile boolean playing = false;

        public PlayTask(PlayStreamState playStreamState) {
            this.playStreamState = playStreamState;
        }

        @Override
        protected Void doInBackground(Void... params) {
            int dataPartSize = (int) PCMUtil.msToBytes(playStreamState.durationStrideInMs);

            playing = true;
            byte[] buffer = new byte[dataPartSize];
            byte[] data = playStreamState.audioMessage.getData();
            try {
                while (!isCancelled() && playing) {
                    int offset = (int) playStreamState.totalPlayedBytes;
                    if (offset >= data.length) {
                        playing = false;
                        break;
                    }

                    int bytesToWrite = Math.min(dataPartSize, data.length - offset);
                    System.arraycopy(data, offset, buffer, 0, bytesToWrite);

                    int bytesWritten = audioTrack.write(buffer, 0, bytesToWrite);
                    if (bytesWritten < 0) {
                        throw new IllegalStateException("Write returned an error code: " + bytesWritten);
                    }

                    playStreamState.totalPlayedBytes += bytesWritten;

                    publishProgress(buffer, bytesWritten);
                }
            } catch (Exception ex) {
                Log.e("audio", ex.getLocalizedMessage(), ex);
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);

            if (playing) {
                byte[] dataChunk = (byte[]) values[0];
                int size = (Integer) values[1];
                if (size > 0) {
                    audioPlayerListener.onWriteDataChunk(playStreamState.audioMessage, dataChunk, size, PCMUtil.bytesToMs(playStreamState.totalPlayedBytes));
                }
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            stop();

            audioPlayerListener.onStopPlaying(playStreamState.audioMessage);
        }

        public void stopPlaying() {
            playing = false;
        }
    }

    static private enum State {
        STOPPED, PLAYING, PAUSED
    }

    static private class PlayStreamState {
        AudioMessage audioMessage;
        int durationStrideInMs;
        long totalPlayedBytes;
    }
}
