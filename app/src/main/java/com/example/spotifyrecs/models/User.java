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

/*

@ParseClassName("Post")
@Parcel(analyze = Post.class)
public class Post extends ParseObject {

    public static final String KEY_DESCRIPTION = "Description";
    public static final String KEY_IMAGE = "Image";
    public static final String KEY_USER = "user";

    public Post() {}

    public String getDescription(){
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description){
        put(KEY_DESCRIPTION, description);
    }

    public ParseFile getImage(){
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile parseFile){
        put(KEY_IMAGE, parseFile);
    }

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user){
        put(KEY_USER, user);
    }

 */