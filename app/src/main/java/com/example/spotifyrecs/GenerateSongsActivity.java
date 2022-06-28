package com.example.spotifyrecs;

import static com.example.spotifyrecs.SpotifyLoginActivity.getAuthToken;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;

import com.example.spotifyrecs.adapters.SwipeSongAdapter;
import com.example.spotifyrecs.models.Song;
import com.example.spotifyrecs.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TracksPager;
import retrofit.client.Response;

public class GenerateSongsActivity extends AppCompatActivity {

    String authToken;

    RecyclerView rvSwipeSongs;
    List<Song> allSongs = new ArrayList<>();
    ProgressBar pb;

    protected SwipeSongAdapter adapter;

    private SpotifyAppRemote mSpotifyAppRemote;

    SpotifyApi api;
    public static SpotifyService spotifyService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_songs);

        pb = findViewById(R.id.pbLoading);

        pb.setVisibility(ProgressBar.VISIBLE);
        rvSwipeSongs = findViewById(R.id.rvSwipeSongs);

        adapter = new SwipeSongAdapter(this, allSongs);

        rvSwipeSongs.setAdapter(adapter);
        // set the layout manager on the recycler view
        rvSwipeSongs.setLayoutManager(new LinearLayoutManager(this));

    //    Bundle bundle = getIntent().getExtras();
     //   authToken = bundle.getString("AUTH_TOKEN");
        String[] artists = getIntent().getStringArrayExtra("artists");

        setServiceApi();

        queryUsers(artists);
    }

    private void generateSongs(SpotifyService spotifyService, String[] artists, List<List<String>> userArtists) {

        List<Song> songs = new ArrayList<>();
        List<String> relatedArtists = new ArrayList<>();

        String artist1 = artists[0];
        String artist2 = artists[1];
        int index = 0;

        while(index < userArtists.size() && relatedArtists.size() < 5){ //List<String> currList : userArtists){
            List<String> currList = userArtists.get(index);
            Log.i("in generate songs", "this is the current list: " + currList);
            // need to make contains that ignores cases
            if(containsIgnoreCase(currList, artist1) || containsIgnoreCase(currList, artist2)){
                relatedArtists.addAll(getOtherArtists(currList, artist1, artist2));
            }
            index++;
        }

        Log.i("TAG TAG TAG","these are all of the related artists: " + relatedArtists);

        if(relatedArtists.size() > 0) {
            for (String artist : relatedArtists) {
                spotifyService.searchTracks(artist, new SpotifyCallback<TracksPager>() {
                    @Override
                    public void failure(SpotifyError spotifyError) {
                        Log.i("error in generate", "error is: " + spotifyError.getMessage());
                    }

                    @Override
                    public void success(TracksPager tracksPager, Response response) {
                        for (Track item : tracksPager.tracks.items) {
                            Log.i("in succes artist 2", "these are the items: "
                                    + item.name);
                            Song song = new Song();
                            song.title = item.name;
                            song.artist = item.artists.get(0).name;
                            song.uri = item.uri;
                            song.imageString = item.album.images.get(0).url;
                            songs.add(song);
                            Collections.shuffle(songs);
                        }

                        querySongs(songs);
                    }
                });
            }
        }

        else {
            spotifyService.searchTracks(artists[0], new SpotifyCallback<TracksPager>() {
                @Override
                public void failure(SpotifyError spotifyError) {
                    Log.i("error in generate", "error is: " + spotifyError.getMessage());
                }

                @Override
                public void success(TracksPager tracksPager, Response response) {
                    for (Track item : tracksPager.tracks.items) {
                        Log.i("in succes artist 1", "these are the items: "
                                + item.name);
                        Song song = new Song();
                        song.title = item.name;
                        song.artist = item.artists.get(0).name;
                        song.uri = item.uri;
                        song.imageString = item.album.images.get(0).url;
                        songs.add(song);
                    }
                }
            });

            spotifyService.searchTracks(artists[1], new SpotifyCallback<TracksPager>() {
                @Override
                public void failure(SpotifyError spotifyError) {
                    Log.i("error in generate", "error is: " + spotifyError.getMessage());
                }

                @Override
                public void success(TracksPager tracksPager, Response response) {
                    for (Track item : tracksPager.tracks.items) {
                        Log.i("in succes artist 2", "these are the items: "
                                + item.name);
                        Song song = new Song();
                        song.title = item.name;
                        song.artist = item.artists.get(0).name;
                        song.uri = item.uri;
                        song.imageString = item.album.images.get(0).url;
                        songs.add(song);
                        Collections.shuffle(songs);
                    }

                    querySongs(songs);
                }
            });
        }
    }

    private boolean containsIgnoreCase(List<String> currList, String artist1) {
        for(String artist : currList){
            if(artist.equalsIgnoreCase(artist1)){
                return true;
            }
        }
        return false;
    }

    private Collection<String> getOtherArtists(List<String> currList, String artist1,
                                               String artist2) {
        List<String> otherArtists = new ArrayList<>();

        for(String artist : currList){
            Log.i("in getOtherArtists", "these are to be compared: " + artist +
                    " and artist1 " + artist1 + " and artist2: " + artist2);
            if(!(artist.equalsIgnoreCase(artist1)) && !(artist.equalsIgnoreCase(artist2))){
                otherArtists.add(artist);
            }
        }
        return otherArtists;
    }

    private void queryUsers(String[] artists) {
        ArrayList<List<String>> allArtists = new ArrayList<>();
        // specify what type of data we want to query - Post.class
        ParseQuery<User> query = ParseQuery.getQuery(User.class);
        // include data referred by user key
        query.include(User.KEY_ARTISTS);
        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> users, ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e("TAG", "Issue with getting users", e);
                    return;
                }

                for (User user : users) {
                    try {
                        Log.i("In query users", "this is the current artists: " +
                                user.getArtists());
                        allArtists.add(jsonToStringArray(user.getArtists()));
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                }

                // send received artists to to the generateSongs method
                generateSongs(spotifyService, artists, allArtists);
            }
        });
    }

    private List<String> jsonToStringArray(JSONArray artists) throws JSONException {
        List<String> newArtists = new ArrayList<>();
        for(int i = 0; i < artists.length(); i++){
            newArtists.add(artists.get(i).toString());
        }
        return newArtists;
    }

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
        Log.i("setSErvice", "authToken is " + authToken);
        api = new SpotifyApi();
        api.setAccessToken(getAuthToken());
        spotifyService = api.getService();
    }
}