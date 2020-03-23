package com.demos.hipwedge;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    Context context=this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
        SharedPreferences sharedPreferences;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }

        @Override
        public void onResume() {
            super.onResume();
            sharedPreferences = getPreferenceManager().getSharedPreferences();

            // we want to watch the preference values' changes
            sharedPreferences.registerOnSharedPreferenceChangeListener(this);

            Map<String, ?> preferencesMap = sharedPreferences.getAll();
            // iterate through the preference entries and update their summary if they are an instance of EditTextPreference
            for (Map.Entry<String, ?> preferenceEntry : preferencesMap.entrySet()) {
                if (preferenceEntry instanceof EditTextPreference) {
                    updateSummary((EditTextPreference) preferenceEntry);
                }
            }
        }

        @Override
        public void onPause() {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
            super.onPause();
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key){
            Map<String, ?> preferencesMap = sharedPreferences.getAll();

            // get the preference that has been changed
            Object changedPreference = preferencesMap.get(key);
            // and if it's an instance of EditTextPreference class, update its summary
            if (preferencesMap.get(key) instanceof EditTextPreference) {
                updateSummary((EditTextPreference) changedPreference);
            }
        }

        private void updateSummary(EditTextPreference preference) {
            // set the EditTextPreference's summary value to its current text
            preference.setSummary(preference.getText());
        }
    }
}