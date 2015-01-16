package ru.android.yellball.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import ru.android.yellball.R;
import ru.android.yellball.activities.audiorecord.AudioRecordActivity;

public class MainActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        View logoOverlay = findViewById(R.id.gestureOverlayView);
        logoOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AudioRecordActivity.class);
                startActivity(intent);
            }
        });
    }
}
