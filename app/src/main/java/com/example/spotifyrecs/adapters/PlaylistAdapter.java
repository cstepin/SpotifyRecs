package com.example.spotifyrecs.adapters;

import static android.app.Activity.RESULT_OK;

import static com.example.spotifyrecs.resources.Resources.getClientId;
import static com.example.spotifyrecs.resources.Resources.getRedirectUrl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.spotifyrecs.ExportActivity;
import com.example.spotifyrecs.R;
import com.example.spotifyrecs.finalPlaylistActivity;
import com.example.spotifyrecs.models.Playlist;
import com.example.spotifyrecs.models.Song;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.types.ImageUri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {
    List<Playlist> playlists;
    private Context context;
    final String TAG = "PlaylistAdapter";

    //To display images
    private SpotifyAppRemote mSpotifyAppRemote;

    public PlaylistAdapter(Context context, List<Playlist> playlists) {
        this.context = context;
        this.playlists = playlists;
    }

    @NonNull
    @Override
    public PlaylistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_playlist, parent, false);
        return new PlaylistAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistAdapter.ViewHolder holder, int position) {
        Playlist playlist = playlists.get(position);
        holder.bind(playlist);
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvTitle;
        View itemView;
        ImageButton ibExport;
        ImageButton ibDelete;
        ImageView ivPlaylistCover;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            ibExport = itemView.findViewById(R.id.ibExport);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            ibDelete = itemView.findViewById(R.id.ibDelete);
            ivPlaylistCover = itemView.findViewById(R.id.ivPlaylistCover);
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(v.getContext(), "I've been clicked", Toast.LENGTH_SHORT).show();
            int position = getAdapterPosition();
            // make sure the position is valid, i.e. actually exists in the view
            if (position != RecyclerView.NO_POSITION) {

                Log.i("postsadapter", "in if onclick");
                // get the movie at the position, this won't work if the class is static
                Playlist playlist = playlists.get(position);
                // create intent for the new activity
                Intent intent = new Intent(context, finalPlaylistActivity.class);
                // serialize the movie using parceler, use its short name as a key
                intent.putExtra("details", true);
                // show the activity
                context.startActivity(intent);
            }
        }

        public Song fromJson(JSONObject jsonObject) throws JSONException{
            Song song = new Song();

            song.title = (String) jsonObject.get("title");
            song.artist = (String) jsonObject.get("artist");

            return song;
        }

        public void bind(Playlist playlist) {
            Log.i("Binding", "Binding the current title: " + playlist.getName());
            tvTitle.setText(playlist.getName());

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                // make sure the position is valid, i.e. actually exists in the view
                if (position != RecyclerView.NO_POSITION) {

                    Log.i("playlist adapter", "in if onclick");
                    Playlist playlist1 = playlists.get(position);
                    // create intent for the new activity
                    Intent intent = new Intent(context, finalPlaylistActivity.class);
                    intent.putExtra("details", true);

                    //Convert each song in the JSONArray to a Song object
                    List<Song> songs = new ArrayList<>();
                    for(int i = 0; i < playlist1.getSongs().length(); i++){
                        try {
                            songs.add(fromJson(playlist1.getSongs().getJSONObject(i)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    intent.putExtra("final songs",
                            Parcels.wrap(songs));
                    context.startActivity(intent);
                }
            });

            //to set image
            startPlay(playlist);

            ibExport.setOnClickListener(v -> onExport(playlist));

            ibDelete.setOnClickListener(v -> {
                int position = getAdapterPosition();
                // make sure the position is valid, i.e. actually exists in the view
                if (position != RecyclerView.NO_POSITION) {

                    Log.i("playlist adapter", "in if onclick");
                    try {
                        onDelete(playlist, position);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }


        public void startPlay(Playlist playlist) {

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
                            connected(playlist);
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            Log.e("Adapter", throwable.getMessage(), throwable);
                        }
                    });
        }

        private void connected(Playlist playlist) {
                //Sets up background by converting image to bitmap then finding the brightest color
                // and setting the background to be that color
                ImageUri newUri = new ImageUri(playlist.getPlaylistCover());
                CallResult<Bitmap> bitmapCallResult = mSpotifyAppRemote.getImagesApi()
                        .getImage(newUri);
                bitmapCallResult.setResultCallback(data -> ivPlaylistCover.setImageBitmap(data));
            }

        void onDelete(Playlist playlist, int position) throws JSONException {

            //First, delete playlist id from user
            JSONArray currPlaylists = ParseUser.getCurrentUser().getJSONArray("playlists");

            String playlistID = playlist.getObjectId();

            Log.i(TAG, "playlist id is: " + playlist.getObjectId());

            assert currPlaylists != null;

            for(int i = 0; i < currPlaylists.length(); i++){
                if(currPlaylists.get(i).toString().contains(playlistID)){
                        Log.i(TAG, "in here lksdjflaksdjf");
                        currPlaylists.remove(i);
                }
            }

            ParseUser.getCurrentUser().put("playlists", currPlaylists);
            ParseUser.getCurrentUser().saveInBackground(e -> {
                if(e != null){
                    Log.e(TAG, "error deleting playlists", e);
                }
                else{
                    Log.i(TAG, "playlists deleted successfully");
                }
            });

            // specify what type of data we want to query - Playlist.class
            ParseQuery<Playlist> query = ParseQuery.getQuery(Playlist.class);
            // include data referred by user key
            // limit query to latest 20 items
            query.setLimit(50);
            // order posts by creation date (newest first)
            query.addDescendingOrder("createdAt");
            // start an asynchronous call for posts
            query.findInBackground((playlists, e) -> {
                // check for errors
                if (e != null) {
                    Log.e("TAG", "Issue with getting playlists", e);
                    return;
                }

                for(Playlist playlist1 : playlists){
                    if(playlist1.getObjectId().equals(playlist.getObjectId())){
                        playlist1.deleteInBackground(e1 -> {
                            if(e1 != null){
                                Log.e(TAG, "Issue with deleting playlist", e1);
                                return;
                            }

                            Log.i(TAG, "successfully deleted playlist");
                        });
                    }
                }
            });

            Log.i(TAG, "position is: " + position);
            playlists.remove(position);
            notifyItemRemoved(position);
        }

        void onExport(Playlist playlist){
            Intent intent = new Intent(context, ExportActivity.class);
            intent.putExtra("playlist name", playlist.getName());
            List<Song> songs = new ArrayList<>();
            for(int i = 0; i < playlist.getSongs().length(); i++){
                Song song = new Song();
                try {
                    songs.add(fromJson(playlist.getSongs().getJSONObject(i)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            intent.putExtra("songs", Parcels.wrap(songs));
            context.startActivity(intent);
        }
    }

    // Clean all elements of the recycler
    @SuppressLint("NotifyDataSetChanged")
    public void clear() {
        playlists.clear();
        notifyDataSetChanged();
    }
}
