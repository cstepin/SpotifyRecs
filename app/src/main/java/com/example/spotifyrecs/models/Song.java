package com.example.spotifyrecs.models;

import androidx.annotation.NonNull;

import com.spotify.protocol.types.ImageUri;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;
import org.parceler.TypeRangeParcelConverter;

import java.lang.annotation.Annotation;

@Parcel
public class Song {
    public String title;
    public String artist;
    public String uri;
    public String imageString;
    public Boolean visible;
    public int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Song(String title, String artist){
        this.title = title;
        this.artist = artist;
    }

    public Song(){}

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public String getTitle() {
        return title;
    }

    public String getImageString() {
        return imageString;
    }

    public void setImageString(String imageString) {
        this.imageString = imageString;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("title", title);
        jo.put("artist", artist);

        return jo;
    }

    @NonNull
    @Override
    public String toString(){
        return "Title: " + title + " and artist: " + artist;
    }
}
