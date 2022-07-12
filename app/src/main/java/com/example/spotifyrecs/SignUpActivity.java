package com.example.spotifyrecs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.spotifyrecs.databinding.ActivitySignUpBinding;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.List;

public class SignUpActivity extends AppCompatActivity {

    EditText etPassword;
    EditText etUsername;
    Button btnSignUp;
    ActivitySignUpBinding binding;
    final String TAG = "SignUpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        binding = ActivitySignUpBinding.inflate(getLayoutInflater());

        // layout of activity is stored in a special property called root
        View view = binding.getRoot();
        setContentView(view);

        // set bindings more efficiently through bindings
        etUsername = binding.etUsername;
        etPassword = binding.etPassword;
        btnSignUp = binding.btnSignUp;

        btnSignUp.setOnClickListener(v -> {
            Log.i(TAG, "onClick signup button");
            String username = etUsername.getText().toString();

            String password = etPassword.getText().toString();
            signUpUser(username, password);
        });
    }

    private void signUpUser(String username, String password) {
        ParseUser user = new ParseUser();
        // Set fields for the user to be created
        user.setUsername(username);
        user.setPassword(password);

        user.signUpInBackground(e -> {
            if (e == null) {
                Log.i(TAG, "signed up");
                Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            } else {
                Log.e(TAG, "sign in unsuccesful", e);
            }
        });
    }
}