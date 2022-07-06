package com.example.spotifyrecs.recommendations;

import static com.example.spotifyrecs.resources.Resources.getAuthToken;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;

import com.example.spotifyrecs.R;
import com.example.spotifyrecs.adapters.CollabSongAdapter;
import com.example.spotifyrecs.adapters.SwipeSongAdapter;
import com.example.spotifyrecs.models.Song;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;

public class CollabFilteringActivity extends AppCompatActivity {
    String authToken;

    RecyclerView rvSwipeSongs;
    List<Song> allSongs = new ArrayList<>();
    ProgressBar pb;

    protected CollabSongAdapter adapter;

    private SpotifyAppRemote mSpotifyAppRemote;

    SpotifyApi api;
    public static SpotifyService spotifyService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collab_filtering);

        pb = findViewById(R.id.pbLoading);

        pb.setVisibility(ProgressBar.VISIBLE);
        rvSwipeSongs = findViewById(R.id.rvSwipeSongs);

        adapter = new CollabSongAdapter(this, allSongs);

        rvSwipeSongs.setAdapter(adapter);
        // set the layout manager on the recycler view
        rvSwipeSongs.setLayoutManager(new LinearLayoutManager(this));

        //Then we authenticate our current api
        setServiceApi();

        // first, we need to show 10 songs which are very different to generate the thing.
        // then we need to have a specific swiping thing to see if we should keep the data as a -1
        // 0 or 1

        getSongs();
    }

    private void getSongs(){
        List<Song> songs = new ArrayList<>();

        //get the ten songs somehow.
        querySongs(songs);
    }

    //Adds suggested songs into the recycler view.
    private void querySongs(List<Song> finalSongs) {
        Log.i("generate", "in query songs" + finalSongs.size());
        allSongs.addAll(finalSongs);
        Log.i("allSongs", "allSongs is: " + allSongs.toString());
        adapter.notifyDataSetChanged();
        Log.i("generate", "after adapter notified");
        // run a background job and once complete
        pb.setVisibility(ProgressBar.INVISIBLE);
    }

    private void setServiceApi() {
        Log.i("setService", "authToken is " + authToken);
        api = new SpotifyApi();
        api.setAccessToken(getAuthToken());
        spotifyService = api.getService();
    }
}