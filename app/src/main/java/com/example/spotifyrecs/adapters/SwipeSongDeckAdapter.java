package com.example.spotifyrecs.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.spotifyrecs.R;
import com.example.spotifyrecs.models.Song;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import java.util.ArrayList;
import java.util.List;

public class SwipeSongDeckAdapter extends BaseAdapter {

    private Context context;
    private List<Song> songs;

    public SwipeSongDeckAdapter(Context context, List<Song> songs) {
        this.context = context;
        this.songs = songs;
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int position) {
        return songs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
     //   TextView tvTitle;
     //   TextView tvArtist;

        // in get view method we are inflating our layout on below line.
        View v = convertView;
        if (v == null) {
            // on below line we are inflating our layout.
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_song, parent,
                    false);
        }

        // on below line we are initializing our variables and setting data to our variables.
        ((TextView) v.findViewById(R.id.tvTitle)).setText(songs.get(position).getTitle());
        ((TextView) v.findViewById(R.id.tvArtist)).setText(songs.get(position).getArtist());

        return v;
    }
}
