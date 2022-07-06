package com.example.spotifyrecs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.spotifyrecs.recommendations.GenerateSongsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

public class EnterArtistsActivity extends AppCompatActivity {

    EditText etArtist1;
    EditText etArtist2;
    Button btnMix;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_artists);

        etArtist1 = findViewById(R.id.etArtist1);
        etArtist2 = findViewById(R.id.etArtist2);
        btnMix = findViewById(R.id.btnMix);
        bottomNavigationView = findViewById(R.id.bottomNavigation);
    //    authToken = getIntent().getStringExtra("AUTH_TOKEN");

        bottomNavigationView.setOnItemSelectedListener(
                menuItem -> {
                    Fragment fragment;
                    if (menuItem.getItemId() == R.id.action_logout) {
                        onLogout();
                    }
                    else if(menuItem.getItemId() == R.id.action_home){
                        onHome();
                    }
                    return true;
                });

        btnMix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String artist1 = etArtist1.getText().toString();
                String artist2 = etArtist2.getText().toString();

                //Check the artists aren't empty
                if(artist1.isEmpty() || artist2.isEmpty()){
                    Toast.makeText(EnterArtistsActivity.this,
                            "The artists cannot be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Check artists are distinct
                else if(artist1.equals(artist2)){
                    Toast.makeText(EnterArtistsActivity.this,
                            "Please input different artists!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent i = new Intent(EnterArtistsActivity.this,
                        GenerateSongsActivity.class);
                i.putExtra("artists", new String[]{artist1, artist2});
                startActivity(i);
                finish();
            }
        });
    }

    //Menu navigation buttons
    private void onHome() {
        startActivity(new Intent(this, MainActivity.class));
    }

    private void onLogout() {
        Toast.makeText(EnterArtistsActivity.this, "logging out", Toast.LENGTH_LONG).show();
       // SpotifyRecs.getRestClient(this).clearAccessToken();
        // navigate backwards to Login screen
        Intent i = new Intent(this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // this makes sure the Back button won't work
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // same as above
        startActivity(i);
        ParseUser.logOutInBackground();
        ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null
        finish();
    }
}
