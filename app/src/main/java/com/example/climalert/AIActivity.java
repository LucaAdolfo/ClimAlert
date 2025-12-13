package com.example.climalert;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AIActivity extends AppCompatActivity {

    private BottomNavigationView navBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ai);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //navigazione orizzontale
        navBar = findViewById(R.id.navBar);

        //cambia selezione della navigation bar
        navBar.setSelectedItemId(R.id.navigation_ai);

        //cambia activity
        navBar.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_ai) {
                //siamo già nell'ai
                return true;

            } else if (itemId == R.id.navigation_home) {
                //avvia la HomeActivity
                Intent intent = new Intent(AIActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;

            } else if (itemId == R.id.navigation_notizie) {
                //avvia la AIActivity
                Intent intent = new Intent(AIActivity.this, NotizieActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            }

            //altro id
            return false;
        });
    }
}