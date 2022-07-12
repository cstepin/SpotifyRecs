package com.example.spotifyrecs;

import static org.junit.Assert.*;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.*;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class MainActivityTest {

    @Rule
    public IntentsTestRule<MainActivity> intentsTestRule =
            new IntentsTestRule<>(MainActivity.class);

    @Rule
    public ActivityScenarioRule<MainActivity> activityActivityScenarioRule
            = new ActivityScenarioRule<>(MainActivity.class);

    private View decorView;


    private ActivityScenario<MainActivity> activityActivityScenario;

    @Before
    public void setup(){
     //   activityActivityScenario = launchActivity();
    }



    @Test
    public void isActivityInView(){
        ActivityScenario<MainActivity> activityScenario = ActivityScenario
                .launch(MainActivity.class);


      //  assertThat(intent).hasAction(Intent.ACTION_VIEW);
    }

}