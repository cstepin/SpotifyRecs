package com.example.spotifyrecs;

import static com.example.spotifyrecs.resources.Resources.getAuthToken;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.spotifyrecs.adapters.SwipeSongDeckAdapter;
import com.example.spotifyrecs.models.Song;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;
import com.yalantis.library.Koloda;
import org.parceler.Parcels;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Pager;

import kaaes.spotify.webapi.android.models.Playlist;

import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.TracksPager;
import kaaes.spotify.webapi.android.models.UserPrivate;
import retrofit.client.Response;


public class ExportActivity extends AppCompatActivity {
    List<Song> songs = new ArrayList<>();
    List<String> faveSongs = new ArrayList<>();
    List<Song> keepSongs = new ArrayList<>();
    Koloda koloda;
    String playlistName;
    List<Song> playlistSongs = new ArrayList<>();
    BottomNavigationView bottomNavigationView;

    protected SwipeSongDeckAdapter adapter;
    ProgressBar pb;
    final String TAG = "ExportActivity";
    LottieAnimationView animationView;

    SpotifyApi api;
    public static SpotifyService spotifyService;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);

        koloda = findViewById(R.id.koloda);
        pb = findViewById(R.id.pbLoading);
        pb.setVisibility(ProgressBar.VISIBLE);
        animationView = new LottieAnimationView(ExportActivity.this);
        animationView = findViewById(R.id.animationView);
        animationView.pauseAnimation();

        bottomNavigationView = findViewById(R.id.bottomNavigation);

        bottomNavigationView.setOnItemSelectedListener(
                menuItem -> {
                    if (menuItem.getItemId() == R.id.action_logout) {
                        onLogout();
                    }
                    else if (menuItem.getItemId() == R.id.action_home) {
                        onHome();
                    }
                    else if (menuItem.getItemId() == R.id.action_liked) {
                        onLiked();
                    }
                    return true;
                });

        Log.i("in export", "in export activity");

        //Then we authenticate our current api
        setServiceApi();


        getUserID();
    }

    private void getUserID() {
        spotifyService.getMe(new SpotifyCallback<UserPrivate>() {
            @Override
            public void failure(SpotifyError spotifyError) {
                Log.e(TAG, "error: ", spotifyError);
            }

            @Override
            public void success(UserPrivate userPrivate, Response response) {
                Log.i(TAG, "got userID yay");
                userID = userPrivate.id;
                createPlaylist();
            }
        });
    }

    private void createPlaylist(){
        if(getIntent().hasExtra("playlist name")){
            playlistName = getIntent().getStringExtra("playlist name");
        }
        if(getIntent().hasExtra("songs")){
            playlistSongs = Parcels.unwrap(getIntent()
                    .getParcelableExtra("songs"));
        }

        Map<String, Object> map = new HashMap<>();

        map.put("name", playlistName);
        map.put("description", "New Playlist 2");
        map.put("public", "false");

        spotifyService.createPlaylist(userID, map, new SpotifyCallback<Playlist>() {
            @Override
            public void failure(SpotifyError spotifyError) {
                Log.e(TAG, "error creating playlist: ", spotifyError);
            }

            @Override
            public void success(Playlist playlist, Response response) {
                Log.i(TAG, "playlist successfully added");
                getSongUris(playlist.id, playlistSongs);
            }
        });
    }

    private void addToPlaylist(String id, String finalUris) {
        Map<String, Object> queryMap = new HashMap<>();
        Map<String, Object> requestBodyMap = new HashMap<>();

     //   String finalUris = getSongUris(playlistSongs);
        Log.i(TAG, "These are finalUris: " + finalUris);

         queryMap.put("uris", finalUris);
     //   queryMap.put("uris", "spotify:track:0muI8DpTEpLqqibPm3sKYf");

        requestBodyMap.put("Request Body", "");

        spotifyService.addTracksToPlaylist(userID, id, queryMap, requestBodyMap, new SpotifyCallback<Pager<PlaylistTrack>>() {
            @Override
            public void failure(SpotifyError spotifyError) {
                Log.e(TAG, "error adding to playlist: ", spotifyError);
            }

            @Override
            public void success(Pager<PlaylistTrack> playlistTrackPager, Response response) {
                Log.i(TAG, "successfully added to new playlist " + playlistTrackPager.items);
                pb.setVisibility(ProgressBar.INVISIBLE);
                animationView.playAnimation();
                Toast.makeText(ExportActivity.this, "Successfully exported playlist!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getSongUris(String id, List<Song> playlistSongs) {
        final String[] uris = {""};

        for(int i = 0; i < playlistSongs.size(); i++) {
            Song song = playlistSongs.get(i);
            int finalI = i;
            spotifyService.searchTracks(song.title, new SpotifyCallback<TracksPager>() {
                @Override
                public void failure(SpotifyError spotifyError) {
                    Log.e(TAG, "error getting song: ", spotifyError);
                }

                @Override
                public void success(TracksPager tracksPager, Response response) {
                    uris[0] += tracksPager.tracks.items.get(0).uri;
                    Log.i(TAG, "uris is: " + uris[0] + " and index: " + finalI);

                    if (finalI != playlistSongs.size() - 1) {
                        uris[0] += ",";
                    } else {
                        addToPlaylist(id, uris[0]);
                    }
                }
            });
        }
    }

    //This sets up our api by passing in the authentication token from the log-in screen
    private void setServiceApi() {
        Log.i("setService", "authToken is " + getAuthToken());
        api = new SpotifyApi();
        api.setAccessToken(getAuthToken());
        spotifyService = api.getService();
    }

    //Menu item functions
    private void onHome() {
        startActivity(new Intent(ExportActivity.this, MainActivity.class));
    }

    private void onLiked(){
        Intent i = new Intent(ExportActivity.this, LikedSongsActivity.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void onLogout() {
        Toast.makeText(ExportActivity.this, "logging out", Toast.LENGTH_LONG).show();
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