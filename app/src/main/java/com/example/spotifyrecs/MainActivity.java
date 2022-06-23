package com.example.spotifyrecs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.spotifyrecs.models.Song;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.ContentApi;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.Item;
import com.spotify.protocol.types.ListItem;
import com.spotify.protocol.types.ListItems;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final String CLIENT_ID = "f67855f9416e4ca999b13ec503540bc8";
    private static final String REDIRECT_URI = "http://localhost:8080";
    private SpotifyAppRemote mSpotifyAppRemote;
    BottomNavigationView bottomNavigationView;
    Button btnNewPlaylist;
    Button btnOldPlaylist;
    Button btnSpotifyAlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        btnNewPlaylist = findViewById(R.id.btnNewPlaylist);
        btnOldPlaylist = findViewById(R.id.btnOldPlaylist);
        btnSpotifyAlg = findViewById(R.id.btnSpotifyAlg);

        bottomNavigationView.setOnItemSelectedListener(
                new BottomNavigationView.OnItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        Fragment fragment;
                        switch (menuItem.getItemId()) {
                            case R.id.action_logout:
                                onLogout();
                            default: return true;
                        }
                    }
                });

        btnSpotifyAlg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toNewPlaylists();
            }
        });
    }

    private void toNewPlaylists() {
        Intent i = new Intent(MainActivity.this, SwipeSongsActivity.class);
        startActivity(i);
    }

    private void toNewPlaylists(ArrayList<Song> songs) {
        Intent i = new Intent(MainActivity.this, SwipeSongsActivity.class);
       // i.putParcelableArrayListExtra("songs", songs);
        startActivity(i);
    }

    private void onLogout() {
        Toast.makeText(MainActivity.this, "logging out", Toast.LENGTH_LONG).show();
        //  InstaClone.getRestClient(this).clearAccessToken();
        // navigate backwards to Login screen
        Intent i = new Intent(this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // this makes sure the Back button won't work
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // same as above
        startActivity(i);
        ParseUser.logOutInBackground();

        //Spotify
        onStop();

        ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null
        finish();
    }

    /*
    @Override
    protected void onStart() {
        super.onStart();

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
        // Then we will write some more code here.

        // Play a playlist
     //   mSpotifyAppRemote.getPlayerApi().play("spotify:playlist:37i9dQZF1DX2sUQwD7tbmL");

        List<Song> songs = new ArrayList<>();

        // This gets the content asynchronously in recommended
        CallResult<ListItems> listItemsCallResult = mSpotifyAppRemote.getContentApi()
                .getRecommendedContentItems(ContentApi.ContentType.DEFAULT);
        listItemsCallResult.setResultCallback(new CallResult.ResultCallback<ListItems>() {
            @Override
            public void onResult(ListItems data) {
                if(data != null){
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
                                    if (data != null) {
                                        ListItem[] items1 = data.items;

                                        for (ListItem item1 : items1) {

                                            if (item1.uri.contains("playlist") || item1.uri.contains("track")) {
                                                Song currSong = new Song();
                                                currSong.title = item1.title;
                                                currSong.uri = item1.uri;
                                                currSong.artist = item1.subtitle;

                                                songs.add(currSong);
                                                Log.i("songs leng", String.valueOf(songs.size()));
                                                Log.i("fsd", "inside + " + item1.toString() + " and title: " + item1.toString());
                                            }

                                            Log.i("fsd", "asdf + " + item1.toString());
                                        }

                                        if(finalI == items.length - 1){
                                            Log.i("asdf", "mainactivity songs check: " + songs.size());
                                          //  onStop();
                                            toNewPlaylists((ArrayList<Song>) songs);
                                        }
                                    }
                                }
                            });
                            Log.i("asdf", "sd" + items[i].title);
                    }
                }
            }
        });

       /* for(Item item : newItems.items){
            Log.i("fsddfdfs fdsdfs", "This is one item: " + item.toString());
        }
        */

        /*
        Log.i("attempt", "asdfasdf " + mSpotifyAppRemote.getContentApi()
                .getRecommendedContentItems("default").toString());
         */

        /*
        SpotifyApi api = new SpotifyApi();

// Most (but not all) of the Spotify Web API endpoints require authorisation.
// If you know you'll only use the ones that don't require authorisation you can skip this step
      //  api.setAccessToken(response.get);

        SpotifyService spotify = api.getService();

        spotify.getAlbum("2dIGnmEIy1WZIcZCFSj6i8", new Callback<Album>() {
            @Override
            public void success(Album album, Response response) {
                Log.d("Album success", album.name);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("Album failure", error.toString());
            }
        });
    }

         */
       // toNewPlaylists(songs.toArray(new String[songs.size()]));
    /*
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Aaand we will finish off here.
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }
    */
}