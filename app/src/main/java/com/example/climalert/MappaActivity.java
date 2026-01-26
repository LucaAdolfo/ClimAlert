
package com.example.climalert;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
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

public class MappaActivity extends AppCompatActivity {
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView map = null;

    private GpsMyLocationProvider myLocation = null;
    private FirebaseFirestore db;
    private ImageButton btnIndietro;

    private final double[][] coordinateZone = {
            {46.1425, 12.2167}, //belluno
            {45.6669, 12.2431}, //treviso
            {45.4408, 12.3155}, //venezia
            {45.5479, 11.5446}, //vicenza
            {45.4064, 11.8768}, //padova
            {45.0711, 11.7907}, //rovigo
            {45.4384, 10.9916}  //verona
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mappa);
        db = FirebaseFirestore.getInstance();

        btnIndietro = findViewById(R.id.btnIndietro);
        btnIndietro.setOnClickListener(view -> {
            Intent intent = new Intent(MappaActivity.this, MainActivity.class);
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

        gestisciPosizioneMappa();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void gestisciPosizioneMappa() {
        SharedPreferences prefs = getSharedPreferences("ClimAlertPrefs", MODE_PRIVATE);
        boolean useGps = prefs.getBoolean("useGps", true);

        if (useGps) {
            avviaLocalizzazioneGps();
        } else {
            int zoneIndex = prefs.getInt("selectedZone", 0);
            GeoPoint point = new GeoPoint(coordinateZone[zoneIndex][0], coordinateZone[zoneIndex][1]);
            map.getController().setZoom(12.0);
            map.getController().animateTo(point);
            aggiungiPinUtente(point, "La tua zona");
        }
    }

    private void avviaLocalizzazioneGps() {
        myLocation = new GpsMyLocationProvider(this);
        myLocation.startLocationProvider(new IMyLocationConsumer() {
            @Override
            public void onLocationChanged(Location location, IMyLocationProvider source) {
                if (location != null) {
                    runOnUiThread(() -> {
                        GeoPoint userLocation = new GeoPoint(location.getLatitude(), location.getLongitude());

                        //mappa centratat sulla posizione
                        map.getController().animateTo(userLocation);

                        //smetto di ascoltare per risparmiare batteria
                        myLocation.stopLocationProvider();

                        aggiungiPinUtente(userLocation, "Tu");
                    });
                }
            }
        });
    }

    private void aggiungiPinUtente(GeoPoint point, String titolo) {
        Marker userMarker = new Marker(map);
        userMarker.setPosition(point);
        userMarker.setTitle(titolo);
        userMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        android.graphics.drawable.Drawable iconaCustom = ContextCompat.getDrawable(this, R.drawable.ic_location);
        if (iconaCustom != null) {
            iconaCustom = iconaCustom.mutate();
            iconaCustom.setColorFilter(android.graphics.Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
            userMarker.setIcon(iconaCustom);
        }
        map.getOverlays().add(userMarker);
        map.invalidate();
    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
                Log.d("MappaActivity", "Permesso concesso dall\'utente.");
            } else {
                Log.w("MappaActivity", "Permesso negato dall\'utente.");
            }
        }
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PERMISSION_GRANTED) {
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
        db.collection("segnalazioni")
                .limit(50)
                .whereEqualTo("stato", "accettata")
                .get()
                .addOnSuccessListener(query -> {
                    for (QueryDocumentSnapshot doc : query) {
                            Double lat = doc.getDouble("lat");
                            Double lon = doc.getDouble("lon");
                            if (lat == null || lon == null) continue;

                            String tipo = doc.getString("tipo");
                            String descrizione = doc.getString("descrizione");
                            String username = doc.getString("username");
                            com.google.firebase.Timestamp timestamp = doc.getTimestamp("timestamp");

                            String dataFormattata = "";
                            if (timestamp != null) {
                                java.util.Date date = timestamp.toDate();
                                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault());
                                dataFormattata = sdf.format(date);
                            }

                            GeoPoint p = new GeoPoint(lat, lon);

                            Marker marker = new Marker(map);
                            marker.setPosition(p);
                            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

                            // --- Inizio modifica ---
                            // Imposta un'icona e un colore diverso in base al tipo di segnalazione.
                            // Per usare icone personalizzate (es. ic_temporale.png), aggiungile
                            // alla cartella 'res/drawable' e usa 'R.drawable.ic_temporale'.
                            android.graphics.drawable.Drawable iconaCustom = ContextCompat.getDrawable(this, R.drawable.ic_location);
                            if (iconaCustom != null) {
                                iconaCustom = iconaCustom.mutate();
                                int color;
                                if (tipo != null) {
                                    switch (tipo) {
                                        case "Temporale":
                                            color = android.graphics.Color.BLUE; // Esempio: Blu per temporale
                                            break;
                                        case "Grandine":
                                            color = android.graphics.Color.CYAN; // Esempio: Ciano per grandine
                                            break;
                                        case "Vento":
                                            color = android.graphics.Color.GRAY; // Esempio: Grigio per vento
                                            break;
                                        default:
                                            color = android.graphics.Color.GREEN; // Colore di default per "Altro" o non specificato
                                            break;
                                    }
                                } else {
                                    color = android.graphics.Color.GREEN; // Colore di default
                                }
                                iconaCustom.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN);
                                marker.setIcon(iconaCustom);
                            }
                            // --- Fine modifica ---

                            marker.setTitle(tipo != null ? "Evento: " + tipo : "Segnalazione");

                            StringBuilder subDesc = new StringBuilder();
                            if (descrizione != null && !descrizione.isEmpty()) {
                                subDesc.append("<b>Descrizione: </b>").append(descrizione);
                            }

                            // Controllo che l'username esista e non sia vuoto prima di aggiungerlo.
                            if (username != null && !username.isEmpty()) {
                                if (subDesc.length() > 0) {
                                    subDesc.append("<br>");
                                }
                                subDesc.append("<b>Username: </b>").append(username);
                            }

                            if (subDesc.length() > 0) {
                                subDesc.append("<br>");
                            }
                            subDesc.append("<b>Effettuata il:</b> ").append(dataFormattata);

                            marker.setSubDescription(subDesc.toString());

                            map.getOverlays().add(marker);
                        }
                    map.invalidate();
                })
                .addOnFailureListener(e -> Log.e("MAPPA", "Errore caricamento segnalazioni: " + e.getMessage()));
    }
}
