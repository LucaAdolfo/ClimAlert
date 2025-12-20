package com.example.climalert;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler; // Importa la classe Handler
import android.os.Looper;  // Importa la classe Looper

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

    //durata splash screen
    private static final int SPLASH_TIMEOUT = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //dopo il timeout passa alla MainActivity (o OnBoarding)
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
            //leggo lo stato
            boolean onboardingCompleted = prefs.getBoolean("onboarding_completed", false);

            Intent intent;
            if (onboardingCompleted) {
                //se finito, vai a main o accedi
                intent = new Intent(SplashScreenActivity.this, AccediActivity.class);
            } else {
                //prima volta, onboarding
                intent = new Intent(SplashScreenActivity.this, WelcomeActivity.class);
            }

            startActivity(intent);
            finish();

        }, SPLASH_TIMEOUT);

    }
}
