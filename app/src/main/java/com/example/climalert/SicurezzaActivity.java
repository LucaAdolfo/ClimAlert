package com.example.climalert;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SicurezzaActivity extends AppCompatActivity {

    private ImageButton btnIndietro;
    private Button btnModificaPassword, btnTerminiServizio;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sicurezza);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mAuth = FirebaseAuth.getInstance();
        btnIndietro = findViewById(R.id.btnIndietro);
        btnIndietro.setOnClickListener(view -> {
            Intent intent = new Intent(SicurezzaActivity.this, ImpostazioniActivity.class);
            startActivity(intent);
            finish();
        });

        btnModificaPassword = findViewById(R.id.btnModificaPassword);
        FirebaseUser users = mAuth.getCurrentUser();
        if (users.isAnonymous()) {
            btnModificaPassword.setEnabled(false);
            btnModificaPassword.setAlpha(0.5f);
        }

        btnModificaPassword.setOnClickListener(view -> {
            Intent intent = new Intent(SicurezzaActivity.this, ModificaPasswordActivity.class);
            startActivity(intent);
        });

        btnTerminiServizio = findViewById(R.id.btnTerminiServizio);
        btnTerminiServizio.setOnClickListener(view -> {
            Intent intent = new Intent(SicurezzaActivity.this, TerminiServizioActivity.class);
            startActivity(intent);
        });
    }
}