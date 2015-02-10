package ru.android.yellball.fragments.settings;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import ru.android.yellball.R;
import ru.android.yellball.bo.Settings;
import ru.android.yellball.managers.SettingsManager;

/**
 * This class provides to user an access to customizable settings.
 * <p/>
 * Created by user on 25.01.2015.
 */
public class SettingsFragment extends Fragment {
    private Settings settings;

    private Switch showCountdown;

    /**
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View settingsView = inflater.inflate(R.layout.settings, container, false);

        showCountdown = (Switch) settingsView.findViewById(R.id.showCountdown);
        showCountdown.setOnCheckedChangeListener(new ShowCountdownChangeListener());

        return settingsView;
    }

    /**
     * @see android.support.v4.app.Fragment#onStart()
     */
    @Override
    public void onStart() {
        super.onStart();

        // Refresh settings
        settings = SettingsManager.readSettings(getActivity());
        initControlValues();
    }

    private void initControlValues() {
        showCountdown.setChecked(settings.isShowCountdown());
    }

    private class ShowCountdownChangeListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            settings.setShowCountdown(isChecked);
            SettingsManager.writeSettings(getActivity(), settings);
        }
    }
}
