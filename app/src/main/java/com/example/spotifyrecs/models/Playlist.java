package com.example.spotifyrecs.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.parceler.Parcel;

import java.util.List;

@ParseClassName("Playlist")
public class Playlist extends ParseObject {
    public static final String KEY_NAME = "name";
    public static final String KEY_SONGS = "songs";
    public static final String KEY_USER = "user";

    public List<Song> songs;
    public String name;

    public Playlist(){}

    /*
    public Playlist(String name, List<Song> songs){
        this.songs = songs;
        this.name = name;
    }
     */

    public String getName(){
        return getString(KEY_NAME);
    }

    public void setName(String name){
        put(KEY_NAME, name);
    }

    public JSONArray getSongs(){
        return getJSONArray(KEY_SONGS);
    }

    public void setSongs(JSONArray songs){
        put(KEY_SONGS, songs);
    }

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user){
        put(KEY_USER, user);
    }
}
