package com.example.climalert;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AccediActivity extends AppCompatActivity {

    private Button btnAccedi, btnRegistrati, btnAccediOspite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_accedi);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //accedi (manca la logica)
        btnAccedi = findViewById(R.id.btnAccedi);
        btnAccedi.setOnClickListener(view -> {
            Intent intent = new Intent(AccediActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        //registrati
        btnRegistrati = findViewById(R.id.btnRegistrati);
        btnRegistrati.setOnClickListener(view -> {
            Intent intent = new Intent(AccediActivity.this, RegistratiActivity.class);
            startActivity(intent);
            finish();
        });

        //accedi senza profilo
        btnAccediOspite = findViewById(R.id.btnAccediOspite);
        btnAccediOspite.setOnClickListener(view -> {
            Intent intent = new Intent(AccediActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}