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
        return Math.min(songs.size(), 5);
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
        Log.i("length", "length2: " + getCount());

        // in get view method we are inflating our layout on below line.
        View v = convertView;
        if (v == null) {
            // on below line we are inflating our layout.
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_song, parent,
                    false);
        }

        // on below line we are initializing our variables and setting data to our variables.
        tvArtist = v.findViewById(R.id.tvArtist);
        tvTitle = v.findViewById(R.id.tvTitle);
        ivCoverArt = v.findViewById(R.id.ivCoverArt);
        ibPlay = v.findViewById(R.id.ibPlay);

        startPlay(getItem(position), false, v);

        ibPlay.setOnClickListener(v1 -> startPlay(getItem(position), true, v1));

        tvArtist.setText(songs.get(position).getArtist());
        tvTitle.setText(songs.get(position).getTitle());

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
                ((ImageView) v.findViewById(R.id.ivCoverArt)).setImageBitmap(data);
            });
        }
    }
}
