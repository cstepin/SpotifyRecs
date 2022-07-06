package com.example.spotifyrecs.recommendations;

import static com.example.spotifyrecs.resources.Resources.getAuthToken;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;

import com.example.spotifyrecs.R;
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
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Artists;
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

        //First, we retrieve the input from user
        String[] artists = getIntent().getStringArrayExtra("artists");

        //Then we authenticate our current api
        setServiceApi();

        //Finally set up the RecyclerView for Android
        queryUsers(artists);
    }

    //This gets related artists and compiles a list of songs by those artists
    private void generateSongs(SpotifyService spotifyService, String[] artists, List<List<String>> userArtists) {

        List<Song> songs = new ArrayList<>();
        List<String> relatedArtists = new ArrayList<>();

        String artist1 = artists[0];
        String artist2 = artists[1];
        int index = 0;

        //For every user list, we see if they contain at least one of the artists that we were asked
        //If it does, we add them to our "relatedArtists" list

        //To-do: we can first try to find "perfect" matches (both artists present"
        // Before going to lists with only one user there.
        while(index < userArtists.size() && relatedArtists.size() < 5){
            List<String> currList = userArtists.get(index);
            Log.i("in generate songs", "this is the current list: " + currList);
            // need to make contains that ignores cases
            if(containsIgnoreCase(currList, artist1) || containsIgnoreCase(currList, artist2)){
                relatedArtists.addAll(getOtherArtists(currList, artist1, artist2));
            }
            index++;
        }

        Log.i("TAG TAG TAG","these are all of the related artists: " + relatedArtists);

        //Here, we just add the top songs of each "related artist"
        if(relatedArtists.size() > 0) {
            for(int i = 0; i < relatedArtists.size(); i++){
                Log.i("i size", "i is now: " + i + " and relatedArtists size is: " +
                        relatedArtists.size());
                String artist = relatedArtists.get(i);
                int finalI = i;
                spotifyService.searchTracks(artist, new SpotifyCallback<TracksPager>() {
                    @Override
                    public void failure(SpotifyError spotifyError) {
                        Log.i("error in generate", "error is: " + spotifyError.getMessage());
                    }

                    @Override
                    public void success(TracksPager tracksPager, Response response) {
                        for (Track item : tracksPager.tracks.items) {
                            Log.i("in succes we have songs", "these are the items: "
                                    + item.name);
                            Song song = new Song();
                            song.title = item.name;
                            song.artist = item.artists.get(0).name;
                            song.uri = item.uri;
                            song.imageString = item.album.images.get(0).url;
                            songs.add(song);
                            Log.i("songs size", "songs size is: " + songs.size());
                        }
                        if(finalI == relatedArtists.size() - 1){
                            Log.i("In at end of for loop", "escaped other for loop");
                            //We shuffle to ensure new results on the screen every time.
                            Collections.shuffle(songs);
                            querySongs(songs);
                        }
                    }
                });
            }
        }

        else {
            //If no lists contain our artists, we just recommend songs of those original artists
            //To-do: change to instead be songs in the genre
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
                    }

                    Log.i("success", "escaped the for loop");
                    Collections.shuffle(songs);
                    querySongs(songs);
                }
            });
        }
    }

    //Checks if there's already a similar word in a list using equalsIgnoreCase
    private boolean containsIgnoreCase(List<String> currList, String artist1) {
        for(String artist : currList){
            if(artist.equalsIgnoreCase(artist1)){
                return true;
            }
        }
        return false;
    }

    //If a match in artist has been found, gets other artists in the user's playlist to generate
    // their top songs
    private Collection<String> getOtherArtists(List<String> currList, String artist1,
                                               String artist2) {
        List<String> otherArtists = new ArrayList<>();
        int index = 0;

        while(index < currList.size() && otherArtists.size() < 2){
            String artist = currList.get(index);
            Log.i("in getOtherArtists", "these are to be compared: " + artist +
                    " and artist1 " + artist1 + " and artist2: " + artist2);
            if(!(artist.equalsIgnoreCase(artist1)) && !(artist.equalsIgnoreCase(artist2))){
                otherArtists.add(artist);
            }
            index++;
        }
        return otherArtists;
    }

    //Gets a list of all of the users' artists list.
    private void queryUsers(String[] artists) {
        ArrayList<List<String>> allArtists = new ArrayList<>();
        // specify what type of data we want to query - Post.class
        ParseQuery<User> query = ParseQuery.getQuery(User.class);
        // include data referred by user key
        query.include(User.KEY_ARTISTS);
        // start an asynchronous call for posts
        query.findInBackground((users, e) -> {
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
        });
    }

    //Turns the JSONArray of artists back into a String array
    private List<String> jsonToStringArray(JSONArray artists) throws JSONException {
        List<String> newArtists = new ArrayList<>();
        for(int i = 0; i < artists.length(); i++){
            newArtists.add(artists.get(i).toString());
        }
        return newArtists;
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

    //This sets up our api by passing in the authentication token from the log-in screen
    private void setServiceApi() {
        Log.i("setService", "authToken is " + authToken);
        api = new SpotifyApi();
        api.setAccessToken(getAuthToken());
        spotifyService = api.getService();
    }
}