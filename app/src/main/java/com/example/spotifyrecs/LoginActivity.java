package com.example.spotifyrecs;
import static com.example.spotifyrecs.Resources.getClientId;
import static com.example.spotifyrecs.Resources.getRedirectUrl;
import static com.example.spotifyrecs.Resources.getReqCode;

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
    EditText etUsername;
    EditText etPassword;
    Button btnLogin;
    Button btnSignUp;
    ActivityLoginBinding binding;
    final String TAG = "LoginActivity";
    String authToken = "";

    AuthorizationRequest.Builder builder =
            new AuthorizationRequest.Builder(getClientId(),
                    AuthorizationResponse.Type.TOKEN, getRedirectUrl());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        builder.setScopes(new String[]{"streaming", "user-follow-modify",
                "playlist-read-collaborative", "app-remote-control", "playlist-modify-public",
                "user-follow-read", "playlist-modify-private"});
        AuthorizationRequest request = builder.build();

        AuthorizationClient.openLoginActivity(this, getReqCode(), request);

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

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();
            onLoginClick(username, password);
        });

        btnSignUp.setOnClickListener(v -> onSignUpClick());
    }

    private void onSignUpClick() {
        Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(i);
    }

    private void onLoginClick(String username, String password) {
        Log.i(TAG, "Attempting to log in user " + username);

        ParseUser.logInInBackground(username, password, (user, e) -> {
            if(e != null){
                Log.e(TAG, "Issue with Login", e);
                return;
            }
            goMainActivity();
            Toast.makeText(LoginActivity.this, "Success!",
                    Toast.LENGTH_SHORT).show();
        });
    }

    private void goMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
      //  authToken = getIntent().getStringExtra("AUTH_TOKEN");
       // i.putExtra("AUTH_TOKEN", authToken);
      //  Log.i("auth-token", "authtoken is " + authToken);
        startActivity(i);
        finish();
    }
}