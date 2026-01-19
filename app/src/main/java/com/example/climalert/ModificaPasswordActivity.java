package com.example.climalert;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ModificaPasswordActivity extends AppCompatActivity {

    private ImageButton btnIndietro;
    private Button btnSalva;
    private TextView edit_password, edit_conferma_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_modifica_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnIndietro = findViewById(R.id.btnIndietro);
        btnIndietro.setOnClickListener(view -> {
           Intent intent = new Intent(ModificaPasswordActivity.this, SicurezzaActivity.class);
            startActivity(intent);
            finish();
        });

        edit_password = findViewById(R.id.edit_new_password);
        edit_conferma_password = findViewById(R.id.edit_conferma_password);

        btnSalva = findViewById(R.id.btnSalva);
        btnSalva.setOnClickListener(view -> {

            String password = edit_password.getText().toString().trim();
            String confermaPassword = edit_conferma_password.getText().toString().trim();

            if (!password.equals(confermaPassword)) {
                Toast.makeText(ModificaPasswordActivity.this, "Le password non corrispondono", Toast.LENGTH_SHORT).show();
                edit_password.setText("");
                edit_conferma_password.setText("");
                return;
            }
            if (password.isEmpty() || password.length() < 6) {
                Toast.makeText(ModificaPasswordActivity.this, "Password non valida almeno 6 caratteri", Toast.LENGTH_SHORT).show();
                return;
            }

            //TODO: modifica password nel database e controlla che old password sia corretta e diversa da new



            Intent intent = new Intent(ModificaPasswordActivity.this, SicurezzaActivity.class);
            startActivity(intent);
            finish();
        });
    }
}