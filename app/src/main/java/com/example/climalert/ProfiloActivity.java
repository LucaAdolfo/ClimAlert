package com.example.climalert;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class ProfiloActivity extends AppCompatActivity {

    private ImageButton btnIndietro;
    private TextView txtUsername;
    private TextView txtEmail;
    private FirebaseAuth mAuth;
    private Button btnCambiaUsername;
    private FirebaseFirestore database;
    private LinearLayout containerSegnalazioni;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profilo);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnIndietro = findViewById(R.id.btnIndietro);
        btnIndietro.setOnClickListener(view -> {
            Intent intent = new Intent(ProfiloActivity.this, ImpostazioniActivity.class);
            startActivity(intent);
            finish();
        });

        txtUsername = findViewById(R.id.txtUsername);
        txtEmail = findViewById(R.id.txtEmail);
        database = FirebaseFirestore.getInstance();
        containerSegnalazioni = findViewById(R.id.containerSegnalazioni);

        btnCambiaUsername = findViewById(R.id.btnCambiaUsername);
        btnCambiaUsername.setOnClickListener(view -> {
            Intent intent = new Intent(ProfiloActivity.this, CambiaUsernameActivity.class);
            startActivity(intent);
        });
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser users = mAuth.getCurrentUser();

        if (mAuth.getCurrentUser()!=null) {
            if (users.isAnonymous()) {
                txtUsername.setText("Username: Ospite");
                txtEmail.setText("E-mail: Non registrata");
                btnCambiaUsername.setVisibility(View.GONE);
            } else {
                DocumentReference docRef = database.collection("users").document(mAuth.getCurrentUser().getUid());
                docRef.get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String username = documentSnapshot.getString("username");
                        String mail = mAuth.getCurrentUser().getEmail();
                        txtUsername.setText("Username: " + username);
                        txtEmail.setText("E-mail: " + mail);
                    }
                });
            }
            caricaSegnalazioniUtente(users.getUid());
        }
    }

    private void caricaSegnalazioniUtente(String uid) {
        database.collection("segnalazioni")
                .whereEqualTo("utente", uid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        containerSegnalazioni.removeAllViews();

                        //nessuna segnalazione
                        if (task.getResult().isEmpty()) {
                            TextView tvVuoto = new TextView(this);
                            tvVuoto.setText("Non hai ancora inviato segnalazioni.");
                            containerSegnalazioni.addView(tvVuoto);
                            return;
                        }

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            //estrai dati
                            String idDoc = document.getId();
                            String tipo = document.getString("tipo");
                            String descrizione = document.getString("descrizione");
                            String stato = document.getString("stato");

                            aggiungiElementoLista(idDoc, tipo, descrizione + " (" + stato + ")");
                        }
                    } else {
                        Log.e("Firestore", "Errore recupero segnalazioni", task.getException());
                    }
                });
    }

    private void aggiungiElementoLista(String documentoId, String titolo, String sottotitolo) {
        //riga
        LinearLayout riga = new LinearLayout(this);
        riga.setOrientation(LinearLayout.HORIZONTAL);
        riga.setGravity(android.view.Gravity.CENTER_VERTICAL);
        riga.setPadding(0, 20, 0, 20);
        LinearLayout.LayoutParams rigaParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        riga.setLayoutParams(rigaParams);

        //icona
        ImageView iconaSegnalazione = new ImageView(this);
        iconaSegnalazione.setLayoutParams(new LinearLayout.LayoutParams(80, 80));
        iconaSegnalazione.setImageResource(R.drawable.ic_posizione); // O un'icona di allerta
        iconaSegnalazione.setPadding(10, 10, 10, 10);

        //testo
        LinearLayout colonnaTesti = new LinearLayout(this);
        colonnaTesti.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams testiParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        testiParams.setMarginStart(20);
        colonnaTesti.setLayoutParams(testiParams);

        TextView tvTitolo = new TextView(this);
        tvTitolo.setText(titolo.toUpperCase());
        tvTitolo.setTextSize(16);
        tvTitolo.setTypeface(null, android.graphics.Typeface.BOLD);
        tvTitolo.setTextColor(android.graphics.Color.BLACK);

        TextView tvSottotitolo = new TextView(this);
        tvSottotitolo.setText(sottotitolo);
        tvSottotitolo.setTextSize(14);

        colonnaTesti.addView(tvTitolo);
        colonnaTesti.addView(tvSottotitolo);

        //icona cestino
        ImageButton btnElimina = new ImageButton(this);
        btnElimina.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
        btnElimina.setImageResource(android.R.drawable.ic_menu_delete);
        btnElimina.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        btnElimina.setColorFilter(android.graphics.Color.RED);

        //per eliminare
        btnElimina.setOnClickListener(v -> {
            database.collection("segnalazioni").document(documentoId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        containerSegnalazioni.removeView(riga);
                        android.widget.Toast.makeText(this, "Segnalazione rimossa", android.widget.Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        android.widget.Toast.makeText(this, "Errore durante l'eliminazione", android.widget.Toast.LENGTH_SHORT).show();
                    });
        });

        riga.addView(iconaSegnalazione);
        riga.addView(colonnaTesti);
        riga.addView(btnElimina);

        containerSegnalazioni.addView(riga);
    }
}