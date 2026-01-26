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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class CambiaUsernameActivity extends AppCompatActivity {

    private ImageButton btnIndietro;
    private Button btnSalva;

    private FirebaseFirestore database;
    private FirebaseUser user;

    private TextView edit_username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cambia_username);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        database = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();


        btnIndietro = findViewById(R.id.btnIndietro);

        btnIndietro.setOnClickListener(view -> {
            Intent intent = new Intent(CambiaUsernameActivity.this, ProfiloActivity.class);
            startActivity(intent);
            finish();
        });

        btnSalva = findViewById(R.id.btnSalva);
        edit_username= findViewById(R.id.edit_username);

        btnSalva.setOnClickListener(view -> {
            String username_new = edit_username.getText().toString().trim();
            if(username_new.isEmpty()){
                Toast.makeText(CambiaUsernameActivity.this, "Compila tutti i campi", Toast.LENGTH_SHORT).show();
                return;
            }

            if(user!=null){
                database.collection("users").document(user.getUid())
                        .update(
                                "username", username_new,
                                "username_modificato_il", com.google.firebase.Timestamp.now()
                        ).addOnSuccessListener(aVoid -> {
                            Toast.makeText(CambiaUsernameActivity.this, "Username modificato con successo", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(CambiaUsernameActivity.this, ProfiloActivity.class);
                            startActivity(intent);
                            finish();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(CambiaUsernameActivity.this, "Errore nella modifica del username", Toast.LENGTH_SHORT).show();
                            edit_username.setText("");
                        });
            }
        });
    }
}