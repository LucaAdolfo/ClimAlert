package com.example.climalert;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.climalert.meteo.ArpavMeteo;
import com.example.climalert.meteo.MeteoCallback;
import com.example.climalert.meteo.parsing.xmlParser;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private TextView textArpav;
    private BottomNavigationView navBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        //Scrivi da qui in poi

        //navigazione orizzontale
        navBar = findViewById(R.id.navBar);

        //cambia activity
        navBar.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_home) {
                //siamo già nella home
                return true;

            } else if (itemId == R.id.navigation_notizie) {
                //avvia la NotizieActivity
                Intent intent = new Intent(MainActivity.this, NotizieActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;

            } else if (itemId == R.id.navigation_ai) {
                //avvia la AIActivity
                Intent intent = new Intent(MainActivity.this, AIActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            }

            //altro id
            return false;
        });


        textArpav = findViewById(R.id.meteoArpav); //VA FATTO PARSING
        textArpav.setText("Arpav");

        ArpavMeteo meteo = new ArpavMeteo();
        try {
            meteo.fetchData(new MeteoCallback() {
                @Override
                public void OnSuccess(String response) {
                    Log.d("main","dati estratti");
                    runOnUiThread(() -> {
//                        xmlParser p = new xmlParser();
//                        Map meteoMap = p.parseXml(response);
//                        String m = (String) meteoMap.get("data_emissione");
                        textArpav.setText(response);
                    });
                }

                @Override
                public void OnFailure(String message, Exception e) {
                    runOnUiThread(() -> {
                        textArpav.setText("Errore caricamento");
                    });
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Button bottoneMappa = findViewById(R.id.mappa);
        bottoneMappa.setOnClickListener(view -> {
                Log.d("main", "bottone premuto mappa");
                Intent intent = new Intent(this, MappaActivity.class);
                startActivity(intent);

                });






        /*Codice per bordi schermo di defualt lascia cosi*/
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });



    }
}