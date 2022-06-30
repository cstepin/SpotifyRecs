package com.example.spotifyrecs.adapters;

import android.content.Context;
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
import com.example.spotifyrecs.models.Playlist;
import com.example.spotifyrecs.models.Song;

import org.json.JSONException;

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
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(v.getContext(), "I've been clicked", Toast.LENGTH_SHORT).show();
        }

        public void bind(Playlist playlist) {
            Log.i("Binding", "Binding the current title: " + playlist.getName());
            tvTitle.setText(playlist.getName());
        }
    }
}
