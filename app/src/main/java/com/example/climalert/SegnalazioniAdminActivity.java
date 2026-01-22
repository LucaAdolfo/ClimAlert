package com.example.climalert;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class SegnalazioniAdminActivity extends AppCompatActivity {

    private ImageButton btnIndietro;
    private TabLayout tabLayout;
    private FirebaseFirestore database;
    private LinearLayout containerSegnalazioniAdmin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_segnalazioni_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnIndietro = findViewById(R.id.btnIndietro);
        btnIndietro.setOnClickListener(view -> {
            Intent intent = new Intent(SegnalazioniAdminActivity.this, MainAdminActivity.class);
            startActivity(intent);
            finish();
        });

        containerSegnalazioniAdmin = findViewById(R.id.containerSegnalazioni);
        tabLayout = findViewById(R.id.tabLayoutAdmin);
        database = FirebaseFirestore.getInstance();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    caricaSegnalazioni("in attesa");
                } else {
                    caricaSegnalazioni("accettata");
                }
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        caricaSegnalazioni("in attesa");
    }

    private void caricaSegnalazioni(String statoFiltro) {
        database.collection("segnalazioni")
                .whereEqualTo("stato", statoFiltro)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    containerSegnalazioniAdmin.removeAllViews();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        aggiungiRiga(doc);
                    }
                });
    }

    private void aggiungiRiga(QueryDocumentSnapshot doc) {
        String documentoId = doc.getId();
        String tipo = doc.getString("tipo");
        String descrizione = doc.getString("descrizione");
        String emailUtente = doc.getString("email");

        LinearLayout marginContainer = new LinearLayout(this);
        marginContainer.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams marginParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        marginContainer.setLayoutParams(marginParams);

        LinearLayout riga = new LinearLayout(this);
        riga.setOrientation(LinearLayout.HORIZONTAL);
        riga.setGravity(android.view.Gravity.CENTER_VERTICAL);
        riga.setPadding(20, 30, 20, 30);
        riga.setBackgroundColor(android.graphics.Color.TRANSPARENT);

        riga.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        LinearLayout colonnaTesti = new LinearLayout(this);
        colonnaTesti.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams testiParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        colonnaTesti.setLayoutParams(testiParams);

        TextView tvTipo = new TextView(this);
        tvTipo.setText(tipo != null ? tipo.toUpperCase() : "SEGNALAZIONE");
        tvTipo.setTextSize(16);
        tvTipo.setTypeface(null, android.graphics.Typeface.BOLD);
        tvTipo.setTextColor(android.graphics.Color.BLACK);

        TextView tvDesc = new TextView(this);
        tvDesc.setText(descrizione);
        tvDesc.setTextSize(14);
        tvDesc.setTextColor(android.graphics.Color.BLACK);

        TextView tvUser = new TextView(this);
        tvUser.setText("Da: " + (emailUtente != null ? emailUtente : "Utente non trovato"));
        tvUser.setTextSize(12);
        tvUser.setTextColor(android.graphics.Color.parseColor("#444444"));

        colonnaTesti.addView(tvTipo);
        colonnaTesti.addView(tvDesc);
        colonnaTesti.addView(tvUser);

        LinearLayout containerAzioni = new LinearLayout(this);
        containerAzioni.setOrientation(LinearLayout.HORIZONTAL);

        ImageButton btnAccetta = new ImageButton(this);
        btnAccetta.setLayoutParams(new LinearLayout.LayoutParams(110, 110));
        btnAccetta.setImageResource(android.R.drawable.ic_menu_save);
        btnAccetta.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        btnAccetta.setColorFilter(android.graphics.Color.parseColor("#2E7D32"));

        btnAccetta.setOnClickListener(v -> {
            database.collection("segnalazioni").document(documentoId)
                    .update("stato", "accettata")
                    .addOnSuccessListener(aVoid -> {
                        containerSegnalazioniAdmin.removeView(marginContainer);
                        android.widget.Toast.makeText(this, "Accettata!", android.widget.Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        android.widget.Toast.makeText(this, "Errore: " + e.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                    });
        });

        ImageButton btnRifiuta = new ImageButton(this);
        btnRifiuta.setLayoutParams(new LinearLayout.LayoutParams(110, 110));
        btnRifiuta.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        btnRifiuta.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        btnRifiuta.setColorFilter(android.graphics.Color.RED);

        btnRifiuta.setOnClickListener(v -> {
            database.collection("segnalazioni").document(documentoId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        containerSegnalazioniAdmin.removeView(marginContainer);
                        android.widget.Toast.makeText(this, "Eliminata!", android.widget.Toast.LENGTH_SHORT).show();
                    });
        });

        containerAzioni.addView(btnAccetta);
        containerAzioni.addView(btnRifiuta);
        riga.addView(colonnaTesti);
        riga.addView(containerAzioni);
        marginContainer.addView(riga);

        containerSegnalazioniAdmin.addView(marginContainer);
    }
}