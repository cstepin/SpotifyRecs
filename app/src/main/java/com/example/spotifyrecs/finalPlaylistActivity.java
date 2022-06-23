package com.example.spotifyrecs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class finalPlaylistActivity extends AppCompatActivity {

    RecyclerView rvSongs;
    ArrayList<String> allSongs;
    protected SongAdapter adapter;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("in here", "final playlist activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_playlist);
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        bottomNavigationView.setOnItemSelectedListener(
                new BottomNavigationView.OnItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        Fragment fragment;
                        switch (menuItem.getItemId()) {
                            case R.id.action_logout:
                                onLogout();
                            default: return true;
                        }
                    }
                });

        rvSongs = findViewById(R.id.rvSongs);
        // initialize the array that will hold posts and create a PostsAdapter
        allSongs = new ArrayList<>();
        adapter = new SongAdapter(this, allSongs);

        rvSongs.setAdapter(adapter);
        // set the layout manager on the recycler view
        rvSongs.setLayoutManager(new LinearLayoutManager(this));
        // query posts from Parstagram

        Bundle bundle = getIntent().getExtras();
        List<String> finalArtists = bundle.getStringArrayList("final songs");

        querySongs(finalArtists);
        Log.i("artists", finalArtists.toString());
    }

    private void querySongs(List<String> finalSongs) {
        allSongs.addAll(finalSongs);
        adapter.notifyDataSetChanged();
    }

    private void onLogout() {
        Toast.makeText(finalPlaylistActivity.this, "logging out", Toast.LENGTH_LONG).show();
        // SpotifyRecs.getRestClient(this).clearAccessToken();
        // navigate backwards to Login screen
        Intent i = new Intent(this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // this makes sure the Back button won't work
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // same as above
        startActivity(i);
        ParseUser.logOutInBackground();

        //Spotify
        //onStop();

        ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null
        finish();
    }
}