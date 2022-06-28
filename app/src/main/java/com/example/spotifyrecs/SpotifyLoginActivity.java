package com.example.spotifyrecs;

import static com.example.spotifyrecs.Resources.getClientId;
import static com.example.spotifyrecs.Resources.getRedirectUrl;
import static com.example.spotifyrecs.Resources.getReqCode;
import static com.spotify.sdk.android.auth.AuthorizationResponse.Type.TOKEN;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

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

        builder.setScopes(new String[]{ "streaming"});

        AuthorizationRequest request = builder.build();

        AuthorizationClient.openLoginActivity(this, getReqCode(),request);

    }

    @Override

    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == getReqCode())

        {
            final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

            switch (response.getType()) {

                // Response was successful and contains auth token

                case TOKEN:

                    authToken = response.getAccessToken();

                    Log.e(TAG,"Auth token: " + response.getAccessToken());

                    Intent intent = new Intent(SpotifyLoginActivity.this,

                            LoginActivity.class);

                //    intent.putExtra(AUTH_TOKEN, response.getAccessToken());

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

    public static String getAuthToken(){
        return authToken;
    }
}