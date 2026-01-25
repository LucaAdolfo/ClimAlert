package com.example.climalert;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
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
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.bumptech.glide.Glide;
import com.example.climalert.alert.parsing.EmergencyWorker;
import com.example.climalert.alert.parsing.Entry;
import com.example.climalert.meteo.MeteoCallback;
import com.example.climalert.meteo.parsing.ArpavMeteo;
import com.example.climalert.meteo.parsing.Previsione;
import com.example.climalert.meteo.parsing.Previsioni;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private TextView txtGradi, txtPosizione, lblAlert;
    private BottomNavigationView navBar;
    private ImageButton btnImpostazioni;
    private Button btnSegnalazione;
    private Button btnMeteo;
    private ImageView imgMeteo;
    private com.google.android.gms.location.FusedLocationProviderClient fusedLocationClient;

    private FirebaseAnalytics mFirebaseAnalytics;

    private FirebaseAuth mAuth;

    private String regione = "Veneto"; //All avvio impostera regioneVeneto sicuramente il worker non avra la posizione
    private static String TAG = "MainActivity";
    private CardView alertContainer;

    private final double[][] coordinateZone = {
            {46.1425, 12.2167}, //belluno
            {45.6669, 12.2431}, //treviso
            {45.4408, 12.3155}, //venezia
            {45.5479, 11.5446}, //vicenza
            {45.4064, 11.8768}, //padova
            {45.0711, 11.7907}, //rovigo
            {45.4384, 10.9916}  //verona
    };


    //gestisco la posizione in base alle preferenze
    private void gestisciPosizioneEMeteo() {
        SharedPreferences prefs = getSharedPreferences("ClimAlertPrefs", MODE_PRIVATE);
        boolean useGps = prefs.getBoolean("useGps", true);

        if (useGps) {
            recuperaPosizioneGPS();
        } else {
            int zoneIndex = prefs.getInt("selectedZone", 0);
            String[] opzioni = {"Belluno e Prealpi orientali", "Treviso e pianura orientale", "Venezia e laguna", "Vicenza e pedemontana", "Padova e pianura centrale", "Rovigo e pianura meridionale", "Verona e pedemontana"};
            String zonaScelta = opzioni[zoneIndex];

            txtPosizione.setText(zonaScelta);
            caricaDatiMeteoReali(zonaScelta);
            this.regione = "Veneto";
            setWorkerEmergenze();


            MapView mapPreview = findViewById(R.id.mapPreview);
            if (mapPreview != null) {
                GeoPoint zonePoint = new GeoPoint(coordinateZone[zoneIndex][0], coordinateZone[zoneIndex][1]);
                mapPreview.getController().setCenter(zonePoint);
            }
        }
    }


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
                this.regione = ottieniNomeRegione(location.getLatitude(), location.getLongitude());
                setWorkerEmergenze();
            } else {
                txtPosizione.setText("GPS non disponibile");
                this.regione = getLastRegione();
            }
        });
    }

    //per trovare città
    private void ottieniNomeCitta(double lat, double lon) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            //trova l'indirizzo per le coordinate fornite
            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);

            if (addresses != null && !addresses.isEmpty()) {
                //trova città
                String citta = addresses.get(0).getLocality();

                //se è null, prende nome dell'area urbana
                if (citta == null) {
                    citta = addresses.get(0).getSubAdminArea();
                }

                if (citta != null) {
                    txtPosizione.setText(ArpavMeteo.nomeCittaArpavCasting(citta.trim()));
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
    private String ottieniNomeRegione(double lat, double lon) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            //trova l'indirizzo per le coordinate fornite
            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);

            if (addresses != null && !addresses.isEmpty()) {
                //trova regione
                String regione = addresses.get(0).getAdminArea();
                setRegioneUpdate(regione);
                return regione;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    private void caricaDatiMeteoReali(String nomeCitta) {
        ArpavMeteo meteo = new ArpavMeteo();
        try {
            meteo.fetchData(new MeteoCallback() {
                @Override
                public void OnSuccess(Previsioni previsioni) {
                    runOnUiThread(() -> {
                        try {
                            var meteogramma = previsioni.getMeteogrammi(ArpavMeteo.nomeCittaArpavCasting(nomeCitta), getDataPrevisione());

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

        fusedLocationClient = com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(this);
        

        txtPosizione = findViewById(R.id.txtPosizione);
        txtGradi = findViewById(R.id.previsioniPosizione);
        imgMeteo = findViewById(R.id.meteoOggi);
        gestisciPosizioneEMeteo();

        //CHiedo permesso per notifiche
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

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


        if (isDarkMode) {
            navBar.setBackground(getResources().getDrawable(R.drawable.rounded_top_nav_dark));
        } else {
            navBar.setBackground(getResources().getDrawable(R.drawable.rounded_top_nav));
        }

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
        setWorkerEmergenze();

        lblAlert = findViewById(R.id.lblAlert);
        alertContainer = findViewById(R.id.alertContainer);

        Entry entry = getEntry();

        if(entry != null) {
            String allerta = "Allerta emanata " + castData(entry.getUpdated()) + "\nPrevista Per: " + castData(entry.getOnset()) + "\nTipo: " + entry.getEvent() + "\nUrgenza: " + entry.getUrgency();
            lblAlert.setText(allerta);
            alertContainer.setCardBackgroundColor(android.graphics.Color.RED);
            lblAlert.setTextColor(android.graphics.Color.WHITE);
        }
        else {
            lblAlert.setText("Nessuna allerta rilevata");
            alertContainer.setCardBackgroundColor(android.graphics.Color.WHITE);
            lblAlert.setTextColor(android.graphics.Color.BLACK);
        }
    }

    private String castData(String data){
        try {
            OffsetDateTime odt = OffsetDateTime.parse(data);
            OffsetDateTime dataLocale = odt.atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE d MMMM, HH:mm", Locale.ITALIAN);
            return dataLocale.format(formatter);
        } catch (Exception e) {
            return data; //in caso di errore ritorna l'originale
        }

    }
    private void setWorkerEmergenze(){/*Metto worker a fare*/
        //-devo -> prendere preferenze della ultima update e scrivere con un return la preferenza
        //-> ho bisogno regione
        String regione = this.regione;
        String last_fetched_time = getLastFetchedTime();
        Data data = new Data.Builder()
                .putString("target_region", regione)
                .putString("updated_time", last_fetched_time)
                .build();
        PeriodicWorkRequest emergencyWorkRequest =
                new PeriodicWorkRequest.Builder(EmergencyWorker.class, 30, TimeUnit.MINUTES)
                        .setInputData(data)
                        .setConstraints(new androidx.work.Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                        .build();
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "EmergencyAlertWork",
                ExistingPeriodicWorkPolicy.KEEP,
                emergencyWorkRequest
        );
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(emergencyWorkRequest.getId())
                .observe(this, workInfo -> {
                    if (workInfo != null) {
                        Log.d("MainActivity", "Stato del worker cambiato: " + workInfo.getState());
                    }

                    if (workInfo != null && workInfo.getState() == androidx.work.WorkInfo.State.SUCCEEDED) {
                        String newUpdateTime = workInfo.getOutputData().getString("new_fetched_time");
                        if (newUpdateTime != null) {
                            Log.d("MainActivity", "Worker ha finito! Salvo il nuovo tempo di aggiornamento: " + newUpdateTime);
                            setTimeUpdate(newUpdateTime);
                        }
                    }
                });

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //utente ha cliccato consenti
                recuperaPosizioneGPS();
            } else {
                //L'utente ha negato
                txtPosizione.setText("Permesso negato");
            }
        }
    }

    private String getLastFetchedTime() {
        SharedPreferences sharedPreferences = getSharedPreferences("EmergencyAlert", MODE_PRIVATE);
        return sharedPreferences.getString("last_fetched_time", null);
    }

    private void setTimeUpdate(String date) {
        SharedPreferences sharedPreferences = getSharedPreferences("EmergencyAlert", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("last_fetched_time",date);
        editor.apply();

    }
    private void setRegioneUpdate(String regione) {
        SharedPreferences sharedPreferences = getSharedPreferences("EmergencyAlert", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("regione",regione);
        editor.apply();
    }
    private String getLastRegione() {
        SharedPreferences sharedPreferences = getSharedPreferences("EmergencyAlert", MODE_PRIVATE);
        return sharedPreferences.getString("regione", null);
    }
    private Entry getEntry() {
        SharedPreferences sharedPreferences = getSharedPreferences("EmergencyAlert", MODE_PRIVATE);
        String json = sharedPreferences.getString("ultima_entry", null);
        if(json==null){
            return null;
        }
        try {
            Gson gson = new Gson();
            return gson.fromJson(json, Entry.class);
        } catch (Exception e) {
            Log.e("GSON", "Errore nel parsing dell'entry salvata: " + e.getMessage());
            return null;
        }
    }
    @Override
    protected void onResume(){
        super.onResume();
        gestisciPosizioneEMeteo();
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