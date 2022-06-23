package com.example.spotifyrecs;


import android.app.Application;

import com.parse.Parse;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("ECKrwWJds2hTllc8pLUl8kULfXqL0TPYPadof62e")
                .clientKey("jcC8EQDh4XLrQ0c1WKpzWW0XumzwtibGQHtDqe7N")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
