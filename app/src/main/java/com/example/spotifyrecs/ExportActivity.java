package com.example.spotifyrecs;

import static com.example.spotifyrecs.resources.Resources.getClientId;
import static com.example.spotifyrecs.resources.Resources.getRedirectUrl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.spotifyrecs.adapters.SwipeSongAdapter;
import com.example.spotifyrecs.adapters.SwipeSongDeckAdapter;
import com.example.spotifyrecs.models.Song;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.ContentApi;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.types.ListItem;
import com.spotify.protocol.types.ListItems;
import com.yalantis.library.Koloda;
import com.yalantis.library.KolodaListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class ExportActivity extends AppCompatActivity {


    private SpotifyAppRemote mSpotifyAppRemote;
    List<Song> songs = new ArrayList<>();
    Koloda koloda;
    protected SwipeSongDeckAdapter adapter;
    ProgressBar pb;
    final String TAG = "ExportActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);

        Koloda koloda = findViewById(R.id.koloda);
        pb = findViewById(R.id.pbLoading);
    }

    private void querySongs(List<Song> finalSongs) {

        Log.i(TAG, "length: " + finalSongs.size());
        songs.addAll(finalSongs);
       // adapter.notifyDataSetChanged();

        adapter = new SwipeSongDeckAdapter(this, songs);
        koloda.setAdapter(adapter);

        koloda.setKolodaListener(new KolodaListener() {
            @Override
            public void onNewTopCard(int i) {

            }

            @Override
            public void onCardDrag(int i, @NonNull View view, float v) {

            }

            @Override
            public void onCardSwipedLeft(int i) {
                Log.i("koloda", "detected left swipe");
            }

            @Override
            public void onCardSwipedRight(int i) {
                Log.i("koloda", "detected right swipe");
            }

            @Override
            public void onClickRight(int i) {

            }

            @Override
            public void onClickLeft(int i) {

            }

            @Override
            public void onCardSingleTap(int i) {

            }

            @Override
            public void onCardDoubleTap(int i) {

            }

            @Override
            public void onCardLongPress(int i) {

            }

            @Override
            public void onEmptyDeck() {

            }
        });

// run a background job and once complete
        pb.setVisibility(ProgressBar.INVISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.i("in onStart", "in Onstart");

        ConnectionParams connectionParams =
                new ConnectionParams.Builder(getClientId())
                        .setRedirectUri(getRedirectUrl())
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {

                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.d(TAG, "Connected! Yay!");

                        // Now you can start interacting with App Remote
                        connected();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e(TAG, throwable.getMessage(), throwable);

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
                                                currSong.imageString = item1.imageUri.raw;

                                                songs.add(currSong);
                                                Log.i("fsd", "inside + " + item1 + " and title: " + item1);
                                            }
                                        }
                                    }
                                    if(finalI == items.length - 1){
                                        Collections.shuffle(songs);
                                        querySongs(songs);
                                    }
                                }
                            }
                        });
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