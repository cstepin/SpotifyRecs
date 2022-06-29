package com.example.spotifyrecs.models;

import com.parse.ParseObject;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class Playlist extends ParseObject {
    public List<Song> songs;
    public String name;

    public Playlist(){}

    public Playlist(String name, List<Song> songs){
        this.songs = songs;
        this.name = name;
    }
}
