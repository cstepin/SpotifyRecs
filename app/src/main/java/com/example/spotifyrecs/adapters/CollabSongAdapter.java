package com.example.spotifyrecs.adapters;

import static com.example.spotifyrecs.resources.Resources.getClientId;
import static com.example.spotifyrecs.resources.Resources.getRedirectUrl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.spotifyrecs.AnalyzeRecommendActivity;
import com.example.spotifyrecs.ExportActivity;
import com.example.spotifyrecs.R;
import com.example.spotifyrecs.finalPlaylistActivity;
import com.example.spotifyrecs.models.Song;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.types.ImageUri;

import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class CollabSongAdapter extends RecyclerView.Adapter<CollabSongAdapter.ViewHolder> {
    private Context context;
    private List<Song> songs;
    private List<Song> faveSongs;
    float[] user_x_rating_raw = new float[11];
    int user_rating_index = 1;
    List<Song> finalSongs = new ArrayList<>();
    int swiped = 0;
    private SpotifyAppRemote mSpotifyAppRemote;
    //These floats keep track of the coordinates of where the user's mouse is
    //to detect swipes
    static float x1_coord, x2_coord;

    public CollabSongAdapter(Context context, List<Song> songs) {
        this.context = context;
        this.songs = songs;
    }

    @NonNull
    @Override
    public CollabSongAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        user_x_rating_raw[0] = 11.0F;
        View view = LayoutInflater.from(context).inflate(R.layout.item_collab_song, parent,
                false);

        Log.i("adapter", "in create");
        return new CollabSongAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CollabSongAdapter.ViewHolder holder, int position) {
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
        Button btnIgnore;
        ImageView ivCoverArt;
        View itemView;
        Boolean pressed = false;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvArtist = itemView.findViewById(R.id.tvArtist);
            ibPlay = itemView.findViewById(R.id.ibPlay);
            ivCoverArt = itemView.findViewById(R.id.ivCoverArt);
            btnIgnore = itemView.findViewById(R.id.btnIgnore);
        }

        @SuppressLint("ClickableViewAccessibility")
        public void bind(Song song) throws JSONException {
            Log.i("adapter", "in item bind");
            //set up the image
            if(song.imageString != null){
                Glide.with(context).load(song.imageString).into(ivCoverArt);
            }

            //this sets-up the backgrounds of each item view
            startPlay(song, false);
            tvTitle.setText(song.title);
            tvArtist.setText(song.artist);

            //otherwise this sets on an click listener for the play button
            ibPlay.setOnClickListener(v -> startPlay(song, true));

            AtomicLong lastClickTime = new AtomicLong();
            lastClickTime.set(0);

            btnIgnore.setOnClickListener(this::ignoreClicked);

            this.itemView.setOnTouchListener((v, event) -> {
                onSongClick(v, event, lastClickTime.get(), song);
                lastClickTime.set(System.currentTimeMillis());
                return true;
            });
        }

        private void ignoreClicked(View v) {
            user_x_rating_raw[user_rating_index] = 0.0F;
            user_rating_index++;

            if(user_rating_index == 10){
                Intent i = new Intent(v.getContext(),
                        AnalyzeRecommendActivity.class);
                i.putExtra("floats", user_x_rating_raw);
                v.getContext().startActivity(i);
            }
        }

        //an override method because it's required (we're only going to be swiping on image
        // views, so we don't necessarily need this
        @Override
        public void onClick(View v) {
        }

        //This function if set by the on Click turns on the specific song that the listener
        // wanted to listen to (or turns off
        //If sent by the general call, just sets up the background color for the item view
        protected void startPlay(Song s, Boolean onClick) {
            //    super.onStart();

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
                //Sets up background by converting image to a bitmap then finding the brightest color
                // and setting the background to be that color
                Log.i("adapter", "in has image string with image string: " + s.imageString);
                ImageUri newUri = new ImageUri(s.imageString);
                CallResult<Bitmap> bitmapCallResult = mSpotifyAppRemote.getImagesApi()
                        .getImage(newUri);
                bitmapCallResult.setResultCallback(data -> {
                    Log.i("adapter", "in on result bit map");
                    Palette palette = Palette.from(data).generate();
                    Palette.Swatch vibrant = palette.getVibrantSwatch();
                    if (vibrant != null) {
                        // Set the background color of a layout based on the vibrant color
                        itemView.setBackgroundColor(vibrant.getRgb());
                    }
                    ivCoverArt.setImageBitmap(data);
                });
            }
        }

        protected void onStop() {
            // Aaand we will finish off here.
            SpotifyAppRemote.disconnect(mSpotifyAppRemote);
        }

        private void onSongClick(View v, MotionEvent event, long lastClickTime, Song s){
            final long DOUBLE_CLICK_TIME_DELTA = 300;//milliseconds

            //  long lastClickTime = 0;
            //This represents the minimum amount of pixels moved which would signify
            // an intentional swipe
            final int MIN_DISTANCE = 150;

            Log.i("here", "in swipe");
            long clickTime = System.currentTimeMillis();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x1_coord = event.getX();
                    if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA){
                        Log.i("here2", "in double tap with delta time: " + (clickTime - lastClickTime));
                        onDoubleClick(v, s);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    x2_coord = event.getX();
                    //We calculate the distance between where the user pressed down
                    // and then released up
                    float deltaX = x2_coord - x1_coord;
                    Log.i("this was", "this was the distinace travelled: " + deltaX);
                    //If we detect a right swipe...
                    if (Math.abs(deltaX) > MIN_DISTANCE && deltaX > 0) {
                        Toast.makeText(v.getContext(), "I'm like this song!",
                                Toast.LENGTH_SHORT).show();
                        user_x_rating_raw[user_rating_index] = 1.0F;
                        user_rating_index++;
                        //This keeps track of how many songs have been reacted to already
                        //To-do: add a way to not swipe all songs and go to the next activity
                        swiped++;
                        Log.i("SwipeSong", String.valueOf(finalSongs.size()));
                        //To-do: figure out how to ensure the screen moves when a song is swiped
                        v.setVisibility(View.GONE);

                        //We check if we've swiped the correct number of songs.
                        if(user_rating_index == 10){
                            Intent i = new Intent(v.getContext(),
                                    AnalyzeRecommendActivity.class);

                            i.putExtra("floats", user_x_rating_raw);
                            v.getContext().startActivity(i);
                        }

                        //Else if we detect a left swipe...
                    } else if (Math.abs(deltaX) > MIN_DISTANCE && deltaX < 0) {
                        Toast.makeText(v.getContext(), "This is not my taste!",
                                Toast.LENGTH_SHORT).show();
                        user_x_rating_raw[user_rating_index] = -1.0F;
                        user_rating_index++;
                        //This means we ignore the song.
                        v.setVisibility(View.GONE);
                        swiped++;
                        if(user_rating_index == 10){
                            Intent i = new Intent(v.getContext(),
                                    AnalyzeRecommendActivity.class);
                            i.putExtra("floats", user_x_rating_raw);
                           /* i.putExtra("final songs",
                                    Parcels.wrap(finalSongs));
                            */
                            v.getContext().startActivity(i);
                        }
                    }
                    break;
            }
        }
    }

    private void onDoubleClick(View v, Song s) {
        Log.i("In double click2", "double click noticed");
        v.setBackgroundColor(Color.parseColor("#000000"));
        // faveSongs.add(s);
    }
}
