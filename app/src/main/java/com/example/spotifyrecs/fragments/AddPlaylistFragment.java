package com.example.spotifyrecs.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.spotifyrecs.R;
import com.example.spotifyrecs.models.Playlist;
import com.example.spotifyrecs.models.Song;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class AddPlaylistFragment extends DialogFragment {
    EditText etPlaylistName;
    Button btnAddPlaylist;
    List<Song> songs;

    public AddPlaylistFragment() {
        // Required empty public constructor
    }

    public AddPlaylistFragment(List<Song> songs) {
        this.songs = songs;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_playlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etPlaylistName = view.findViewById(R.id.etPlaylistName);
        btnAddPlaylist = view.findViewById(R.id.btnSubmitPlaylist);

        btnAddPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPlaylist(songs);
            }
        });
    }

    private void addPlaylist(List<Song> songs) {
        for(Song song : songs){
            Log.i("tag tag", "curr song is: " + song);
        }
        Playlist newPlaylist = new Playlist();
        newPlaylist.name = etPlaylistName.getText().toString();
        newPlaylist.songs = songs;

        Log.i("tag tag ", "new playlist name: " + etPlaylistName.getText().toString());

        JSONArray currPlaylists = ParseUser.getCurrentUser().getJSONArray("playlists");


      //    Log.i("tag tag ", "new playlist name: " + etPlaylistName.getText().toString());

        assert currPlaylists != null;
        currPlaylists.put(newPlaylist);
        ParseUser.getCurrentUser().put("playlists", currPlaylists);
        ParseUser.getCurrentUser().saveInBackground(e -> {
            if(e != null){
                Log.e("AddPlaylistFragment", "error saving playlists", e);
            }
            else{
                Log.i("Addplaylistfragment", "playlists saved successfully");
            }
        });
    }
}