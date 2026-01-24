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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                        userMarker.setTitle("Tu sei qui");
                        userMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

                        // Imposta un'icona personalizzata per la posizione dell'utente (rossa)
                        Drawable userIcon = ContextCompat.getDrawable(MappaActivity.this, R.drawable.ic_location);
                        if (userIcon != null) {
                            userIcon = userIcon.mutate();
                            userIcon.setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
                            userMarker.setIcon(userIcon);
                        }
                    });
                }
            }

        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Funzione per ottenere un colore diverso in base al tipo di segnalazione
    private int getColorForTipo(String tipo) {
        if (tipo == null) {
            return Color.GRAY; // Colore di default per tipo non specificato
        }
        switch (tipo.toLowerCase()) {
            case "incendio":
                return Color.RED;
            case "allagamento":
                return Color.BLUE;
            case "frana":
                return Color.rgb(139, 69, 19); // Marrone
            case "incidente stradale":
                return Color.YELLOW;
            default:
                return Color.GREEN; // Colore per altre segnalazioni
        }
    }

    private void caricaSegnalazioni() {
        db.collection("segnalazioni")
                .limit(50)
                .whereEqualTo("stato", "accettata")
                .get()
                .addOnSuccessListener(query -> {
                    for (QueryDocumentSnapshot doc : query) { //query ha gli elementi trovati

                            Double lat = doc.getDouble("lat");
                            Double lon = doc.getDouble("lon");
                            if (lat == null || lon == null) continue;

                            String tipo = doc.getString("tipo");
                            String descrizione = doc.getString("descrizione");

                            GeoPoint p = new GeoPoint(lat, lon);

                            //Creo un pin sulla mappa
                            Marker marker = new Marker(map);
                            marker.setPosition(p);
                            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

                            android.graphics.drawable.Drawable iconaCustom = ContextCompat.getDrawable(this, R.drawable.ic_location);
                            if (iconaCustom != null) {
                                iconaCustom = iconaCustom.mutate();

                                iconaCustom.setColorFilter(android.graphics.Color.GREEN, android.graphics.PorterDuff.Mode.SRC_IN);

                                marker.setIcon(iconaCustom);
                            }

                            marker.setTitle(tipo != null ? tipo : "Segnalazione");
                            marker.setSubDescription(descrizione != null ? descrizione : "");

                            map.getOverlays().add(marker);
                        }
                    map.invalidate();
                })
                .addOnFailureListener(e -> Log.e("MAPPA", "Errore caricamento segnalazioni: " + e.getMessage()));
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
            if (grantResults.length == 0 || grantResults[0] != PERMISSION_GRANTED) {
                Log.w("MappaActivity", "Permesso di scrittura negato. La mappa potrebbe non funzionare offline.");
            }
        }
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }
        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toArray(new String[0]), REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }
}