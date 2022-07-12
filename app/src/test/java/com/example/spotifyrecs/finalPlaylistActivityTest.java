package com.example.spotifyrecs;

import static com.example.spotifyrecs.finalPlaylistActivity.notADuplicate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class finalPlaylistActivityTest {

    //tests for notDuplicate

    @Test
    public void notADuplicateEmptyList() throws JSONException {
        String artist = "example";
        JSONArray currUserArtists = new JSONArray();
        assertEquals(currUserArtists.length(), 0);
        assertTrue(notADuplicate(currUserArtists, artist));
    }

    @Test
    public void notADuplicateOneElementNoContains() throws JSONException {
        String artist = "example";
        JSONArray currUserArtists = new JSONArray();
        currUserArtists.put("");
        assertEquals(currUserArtists.length(), 1);
        assertTrue(notADuplicate(currUserArtists, artist));
    }

    @Test
    public void notADuplicateOneElementContains() throws JSONException {
        String artist = "example";
        JSONArray currUserArtists = new JSONArray();
        currUserArtists.put("example");
        assertEquals(currUserArtists.length(), 1);
        assertFalse(notADuplicate(currUserArtists, artist));
    }

    @Test
    public void notADuplicateManyElementsNoContains() throws JSONException {
        String artist = "example";
        JSONArray currUserArtists = new JSONArray();
        currUserArtists.put("");
        currUserArtists.put("Example");
        currUserArtists.put(" example");
        currUserArtists.put(132);
        assertEquals(currUserArtists.length(), 4);
        assertTrue(notADuplicate(currUserArtists, artist));
    }

    @Test
    public void notADuplicateManyElementsContains() throws JSONException {
        String artist = "example";
        JSONArray currUserArtists = new JSONArray();
        currUserArtists.put("Not an example");
        currUserArtists.put(34);
        currUserArtists.put("example");
        assertEquals(currUserArtists.length(), 3);
        assertFalse(notADuplicate(currUserArtists, artist));
    }
}