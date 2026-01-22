package com.example.climalert;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccediAdminActivity extends AppCompatActivity {

    private Button btnAccediUtente, btnAccedi;
    private EditText email_text, password_text;
    private FirebaseAuth mAuth;
    private static final String TAG = "AccediAdminActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_accedi_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnAccediUtente = findViewById(R.id.btnAccediUtente);
        btnAccediUtente.setOnClickListener(view -> {
            Intent intent = new Intent(AccediAdminActivity.this, AccediActivity.class);
            startActivity(intent);
            finish();
        });

        email_text = findViewById(R.id.edit_email);
        password_text = findViewById(R.id.edit_password);
        mAuth = FirebaseAuth.getInstance();

        btnAccedi = findViewById(R.id.btnAccedi);
        btnAccedi.setOnClickListener(view -> {
            String email = email_text.getText().toString().trim();
            String password = password_text.getText().toString().trim();
            //adminLogin(email, password);

            //questo pezzo è da sostituire con il commento prima quando funzionerà login admin
            Intent intent = new Intent(AccediAdminActivity.this, MainAdminActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void adminLogin(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            Intent intent = new Intent(AccediAdminActivity.this, MainAdminActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(AccediAdminActivity.this, "Credenziali sbagliate",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}