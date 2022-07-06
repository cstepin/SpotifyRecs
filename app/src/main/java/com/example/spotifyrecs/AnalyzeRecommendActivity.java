package com.example.spotifyrecs;

import static com.example.spotifyrecs.resources.Resources.getAuthToken;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import com.spotify.android.appremote.api.SpotifyAppRemote;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Artists;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TracksPager;
import retrofit.client.Response;

public class AnalyzeRecommendActivity extends AppCompatActivity {
    private Module mModule = null;
    private Bitmap mBitmap = null;

    //For spotify calls
    private SpotifyAppRemote mSpotifyAppRemote;
    SpotifyApi api;
    public static SpotifyService spotifyService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze_recommend);

        try {
            mModule = LiteModuleLoader.load(ExportActivity.assetFilePath(getApplicationContext(), "model_p1.ptl"));
        } catch (IOException e) {
            Log.e("ImageSegmentation", "Error reading assets", e);
            finish();
        }

        setServiceApi();

        run(mModule);

        // This can find the artist and generate songs of 3 related artists
        String artist = "beatles";
        // given a song with an attached artist:
        getSimilarSongs(artist);
    }

    private void getSimilarSongs(String artist) {
        ArrayList<String> simArtists = new ArrayList<>();

        spotifyService.searchArtists("beatles", new SpotifyCallback<ArtistsPager>() {
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
                        for(Artist artist : artists.artists){
                            Log.i("test", "these are related artists: " + artist.name);
                            simArtists.add(artist.name);
                        }
                    }
                });
            }
        });

        for(String artist1 : simArtists){
            spotifyService.searchTracks(artist1, new SpotifyCallback<TracksPager>() {
                @Override
                public void failure(SpotifyError spotifyError) {
                    Log.i("error in generate5", "error is: " + spotifyError.getMessage());
                }

                @Override
                public void success(TracksPager tracksPager, Response response) {
                    Log.i("succes", "success in getting related songs: "
                            + tracksPager.tracks.items.get(0));
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
        final long[] user_x_rating_shape = new long[] {1, 11};
        final long num_user_x_rating_numel = Tensor.numel(user_x_rating_shape);
        final float[] user_x_rating_raw;

        if(getIntent().hasExtra("floats")) {
            user_x_rating_raw = getIntent().getFloatArrayExtra("floats");
        }
        else{
            user_x_rating_raw = new float[]{11.0F, 0.0F, 0.5F, 0.5F, 0.0F, 0.0F, 0.0F, -1.0F, 0.0F, 0.0F, 0.0F};
        }

        System.out.println("num_user_x_rating_numel is: " + num_user_x_rating_numel);

        final FloatBuffer user_x_rating_float_buffer = Tensor.allocateFloatBuffer((int)num_user_x_rating_numel);
        user_x_rating_float_buffer.put(user_x_rating_raw);
        final Tensor user_x_rating_tensor = Tensor.fromBlob(user_x_rating_float_buffer, user_x_rating_shape);
        final IValue user_x_rating = IValue.from(user_x_rating_tensor);
        System.out.println("user_x_rating: " + Arrays.toString(user_x_rating.toTensor().getDataAsFloatArray()));

        final IValue user_x_index = IValue.from(0);
        System.out.println("user_x_index: " + user_x_index.toLong());

        Log.i("export", "this is the rating: " + user_x_rating + " and this is the ");
        final Tensor output_rating = mModule.forward(IValue.from(0), user_x_rating).toTensor();

        output_rating.getDataAsFloatArray();
        // i need to have access to the original matrix
        // to see the songs the most similar liked as well
        // I also need to have access to the list of songs, to recommend similar songs.

        // I need to figure out a way to recommend similar songs given a certain song.


        System.out.println("output rating is: " + Arrays.toString(output_rating.getDataAsFloatArray()));
    }

    //This sets up our api by passing in the authentication token from the log-in screen
    private void setServiceApi() {
      //  Log.i("setService", "authToken is " + authToken);
        api = new SpotifyApi();
        api.setAccessToken(getAuthToken());
        spotifyService = api.getService();
    }
}