package com.example.climalert;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

public class PreferenzeActivity extends AppCompatActivity {

    private ImageButton btnIndietro;
    private SwitchCompat switchNotifiche;
    private MapView mapSelection;

    // Definizioni per SharedPreferences
    public static final String PREFS_NAME = "ClimAlertPrefs";
    public static final String NOTIFICATIONS_ENABLED_KEY = "notificheAbilitate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_preferenze);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnIndietro = findViewById(R.id.btnIndietro);
        btnIndietro.setOnClickListener(view -> {
            // Torna alla schermata delle impostazioni
            Intent intent = new Intent(PreferenzeActivity.this, ImpostazioniActivity.class);
            startActivity(intent);
            finish();
        });

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        // Carica lo stato salvato o imposta true come default
        boolean notificheAbilitate = preferences.getBoolean(NOTIFICATIONS_ENABLED_KEY, true);
        switchNotifiche.setChecked(notificheAbilitate);

        switchNotifiche.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Salva lo stato dello switch nelle SharedPreferences
            SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
            editor.putBoolean(NOTIFICATIONS_ENABLED_KEY, isChecked);
            editor.apply();
        });

        // Configurazione della mappa
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        mapSelection = findViewById(R.id.mapSelection);
        mapSelection.setTileSource(TileSourceFactory.MAPNIK);
        mapSelection.setMultiTouchControls(true);
        mapSelection.getController().setZoom(12.0);
        // Imposta un punto di partenza predefinito (Venezia)
        GeoPoint startPoint = new GeoPoint(45.4408, 12.3155);
        mapSelection.getController().setCenter(startPoint);
    }
}