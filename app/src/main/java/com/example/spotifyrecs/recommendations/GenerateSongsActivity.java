package com.example.spotifyrecs.recommendations;

import static com.example.spotifyrecs.resources.Resources.getAuthToken;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.spotifyrecs.ExportActivity;
import com.example.spotifyrecs.R;
import com.example.spotifyrecs.adapters.SwipeSongAdapter;
import com.example.spotifyrecs.adapters.SwipeSongDeckAdapter;
import com.example.spotifyrecs.finalPlaylistActivity;
import com.example.spotifyrecs.models.Song;
import com.example.spotifyrecs.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.yalantis.library.Koloda;
import com.yalantis.library.KolodaListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Artists;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Recommendations;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TracksPager;
import retrofit.client.Response;

public class GenerateSongsActivity extends AppCompatActivity {
    List<Song> songs = new ArrayList<>();
    List<String> faveSongs = new ArrayList<>();
    List<Song> keepSongs = new ArrayList<>();
    Koloda koloda;
    protected SwipeSongDeckAdapter adapter;
    ProgressBar pb;
    final String TAG = "ExportActivity";
    LottieAnimationView animationView;

    //To time the amount of time it takes to generate songs
    // Currently around 462047 milliseconds
    long startTime;
    long endTime;

    SpotifyApi api;
    public static SpotifyService spotifyService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_songs);

        startTime = System.nanoTime();

        koloda = findViewById(R.id.koloda);
        pb = findViewById(R.id.pbLoading);
        pb.setVisibility(ProgressBar.VISIBLE);
        animationView = new LottieAnimationView(GenerateSongsActivity.this);
        animationView.findViewById(R.id.animationView);
        animationView.pauseAnimation();

        Log.i("in export", "in export activity");
        String[] artists = getIntent().getStringArrayExtra("artists");

        Log.i(TAG, "artists length: " + artists.length);

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
                Toast.makeText(GenerateSongsActivity.this, "Leaving this song behind!",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCardSwipedRight(int i) {
                Log.i("koloda", "detected right swipe " + i);

                Toast.makeText(GenerateSongsActivity.this, "I'm keeping this song!",
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
                Intent i = new Intent(GenerateSongsActivity.this,
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
    }
}