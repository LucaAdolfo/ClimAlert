package com.example.climalert;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.IMyLocationConsumer;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;

import java.util.ArrayList;

public class MappaAdminActivity extends AppCompatActivity {

    private ImageButton btnIndietro;
    private MapView map;
    private GpsMyLocationProvider myLocation;
    private FirebaseFirestore db;
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mappa_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        btnIndietro = findViewById(R.id.btnIndietro);
        btnIndietro.setOnClickListener(view -> {
            Intent intent = new Intent(MappaAdminActivity.this, MainAdminActivity.class);
            startActivity(intent);
            finish();
        });

        //Istanzia osm di default
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.getController().setZoom(9.5);
        map.setMultiTouchControls(true);
        caricaSegnalazioni();
        String[] permissions = new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        requestPermissionsIfNecessary(permissions);

        myLocation = new GpsMyLocationProvider(ctx);
        myLocation.startLocationProvider(new IMyLocationConsumer() {
            @Override
            public void onLocationChanged(Location location, IMyLocationProvider source) {
                if (location != null) {
                    runOnUiThread(() -> {
                        GeoPoint userLocation = new GeoPoint(location.getLatitude(), location.getLongitude());

                        // Centra la mappa sulla posizione dell'utente
                        map.getController().animateTo(userLocation);

                        // Dopo aver trovato la posizione, possiamo smettere di ascoltare per risparmiare batteria
                        myLocation.stopLocationProvider();


                        Marker userMarker = new Marker(map);
                        userMarker.setPosition(userLocation);
                        map.getOverlays().add(userMarker);
                        userMarker.setTitle("You");
                        userMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

                        android.graphics.drawable.Drawable iconaCustom = ContextCompat.getDrawable(MappaAdminActivity.this, R.drawable.ic_location);
                        if (iconaCustom != null) {
                            iconaCustom = iconaCustom.mutate();

                            iconaCustom.setColorFilter(android.graphics.Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);

                            userMarker.setIcon(iconaCustom);
                        }
                    });
                }
            }

        });
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PERMISSION_GRANTED) {
                // Permission is not granted
                permissionsToRequest.add(permission);
            }
        }
        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void caricaSegnalazioni() {
        //Prendo la collezione di segnalazioni (impostato limite 50 evitare rallentamnenti)
        db.collection("segnalazioni")
                .limit(50)
                .get()
                .addOnSuccessListener(query -> {
                    for (QueryDocumentSnapshot doc : query) { //query ha gli elementi trovati

                        Double lat = doc.getDouble("lat");
                        Double lon = doc.getDouble("lon");
                        if (lat == null || lon == null) continue;

                        String stato = doc.getString("stato");
                        String tipo = doc.getString("tipo");
                        String descrizione = doc.getString("descrizione")+"<br><b>Username: </b>"+doc.getString("username");
                        com.google.firebase.Timestamp timestamp = doc.getTimestamp("timestamp");

                        String dataFormattata = "";
                        if (timestamp != null) {
                            java.util.Date date = timestamp.toDate();
                            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault());
                            dataFormattata = sdf.format(date);
                        }

                        GeoPoint p = new GeoPoint(lat, lon);

                        //Creo un pin sulla mappa
                        Marker marker = new Marker(map);
                        marker.setPosition(p);
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

                        android.graphics.drawable.Drawable iconaCustom = ContextCompat.getDrawable(this, R.drawable.ic_location);

                        if (iconaCustom != null) {
                            iconaCustom = iconaCustom.mutate();

                            if ("in attesa".equalsIgnoreCase(stato)) {
                                //giallo se è in attesa
                                iconaCustom.setColorFilter(android.graphics.Color.YELLOW, android.graphics.PorterDuff.Mode.SRC_IN);
                            } else if ("accettata".equalsIgnoreCase(stato)) {
                                //verde se accettata
                                iconaCustom.setColorFilter(android.graphics.Color.GREEN, android.graphics.PorterDuff.Mode.SRC_IN);
                            } else {
                                //grigio per altro (non dovrebbe capitare)
                                iconaCustom.setColorFilter(android.graphics.Color.GRAY, android.graphics.PorterDuff.Mode.SRC_IN);
                            }
                            marker.setIcon(iconaCustom);
                        }

                        marker.setTitle(tipo != null ? "Evento: " + tipo : "Segnalazione");
                        marker.setSubDescription(descrizione != null ? "<b>Descrizione: </b>" + descrizione + "<br><b>Effettuata il:</b> " + dataFormattata : "<br><b>Effettuata il:</b> " + dataFormattata);

                        map.getOverlays().add(marker);
                    }
                    map.invalidate();
                })
                .addOnFailureListener(e -> Log.e("MAPPA", "Errore caricamento segnalazioni: " + e.getMessage()));
    }
}