package com.example.spotifyrecs;


import android.app.Application;

import com.example.spotifyrecs.models.User;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Register your parse models
        ParseObject.registerSubclass(User.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("ECKrwWJds2hTllc8pLUl8kULfXqL0TPYPadof62e")
                .clientKey("jcC8EQDh4XLrQ0c1WKpzWW0XumzwtibGQHtDqe7N")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
