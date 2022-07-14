package com.example.spotifyrecs;

import static com.example.spotifyrecs.resources.Resources.getAuthToken;
import static com.example.spotifyrecs.resources.Resources.getClientId;
import static com.example.spotifyrecs.resources.Resources.getRedirectUrl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.spotifyrecs.adapters.SwipeSongAdapter;
import com.example.spotifyrecs.adapters.SwipeSongDeckAdapter;
import com.example.spotifyrecs.models.Song;
import com.example.spotifyrecs.models.User;
import com.parse.ParseQuery;
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
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Recommendations;
import kaaes.spotify.webapi.android.models.SeedsGenres;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TracksPager;
import retrofit.client.Response;


public class ExportActivity extends AppCompatActivity {
    List<Song> songs = new ArrayList<>();
    List<String> faveSongs = new ArrayList<>();
    List<Song> keepSongs = new ArrayList<>();
    Koloda koloda;
    float[] user_x_rating_raw = new float[10];
    int user_rating_index = 0;

    List<String> genres = new ArrayList<>();
    protected SwipeSongDeckAdapter adapter;
    ProgressBar pb;
    final String TAG = "ExportActivity";
    LottieAnimationView animationView;

    SpotifyApi api;
    public static SpotifyService spotifyService;
    Random rand = new Random(); //instance of random class

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);

        koloda = findViewById(R.id.koloda);
        pb = findViewById(R.id.pbLoading);
        pb.setVisibility(ProgressBar.VISIBLE);
        animationView = new LottieAnimationView(ExportActivity.this);
        animationView.findViewById(R.id.animationView);
        animationView.pauseAnimation();

        Log.i("in export", "in export activity");

        //Then we authenticate our current api
        setServiceApi();
    }

    private void addGenres() {
        spotifyService.getSeedsGenres(new SpotifyCallback<SeedsGenres>() {
            @Override
            public void failure(SpotifyError spotifyError) {
                Log.i("error querying", "error: " + spotifyError.getMessage());
            }

            @Override
            public void success(SeedsGenres seedsGenres, Response response) {
                //this eliminates the benefits of model training, but guarantees new
                // results every time -- what should I do?

                for(int i = 0; i < 10; i++){
                    int int_random = rand.nextInt(seedsGenres.genres.size());
                    genres.add(seedsGenres.genres.get(int_random));
                }

                Log.i("these are genres", "genres: " + genres);

                getSongs();
            }
        });
    }

    private void getSongs(){
        List<Song> songs = new ArrayList<>();

        for(int i = 0; i < genres.size(); i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("seed_genres", genres.get(i));
            Log.i("this is map", "map: " + map);

            int finalI = i;
            spotifyService.getRecommendations(map, new SpotifyCallback<Recommendations>() {
                @Override
                public void failure(SpotifyError spotifyError) {
                    Log.i("error querying", "error: " + spotifyError.getMessage() +
                            " with genre: " + genres.get(finalI));

                    if(finalI == genres.size() - 1){
                        Log.i("in here 3", "these are the songs: " + songs);
                        querySongs(songs);
                    }
                }

                @Override
                public void success(Recommendations recommendations, Response response) {
                    Track track = recommendations.tracks.get(rand.nextInt
                            (recommendations.tracks.size()));
                    Song song = new Song();
                    song.artist = track.artists.get(0).name;
                    song.title = track.name;
                    song.uri = track.uri;
                    song.imageString = track.album.images.get(0).url;
                    song.visible = true;
                    songs.add(song);
                    Log.i("added song", "added song: " + songs.size());

                    Log.i("success querying", "title: " + track.name + " and genre: " +
                            track.type);

                    //  map.clear();

                    if(finalI == genres.size() - 1){
                        Log.i("in here 3", "these are the songs: " + songs);
                        querySongs(songs);
                    }
                }
            });
        }
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
                user_x_rating_raw[user_rating_index] = -1.0F;
                user_rating_index++;
            }

            @Override
            public void onCardSwipedRight(int i) {
                user_x_rating_raw[user_rating_index] = 1.0F;
                user_rating_index++;
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
                Song song = (Song) koloda.getAdapter().getItem(i + 1);
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

                Intent i = new Intent(ExportActivity.this,
                        AnalyzeRecommendActivity.class);
                i.putExtra("floats", user_x_rating_raw);
                i.putExtra("final songs", Parcels.wrap(songs));
                startActivity(i);
            }
        });

// run a background job and once complete
        pb.setVisibility(ProgressBar.INVISIBLE);
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

    //This sets up our api by passing in the authentication token from the log-in screen
    private void setServiceApi() {
        Log.i("setService", "authToken is " + getAuthToken());
        api = new SpotifyApi();
        api.setAccessToken(getAuthToken());
        spotifyService = api.getService();

        addGenres();
    }
}