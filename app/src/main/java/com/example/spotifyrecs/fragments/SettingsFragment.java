package com.example.spotifyrecs.fragments;

import android.os.Bundle;
import android.util.Log;

import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceFragmentCompat;

import com.example.spotifyrecs.R;

import javax.annotation.Nullable;

public class SettingsFragment extends PreferenceFragmentCompat {

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


    }
}