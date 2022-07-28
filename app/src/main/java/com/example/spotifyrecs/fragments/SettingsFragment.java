package com.example.spotifyrecs.fragments;

import android.os.Bundle;
import android.util.Log;

import androidx.preference.CheckBoxPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceFragmentCompat;

import com.example.spotifyrecs.R;

import java.util.Arrays;

import javax.annotation.Nullable;

public class SettingsFragment extends PreferenceFragmentCompat {

    public CheckBoxPreference checkbox_spotify;
    public CheckBoxPreference checkbox_collab_filter;
    public CheckBoxPreference checkbox_nn;
    public ListPreference listPreference;

    /*
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i("settinsg fragment", "in settings fragment");
    }
     */

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        Log.i("settinsg fragment", "in settings fragment 2");

        checkbox_collab_filter = findPreference("check_box_collab_filter");
        checkbox_spotify = findPreference("check_box_spotify");
        checkbox_nn = findPreference("check_box_nn");
        listPreference = findPreference("list_preference");

        assert listPreference != null;
        Log.i("here", "value is: " + listPreference.getEntry());

        /*
        listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Log.i("asdfsa", "new object: " + newValue + " and preference: " + preference);
                return false;
            }
        });
         */

        if(checkbox_collab_filter.isChecked()){
            Log.i("asdf", "i'm checked");
        }
        if(!checkbox_collab_filter.isChecked()){
            Log.i("asdf", "i'm not checked");
        }

        /*
        checkbox_collab_filter.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                checkbox_collab_filter.setChecked(!checkbox_collab_filter.isChecked());
                return true;
            }
        });
         */
    }
}