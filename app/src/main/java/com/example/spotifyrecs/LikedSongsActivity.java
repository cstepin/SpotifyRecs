package com.example.spotifyrecs;

import static com.example.spotifyrecs.resources.Resources.getAuthToken;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.example.spotifyrecs.adapters.SongAdapter;
import com.example.spotifyrecs.models.Playlist;
import com.example.spotifyrecs.models.Song;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Item;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TracksPager;
import retrofit.client.Response;

public class LikedSongsActivity extends AppCompatActivity {
    RecyclerView rvSongs;
    ArrayList<Song> allSongs;
    protected SongAdapter adapter;
    BottomNavigationView bottomNavigationView;
    JSONArray likedSongs;
    public static final String TAG = "LikeSongs";

    private SpotifyAppRemote mSpotifyAppRemote;
    SpotifyApi api;
    public static SpotifyService spotifyService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liked_songs);

        bottomNavigationView = findViewById(R.id.bottomNavigation);

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

        setServiceApi();

        try {
            queryLikedSongs();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void queryLikedSongs() throws JSONException {
        ArrayList<Song> songs = new ArrayList<>();
        likedSongs = ParseUser.getCurrentUser().getJSONArray("faveSongs");
        assert likedSongs != null;
        Log.i("tag", "this is the likeSongs lenght: " + likedSongs.length());
        for(int i = 0; i < likedSongs.length(); i++) {
            String songTitle = likedSongs.get(i).toString();
            Log.i("songTitle", "this is the song title: " + songTitle + " and this is what I got: " +
                    likedSongs.get(i));
            int finalI = i;
            spotifyService.searchTracks(songTitle, new SpotifyCallback<TracksPager>() {
                @Override
                public void failure(SpotifyError spotifyError) {
                    Log.i("error in generate, liked songs activity", "error is: " + spotifyError.getMessage());
                }

                @Override
                public void success(TracksPager tracksPager, Response response) {
                    Song song = new Song();
                    for(Track track : tracksPager.tracks.items) {
                        Log.i("this is track", "track name: " + track.name);
                        song.artist = tracksPager.tracks.items.get(0).artists.get(0).name;
                        song.title = songTitle;
                        songs.add(song);
                    }
                    if(finalI == likedSongs.length() - 1){
                        querySongs(songs);
                    }
                }
            });
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
        Toast.makeText(LikedSongsActivity.this, "logging out", Toast.LENGTH_LONG).show();
        // SpotifyRecs.getRestClient(this).clearAccessToken();
        // navigate backwards to Login screen
        Intent i = new Intent(this, SpotifyLoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // this makes sure the Back button won't work
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // same as above
        startActivity(i);
        ParseUser.logOutInBackground();
        ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null
        finish();
    }

    //This sets up our api by passing in the authentication token from the log-in screen
    private void setServiceApi() {
        api = new SpotifyApi();
        api.setAccessToken(getAuthToken());
        spotifyService = api.getService();
    }
}