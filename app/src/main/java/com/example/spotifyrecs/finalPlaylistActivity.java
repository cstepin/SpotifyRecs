package com.example.spotifyrecs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.spotifyrecs.adapters.SongAdapter;
import com.example.spotifyrecs.fragments.AddPlaylistFragment;
import com.example.spotifyrecs.models.Song;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class finalPlaylistActivity extends AppCompatActivity {

    RecyclerView rvSongs;
    ArrayList<Song> allSongs;
    protected SongAdapter adapter;
    BottomNavigationView bottomNavigationView;
    JSONArray currUserArtists;
    Button btnAddPlaylist;
    public static final String TAG = "FinalPlaylist";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("in here", "final playlist activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_playlist);
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        btnAddPlaylist = findViewById(R.id.btnAddPlaylist);
        if(getIntent().hasExtra("details")){
            btnAddPlaylist.setVisibility(View.GONE);
        }

        bottomNavigationView.setOnItemSelectedListener(
                menuItem -> {
                    if (menuItem.getItemId() == R.id.action_logout) {
                        onLogout();
                    }
                    else if (menuItem.getItemId() == R.id.action_home) {
                        onHome();
                    }
                    return true;
                });

        rvSongs = findViewById(R.id.rvSongs);
        // initialize the array that will hold posts and create a PostsAdapter
        allSongs = new ArrayList<>();
        adapter = new SongAdapter(this, allSongs);

        rvSongs.setAdapter(adapter);
        // set the layout manager on the recycler view
        rvSongs.setLayoutManager(new LinearLayoutManager(this));
        // query posts from Parstagram

        ArrayList<Song> finalArtists = Parcels.unwrap(getIntent()
                .getParcelableExtra("final songs"));
        currUserArtists = ParseUser.getCurrentUser().getJSONArray("artists");

        //This stores information on the artists the user liked to use for collaborative filtering
        for(int i = 0; i < finalArtists.size(); i++){
            Song song = finalArtists.get(i);
            // Does a little more work splitting by commas and also ensuring no duplicates
            if(!song.artist.equals("")) {
              //  Log.i("in final artists artists", "the current artist is: " + song.artist);
                try {
                    //If the artist is given as a list, we have to enter each artist separately
                    if(song.artist.contains(",")) {
                        splitArtist(song.artist);
                    }
                    //else we can just enter the artist
                    else {
                        assert currUserArtists != null;
                        if (notADuplicate(currUserArtists, song.artist)){
                            Log.i(TAG, "i'm adding2: " + song.artist);
                            currUserArtists.put(song.artist);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            //Because saveArtists() is async, we call it at the end of this for loop
            if(i == finalArtists.size() - 1){
                saveArtists();
            }
        }

        //This puts in the artists in the adapter
        querySongs(finalArtists);
        Log.i("artists", finalArtists.toString());

        btnAddPlaylist.setOnClickListener(v -> addNewPlaylist(finalArtists));
    }

    private void addNewPlaylist(ArrayList<Song> finalArtists) {
        FragmentManager fm = getSupportFragmentManager();
        AddPlaylistFragment addPlaylistFragment = new AddPlaylistFragment(finalArtists);
        addPlaylistFragment.show(fm, "hello");
    }

    //This just adds new artists for every user into the database
    private void saveArtists() {
        ParseUser.getCurrentUser().put("artists", currUserArtists);
        ParseUser.getCurrentUser().saveInBackground(e -> {
            if(e != null){
                Log.e("finalPlaylistActivity", "error saving artists", e);
            }
            else{
                Log.i("finalPlaylistActivity", "artists saved successfully");
            }
        });
    }

    // Takes a JSONArray and artist and returns if the string is NOT in the array
    public static boolean notADuplicate(JSONArray currUserArtists, String artist) throws JSONException {
    //    Log.i("this is the length", "length: " + currUserArtists.length());
        for(int i = 0; i < currUserArtists.length(); i++){
            if(currUserArtists.get(i).toString().equals(artist)){
                return false;
            }
        }
        return true;
    }

    //This splits by artist ad ensures that spaces and "and more" aren't included in artist names
    private void splitArtist(String artists) throws JSONException {
        String[] subArtists = artists.split(", ");
        for(String artist : subArtists){
            if(notADuplicate(currUserArtists, artist)) {
                Log.i(TAG, "i'm adding: " + artist);
                if(artist.contains(" and more")){
                    currUserArtists.put(artist.split(" and more")[0]);
                }
                else {
                    currUserArtists.put(artist);
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void querySongs(List<Song> finalSongs) {
        allSongs.addAll(finalSongs);
        adapter.notifyDataSetChanged();
    }

    //Menu item functions
    private void onHome() {
        startActivity(new Intent(this, MainActivity.class));
    }

    private void onLogout() {
        Toast.makeText(finalPlaylistActivity.this, "logging out", Toast.LENGTH_LONG).show();
        // navigate backwards to Login screen
        Intent i = new Intent(this, SpotifyLoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // this makes sure the Back button won't work
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // same as above
        startActivity(i);
        ParseUser.logOutInBackground();
        ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null
        finish();
    }
}