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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

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
        btnInvia.setOnClickListener(view -> {
            String descrizione= etDescrizione.getText().toString().trim();
            String tipo= spinnerTipo.getSelectedItem().toString().trim();
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {//per sicurezza
                Map<String, Object> segnalazioneData = new HashMap<>();
                segnalazioneData.put("descrizione", descrizione);
                segnalazioneData.put("tipo", tipo);
                segnalazioneData.put("utente", user.getUid());
                segnalazioneData.put("email", user.getEmail());
                segnalazioneData.put("stato", "in attesa");
                segnalazioneData.put("lat", 0.0); //TODO inserire posizione
                segnalazioneData.put("lon", 0.0);//TODO inserire posizione
                segnalazioneData.put("tipo", tipo);
                segnalazioneData.put("timestamp", com.google.firebase.Timestamp.now());
                database.collection("segnalazioni").add(segnalazioneData).addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Segnalazione riuscita");
                    segnalationSuccessUI();
                }).addOnFailureListener(e -> {
                    Log.e(TAG, "Errore nell'aggiunta della segnalazione al database", e);
                    Toast.makeText(SegnalazioneActivity.this, "Segnalazione non riuscita",
                            Toast.LENGTH_SHORT).show();
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
}