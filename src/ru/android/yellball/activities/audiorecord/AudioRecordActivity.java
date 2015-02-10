package ru.android.yellball.activities.audiorecord;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ViewFlipper;
import ru.android.yellball.R;
import ru.android.yellball.fragments.messages.MessagesFragment;
import ru.android.yellball.fragments.recorder.RecorderFragment;
import ru.android.yellball.fragments.settings.SettingsFragment;

/**
 * Activity, used to record and play audio messages.
 * <p/>
 * Created by user on 22.12.2014.
 */
public class AudioRecordActivity extends ActionBarActivity {
    private ViewFlipper viewFlipper;
    private AudioInfoView audioInfoView;
    private AudioRecordView audioRecordView;

    /**
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("Title");
        actionBar.setSubtitle("Sub title");
        actionBar.setLogo(R.drawable.perm_group_calendar);

        ActionBar.Tab messagesTab = actionBar.newTab().setText(R.string.messages_tab).setTabListener(new TabListener(new MessagesFragment(), "messages"));
        actionBar.addTab(messagesTab);

        ActionBar.Tab settingsTab = actionBar.newTab().setText(R.string.settings_tab).setTabListener(new TabListener(new SettingsFragment(), "settings"));
        actionBar.addTab(settingsTab);

        ActionBar.Tab recordTab = actionBar.newTab().setText("Record").setTabListener(new TabListener(new RecorderFragment(), "record"));
        actionBar.addTab(recordTab);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_action_menu, menu);
        return result;
    }

    private static class TabListener implements ActionBar.TabListener {
        private Fragment fragment;
        private String tag;
        private boolean added = false;

        public TabListener(Fragment fragment, String tag) {
            this.fragment = fragment;
            this.tag = tag;
        }

        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            if (!added) {
                fragmentTransaction.replace(android.R.id.content, fragment, tag);
                added = true;
            } else {
                fragmentTransaction.attach(fragment);
            }
        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            if (added) {
                fragmentTransaction.detach(fragment);
            }
        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

        }
    }
}
