package com.example.climalert;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.HashMap;
import java.util.Map;

public class SegnalazioneActivity extends AppCompatActivity {

    private ImageButton btnIndietro;
    private Button btnInvia;
    private Spinner spinnerTipo;
    private EditText etDescrizione;
    private FirebaseFirestore database;
    private FirebaseAuth mAuth;

    private static final String TAG = "SegnalazioneActivity";
    private MapView mapSelection;
    private Marker selectedMarker;
    private GeoPoint currentSelection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_segnalazione);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        org.osmdroid.config.Configuration.getInstance().load(this,
                android.preference.PreferenceManager.getDefaultSharedPreferences(this));

        database = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        spinnerTipo = findViewById(R.id.spinnerTipo);
        etDescrizione = findViewById(R.id.etDescrizione);
        btnIndietro = findViewById(R.id.btnIndietro);
        btnInvia = findViewById(R.id.btnInvia);
        String[] opzioni = {"Temporale", "Grandine", "Vento", "Altro"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opzioni);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(adapter);
        // Configurazione Mappa
        mapSelection = findViewById(R.id.mapSelection);
        mapSelection.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK);
        mapSelection.setMultiTouchControls(true);
        mapSelection.getController().setZoom(15.0);

        // Posizione iniziale (es. Venezia o una di default)
        currentSelection = new GeoPoint(45.4408, 12.3155);
        mapSelection.getController().setCenter(currentSelection);

        // Marker iniziale
        selectedMarker = new Marker(mapSelection);
        selectedMarker.setPosition(currentSelection);
        selectedMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        android.graphics.drawable.Drawable iconaCustom = ContextCompat.getDrawable(this, R.drawable.ic_location);
        if (iconaCustom != null) {
            iconaCustom = iconaCustom.mutate();
            iconaCustom.setColorFilter(android.graphics.Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
            selectedMarker.setIcon(iconaCustom);
        }

        selectedMarker.setTitle("Trascina o clicca per spostare");
        mapSelection.getOverlays().add(selectedMarker);

        // Gestione del click sulla mappa per cambiare posizione
        org.osmdroid.events.MapEventsReceiver mReceive = new org.osmdroid.events.MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                currentSelection = p;
                selectedMarker.setPosition(p);
                mapSelection.invalidate(); // Rinfresca la mappa
                return true;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) { return false; }
        };

        mapSelection.getOverlays().add(new org.osmdroid.views.overlay.MapEventsOverlay(mReceive));

        // Modifica il click di btnInvia per usare currentSelection
        btnInvia.setOnClickListener(view -> {
            String descrizione = etDescrizione.getText().toString().trim();
            String tipo = spinnerTipo.getSelectedItem().toString().trim();
            FirebaseUser user = mAuth.getCurrentUser();

            if (user != null) {
                Map<String, Object> segnalazioneData = new HashMap<>();
                segnalazioneData.put("descrizione", descrizione);
                segnalazioneData.put("tipo", tipo);
                segnalazioneData.put("utente", user.getUid());
                segnalazioneData.put("email", user.getEmail());
                segnalazioneData.put("stato", "in attesa");
                DocumentReference docRef = database.collection("users").document(user.getUid());
                docRef.get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String username = documentSnapshot.getString("username");
                        segnalazioneData.put("username", username);
                    }
                });


                // COORDINATE REALI
                segnalazioneData.put("lat", currentSelection.getLatitude());
                segnalazioneData.put("lon", currentSelection.getLongitude());

                segnalazioneData.put("timestamp", com.google.firebase.Timestamp.now());

                database.collection("segnalazioni").add(segnalazioneData)
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "Segnalazione riuscita");
                            segnalationSuccessUI();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Errore invio", Toast.LENGTH_SHORT).show();
                        });
            }
        });
        btnIndietro.setOnClickListener(view -> {
            Intent intent = new Intent(SegnalazioneActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
    private void segnalationSuccessUI(){
        Intent intent = new Intent(SegnalazioneActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapSelection != null) {
            mapSelection.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapSelection != null) {
            mapSelection.onPause();
        }
    }

}