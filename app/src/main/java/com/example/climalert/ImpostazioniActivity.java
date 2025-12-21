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

public class ImpostazioniActivity extends AppCompatActivity {

    private ImageButton btnIndietro;
    private MaterialButton btnDisconnetti;
    private MaterialButton btnElimina;
    private FirebaseAuth mAuth;

    private static final String TAG = "ImpostazioniActivity";

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
        btnDisconnetti = findViewById(R.id.btnDisconnetti);
        btnIndietro = findViewById(R.id.btnIndietro);
        btnElimina = findViewById(R.id.btnElimina);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

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
            if (user == null) {
                Toast.makeText(ImpostazioniActivity.this, "Non sei loggato!", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Utente non loggato svolge disconetti account, come è arrivato?");
                logOutUI();
                return;
            }
            accountDelete(user);
            Toast.makeText(ImpostazioniActivity.this, "Account cancellato!", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Account cancellato");

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
        user.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(ImpostazioniActivity.this, "Account cancellato!", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "Account cancellato");
                logOutUI();
            } else {
                Toast.makeText(ImpostazioniActivity.this, "Errore nella cancellazione dell'account!", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Errore nella cancellazione dell'account");
            }
        });
    }


}