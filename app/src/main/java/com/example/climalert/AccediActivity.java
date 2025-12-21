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

public class AccediActivity extends AppCompatActivity {
    private static final String TAG = "AccediActivity";
    private Button btnAccedi, btnRegistrati, btnAccediOspite;

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
            String username = email_text.getText().toString().trim();
            String password = password_text.getText().toString().trim();
            emailPasswordLogin(username, password);
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
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            loginSuccesUI();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
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
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInAnonymously:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            loginSuccesUI();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                            Toast.makeText(AccediActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}