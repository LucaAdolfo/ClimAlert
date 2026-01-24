package com.example.climalert;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainAdminActivity extends AppCompatActivity {

    private Button btnSegnalazioni;
    private Button btnLogout;
    private View btnMappa;
    private FirebaseAuth mAuth;

    private static final String TAG = "MainAdminActivity";


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

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(view -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser(); // per sicurezza in casp lo statp è cambiato
            if (currentUser == null) {
                Toast.makeText(MainAdminActivity.this, "Non sei loggato!", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Utente non loggato svolge disconetti account, come è arrivato?");
                logOutUI();
                return;
            }
            else {
                FirebaseAuth.getInstance().signOut();
                logOutUI();
            }
            Log.i(TAG, "Utente disconnesso");
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

    private void logOutUI() {
        Intent intent = new Intent(MainAdminActivity.this, AccediActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}