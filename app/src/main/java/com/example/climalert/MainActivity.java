package com.example.climalert;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private TextView textArpav;
    private BottomNavigationView navBar;
    private ImageButton btnImpostazioni;
    private Button btnSegnalazione;

    private FirebaseAnalytics mFirebaseAnalytics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        //Scrivi da qui in poi

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "main");
        mFirebaseAnalytics.logEvent("main", bundle);

        //vedi impostazioni
        btnImpostazioni = findViewById(R.id.btnImpostazioni);
        btnImpostazioni.setOnClickListener(view -> {
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Impostazioni");
            mFirebaseAnalytics.logEvent("click_impostazioni", bundle);

            Intent intent = new Intent(MainActivity.this, ImpostazioniActivity.class);

            startActivity(intent);
        });

        //fai segnalazione
        btnSegnalazione = findViewById(R.id.btnSegnalazione);
        btnSegnalazione.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SegnalazioneActivity.class);
            Bundle segn_bundle = new Bundle();
            segn_bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Segnalazione");
            mFirebaseAnalytics.logEvent("click_segnalazione", bundle);

            startActivity(intent);
        });

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
                Bundle notizie_bundle = new Bundle();
                notizie_bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Notizie");
                mFirebaseAnalytics.logEvent("click_notizie", bundle);
                Intent intent = new Intent(MainActivity.this, NotizieActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;

            } else if (itemId == R.id.navigation_ai) {
                //avvia la AIActivity
                Bundle ai_bundle = new Bundle();
                ai_bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Ai");
                mFirebaseAnalytics.logEvent("click_ai", bundle);

                Intent intent = new Intent(MainActivity.this, AIActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            }

            //altro id
            return false;
        });

        textArpav = findViewById(R.id.previsioniPosizione);


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
                        xmlParser parser = new xmlParser();

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Map valoriMeteo = parser.parseXml(response);
                                    Log.d("Luca", (String) valoriMeteo.get("data_emissione"));

                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            }).start();






                        //textArpav.setText();
                        //dentro response ci sono i dati dell'arpav


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