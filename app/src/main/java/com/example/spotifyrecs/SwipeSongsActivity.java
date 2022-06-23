package com.example.spotifyrecs;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifyrecs.models.Song;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.ContentApi;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.types.ListItem;
import com.spotify.protocol.types.ListItems;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SwipeSongsActivity extends AppCompatActivity {

    private static final String CLIENT_ID = "f67855f9416e4ca999b13ec503540bc8";
    private static final String REDIRECT_URI = "http://localhost:8080";
    private SpotifyAppRemote mSpotifyAppRemote;
    private float x1,x2;
    static final int MIN_DISTANCE = 150;

  //  TextView tvExample;
    RecyclerView rvSwipeSongs;
    List<Song> allSongs = new ArrayList<>();
    ProgressBar pb;

    protected SwipeSongAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_songs);
        pb = findViewById(R.id.pbLoading);

        pb.setVisibility(ProgressBar.VISIBLE);
        rvSwipeSongs = findViewById(R.id.rvSwipeSongs);
    }

    private void querySongs(List<Song> finalSongs) {
        allSongs.addAll(finalSongs);
        adapter.notifyDataSetChanged();
// run a background job and once complete
        pb.setVisibility(ProgressBar.INVISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();

           adapter = new SwipeSongAdapter(this, allSongs);

           rvSwipeSongs.setAdapter(adapter);
        // set the layout manager on the recycler view
            rvSwipeSongs.setLayoutManager(new LinearLayoutManager(this));

        Log.i("in onStart", "in Onstart");

        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {

                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.d("MainActivity", "Connected! Yay!");

                        // Now you can start interacting with App Remote
                        connected();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e("MainActivity", throwable.getMessage(), throwable);

                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });
    }

    private void connected() {
        // Play a playlist

        List<Song> songs = new ArrayList<>();

        // This gets the content asynchronously in recommended
        CallResult<ListItems> listItemsCallResult = mSpotifyAppRemote.getContentApi()
                .getRecommendedContentItems(ContentApi.ContentType.DEFAULT);
        listItemsCallResult.setResultCallback(new CallResult.ResultCallback<ListItems>() {
            @Override
            public void onResult(ListItems data) {
                if(data != null){
                    Log.i("first", "first on Result");
                    Log.i("asd", "data : " + data.toString());
                    ListItem[] items = data.items;

                    for(int i = 0; i < items.length; i++) {
                        //     for (ListItem item : items) {
                        CallResult<ListItems> listItemsCallResult1 = mSpotifyAppRemote
                                .getContentApi()
                                .getChildrenOfItem(items[i], 3, 0);

                        int finalI = i;
                        listItemsCallResult1.setResultCallback(new CallResult.ResultCallback<ListItems>() {
                            @Override
                            public void onResult(ListItems data) {

                                Log.i("second", "second on Result");
                                if (data != null) {
                                    ListItem[] items1 = data.items;

                                    for (ListItem item1 : items1) {

                                        if (item1.uri.contains("playlist") ||
                                                item1.uri.contains("track") ||
                                                item1.uri.contains("album")){
                                            if(!InList(item1.title, songs)) {
                                                Song currSong = new Song();
                                                currSong.title = item1.title;
                                                currSong.uri = item1.uri;
                                                currSong.artist = item1.subtitle;
                                                currSong.imageLink = item1.imageUri;

                                                songs.add(currSong);
                                                Log.i("fsd", "inside + " + item1.toString() + " and title: " + item1.toString());
                                            }
                                        }

                                        Log.i("fsd", "asdf + " + item1.toString());
                                    }

                                    if(finalI == items.length - 1){
                                        querySongs(songs);
                                    }
                                }
                            }
                        });
                        Log.i("asdf", "sd" + items[i].title);
                    }
                }
            }
        });
    }

    protected Boolean InList(String title, List<Song> songs){
        for(Song song : songs){
            if(song.title.equals(title)){
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Aaand we will finish off here.
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }
}
