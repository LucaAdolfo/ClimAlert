package com.example.climalert;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ImpostazioniActivity extends AppCompatActivity {

    private ImageButton btnIndietro;
    private MaterialButton btnDisconnetti;
    private MaterialButton btnElimina;
    private MaterialButton btnTema, btnProfilo, btnSicurezza, btnPreferenze, btnFaq;
    private FirebaseAuth mAuth;

    private static final String TAG = "ImpostazioniActivity";


    private FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_impostazioni);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnProfilo = findViewById(R.id.btnInfo);
        btnSicurezza = findViewById(R.id.btnSicurezza);
        btnPreferenze = findViewById(R.id.btnPreferenze);
        btnFaq = findViewById(R.id.btnFaq);
        btnTema = findViewById(R.id.btnTema);
        btnDisconnetti = findViewById(R.id.btnDisconnetti);
        btnIndietro = findViewById(R.id.btnIndietro);
        btnElimina = findViewById(R.id.btnElimina);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        database = FirebaseFirestore.getInstance();


        btnDisconnetti.setOnClickListener(view -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser(); // per sicurezza in casp lo statp è cambiato
            if (currentUser == null) {
                Toast.makeText(ImpostazioniActivity.this, "Non sei loggato!", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Utente non loggato svolge disconetti account, come è arrivato?");
                logOutUI();
                return;
            }
            if (currentUser.isAnonymous()) {
                accountDelete(currentUser);
                return;
            }
            else {
                FirebaseAuth.getInstance().signOut();
                logOutUI();
            }
            Log.i(TAG, "Utente disconnesso");
        });


        btnIndietro.setOnClickListener(view -> {
            Intent intent = new Intent(ImpostazioniActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
        btnElimina.setOnClickListener(view -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser == null) {
                Toast.makeText(ImpostazioniActivity.this, "Non sei loggato!", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Utente non loggato svolge disconetti account, come è arrivato?");
                logOutUI();
                return;
            }
            accountDelete(currentUser);
            Toast.makeText(ImpostazioniActivity.this, "Account cancellato!", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Account cancellato");

        });

        btnProfilo.setOnClickListener(view -> {
            Intent intent = new Intent(ImpostazioniActivity.this, ProfiloActivity.class);
            startActivity(intent);
        });

        btnSicurezza.setOnClickListener(view -> {
            Intent intent = new Intent(ImpostazioniActivity.this, SicurezzaActivity.class);
            startActivity(intent);
        });

        btnTema.setOnClickListener(view -> {
            Intent intent = new Intent(ImpostazioniActivity.this, TemaActivity.class);
            startActivity(intent);
        });

        btnPreferenze.setOnClickListener(view -> {
            Intent intent = new Intent(ImpostazioniActivity.this, PreferenzeActivity.class);
            startActivity(intent);
        });

        btnFaq.setOnClickListener(view -> {
            Intent intent = new Intent(ImpostazioniActivity.this, FaqActivity.class);
            startActivity(intent);
        });
    }

    private void logOutUI() {
        Intent intent = new Intent(ImpostazioniActivity.this, AccediActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        //Toast.makeText(ImpostazioniActivity.this, "Sei stato disconnesso!", Toast.LENGTH_SHORT).show();
        finish();
    }
    private void accountDelete(FirebaseUser user){
        String uid = user.getUid();
        database.collection("users").document(uid)
                .delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Dati utente rimossi da Firestore");
                    } else {
                        Log.e(TAG, "Errore rimozione dati Firestore", task.getException());
                    }

                    user.delete().addOnCompleteListener(authTask -> {
                        if (authTask.isSuccessful()) {
                            Toast.makeText(ImpostazioniActivity.this, "Account cancellato definitivamente!", Toast.LENGTH_SHORT).show();
                            Log.i(TAG, "Account Auth eliminato");
                            logOutUI();
                        } else {
                            Toast.makeText(ImpostazioniActivity.this, "Errore nella cancellazione dell'account!", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Errore Auth delete", authTask.getException());
                        }
                    });
                });
    }


}