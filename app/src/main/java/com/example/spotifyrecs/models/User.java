package com.example.spotifyrecs.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;

@ParseClassName("_User")
public class User extends ParseUser {

    public static final String KEY_ARTISTS = "artists";

    public User() {}

    public JSONArray getArtists(){
        return getJSONArray(KEY_ARTISTS);
    }

    public void setArtists(String[] artists){
        put(KEY_ARTISTS, artists);
    }
}