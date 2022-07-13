package com.example.spotifyrecs.adapters;

import static com.example.spotifyrecs.resources.Resources.getClientId;
import static com.example.spotifyrecs.resources.Resources.getRedirectUrl;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.palette.graphics.Palette;

import com.example.spotifyrecs.R;
import com.example.spotifyrecs.models.Song;
import com.parse.ParseUser;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.types.ImageUri;

import org.json.JSONArray;

import java.util.List;

public class SwipeSongDeckAdapter extends BaseAdapter {

    TextView tvTitle;
    TextView tvArtist;
    ImageView ivCoverArt;
    ImageButton ibPlay;
    Boolean pressed = false;

    private Context context;
    private List<Song> songs;
    private SpotifyAppRemote mSpotifyAppRemote;

    public SwipeSongDeckAdapter(Context context, List<Song> songs) {
        this.context = context;
        this.songs = songs;
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Song getItem(int position) {
        return songs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // in get view method we are inflating our layout on below line.
        View v = convertView;
        if (v == null) {
            // on below line we are inflating our layout.
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_song, parent,
                    false);
        }

        // on below line we are initializing our variables and setting data to our variables.
        tvArtist = (TextView) v.findViewById(R.id.tvArtist);
        tvTitle = (TextView) v.findViewById(R.id.tvTitle);
        ivCoverArt = (ImageView) v.findViewById(R.id.ivCoverArt);
        ibPlay = (ImageButton) v.findViewById(R.id.ibPlay);

        ibPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPlay((Song) getItem(position), true, v);
            }
        });

        startPlay((Song) getItem(position), false, v);
        tvArtist.setText(songs.get(position).getArtist());
        tvTitle.setText(songs.get(position).getTitle());

        // (ImageView) v.findViewById(R.id.ivCoverArt)...;

        return v;
    }

    public void startPlay(Song s, Boolean onClick, View v) {

        ConnectionParams connectionParams =
                new ConnectionParams.Builder(getClientId())
                        .setRedirectUri(getRedirectUrl())
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(context, connectionParams,
                new Connector.ConnectionListener() {
                    //Connects with the Android SDK
                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.d("Adapter", "Connected! Yay!");
                        // Now you can start interacting with App Remote
                        connected(s, onClick, v);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e("Adapter", throwable.getMessage(), throwable);
                    }
                });
    }

    private void connected(Song s, Boolean onClick, View v) {
        if (onClick) {
            if (!pressed) {
                // Play a song
                mSpotifyAppRemote.getPlayerApi().play(s.uri);
                pressed = true;
            } else {
                mSpotifyAppRemote.getPlayerApi().pause();
                pressed = false;
            }
        } else {
            //Sets up background by converting image to bitmap then finding the brightest color
            // and setting the background to be that color
            Log.i("adapter", "in image string: " + s.imageString);
            ImageUri newUri = new ImageUri(s.imageString);
            CallResult<Bitmap> bitmapCallResult = mSpotifyAppRemote.getImagesApi()
                    .getImage(newUri);
            bitmapCallResult.setResultCallback(data -> {
                Log.i("adapter", "in on result bit map");
                Palette palette = Palette.from(data).generate();
                Palette.Swatch vibrant = palette.getVibrantSwatch();
                if (vibrant != null) {
                    // Set the background color of a layout based on the vibrant color
                    v.setBackgroundColor(vibrant.getRgb());
                }
                ivCoverArt.setImageBitmap(data);
            });
        }
    }

    /*
    private void onSongClick(View v, MotionEvent event, long lastClickTime){
        final long DOUBLE_CLICK_TIME_DELTA = 300;//milliseconds
        long clickTime = System.currentTimeMillis();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA){
                    Log.i("here2", "in double tap with delta time: " + (clickTime
                            - lastClickTime));
                    onDoubleClick(v);
                }
                break;
        }
    }

    private void onDoubleClick(View v) {
        Log.i("In double click2", "double click noticed");
        Song song = new Song();
        song.artist = (String) tvArtist.getText();
        song.title = (String) tvTitle.getText();
        //  Log.i("in double click3", "this is song: " + song.toString());
        faveSongs.add(song.title);
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
     */
}
