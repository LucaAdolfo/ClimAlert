package com.example.climalert;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.climalert.meteo.MeteoCallback;
import com.example.climalert.meteo.parsing.ArpavMeteo;
import com.example.climalert.meteo.parsing.Previsione;
import com.example.climalert.meteo.parsing.Previsioni;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;
import org.osmdroid.util.GeoPoint;
import android.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextView textArpav;
    private BottomNavigationView navBar;
    private ImageButton btnImpostazioni;
    private Button btnSegnalazione;
    private Button btnMeteo;
    private ImageView immagine1;

    private FirebaseAnalytics mFirebaseAnalytics;
    private String getDataPrevisione(){
        Calendar calendario = Calendar.getInstance();
        SimpleDateFormat formatoGiorno = new SimpleDateFormat("EEE", Locale.ITALIAN); //Significa nome abbreviato EEE le prime tre lettere ... quindi es Lun
        String giornoSettimana = formatoGiorno.format(calendario.getTime()).toLowerCase();
        int giornoMese = calendario.get(Calendar.DAY_OF_MONTH);
        int oraDelGiorno = calendario.get(Calendar.HOUR_OF_DAY); // Formato 24 ore
        SimpleDateFormat formatoMese = new SimpleDateFormat("MMMM", Locale.ITALIAN);
        String meseEsteso = formatoMese.format(calendario.getTime()).toLowerCase();

        String periodo;
        if (oraDelGiorno < 12) {
            periodo = "mattina";
        } else {
            periodo = "pomeriggio";
        }
        return giornoSettimana + " " + giornoMese + " "+meseEsteso + " " + periodo;
    }
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

        btnMeteo = findViewById(R.id.btnMeteo);
        btnMeteo.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, MeteoActivity.class);
            startActivity(intent);
        });


        textArpav = findViewById(R.id.previsioniPosizione);
        immagine1 = findViewById(R.id.meteoOggi);
        ArpavMeteo meteo = new ArpavMeteo();
        try {
            meteo.fetchData(new MeteoCallback() {
                @Override
                public void OnSuccess(Previsioni previsioni) {
                    runOnUiThread(
                            () -> {
                                Previsione previsioniInThread = previsioni.getMeteogrammi("Venezia e laguna",getDataPrevisione()).getPrevisioni("image");
                                textArpav.setText(
                                        previsioniInThread.getTitle() + "°C"
                                    );

                                String url = previsioniInThread.getValue();
                                Glide.with(MainActivity.this).load(url)
                                        .placeholder(R.drawable.ic_lock)
                                        .error(R.drawable.ic_disconnect)
                                        .into(immagine1);



                            }
                    );
                }

                @Override
                public void OnFailure(String message, Exception e) {
                    runOnUiThread(() -> {
                        textArpav.setText("Errore caricamento");
                        Log.e("ArpavMeteo", "Errore durante il caricamento: " + e.getMessage());
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


        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        MapView mapPreview = findViewById(R.id.mapPreview);
        View mapOverlay = findViewById(R.id.mapOverlay);
        mapPreview.setTileSource(TileSourceFactory.MAPNIK);
        mapPreview.setMultiTouchControls(false); //da lasciare se no possibile che si sposti la mappa
        mapPreview.getController().setZoom(12.0);
        GeoPoint startPoint = new GeoPoint(45.4408, 12.3155);
        mapPreview.getController().setCenter(startPoint);

        View.OnClickListener openMapListener = v -> {
            Log.d("main", "Apertura mappa da preview o bottone");
            Intent intent = new Intent(MainActivity.this, MappaActivity.class);
            startActivity(intent);
        };

        mapOverlay.setOnClickListener(openMapListener);
        findViewById(R.id.mappa).setOnClickListener(openMapListener);
    }

    @Override
    protected void onResume(){
        super.onResume();
        if (findViewById(R.id.mapPreview)!=null){
            ((MapView) findViewById(R.id.mapPreview)).onResume();
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        if(findViewById(R.id.mapPreview)!=null){
            ((MapView) findViewById(R.id.mapPreview)).onPause();
        }
    }

}