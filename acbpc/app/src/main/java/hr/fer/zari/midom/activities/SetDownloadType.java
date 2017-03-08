package hr.fer.zari.midom.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBarActivity;

import hr.fer.zari.midom.R;

public class SetDownloadType extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String DOWNLOAD_TYPE_PREFERENCE = "downloadType";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        if (key.equals(DOWNLOAD_TYPE_PREFERENCE)) {
            Preference connectionPref = findPreference(key);
            // Set summary to be the user-description for the selected value
            connectionPref.setSummary(sharedPreferences.getString(key, ""));
        }
    }
}
