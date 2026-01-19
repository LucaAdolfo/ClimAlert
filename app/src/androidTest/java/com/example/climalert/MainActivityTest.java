//testo se mainactivity effettivamente funziona e non crasha
//nel caso in cui crasha allora il test fallisce in automatico
package com.example.climalert;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> rule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void appSiAvviaSenzaCrash(){

    }
}
