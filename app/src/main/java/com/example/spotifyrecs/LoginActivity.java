package com.example.spotifyrecs;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.net.ParseException;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.spotifyrecs.databinding.ActivityLoginBinding;
import com.parse.LogInCallback;
import com.parse.ParseUser;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

public class LoginActivity extends AppCompatActivity {

    private static final String CLIENT_ID = "f67855f9416e4ca999b13ec503540bc8";
    EditText etUsername;
    EditText etPassword;
    Button btnLogin;
    Button btnSignUp;
    ActivityLoginBinding binding;
    final String TAG = "LoginActivity";
    String authToken = "";

    AuthorizationRequest.Builder builder =
            new AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI);

    private static final int REQUEST_CODE = 1337;
    private static final String REDIRECT_URI = "com.example.capstoneapp://callback";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        builder.setScopes(new String[]{"streaming", "user-follow-modify",
                "playlist-read-collaborative", "app-remote-control", "playlist-modify-public",
                "user-follow-read", "playlist-modify-private"});
        AuthorizationRequest request = builder.build();

        AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request);

        if(ParseUser.getCurrentUser() != null){
            goMainActivity();
        }

        binding = ActivityLoginBinding.inflate(getLayoutInflater());

        // layout of activity is stored in a special property called root
        View view = binding.getRoot();
        setContentView(view);

        // set bindings more efficiently through bindings
        etUsername = binding.etUsername;
        etPassword = binding.etPassword;
        btnLogin = binding.btnLogin;
        btnSignUp = binding.btnSignUp;

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                onLoginClick(username, password);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSignUpClick();
            }
        });
    }

    private void onSignUpClick() {
        Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
        i.putExtra("AUTH_TOKEN", authToken);
        startActivity(i);
    }

    private void onLoginClick(String username, String password) {
        Log.i(TAG, "Attempting to log in user " + username);

        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, com.parse.ParseException e) {
                if(e != null){
                    Log.e(TAG, "Issue with Login", e);
                    return;
                }
                goMainActivity();
                Toast.makeText(LoginActivity.this, "Success!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        Log.i(TAG, "in onActivityResult");
        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    Log.i(TAG, "connected");
                    authToken = response.getAccessToken();
                    // Handle successful response
                    break;

                // Auth flow returned an error
                case ERROR:
                    Log.e("error log-in", response.getError());
                    // Handle error response
                    break;

                // Most likely auth flow was cancelled
                default:
                    Log.i("login", "no token or error :0");
                    // Handle other cases
            }
        }
    }

}