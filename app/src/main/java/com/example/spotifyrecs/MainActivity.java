package com.example.spotifyrecs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.spotifyrecs.models.Song;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.ContentApi;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.Item;
import com.spotify.protocol.types.ListItem;
import com.spotify.protocol.types.ListItems;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    Button btnNewPlaylist;
    Button btnOldPlaylist;
    Button btnSpotifyAlg;
    String authToken;
    Button btnExport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        btnNewPlaylist = findViewById(R.id.btnNewPlaylist);
        btnOldPlaylist = findViewById(R.id.btnOldPlaylist);
        btnSpotifyAlg = findViewById(R.id.btnSpotifyAlg);
        btnExport = findViewById(R.id.btnExport);

        bottomNavigationView.setOnItemSelectedListener(
                menuItem -> {
                    Fragment fragment;
                    if (menuItem.getItemId() == R.id.action_logout) {
                        onLogout();
                    }
                    return true;
                });

        btnSpotifyAlg.setOnClickListener(v -> toNewPlaylists());

        btnNewPlaylist.setOnClickListener(v -> toSelectArtists());

        btnExport.setOnClickListener(v -> toExport());
    }

    //Sending users to the right place
    private void toNewPlaylists() {
        Intent i = new Intent(MainActivity.this, SwipeSongsActivity.class);
        startActivity(i);
    }

    private void onLogout() {
        Toast.makeText(MainActivity.this, "logging out", Toast.LENGTH_LONG).show();
        //  InstaClone.getRestClient(this).clearAccessToken();
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
    }

    private void toExport(){
        Intent i = new Intent(MainActivity.this, ExportActivity.class);
        startActivity(i);
    }
}