package com.example.climalert;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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

public class AccediActivity extends AppCompatActivity {
    private static final String TAG = "AccediActivity";
    private Button btnAccedi, btnRegistrati, btnAccediOspite, btnAccediAdmin;

    private EditText email_text = null;
    private EditText password_text = null;
    private FirebaseAuth mAuth;
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
        email_text = findViewById(R.id.edit_email);
        password_text = findViewById(R.id.edit_password);
        mAuth = FirebaseAuth.getInstance();

        btnAccedi = findViewById(R.id.btnAccedi);
        btnAccedi.setOnClickListener(view -> {
            String email = email_text.getText().toString().trim();
            String password = password_text.getText().toString().trim();
            if(email.toLowerCase().contains("admin")){
                Log.w(TAG,"Tentativo di accesso da schermata sbaglaita con nome admin");
                Toast.makeText(AccediActivity.this, "Email non consentita", Toast.LENGTH_SHORT).show();
                startLoginCountdown(3000);
                return;
            }
            if(email.isEmpty() || password.isEmpty() || email.contains(" ") || password.contains(" ")){
                Toast.makeText(AccediActivity.this, "Compilare tutti i campi", Toast.LENGTH_SHORT).show();
                startLoginCountdown(3000);
              return;
            }else{
            emailPasswordLogin(email, password);
            }
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
            anonymousLogin();
        });

        //accedi come admin
        btnAccediAdmin = findViewById(R.id.btnAccediAdmin);
        btnAccediAdmin.setOnClickListener(view -> {
            Intent intent = new Intent(AccediActivity.this, AccediAdminActivity.class);
            startActivity(intent);
            finish();
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //Se è gia registrato si sposta nel main,
        if(currentUser != null){
            loginSuccesUI();
        }
    }
    private void loginSuccesUI(){
        Intent intent = new Intent(AccediActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    private void emailPasswordLogin(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {//riuscito
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            loginSuccesUI();
                        } else {//fallisce
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            startLoginCountdown(3000);
                            Toast.makeText(AccediActivity.this, "Credenziali sbagliate",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void anonymousLogin(){
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //riuscito
                            Log.d(TAG, "signInAnonymously:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            loginSuccesUI();
                        } else {
                            // fallisce
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                            Toast.makeText(AccediActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void startLoginCountdown(long millisInFuture) {
        btnAccedi.setEnabled(false); // Disabilita il tasto

        new CountDownTimer(millisInFuture, 1000) { // 1000 ogni quanto il metodo on tick viene chiamato

            public void onTick(long millisUntilFinished) {
                // Aggiorna il testo del bottone con i secondi rimanenti
                btnAccedi.setText("Riprova tra: " + millisUntilFinished / 1000 + "s");
            }

            public void onFinish() {
                btnAccedi.setEnabled(true); // Riabilita il tasto
                btnAccedi.setText("Accedi"); // Ripristina il testo originale
            }
        }.start();
    }

}