package com.example.climalert;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PreferenzeActivity extends AppCompatActivity {

    private ImageButton btnIndietro;
    private SwitchCompat switchNotifiche;


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
            Intent intent = new Intent(PreferenzeActivity.this, ImpostazioniActivity.class);
            startActivity(intent);
            finish();
        });

        switchNotifiche = findViewById(R.id.switchNotifiche);
        switchNotifiche.setOnCheckedChangeListener((buttonView, isChecked) -> {
            //TODO: modifica stato notifiche

        });
    }
}