package com.example.spotifyrecs;

import static com.example.spotifyrecs.resources.Resources.decodeBase62;
import static com.example.spotifyrecs.resources.Resources.getAlgorithm;
import static com.example.spotifyrecs.resources.Resources.getAuthToken;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.spotifyrecs.R;
import com.example.spotifyrecs.adapters.CollabSongDeckAdapter;
import com.example.spotifyrecs.adapters.SwipeSongDeckAdapter;
import com.example.spotifyrecs.finalPlaylistActivity;
import com.example.spotifyrecs.models.Song;
import com.example.spotifyrecs.models.User;
import com.parse.ParseUser;
import com.yalantis.library.Koloda;
import com.yalantis.library.KolodaListener;

import org.json.JSONArray;
import org.parceler.Parcels;
import org.pytorch.IValue;
import org.pytorch.LiteModuleLoader;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Artists;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.client.Response;

public class AnalyzeRecommendActivity extends AppCompatActivity {
    List<Song> songs = new ArrayList<>();
    List<String> faveSongs = new ArrayList<>();
    List<Song> keepSongs = new ArrayList<>();
    Koloda koloda;

    private Module cosineSimModule = null;
    private Module naiveModule = null;
    private Module betterModule = null;

    protected SwipeSongDeckAdapter adapter;
    ProgressBar pb;
    final String TAG = "AnalyzeRecommendedActivity";
    LottieAnimationView animationView;

    SpotifyApi api;
    public static SpotifyService spotifyService;
    Random rand = new Random(); //instance of random class

