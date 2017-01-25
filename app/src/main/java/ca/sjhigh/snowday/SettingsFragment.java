package ca.sjhigh.snowday;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by Jason on 2017-01-24.
 */

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference_settings);
    }
}
