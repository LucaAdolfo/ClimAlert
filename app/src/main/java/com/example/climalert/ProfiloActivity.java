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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfiloActivity extends AppCompatActivity {

    private ImageButton btnIndietro;
    private TextView txtUsername;
    private TextView txtEmail;
    private FirebaseAuth mAuth;
    private Button btnCambiaUsername;
    private FirebaseFirestore database;


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
        }







        //TODO: lista delle segnalazioni
    }
}