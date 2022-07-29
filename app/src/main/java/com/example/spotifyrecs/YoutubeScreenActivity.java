package com.example.spotifyrecs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;


import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collection;

/*
public class YoutubeScreenActivity extends Activity {
    // You need to set this value for your code to compile.
    // For example: ... DEVELOPER_KEY = "YOUR ACTUAL KEY";
    private static final String DEVELOPER_KEY = BuildConfig.YOUTUBE_API_KEY;

    private static final String APPLICATION_NAME = "SpotifyRecs";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

  //  YouTubePlayerView youTubePlayerView;
    YouTubePlayer.OnInitializedListener mOnInitializedListener;
    ArrayList<String> mListData = new ArrayList<>();

    final static String TAG = "YoutubeScreenActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_screen);

  //      youTubePlayerView = findViewById(R.id.player);

        Log.i(TAG, "here");
        new TestAsync().execute();
        //   mainFunction();
    }

    /**
     * Build and return an authorized API client service.
     *
     * @return an authorized API client service
     * @throws GeneralSecurityException, IOException
     */

/*
    public static YouTube getService() throws GeneralSecurityException, IOException {
     //   final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        final NetHttpTransport httpTransport = new com.google.api.client.http.javanet.NetHttpTransport();
        Log.i(TAG, "success in service");
        return new YouTube.Builder(httpTransport, JSON_FACTORY, null)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Call function to create API service object. Define and
     * execute API request. Print API response.
     *
     * @throws GeneralSecurityException, IOException, GoogleJsonResponseException
     */

/*
    public static void mainFunction()
            throws GeneralSecurityException, IOException, GoogleJsonResponseException {
        YouTube youtubeService = getService();
        // Define and execute the API request
        YouTube.Search.List request = youtubeService.search()
                .list("");
        SearchListResponse response = request.setKey(DEVELOPER_KEY)
                .setQ("chopin")
                .execute();
        System.out.println(response);
        Log.i("example", "this is the response: " + response);
    }

    static class TestAsync extends AsyncTask<Void, Integer, String> {
        String TAG = getClass().getSimpleName();

        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG + " PreExceute","On pre Exceute......");
        }

        protected String doInBackground(Void...arg0) {
            Log.d(TAG + " DoINBackGround", "On doInBackground...");

            YouTube youtubeService = null;
            try {
                youtubeService = getService();
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Define and execute the API request
            YouTube.Search.List request = null;
            try {
                request = youtubeService.search()
                        .list("");
            } catch (IOException e) {
                e.printStackTrace();
            }
            SearchListResponse response = null;
            try {
                response = request.setKey(DEVELOPER_KEY)
                        .setQ("chopin")
                        .execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println(response);

            return "You are at PostExecute";
        }

        protected void onProgressUpdate(Integer...a) {
            super.onProgressUpdate(a);
       //     Log.d(TAG + " onProgressUpdate", "You are in progress update ... " + a[0]);
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(TAG + " onPostExecute", "" + result);
        }
    }
}

*/

public class YoutubeScreenActivity extends YouTubeBaseActivity {

 //   public static String CHANNEL_ID = "UCoMdktPbSTixAyNGwb-UYkQ";
    public static String searchQuery = "";

    public static String CHANNEL_GET_URL = "";

    YouTubePlayerView youTubePlayerView;
    YouTubePlayer.OnInitializedListener mOnInitializedListener;
    ArrayList<String> mListData = new ArrayList<>();

    final String TAG = "YoutubeScreenActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_screen);

        youTubePlayerView = findViewById(R.id.player);

        OkHttpClient client = new OkHttpClient();

