package com.example.climalert;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

public class MainAdminActivity extends AppCompatActivity {

    private Button btnSegnalazioni;
    private Button btnLogout; // Aggiunto pulsante per il logout
    private View btnMappa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnSegnalazioni = findViewById(R.id.btnGestisciSegnalazioni);
        btnSegnalazioni.setOnClickListener(view -> {
            Intent intent = new Intent(MainAdminActivity.this, SegnalazioniAdminActivity.class);
            startActivity(intent);
        });

        // Gestione del pulsante di logout
        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(view -> {
            // Reindirizza alla schermata di accesso dell'admin
            Intent intent = new Intent(MainAdminActivity.this, AccediAdminActivity.class);
            startActivity(intent);
            finish(); // Chiude l'activity corrente
        });

        btnMappa = findViewById(R.id.mapOverlay);

        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        MapView mapPreview = findViewById(R.id.mapPreview);
        mapPreview.setTileSource(TileSourceFactory.MAPNIK);
        mapPreview.setMultiTouchControls(false); //da lasciare se no possibile che si sposti la mappa
        mapPreview.getController().setZoom(12.0);
        GeoPoint startPoint = new GeoPoint(45.4408, 12.3155);
        mapPreview.getController().setCenter(startPoint);

        View.OnClickListener openMapListener = v -> {
            Intent intent = new Intent(MainAdminActivity.this, MappaAdminActivity.class);
            startActivity(intent);
        };

        btnMappa.setOnClickListener(openMapListener);

    }
}