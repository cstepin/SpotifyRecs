package com.example.spotifyrecs.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.preference.CheckBoxPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceFragmentCompat;

import com.example.spotifyrecs.R;
import com.parse.ParseUser;

import java.util.Arrays;

import javax.annotation.Nullable;

public class SettingsFragment extends PreferenceFragmentCompat {

    public CheckBoxPreference checkbox_spotify;
    public CheckBoxPreference checkbox_collab_filter;
    public CheckBoxPreference checkbox_nn;
    public ListPreference listPreference;

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

        checkbox_spotify.setChecked(ParseUser.getCurrentUser().getBoolean("checkedSpotify"));

        checkbox_nn.setChecked(ParseUser.getCurrentUser().getBoolean("checkedNN"));

        checkbox_collab_filter.setChecked(ParseUser.getCurrentUser().getBoolean("checkedCollab"));
    }
}