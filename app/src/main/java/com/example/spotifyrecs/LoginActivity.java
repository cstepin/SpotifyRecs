package com.example.spotifyrecs;
import static com.example.spotifyrecs.resources.Resources.getClientId;
import static com.example.spotifyrecs.resources.Resources.getRedirectUrl;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.spotifyrecs.databinding.ActivityLoginBinding;
import com.parse.ParseUser;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

public class LoginActivity extends AppCompatActivity {
    EditText etUsername;
    EditText etPassword;
    Button btnLogin;
    Button btnSignUp;
    ActivityLoginBinding binding;
    final String TAG = "LoginActivity";

    AuthorizationRequest.Builder builder =
            new AuthorizationRequest.Builder(getClientId(),
                    AuthorizationResponse.Type.TOKEN, getRedirectUrl());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //If already logged-in, go straight to main activity.
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

    //Sending users to correct places functions

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
        startActivity(i);
        finish();
    }
}