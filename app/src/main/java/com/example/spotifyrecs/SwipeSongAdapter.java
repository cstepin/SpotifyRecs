package com.example.spotifyrecs;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.spotifyrecs.models.Song;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.ContentApi;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.types.ListItem;
import com.spotify.protocol.types.ListItems;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

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
        return new SwipeSongAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Song song = songs.get(position);
        try {
            holder.bind(song);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if(songs.size() < 5){
            return songs.size();
        }

        return 5;
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

        private float x1, x2;
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
                  //  Log.i("here", "in here here");
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                         //   Log.i("down", "in here");
                            x1 = event.getX();
                            break;
                        case MotionEvent.ACTION_UP:
                        //    Log.i("up", "in here 2");
                            x2 = event.getX();
                            float deltaX = x2 - x1;
                            if (Math.abs(deltaX) > MIN_DISTANCE && deltaX > 0) {
                             //   Log.i("up", "in here 3");
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
                                Log.i("up", "in here 4");
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

        public void bind(Song song) throws JSONException {
            tvTitle.setText(song.title);
            tvArtist.setText(song.artist);

            ibPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startPlay(song, true);
                }
            });

            startPlay(song, false);
        }

        @Override
        public void onClick(View v) {
            Log.i("adapter", "fsda");
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
            else{
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
                            // Update the title TextView with the proper text color
                          //  titleView.setTextColor(vibrant.getTitleTextColor());
                        }
                        ivCoverArt.setImageBitmap(data);
                    }
                });
            }
        }

      //  @Override
        protected void onStop() {
        //    super.onStop();
            // Aaand we will finish off here.
            SpotifyAppRemote.disconnect(mSpotifyAppRemote);
        }

        /*
        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            // make sure the position is valid, i.e. actually exists in the view
            if (position != RecyclerView.NO_POSITION) {
                Log.i("songadapter", "in if onclick");
                // get the movie at the position, this won't work if the class is static
                String song = songs.get(position);
                // create intent for the new activity
                Toast.makeText(context, "Song: " + song, Toast.LENGTH_SHORT).show();
            }
        }
         */
    }
}
