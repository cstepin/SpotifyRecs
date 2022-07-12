package com.example.spotifyrecs;

import static org.junit.Assert.*;

import android.util.Log;

import com.example.spotifyrecs.models.Song;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class SongTest {

    //tests for Song.toString

    @Test
    public void toStringEmpty(){
        Song song = new Song();
        song.artist = "";
        song.title = "";
        String toStringResult = song.toString();
        assertEquals(toStringResult, "Title:  and artist: ");
    }

    @Test
    public void toStringShort(){
        Song song = new Song();
        song.artist = "1234";
        song.title = "oneWord";
        String toStringResult = song.toString();
        assertEquals(toStringResult, "Title: oneWord and artist: 1234");
    }

    @Test
    public void toStringValid(){
        Song song = new Song();
        song.artist = "Bach";
        song.title = "Chaconne in G";
        String toStringResult = song.toString();

        assertEquals(toStringResult, "Title: Chaconne in G and artist: Bach");
    }

    // Tests for Song.toJSON
    @Test
    public void toJsonEmpty() throws JSONException {
        Song song = new Song();
        song.artist = "";
        song.title = "";
        JSONObject result = song.toJSON();
        assert(result.has("title"));
        assert(result.has("artist"));
        assert(result.get("title")).equals("");
        assert(result.get("artist")).equals("");
    }

    @Test
    public void toJsonShort() throws JSONException {
        Song song = new Song();
        song.artist = "1234";
        song.title = "oneWord";
        JSONObject result = song.toJSON();
        assert(result.has("title"));
        assert(result.has("artist"));
        assert(result.get("title")).equals("oneWord");
        assert(result.get("artist")).equals("1234");
    }

    @Test
    public void toJsonValid() throws JSONException {
        Song song = new Song();
        song.artist = "Bach";
        song.title = "Chaconne in G";
        JSONObject result = song.toJSON();
        assert(result.has("title"));
        assert(result.has("artist"));
        assert(result.get("title")).equals("Chaconne in G");
        assert(result.get("artist")).equals("Bach");
    }
}