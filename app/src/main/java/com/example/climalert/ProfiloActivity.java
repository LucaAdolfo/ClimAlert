package com.example.climalert;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class ProfiloActivity extends AppCompatActivity {

    private ImageButton btnIndietro;
    private TextView txtUsername;
    private TextView txtEmail;
    private FirebaseAuth mAuth;
    private Button btnCambiaUsername;



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

        mAuth = FirebaseAuth.getInstance();

        //prendere username e email dal database
        String user = "";    //TODO: prendi username
        String mail = mAuth.getCurrentUser().getEmail();

        txtUsername.setText("Username: " + user);
        txtEmail.setText("E-mail: " + mail);

        btnCambiaUsername = findViewById(R.id.btnCambiaUsername);
        btnCambiaUsername.setOnClickListener(view -> {
            Intent intent = new Intent(ProfiloActivity.this, CambiaUsernameActivity.class);
            startActivity(intent);
        });

        //TODO: lista delle segnalazioni
    }
}