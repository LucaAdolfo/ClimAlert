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

public class SicurezzaActivity extends AppCompatActivity {

    private ImageButton btnIndietro;
    private Button btnModificaPassword, btnTerminiServizio;


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

        btnIndietro = findViewById(R.id.btnIndietro);
        btnIndietro.setOnClickListener(view -> {
            Intent intent = new Intent(SicurezzaActivity.this, ImpostazioniActivity.class);
            startActivity(intent);
            finish();
        });

        btnModificaPassword = findViewById(R.id.btnModificaPassword);
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