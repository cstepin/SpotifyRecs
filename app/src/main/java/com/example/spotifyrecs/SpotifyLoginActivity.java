package com.example.spotifyrecs;

import static com.example.spotifyrecs.resources.Resources.getClientId;
import static com.example.spotifyrecs.resources.Resources.getRedirectUrl;
import static com.example.spotifyrecs.resources.Resources.getReqCode;
import static com.spotify.sdk.android.auth.AuthorizationResponse.Type.TOKEN;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.spotifyrecs.resources.Resources;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

// Used this guide to set everything up:
//https://www.mindbowser.com/android-spotify-sdk-implementation-search-songs-using-spotify-api/

public class SpotifyLoginActivity extends AppCompatActivity {

    private static final String TAG = "Spotify " + SpotifyLoginActivity.class.getSimpleName();
    public static final String AUTH_TOKEN = "AUTH_TOKEN";
    public static String authToken = "";

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_spotify_login);

        Button mLoginButton = (Button)findViewById(R.id.login_button);

        mLoginButton.setOnClickListener(mListener);

    }

    private void openLoginWindow() {

        AuthorizationRequest.Builder builder = new AuthorizationRequest.Builder(getClientId(),
                TOKEN, getRedirectUrl());

        //Setting correct scopes to generate authorization token
        builder.setScopes(new String[]{ "streaming", "user-follow-modify",
                "playlist-read-collaborative", "app-remote-control", "playlist-modify-public",
                "user-follow-read", "playlist-modify-private"});

        AuthorizationRequest request = builder.build();

        AuthorizationClient.openLoginActivity(this, getReqCode(),request);

    }

    //Gets authorization token
    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == getReqCode())
        {
            final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);
            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    Resources.setAuthToken(response.getAccessToken());
                    authToken = response.getAccessToken();
                    Log.e(TAG,"Auth token: " + authToken);
                    Intent intent = new Intent(SpotifyLoginActivity.this,
                            LoginActivity.class);
                    startActivity(intent);
                    destroy();
                    break;
                // Auth flow returned an error
                case ERROR:
                    Log.e(TAG,"Auth error: " + response.getError());
                    break;
                // Most likely auth flow was cancelled
                default:
                    Log.d(TAG,"Auth result: " + response.getType());
            }
        }
    }

    View.OnClickListener mListener = view -> {
        if (view.getId() == R.id.login_button) {
            openLoginWindow();
        }
    };

    public void destroy(){
        SpotifyLoginActivity.this.finish();
    }
}