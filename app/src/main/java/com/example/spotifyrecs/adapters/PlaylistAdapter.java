package com.example.spotifyrecs.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifyrecs.R;
import com.example.spotifyrecs.finalPlaylistActivity;
import com.example.spotifyrecs.models.Playlist;
import com.example.spotifyrecs.models.Song;

import org.json.JSONException;
import org.parceler.Parcels;

import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {
    List<Playlist> playlists;
    private Context context;

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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            tvTitle = itemView.findViewById(R.id.tvTitle);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    // make sure the position is valid, i.e. actually exists in the view
                    if (position != RecyclerView.NO_POSITION) {

                        Log.i("playlist adapter", "in if onclick");
                        // get the movie at the position, this won't work if the class is static
                        Playlist playlist = playlists.get(position);
                        // create intent for the new activity
                        Intent intent = new Intent(context, finalPlaylistActivity.class);
                        // serialize the movie using parceler, use its short name as a key
                        intent.putExtra("details", true);
                        intent.putExtra("final songs",
                                Parcels.wrap(playlist.getSongs()));
                        // show the activity
                        context.startActivity(intent);
                    }
                }
            });
            /*
            tvTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, finalPlaylistActivity.class);
                    i.putExtra("details", true);
                  //  getContext().startActivity();
                }
            });
             */
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
                intent.putExtra("final songs",
                        Parcels.wrap(playlist.getSongs()));
                // show the activity
                context.startActivity(intent);
            }
        }

        public void bind(Playlist playlist) {
            Log.i("Binding", "Binding the current title: " + playlist.getName());
            tvTitle.setText(playlist.getName());
        }
    }
}
