package com.example.climalert;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PreferenzeActivity extends AppCompatActivity {

    private ImageButton btnIndietro;
    private Spinner spinnerZona;
    private RadioGroup rgPosizione;
    private RadioButton rbGps, rbManuale;

    // Definizioni per SharedPreferences
    public static final String PREFS_NAME = "ClimAlertPrefs";
    public static final String USE_GPS_KEY = "useGps";
    public static final String SELECTED_ZONE_KEY = "selectedZone";

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
        rgPosizione = findViewById(R.id.rgPosizione);
        rbGps = findViewById(R.id.rbGps);
        rbManuale = findViewById(R.id.rbManuale);
        spinnerZona = findViewById(R.id.spinnerZona);

        btnIndietro.setOnClickListener(view -> {
            Intent intent = new Intent(PreferenzeActivity.this, ImpostazioniActivity.class);
            startActivity(intent);
            finish();
        });

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean useGps = preferences.getBoolean(USE_GPS_KEY, true);
        int savedZoneIndex = preferences.getInt(SELECTED_ZONE_KEY, 0);

        //setuo dello spinner
        String[] opzioni = {"Belluno e Prealpi orientali", "Treviso e pianura orientale", "Venezia e laguna", "Vicenza e pedemontana", "Padova e pianura centrale", "Rovigo e pianura meridionale", "Verona e pedemontana"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opzioni);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerZona.setAdapter(adapter);
        spinnerZona.setSelection(savedZoneIndex);


        if (useGps) {
            rbGps.setChecked(true);
            spinnerZona.setVisibility(View.GONE);
        } else {
            rbManuale.setChecked(true);
            spinnerZona.setVisibility(View.VISIBLE);
        }

        //Per il cambio della modalità gps o manuale
        rgPosizione.setOnCheckedChangeListener((group, checkedId) -> {
            SharedPreferences.Editor editor = preferences.edit();
            if (checkedId == R.id.rbGps) {
                spinnerZona.setVisibility(View.GONE);
                editor.putBoolean(USE_GPS_KEY, true);
            } else if (checkedId == R.id.rbManuale) {
                spinnerZona.setVisibility(View.VISIBLE);
                editor.putBoolean(USE_GPS_KEY, false);
            }
            editor.apply();
        });


        spinnerZona.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(SELECTED_ZONE_KEY, position);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
}