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
    public static final String KEY_ID = "objectId";
    public static String id;

    public String playlistCover;

    public String getId() {
        return getString("objectId");
    }

   /*
    public void setId(String id) {
        {
            put(KEY_ID, id);;
        }
    */

    public void setPlaylistCover(String playlistCover) {
        this.playlistCover = playlistCover;
    }

    public String getPlaylistCover() {
        return playlistCover;
    }

    public Playlist(){}

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
