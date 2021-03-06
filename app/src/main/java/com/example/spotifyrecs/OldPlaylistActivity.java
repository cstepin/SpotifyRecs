package com.example.spotifyrecs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.spotifyrecs.adapters.PlaylistAdapter;
import com.example.spotifyrecs.adapters.SongAdapter;
import com.example.spotifyrecs.models.Playlist;
import com.example.spotifyrecs.models.Song;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class OldPlaylistActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    RecyclerView rvPlaylists;
    ArrayList<Playlist> allPlaylists;
    protected PlaylistAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_playlist);

        rvPlaylists = findViewById(R.id.rvPlaylists);
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        bottomNavigationView.setOnItemSelectedListener(
                menuItem -> {
                    if (menuItem.getItemId() == R.id.action_logout) {
                        onLogout();
                    }
                    else if (menuItem.getItemId() == R.id.action_home) {
                        onHome();
                    }
                    else if (menuItem.getItemId() == R.id.action_liked){
                        onLiked();
                    }
                    return true;
                });

        allPlaylists = new ArrayList<>();
        adapter = new PlaylistAdapter(this, allPlaylists);

        rvPlaylists.setAdapter(adapter);
        // set the layout manager on the recycler view
        rvPlaylists.setLayoutManager(new LinearLayoutManager(this));

        queryPlaylists();
    }

    private void onHome() {
        startActivity(new Intent(this, MainActivity.class));
    }

    private void onLiked(){
        Intent i = new Intent(OldPlaylistActivity.this, LikedSongsActivity.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void onLogout() {
        Toast.makeText(this, "logging out", Toast.LENGTH_LONG).show();
        // navigate backwards to Login screen
        Intent i = new Intent(this, SpotifyLoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // this makes sure the Back button won't work
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // same as above
        startActivity(i);
        ParseUser.logOutInBackground();
        ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null
        finish();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void queryPlaylists() {
        // specify what type of data we want to query - Post.class
        ParseQuery<Playlist> query = ParseQuery.getQuery(Playlist.class);
        // include data referred by user key
        query.include(Playlist.KEY_USER);
        // limit query to latest 20 items
        query.setLimit(20);

        query.whereEqualTo(Playlist.KEY_USER, ParseUser.getCurrentUser());
        // order posts by creation date (newest first)
        query.addDescendingOrder("createdAt");
        // start an asynchronous call for posts
        query.findInBackground((playlists, e) -> {
            // check for errors
            if (e != null) {
                Log.e("TAG", "Issue with getting playlists", e);
                return;
            }

            // save received playlists to list and notify adapter of new data
            allPlaylists.addAll(playlists);
            adapter.notifyDataSetChanged();
        });
    }
}