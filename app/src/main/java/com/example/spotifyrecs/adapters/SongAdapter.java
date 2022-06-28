package com.example.spotifyrecs.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifyrecs.R;
import com.example.spotifyrecs.models.Song;

import org.json.JSONException;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {

    private Context context;
    private List<Song> songs;

    public SongAdapter(Context context, List<Song> songs) {
        this.context = context;
        this.songs = songs;
    }

    @NonNull
    @Override
    public SongAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_song, parent, false);
        return new ViewHolder(view);
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
        return songs.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvTitle;
        TextView tvArtist;
        View itemView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvArtist = itemView.findViewById(R.id.tvArtist);
        }

        public void bind(Song song) throws JSONException {
            tvTitle.setText(song.title);
            if(!song.artist.equals("")) {
                tvArtist.setText(song.artist);
            }
            else{
                tvArtist.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            // make sure the position is valid, i.e. actually exists in the view
            if (position != RecyclerView.NO_POSITION) {
                Log.i("songadapter", "in if onclick");
                // get the movie at the position, this won't work if the class is static
                Song song = songs.get(position);
                // create intent for the new activity
                Toast.makeText(context, "Song: " + song, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
