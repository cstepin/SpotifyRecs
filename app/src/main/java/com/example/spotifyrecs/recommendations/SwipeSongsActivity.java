package com.example.spotifyrecs.recommendations;

import static com.example.spotifyrecs.resources.Resources.decodeBase62;
import static com.example.spotifyrecs.resources.Resources.getClientId;
import static com.example.spotifyrecs.resources.Resources.getRedirectUrl;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.spotifyrecs.ExportActivity;
import com.example.spotifyrecs.R;
import com.example.spotifyrecs.adapters.SwipeSongAdapter;
import com.example.spotifyrecs.adapters.SwipeSongDeckAdapter;
import com.example.spotifyrecs.finalPlaylistActivity;
import com.example.spotifyrecs.models.Song;
import com.parse.ParseUser;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.ContentApi;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.types.ListItem;
import com.spotify.protocol.types.ListItems;
import com.yalantis.library.Koloda;
import com.yalantis.library.KolodaListener;

import org.json.JSONArray;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SwipeSongsActivity extends AppCompatActivity {


    private SpotifyAppRemote mSpotifyAppRemote;
    List<Song> songs = new ArrayList<>();
    List<String> faveSongs = new ArrayList<>();
    List<Song> keepSongs = new ArrayList<>();
    Koloda koloda;
    protected SwipeSongDeckAdapter adapter;
    ProgressBar pb;
    final String TAG = "ExportActivity";
    LottieAnimationView animationView;

    //We time the amount it takes to generate all of the song items
    long startTime;
    long endTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_songs);
        startTime = System.nanoTime();

        koloda = findViewById(R.id.koloda);
        pb = findViewById(R.id.pbLoading);
        pb.setVisibility(ProgressBar.VISIBLE);
        animationView = new LottieAnimationView(SwipeSongsActivity.this);
        animationView.findViewById(R.id.animationView);
        animationView.pauseAnimation();
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
                animationView.pauseAnimation();
            }

            @Override
            public void onCardDrag(int i, @NonNull View view, float v) {

            }

            @Override
            public void onCardSwipedLeft(int i) {
                Log.i("koloda", "detected left swipe " + i);
                Toast.makeText(SwipeSongsActivity.this, "Leaving this song behind!",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCardSwipedRight(int i) {
                Log.i("koloda", "detected right swipe " + i);

                Toast.makeText(SwipeSongsActivity.this, "I'm keeping this song!",
                        Toast.LENGTH_SHORT).show();
                Song song = (Song) Objects.requireNonNull(koloda.getAdapter())
                        .getItem(i + 1);
                //this means they liked the song, so we keep the song
                keepSongs.add(song);
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
                animationView.playAnimation();
                Song song = (Song) Objects.requireNonNull(koloda.getAdapter())
                        .getItem(i + 1);
                Log.i(TAG, "This is the song: " +
                        ((Song) koloda.getAdapter().getItem(i + 1)).title);
                faveSongs.add(song.title);
                //   animationView.resumeAnimation();

            }

            @Override
            public void onCardLongPress(int i) {

            }

            @Override
            public void onEmptyDeck() {
                updateLikedSongs();
                Intent i = new Intent(SwipeSongsActivity.this,
                        finalPlaylistActivity.class);
                i.putExtra("final songs", Parcels.wrap(keepSongs));
                startActivity(i);
            }
        });

// run a background job and once complete
        pb.setVisibility(ProgressBar.INVISIBLE);
        endTime = System.nanoTime();
        long duration = (endTime - startTime);
        Log.i(TAG, "this is the duration: " + duration);
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
        listItemsCallResult.setResultCallback(data -> {
            if(data != null){
                Log.i("first", "first on Result");
                ListItem[] items = data.items;

                for(int i = 0; i < items.length; i++) {
                    //     for (ListItem item : items) {
                    CallResult<ListItems> listItemsCallResult1 = mSpotifyAppRemote
                            .getContentApi()
                            .getChildrenOfItem(items[i], 3, 0);

                    int finalI = i;
                    listItemsCallResult1.setResultCallback(data1 -> {
                        if (data1 != null) {
                            ListItem[] items1 = data1.items;

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

                                        currSong.setId(decodeBase62(item1.id));

                                        Log.i(TAG, "this is the id: " + currSong.getId());

                                        songs.add(currSong);
                                    }
                                }
                            }
                            if(finalI == items.length - 1){
                                Collections.shuffle(songs);
                                querySongs(songs);
                            }
                        }
                    });
                }
            }
        });
    }

    private void updateLikedSongs() {
        if(faveSongs.size() == 0){
            return;
        }

        JSONArray currLiked = ParseUser.getCurrentUser().getJSONArray("faveSongs");
        assert currLiked != null;
        for(String song : faveSongs){
            currLiked.put(song);
        }
        ParseUser.getCurrentUser().put("faveSongs", currLiked);
        ParseUser.getCurrentUser().saveInBackground(e -> {
            if(e != null){
                Log.e("AddPlaylistFragment", "error saving playlists", e);
            }
            else{
                Log.i("Addplaylistfragment", "faveSongs saved successfully");
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
