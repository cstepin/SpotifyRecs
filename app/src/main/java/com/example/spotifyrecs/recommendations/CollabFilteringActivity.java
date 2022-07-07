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

public class CollabFilteringActivity extends AppCompatActivity {
    String authToken;

    RecyclerView rvSwipeSongs;
    List<Song> allSongs = new ArrayList<>();
    ProgressBar pb;

    List<String> genres = new ArrayList<>();

    protected CollabSongAdapter adapter;

    private SpotifyAppRemote mSpotifyAppRemote;

    SpotifyApi api;
    public static SpotifyService spotifyService;
    Random rand = new Random(); //instance of random class

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

        addGenres();
    }
}