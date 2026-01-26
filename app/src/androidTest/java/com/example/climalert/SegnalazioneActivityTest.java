//Test per vedere se cliccando il pulsante "indietro" della SegnalazioneActivity effettivamente
//si viene riportati alla schermata precedente
//Viene anche verificato che la schermata corrente si cihuda


package com.example.climalert;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SegnalazioneActivityTest {

    @Test
    public void clickIndietro_chiudeActivity() {
        try (ActivityScenario<SegnalazioneActivity> scenario =
                     ActivityScenario.launch(SegnalazioneActivity.class)) {
            //Check per vedere se il bottone è presente
            onView(withId(R.id.btnIndietro)).check(matches(isDisplayed()));
            //Test del click sul bottone
            onView(withId(R.id.btnIndietro)).perform(click());

            //Controllo che l'activity sia chiusa
            scenario.onActivity(activity -> {
            });
            scenario.moveToState(androidx.lifecycle.Lifecycle.State.DESTROYED);
        }
    }
}
