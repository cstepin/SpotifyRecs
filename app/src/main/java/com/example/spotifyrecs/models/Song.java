package com.example.spotifyrecs.models;

import com.spotify.protocol.types.ImageUri;

import org.parceler.Parcel;
import org.parceler.TypeRangeParcelConverter;

import java.lang.annotation.Annotation;

@Parcel
public class Song {
    public String title;
    public String artist;
    public String uri;
  //  public ImageUri imageLink;
    public String imageString;

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

    public Song(){}
}