    // To time amount it takes to generate 10 random songs
    // Currently around 583740 milliseconds
    long startTime;
    long endTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze_recommend);

        startTime = System.nanoTime();

        koloda = findViewById(R.id.koloda);
        pb = findViewById(R.id.pbLoading);
        animationView = new LottieAnimationView(AnalyzeRecommendActivity.this);
        animationView.findViewById(R.id.animationView);
        animationView.pauseAnimation();

        Log.i("in export", "in export activity");

        //Then we authenticate our current api
        setServiceApi();

        try {
            cosineSimModule = LiteModuleLoader.load(assetFilePath(getApplicationContext(), "model_p3.ptl"));
        } catch (IOException e) {
            Log.e("AnalyzeRecommendActivity", "Error reading assets", e);
            finish();
        }

        try {
            naiveModule = LiteModuleLoader.load(assetFilePath(getApplicationContext(), "model_nn_naive.ptl"));
        } catch (IOException e) {
            Log.e("AnalyzeRecommendActivity", "Error reading assets", e);
            finish();
        }

        try {
            betterModule = LiteModuleLoader.load(assetFilePath(getApplicationContext(), "model_nn_better.ptl"));
        } catch (IOException e) {
            Log.e("AnalyzeRecommendActivity", "Error reading assets", e);
            finish();
        }

        //Then call to ask which setting the user inputted
        selectedButton();
    }

    private void selectedButton() {
        pb.setVisibility(ProgressBar.VISIBLE);
        String selectedAlgorithm = ((User) ParseUser.getCurrentUser()).getAlgorithm();

        Log.i(TAG, "selected alg: " + selectedAlgorithm);

        if(selectedAlgorithm.equals("Simple Cosine Similarity")){
            Log.i(TAG, "here!");
            run(cosineSimModule);
        }
        else if(selectedAlgorithm.equals("Naive Training NN")){
            Log.i(TAG, "here 2!");
            runNaive(naiveModule);
        }
        else{
            Log.i(TAG, "here 3!");
            runBetter(betterModule);
        }
    }

    // Given a certain song, it pulls the artist of the song and finds similar artists to
    // that given artist and pulls their top songs.
    private void getSimilarArtists(String artist) {
        ArrayList<String> simArtists = new ArrayList<>();

        Log.i("in get similar", "in get similar");

        spotifyService.searchArtists(artist, new SpotifyCallback<ArtistsPager>() {
            @Override
            public void failure(SpotifyError spotifyError) {
                Log.i("error in generate34", "error is: " + spotifyError.getMessage());
            }

            @Override
            public void success(ArtistsPager artistsPager, Response response) {
                String id = artistsPager.artists.items.get(0).id;

                spotifyService.getRelatedArtists(id, new SpotifyCallback<Artists>() {
                    @Override
                    public void failure(SpotifyError spotifyError) {
                        Log.i("error in generate234", "error is: " + spotifyError.getMessage());
                    }

                    @Override
                    public void success(Artists artists, Response response) {
                        for(int i = 0; i < 3; i++) {
                            simArtists.add(artists.artists.get(i).id);
                        }
                        getSimilarSongs(simArtists);
                    }
                });
            }
        });
    }

    private void getSimilarSongs(ArrayList<String> simArtists) {
        List<Song> songs = new ArrayList<>();

        for(String artist1 : simArtists) {
            spotifyService.getArtistTopTrack(artist1, "ES", new SpotifyCallback<Tracks>() {
                @Override
                public void failure(SpotifyError spotifyError) {
                    Log.i("error in generate top tracks", "error is: " + spotifyError.getMessage());
                }

                @Override
                public void success(Tracks tracks, Response response) {
                    for(Track track : tracks.tracks){
                        Song song = new Song();
                        song.uri = track.uri;
                        song.title = track.name;
                        song.artist = track.artists.get(0).name;
                        song.imageString = track.album.images.get(0).url;
                        song.visible = true;
                        songs.add(song);
                        Log.i("success getting tracks", "track: " + track.name);
                    }
                    if(songs.size() > 20) {
                        querySongs(songs);
                    }
                }
            });
        }
    }

    public static String assetFilePath(Context context, String assetName) throws IOException {
        File file = new File(context.getFilesDir(), assetName);
        if (file.exists() && file.length() > 0) {
            return file.getAbsolutePath();
        }

        try (InputStream is = context.getAssets().open(assetName)) {
            try (OutputStream os = new FileOutputStream(file)) {
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                os.flush();
            }
            return file.getAbsolutePath();
        }
    }

    public void run(Module mModule){
        Log.i("in run", "run message here");
        final long[] user_x_rating_shape = new long[] {1, 10};
        final long num_user_x_rating_numel = Tensor.numel(user_x_rating_shape);
        final float[] user_x_rating_raw;

        if(getIntent().hasExtra("floats")) {
            user_x_rating_raw = getIntent().getFloatArrayExtra("floats");
            Log.i("floats", "got floats from user input: " + Arrays.toString(user_x_rating_raw));
        }
        else{
            user_x_rating_raw = new float[]{0.0F, 0.5F, 0.5F, 0.0F, 0.0F, 0.0F, -1.0F, 0.0F, 0.0F, 0.0F};
        }

        System.out.println("num_user_x_rating_numel is: " + num_user_x_rating_numel);

        final FloatBuffer user_x_rating_float_buffer = Tensor.allocateFloatBuffer((int)num_user_x_rating_numel);
        user_x_rating_float_buffer.put(user_x_rating_raw);
        final Tensor user_x_rating_tensor = Tensor.fromBlob(user_x_rating_float_buffer, user_x_rating_shape);
        final IValue user_x_rating = IValue.from(user_x_rating_tensor);
        System.out.println("user_x_rating: " + Arrays.toString(user_x_rating.toTensor().getDataAsFloatArray()));

        Log.i("export", "this is the rating: " + user_x_rating + " and this is the ");
        final Tensor output_rating = mModule.forward(user_x_rating).toTensor(); //IValue.from(0)

        output_rating.getDataAsFloatArray();

        System.out.println("output rating is: " + Arrays.toString(output_rating.getDataAsFloatArray()));

        mostRelatedUser(output_rating.getDataAsFloatArray());
    }

    public void runNaive(Module mModule){
      //  final long[] user_x_rating_shape = new long[] {5, 2};
        final long[] user_x_rating_shape = new long[] {10, 2};
        final long num_user_x_rating_numel = Tensor.numel(user_x_rating_shape);
        final int[] user_x_rating_raw;

        //to get song id from here
        List<Song> songs = Parcels.unwrap(getIntent()
                .getParcelableExtra("songs"));

        user_x_rating_raw = new int[]{195214,   1169,
        76700,   2020,
        79741,  23960,
        123533,   4710,
        195514,  43495,
        101539,  20038,
        82228,  29767,
        170764, 107713,
        83770,  89582,
        22045,     76};


        int userId = decodeBase62(ParseUser.getCurrentUser().getObjectId());
        Log.i(TAG, "userID is: " + userId);

        for(int i = 0; i < 10; i++){
            Log.i(TAG, "curr id is: " + songs.get(i).getId());
            user_x_rating_raw[i*2] = songs.get(i).getId();
            user_x_rating_raw[i*2 + 1] = userId;
        }

        final IntBuffer user_x_rating_int_buffer = Tensor.allocateIntBuffer((int)num_user_x_rating_numel);
        user_x_rating_int_buffer.put(user_x_rating_raw);
        final Tensor user_x_rating_tensor = Tensor.fromBlob(user_x_rating_int_buffer, user_x_rating_shape);

        final IValue user_x_rating = IValue.from(user_x_rating_tensor);

        System.out.println("num_user_x_rating_numel is 2 : " + num_user_x_rating_numel);

        final Tensor output_rating = mModule.forward(user_x_rating).toTensor();

        output_rating.getDataAsFloatArray();

        System.out.println("output rating is 2: " + Arrays.toString(output_rating.getDataAsFloatArray()));

        mostRelatedUser(output_rating.getDataAsFloatArray());
    }

    public void runBetter(Module mModule){

        // First variable
        final long[] user_x_rating_shape = new long[] {1, 10};
        final long num_user_x_rating_numel = Tensor.numel(user_x_rating_shape);
        final int[] user_x_rating_raw;

        int userId = decodeBase62(ParseUser.getCurrentUser().getObjectId());

        user_x_rating_raw = new int[]{userId, userId, userId, userId, userId, userId, userId,
                userId, userId, userId};

        final IntBuffer user_x_rating_int_buffer = Tensor.allocateIntBuffer((int)num_user_x_rating_numel);
        user_x_rating_int_buffer.put(user_x_rating_raw);
        final Tensor user_x_rating_tensor = Tensor.fromBlob(user_x_rating_int_buffer, user_x_rating_shape);

        final IValue user_x_rating = IValue.from(user_x_rating_tensor);

        //to get song id from here
        List<Song> songs = Parcels.unwrap(getIntent()
                .getParcelableExtra("songs"));

        // Second variable
        final long[] user_y_rating_shape = new long[] {1, 10};
        final long num_user_y_rating_numel = Tensor.numel(user_x_rating_shape);
        final int[] user_y_rating_raw;

        user_y_rating_raw = new int[10];

        for(int i = 0; i < 10; i++){
            Log.i(TAG, "curr id is: " + songs.get(i).getId());
            user_y_rating_raw[i] = songs.get(i).getId();
        }

        final IntBuffer user_y_rating_int_buffer = Tensor.allocateIntBuffer((int)num_user_y_rating_numel);
        user_y_rating_int_buffer.put(user_y_rating_raw);
        final Tensor user_y_rating_tensor = Tensor.fromBlob(user_y_rating_int_buffer, user_y_rating_shape);

        final IValue user_y_rating = IValue.from(user_y_rating_tensor);


        System.out.println("num_user_x_rating_numel is 3 : " + num_user_x_rating_numel);

        final Tensor output_rating = mModule.forward(user_x_rating, user_y_rating, IValue.from(-1), IValue.from(1)).toTensor(); //IValue.from(0)

        float[] output_float_rating = output_rating.getDataAsFloatArray();
        float[] final_output_float = new float[10];
        for(int i = 0; i < final_output_float.length; i++){
            final_output_float[i] = output_float_rating[i];
        }

        System.out.println("output rating is 3: " + Arrays.toString(output_rating.getDataAsFloatArray()));

        mostRelatedUser(final_output_float);
    }

    private void mostRelatedUser(float[] data) {
        Log.i("in most related", "in most related");
        // I need to change variable names in this function
        float max = -2.0F;
        int pos = -1;

        for(int i = 0; i < data.length; i++){
            float f = data[i];
            if(f > max){
                max = f;
                pos = i;
            }
        }

        double[][] arr = {
                {1, 0.5, 0.5, 1, 1, 1, 1, 1, 0, 0},
                {-1, 0, -0.5, 1, 0, 0, 1, 0, 0, 0.5},
                {0, 0, 0, 0, 0, -0.5, 0, 0, -0.5, -0.5},
                {0, 0, 0, -1, -1, 0.5, 1, 0, 0, 0},
                {0, 0, 0, 0, -1, -1, -1, -1, -1, 0},
                {0, -0.5, 0.5, 0, 0, 0, 0.5, 1, 0.5, 0},
                {0, 0, 1, 0, 0.5, 1, 1, 0, 0, 0},
                {0, 0, 0.5, 0, 0, -1, 0.5, 0, 0, 0},
                {0, 1, -1, 1, 1, 0, 0, 0, 1, 0},
                {0, 0.5, 0.5, 0, 0, 0, -1, 0, 0, 0}
        };

        double max2 = -2;
        int pos2 = -1;

        for(int i = 0; i < arr[pos].length; i++){
            if(arr[pos][i] > max2){
                max2 = arr[pos][i];
                pos2 = i;
            }
        }
        //now we know which is the favorite song...
        List<Song> songs = Parcels.unwrap(getIntent()
                .getParcelableExtra("songs"));

        getSimilarArtists(songs.get(pos2).artist);
    }

    private void querySongs(List<Song> finalSongs) {

        Log.i(TAG, "length: " + finalSongs.size());
        songs.addAll(finalSongs);
        // adapter.notifyDataSetChanged();

        adapter = new SwipeSongDeckAdapter(AnalyzeRecommendActivity.this, songs);

        koloda.setAdapter(adapter);

        koloda.setKolodaListener(new KolodaListener() {
            @Override
            public void onNewTopCard(int i) {
                animationView.pauseAnimation();
            }

            @Override
            public void onCardDrag(int i, @NonNull View view, float v) {

            }

            @Override
            public void onCardSwipedLeft(int i) {
                Log.i("koloda", "detected left swipe " + i);
                Toast.makeText(AnalyzeRecommendActivity.this, "Leaving this song behind!",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCardSwipedRight(int i) {
                Log.i("koloda", "detected right swipe " + i);

                Toast.makeText(AnalyzeRecommendActivity.this, "I'm keeping this song!",
                        Toast.LENGTH_SHORT).show();
                Song song = (Song) Objects.requireNonNull(koloda.getAdapter())
                        .getItem(i + 1);
                //this means they liked the song, so we keep the song
                keepSongs.add(song);
            }

            @Override
            public void onClickRight(int i) {

            }

            @Override
            public void onClickLeft(int i) {

            }

            @Override
            public void onCardSingleTap(int i) {
            }

            @Override
            public void onCardDoubleTap(int i) {
                animationView.playAnimation();
                Song song = (Song) Objects.requireNonNull(koloda.getAdapter()).getItem(i + 1);
                Log.i(TAG, "This is the song: " +
                        ((Song) koloda.getAdapter().getItem(i + 1)).title);
                faveSongs.add(song.title);
            }

            @Override
            public void onCardLongPress(int i) {

            }

            @Override
            public void onEmptyDeck() {
                updateLikedSongs();
                Intent i = new Intent(AnalyzeRecommendActivity.this,
                        finalPlaylistActivity.class);
                i.putExtra("final songs", Parcels.wrap(keepSongs));
                startActivity(i);
            }
        });

// run a background job and once complete
        pb.setVisibility(ProgressBar.INVISIBLE);
        endTime = System.nanoTime();
        long duration = ((endTime - startTime)/1000);
        Log.i(TAG, "this is the duration: " + duration);
    }

    private void updateLikedSongs() {
        if(faveSongs.size() == 0){
            return;
        }

        JSONArray currLiked = ParseUser.getCurrentUser().getJSONArray("faveSongs");
        assert currLiked != null;
        for(String song : faveSongs){
            currLiked.put(song);
        }
        ParseUser.getCurrentUser().put("faveSongs", currLiked);
        ParseUser.getCurrentUser().saveInBackground(e -> {
            if(e != null){
                Log.e("AddPlaylistFragment", "error saving playlists", e);
            }
            else{
                Log.i("Addplaylistfragment", "faveSongs saved successfully");
            }
        });
    }

    //This sets up our api by passing in the authentication token from the log-in screen
    private void setServiceApi() {
        Log.i("setService", "authToken is " + getAuthToken());
        api = new SpotifyApi();
        api.setAccessToken(getAuthToken());
        spotifyService = api.getService();
    }
}