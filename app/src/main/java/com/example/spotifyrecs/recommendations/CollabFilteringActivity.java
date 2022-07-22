package com.example.spotifyrecs.recommendations;

import static com.example.spotifyrecs.resources.Resources.decodeBase62;
import static com.example.spotifyrecs.resources.Resources.getAuthToken;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.airbnb.lottie.LottieAnimationView;
import com.example.spotifyrecs.AnalyzeRecommendActivity;
import com.example.spotifyrecs.R;
import com.example.spotifyrecs.adapters.CollabSongDeckAdapter;
import com.example.spotifyrecs.models.Song;
import com.parse.ParseUser;
import com.yalantis.library.Koloda;
import com.yalantis.library.KolodaListener;

import org.json.JSONArray;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Recommendations;
import kaaes.spotify.webapi.android.models.SeedsGenres;
import kaaes.spotify.webapi.android.models.Track;
import retrofit.client.Response;

public class CollabFilteringActivity extends AppCompatActivity {
    private static final String TAG = "CollabFilteringActivity";

    List<String> faveSongs = new ArrayList<>();
    List<Song> songs = new ArrayList<>();
    ProgressBar pb;
    Koloda koloda;
    float[] user_x_rating_raw = new float[10];
    int user_rating_index = 0;
    Boolean ignoreClicked = false;

    List<String> genres = new ArrayList<>();

    LottieAnimationView animationView;
    protected CollabSongDeckAdapter adapter;

    SpotifyApi api;
    public static SpotifyService spotifyService;
    Random rand = new Random(); //instance of random class

// To time amount it takes to generate 10 random songs
    // Currently around 2562890 milliseconds
    long startTime;
    long endTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collab_filtering);

        startTime = System.nanoTime();

        koloda = findViewById(R.id.koloda);
        pb = findViewById(R.id.pbLoading);
        pb.setVisibility(ProgressBar.VISIBLE);
        animationView = new LottieAnimationView(CollabFilteringActivity.this);
        animationView = findViewById(R.id.animationView);
        animationView.pauseAnimation();

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
                        querySongs(songs);
                    }
                }

                @Override
                public void success(Recommendations recommendations, Response response) {
                    Log.i(TAG, "these are tracks size: " + recommendations.tracks.size());

                    Track track = recommendations.tracks.get(rand.nextInt
                            (recommendations.tracks.size()));
                    Song song = new Song();
                    song.artist = track.artists.get(0).name;
                    song.title = track.name;
                    song.uri = track.uri;
                    song.imageString = track.album.images.get(0).url;
                    song.visible = true;

                    song.setId(decodeBase62(track.id));

                    Log.i(TAG, "this is the id: " + song.getId());

                    songs.add(song);

                    Log.i("success querying", "title: " + track.name + " and genre: " +
                            track.type);

                    if(finalI == genres.size() - 1){
                        querySongs(songs);
                    }
                }
            });
        }
    }

    private void querySongs(List<Song> finalSongs) {

        Log.i(TAG, "length: " + finalSongs.size());
        songs.addAll(finalSongs);

        adapter = new CollabSongDeckAdapter(this, songs);
        koloda.setAdapter(adapter);

        koloda.setKolodaListener(new KolodaListener() {
            @Override
            public void onNewTopCard(int i) {
                animationView.setVisibility(View.INVISIBLE);
                animationView.pauseAnimation();
            }

            @Override
            public void onCardDrag(int i, @NonNull View view, float v) {

            }

            @Override
            public void onCardSwipedLeft(int i) {
                if(adapter.getIgnoreClicked()){
                    user_x_rating_raw[user_rating_index] = 0.0F;
                    user_rating_index++;
                    adapter.setIgnoreClicked(false);
                }
                else if(ignoreClicked){
                    ignoreClicked = false;
                }
                else {
                    user_x_rating_raw[user_rating_index] = -1.0F;
                    user_rating_index++;
                }
            }

            @Override
            public void onCardSwipedRight(int i) {
                if(adapter.getIgnoreClicked()){
                    user_x_rating_raw[user_rating_index] = 0.0F;
                    user_rating_index++;
                    adapter.setIgnoreClicked(false);
                }
                else if(ignoreClicked){
                    ignoreClicked = false;
                }
                else {
                    user_x_rating_raw[user_rating_index] = 1.0F;
                    user_rating_index++;
                }
            }

            @Override
            public void onClickRight(int i) {

            }

            @Override
            public void onClickLeft(int i) {

            }

            @Override
            public void onCardSingleTap(int i) {
                if(adapter.getIgnoreClicked()){
                    user_x_rating_raw[user_rating_index] = 0.0F;
                    user_rating_index++;
                    adapter.setIgnoreClicked(false);
                    ignoreClicked = true;
                }
            }

            @Override
            public void onCardDoubleTap(int i) {
                animationView.setVisibility(View.VISIBLE);
                animationView.playAnimation();
                Song song = (Song) Objects.requireNonNull(koloda.getAdapter()).getItem(i + 1);
                Log.i(TAG, "This is the song: " +
                        ((Song) koloda.getAdapter().getItem(i + 1)).title);
                faveSongs.add(song.title);
            }

            @Override
            public void onCardLongPress(int i) {

            }

            @Override
            public void onEmptyDeck() {

                //To ensure data from swipe is saved
                Handler handler = new Handler();
                handler.postDelayed(() -> {
                updateLikedSongs();

                Intent i = new Intent(CollabFilteringActivity.this,
                        AnalyzeRecommendActivity.class);
                Log.i(TAG, "and this is songs: " + songs);
                i.putExtra("floats", user_x_rating_raw);
                i.putExtra("songs", Parcels.wrap(songs));
                startActivity(i);
                }, 800);   //5 seconds

            }
        });

// run a background job and once complete
        pb.setVisibility(ProgressBar.INVISIBLE);
        endTime = System.nanoTime();
        long duration = ((endTime - startTime)/1000);
        Log.i(TAG, "this is the duration: " + duration);
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