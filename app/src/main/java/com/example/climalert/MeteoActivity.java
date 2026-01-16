package com.example.climalert;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;
import java.util.Locale;

public class MeteoActivity extends AppCompatActivity {

    private ImageButton btnIndietro;
    private TextView txtPosizione, txtGradi;
    private com.google.android.gms.location.FusedLocationProviderClient fusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_meteo);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fusedLocationClient = com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(this);
        recuperaPosizioneGPS();

        btnIndietro = findViewById(R.id.btnIndietro);

        btnIndietro.setOnClickListener(view -> {
            Intent intent = new Intent(MeteoActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        txtPosizione = findViewById(R.id.txtPosizione);


        txtGradi = findViewById(R.id.txtGradi);

        String gradi = "10°C";   //da modificare con temperatura attuale
        txtGradi.setText(gradi);
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
                } else {
                    txtPosizione.setText("Posizione sconosciuta");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            txtPosizione.setText("Errore localizzazione");
        }
    }
}