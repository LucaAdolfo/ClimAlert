package com.example.climalert;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    //Prova per vedere se tutto ok!
    private TextView txtGradi, txtPosizione;
    private BottomNavigationView navBar;
    private ImageButton btnImpostazioni;
    private Button btnSegnalazione;
    private Button btnMeteo;
    private ImageView imgMeteo;
    private com.google.android.gms.location.FusedLocationProviderClient fusedLocationClient;

    private FirebaseAnalytics mFirebaseAnalytics;

    private FirebaseAuth mAuth;




    //legge gps
    private void recuperaPosizioneGPS() {
        //permessi
        if (androidx.core.app.ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            androidx.core.app.ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                ottieniNomeCitta(location.getLatitude(), location.getLongitude());
            } else {
                txtPosizione.setText("GPS non disponibile");
            }
        });
    }

    //per trovare città
    private void ottieniNomeCitta(double lat, double lon) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            // trova l'indirizzo per le coordinate fornite
            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);

            if (addresses != null && !addresses.isEmpty()) {
                //trova città
                String citta = addresses.get(0).getLocality();

                // se è null, prendi nome dell'area urbana
                if (citta == null) {
                    citta = addresses.get(0).getSubAdminArea();
                }

                if (citta != null) {
                    txtPosizione.setText(citta.trim());
                    caricaDatiMeteoReali(citta.trim());
                } else {
                    txtPosizione.setText("Posizione sconosciuta");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            txtPosizione.setText("Errore localizzazione");
        }
    }


    private void caricaDatiMeteoReali(String nomeCitta) {
        ArpavMeteo meteo = new ArpavMeteo();
        try {
            meteo.fetchData(new MeteoCallback() {
                @Override
                public void OnSuccess(Previsioni previsioni) {
                    runOnUiThread(() -> {
                        try {
                            var meteogramma = previsioni.getMeteogrammi(nomeCitta, getDataPrevisione());

                            String gradiFinali = "--°C";
                            for (Previsione p : meteogramma.getPrevisioni()) {
                                if ("Temperatura".equalsIgnoreCase(p.getTitle())) {
                                    String valoreGrezzo = p.getValue();
                                    gradiFinali = valoreGrezzo.replace("max ", "")
                                            .replace("min ", "")
                                            .replace(" ", "")
                                            .replace("C", "°C");
                                    break;
                                }
                            }
                            txtGradi.setText(gradiFinali);

                            Previsione datiImmagine = meteogramma.getPrevisioni("image");
                            if (datiImmagine != null) {
                                String urlIcona = datiImmagine.getValue();
                                Glide.with(MainActivity.this)
                                        .load(urlIcona)
                                        .placeholder(R.drawable.ic_lock)
                                        .into(imgMeteo);
                            }

                        } catch (Exception e) {
                            android.util.Log.e("ARPAV", "Errore parsing per: " + nomeCitta + " - " + e.getMessage());

                            if (!nomeCitta.equals("Venezia e laguna")) {
                                caricaDatiMeteoReali("Venezia e laguna");
                            } else {
                                txtGradi.setText("N/D");
                            }
                        }
                    });
                }

                @Override
                public void OnFailure(String message, Exception e) {
                    android.util.Log.e("ARPAV", "Errore: " + message);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser users = mAuth.getCurrentUser();

        SharedPreferences sharedPreferences=getSharedPreferences("ImpostazioniTema", MODE_PRIVATE);
        boolean isDarkMode=sharedPreferences.getBoolean("isDarkMode", false);
        if (isDarkMode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        //Scrivi da qui in poi

        fusedLocationClient = com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(this);
        recuperaPosizioneGPS();

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
        if (users!=null) {
            if (users.isAnonymous()) {
                btnSegnalazione.setVisibility(View.GONE);
            }
        }

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

        txtPosizione = findViewById(R.id.txtPosizione);
        txtGradi = findViewById(R.id.previsioniPosizione);
        imgMeteo = findViewById(R.id.meteoOggi);

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