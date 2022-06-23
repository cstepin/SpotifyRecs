package com.example.spotifyrecs.models;

import com.spotify.protocol.types.ImageUri;

public class Song {
    public String title;
    public String artist;
    public String uri;
    public ImageUri imageLink;

    public String getTitle() {
        return title;
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

    public Song(){}
}
