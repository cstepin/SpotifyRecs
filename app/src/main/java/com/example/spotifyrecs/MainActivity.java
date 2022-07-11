package com.example.spotifyrecs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.spotifyrecs.models.Song;
import com.example.spotifyrecs.recommendations.CollabFilteringActivity;
import com.example.spotifyrecs.recommendations.SwipeSongsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;


public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    Button btnNewPlaylist;
    Button btnOldPlaylist;
    Button btnSpotifyAlg;
    String authToken;
    Button btnExport;
    Button btnCollab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        btnNewPlaylist = findViewById(R.id.btnNewPlaylist);
        btnOldPlaylist = findViewById(R.id.btnOldPlaylist);
        btnSpotifyAlg = findViewById(R.id.btnSpotifyAlg);
        btnExport = findViewById(R.id.btnExport);
        btnCollab = findViewById(R.id.btnCollab);

        Song song = new Song();
        String toStringResult = song.toString();
        Log.i("empty", "toStringResult: " + toStringResult);
        
        btnCollab.setOnClickListener(v -> toCollab());

        bottomNavigationView.setOnItemSelectedListener(
                menuItem -> {
                    Fragment fragment;
                    if (menuItem.getItemId() == R.id.action_logout) {
                        onLogout();
                    }
                    else if(menuItem.getItemId() == R.id.action_liked){
                        onLiked();
                    }
                    return true;
                });

        btnSpotifyAlg.setOnClickListener(v -> toNewPlaylists());

        btnOldPlaylist.setOnClickListener(v -> toOldPlaylist());

        btnNewPlaylist.setOnClickListener(v -> toSelectArtists());

        btnExport.setOnClickListener(v -> toExport());
    }

    private void toCollab() {
        Intent i = new Intent(MainActivity.this, CollabFilteringActivity.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void toOldPlaylist() {
        Intent i = new Intent(MainActivity.this, OldPlaylistActivity.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    //Sending users to the right place
    private void toNewPlaylists() {
        Intent i = new Intent(MainActivity.this, SwipeSongsActivity.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void onLiked(){
        Intent i = new Intent(MainActivity.this, LikedSongsActivity.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void onLogout() {
        Toast.makeText(MainActivity.this, "logging out", Toast.LENGTH_LONG).show();
        // navigate backwards to Login screen
        Intent i = new Intent(this, SpotifyLoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // this makes sure the Back button won't work
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // same as above
        startActivity(i);
        ParseUser.logOutInBackground();

        //Spotify
        onStop();
        ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null
        finish();
    }

    private void toSelectArtists(){
        Intent i = new Intent(MainActivity.this, EnterArtistsActivity.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void toExport(){
        Intent i = new Intent(MainActivity.this, ExportActivity.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}