package com.example.spotifyrecs;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.spotifyrecs.models.Song;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.ContentApi;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.types.ImageUri;
import com.spotify.protocol.types.ListItem;
import com.spotify.protocol.types.ListItems;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SwipeSongAdapter extends RecyclerView.Adapter<SwipeSongAdapter.ViewHolder> {

    private Context context;
    private List<Song> songs;
    List<String> finalSongs = new ArrayList<>();
    int swiped = 0;
    private static final String CLIENT_ID = "f67855f9416e4ca999b13ec503540bc8";
    private static final String REDIRECT_URI = "http://localhost:8080";
    private SpotifyAppRemote mSpotifyAppRemote;

    public SwipeSongAdapter(Context context, List<Song> songs) {
        this.context = context;
        this.songs = songs;
    }

    @NonNull
    @Override
    public SwipeSongAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_swipe_song, parent,
                false);

        Log.i("adapter", "in create");
        return new SwipeSongAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Song song = songs.get(position);
        try {
            Log.i("adapter", "in bind");
            holder.bind(song);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return Math.min(songs.size(), 5);
    }

    public int numSwiped() {
        return swiped;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvTitle;
        TextView tvArtist;
        ImageButton ibPlay;
        ImageView ivCoverArt;
        View itemView;
        Boolean pressed = false;

        //These floats keep track of the coordinates of where the user's mouse is
        //to detect swipes
        private float x1_coord, x2_coord;
        //This represents the minimum amount of pixels moved which would signify
        // an intentional swipe
        static final int MIN_DISTANCE = 150;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvArtist = itemView.findViewById(R.id.tvArtist);
            ibPlay = itemView.findViewById(R.id.ibPlay);
            ivCoverArt = itemView.findViewById(R.id.ivCoverArt);

            this.itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.i("here", "in swipe");
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            x1_coord = event.getX();
                            break;
                        case MotionEvent.ACTION_UP:
                            x2_coord = event.getX();
                            float deltaX = x2_coord - x1_coord;
                            if (Math.abs(deltaX) > MIN_DISTANCE && deltaX > 0) {
                                Toast.makeText(v.getContext(), "I'm keeping this song!",
                                        Toast.LENGTH_SHORT).show();
                                finalSongs.add((String) tvTitle.getText());
                                swiped++;
                                Log.i("SwipeSong", String.valueOf(finalSongs.size()));
                                v.setVisibility(View.GONE);

                                if(numSwiped() == getItemCount()){
                                    Intent i = new Intent(v.getContext(),
                                            finalPlaylistActivity.class);
                                    i.putStringArrayListExtra("final songs",
                                            (ArrayList<String>) finalSongs);
                                    v.getContext().startActivity(i);
                                }

                            } else if (Math.abs(deltaX) > MIN_DISTANCE && deltaX < 0) {
                                Toast.makeText(v.getContext(), "Leaving this song behind!",
                                        Toast.LENGTH_SHORT).show();
                                v.setVisibility(View.GONE);
                                swiped++;
                                if(numSwiped() == getItemCount()){
                                    Intent i = new Intent(v.getContext(),
                                            finalPlaylistActivity.class);
                                    i.putStringArrayListExtra("final songs",
                                            (ArrayList<String>) finalSongs);
                                    v.getContext().startActivity(i);
                                }
                            }
                            break;
                    }
                    return true;
                }
            });
        }

        public Bitmap getBitmapFromURL(String src) {
            try {
                URL url = new URL(src);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                return BitmapFactory.decodeStream(input);
            } catch (IOException e) {
                // Log exception
                return null;
            }
        }

        public void bind(Song song) throws JSONException {
            Log.i("adapter", "in item bind");
            if(song.imageString != null){
                Glide.with(context).load(song.imageString).into(ivCoverArt);
            }

            startPlay(song, false);
            tvTitle.setText(song.title);
            tvArtist.setText(song.artist);

            ibPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startPlay(song, true);
                }
            });
        }

        @Override
        public void onClick(View v) {
          //  Log.i("adapter", "fsda");
        }

        protected void startPlay(Song s, Boolean onClick) {
            //    super.onStart();

            ConnectionParams connectionParams =
                    new ConnectionParams.Builder(CLIENT_ID)
                            .setRedirectUri(REDIRECT_URI)
                            .showAuthView(true)
                            .build();

            SpotifyAppRemote.connect(context, connectionParams,
                    new Connector.ConnectionListener() {

                        @Override
                        public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                            mSpotifyAppRemote = spotifyAppRemote;
                            Log.d("Adapter", "Connected! Yay!");
                            // Now you can start interacting with App Remote
                            connected(s, onClick);
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            Log.e("Adapter", throwable.getMessage(), throwable);
                            // Something went wrong when attempting to connect! Handle errors here
                        }
                    });
        }

        private void connected(Song s, Boolean onClick) {
            if(onClick) {
                if (!pressed) {
                    // Play a playlist
                    mSpotifyAppRemote.getPlayerApi().play(s.uri);
                    pressed = true;
                } else {
                    mSpotifyAppRemote.getPlayerApi().pause();
                    pressed = false;
                }
            }
            else {
                if (s.imageString != null) {
                    ImageUri newUri = new ImageUri(s.imageString);
                    CallResult<Bitmap> bitmapCallResult = mSpotifyAppRemote
                            .getImagesApi().getImage(newUri);
                    bitmapCallResult.setResultCallback(new CallResult.ResultCallback<Bitmap>() {
                        @Override
                        public void onResult(Bitmap data) {
                            Palette palette = Palette.from(data).generate();
                            Palette.Swatch vibrant = palette.getVibrantSwatch();
                            if (vibrant != null) {
                                // Set the background color of a layout based on the vibrant color
                                itemView.setBackgroundColor(vibrant.getRgb());
                            }
                        }
                    });
                } else {
                    CallResult<Bitmap> bitmapCallResult = mSpotifyAppRemote
                            .getImagesApi().getImage(s.imageLink);
                    bitmapCallResult.setResultCallback(new CallResult.ResultCallback<Bitmap>() {
                        @Override
                        public void onResult(Bitmap data) {
                            Palette palette = Palette.from(data).generate();
                            Palette.Swatch vibrant = palette.getVibrantSwatch();
                            if (vibrant != null) {
                                // Set the background color of a layout based on the vibrant color
                                itemView.setBackgroundColor(vibrant.getRgb());
                            }
                            ivCoverArt.setImageBitmap(data);
                        }
                    });
                }
            }
        }

      //  @Override
        protected void onStop() {
            // Aaand we will finish off here.
            SpotifyAppRemote.disconnect(mSpotifyAppRemote);
        }
    }
}