        if(getIntent().hasExtra("title")){
            searchQuery = "";
            searchQuery += ((String)getIntent().getExtras().get("title")).replaceAll("\\s", "");
            searchQuery += "by";
            searchQuery += ((String) getIntent().getExtras().get("artist")).replaceAll("\\s", "");
            CHANNEL_GET_URL = "https://www.googleapis.com/youtube/v3/search?part=snippet&order=date&q="+searchQuery+"&maxResults=20&order=viewCount&type=video&key="+BuildConfig.YOUTUBE_API_KEY+"";

            new RequestYoutubeAPI().execute();
        }
    }

    /**
     * Sample Java code for youtube.search.list
     * See instructions for running these code samples locally:
     * https://developers.google.com/explorer-help/code-samples#java
     */

    /*
    public class ApiExample {
        private static final String CLIENT_SECRETS= "client_secret.json";
        private static final Collection<String> SCOPES =
                Arrays.asList("https://www.googleapis.com/auth/youtube.force-ssl");

        private static final String APPLICATION_NAME = "API code samples";
        private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

        /**
         * Create an authorized Credential object.
         *
         * @return an authorized Credential object.
         * @throws IOException
         */

    /*

        public static Credential authorize(final NetHttpTransport httpTransport) throws IOException {
            // Load client secrets.
            InputStream in = ApiExample.class.getResourceAsStream(CLIENT_SECRETS);
            GoogleClientSecrets clientSecrets =
                    GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
            // Build flow and trigger user authorization request.
            GoogleAuthorizationCodeFlow flow =
                    new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                            .build();
            Credential credential =
                    new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
            return credential;
        }

        /**
         * Build and return an authorized API client service.
         *
         * @return an authorized API client service
         * @throws GeneralSecurityException, IOException
         */

        /*

        public static YouTube getService() throws GeneralSecurityException, IOException {
            final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            Credential credential = authorize(httpTransport);
            return new YouTube.Builder(httpTransport, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        }

        /**
         * Call function to create API service object. Define and
         * execute API request. Print API response.
         *
         * @throws GeneralSecurityException, IOException, GoogleJsonResponseException
         */

    /*
        public static void main(String[] args)
                throws GeneralSecurityException, IOException, GoogleJsonResponseException {
            YouTube youtubeService = getService();
            // Define and execute the API request
            YouTube.Search.List request = youtubeService.search()
                    .list("");
            SearchListResponse response = request.setQ("chopin").execute();
            System.out.println(response);
        }
    }
    */

    @SuppressLint("StaticFieldLeak")
    private class RequestYoutubeAPI extends AsyncTask<Void, String, String> {

        @Override
        protected String doInBackground(Void... voids) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(CHANNEL_GET_URL);
            Log.e("URL", CHANNEL_GET_URL);

            try {
                HttpResponse response = httpClient.execute(httpGet);
                HttpEntity httpEntity = response.getEntity();
                return EntityUtils.toString(httpEntity);

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String response){
            super.onPostExecute(response);
            if(response != null){
                try {
                    JSONObject jsonObject =  new JSONObject(response);
                    Log.e("response", jsonObject.toString());
                    mListData = parseVideoListFromResponse(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private ArrayList<String> parseVideoListFromResponse(JSONObject jsonObject) throws JSONException {
        Log.i(TAG, "in parseVideoList");

        ArrayList<String> mList = new ArrayList<>();
        if(jsonObject.has("items")){
            Log.i(TAG, "has items");
            JSONArray jsonArray = jsonObject.getJSONArray("items");
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject object = jsonArray.getJSONObject(i);
                Log.i(TAG, "curr jsonobject: " + object);
                if(object.has("id")){
                    Log.i(TAG, "has id");
                    JSONObject idObject = object.getJSONObject("id");
                    if(idObject.has("videoId")){
                        Log.i(TAG, "videoId is2: " + idObject.get("videoId"));
                        if(i == 0){
                            youTubePlayerView.initialize(BuildConfig.YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
                                @Override
                                public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                                    Log.i(TAG, "onSuccess");
                                    try {
                                        youTubePlayer.cueVideo((String) idObject.get("videoId"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                                    Log.e(TAG, "failure: " + youTubeInitializationResult);
                                }
                            });
                        }
                    }
                }
                if(object.has("videoId")) {
                    Log.i(TAG, "videoId is: " + jsonArray.getJSONObject(2).get("videoId"));
                }
            }
        }
        return mList;
    }
}