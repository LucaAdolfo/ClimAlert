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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

public class MainAdminActivity extends AppCompatActivity {

    private Button btnSegnalazioni;
    private Button btnLogout; // Aggiunto pulsante per il logout
    private View btnMappa;
    static final String TAG = "MainAdminActivity";
    private FirebaseAuth mAuth;
    private FirebaseFirestore database;
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
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        checkIsAdmin();
        btnSegnalazioni = findViewById(R.id.btnGestisciSegnalazioni);
        btnSegnalazioni.setOnClickListener(view -> {
            Intent intent = new Intent(MainAdminActivity.this, SegnalazioniAdminActivity.class);
            startActivity(intent);
        });

        // Gestione del pulsante di logout
        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(view -> {
            // Reindirizza alla schermata di accesso dell'admin
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser(); // per sicurezza in casp lo statp è cambiato
            if (currentUser == null) {
                Toast.makeText(MainAdminActivity.this, "Non sei loggato!", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Utente non loggato svolge disconetti account admin, come è arrivato?");
                Intent intent = new Intent(MainAdminActivity.this, AccediAdminActivity.class);
                startActivity(intent);
                finish(); // Chiude l'activity corrente
            }
            else {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainAdminActivity.this, AccediAdminActivity.class);
                startActivity(intent);
                finish(); // Chiude l'activity corrente
            }
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
    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseAuth.getInstance().signOut(); // Disconnette l'utente se l'app viene chiusa
    }
    private void emergencyRedirect(){
        Intent intent = new Intent(MainAdminActivity.this, AccediActivity.class);
        startActivity(intent);
        finish();
    }
    private void checkIsAdmin(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            emergencyRedirect();
            return;
        }
        String id = user.getUid();
        Log.w(TAG, "ID utente: " + id);
        DocumentReference docRef = database.collection("users").document(id);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String username = documentSnapshot.getString("tipo_utente");
                if (!username.equals("admin")) {
                    emergencyRedirect();
                }
            }else{
                Log.e("AUTH", "Documento utente non trovato!");
                emergencyRedirect();
            }
        }).addOnFailureListener(v->{
            Log.e("AUTH", "Errore recupero documento utente!");
            emergencyRedirect();
        });
    }
}