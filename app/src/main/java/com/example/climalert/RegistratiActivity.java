package com.example.climalert;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.checkerframework.checker.nullness.qual.NonNull;

public class RegistratiActivity extends AppCompatActivity {
    private static final String TAG = "RegistratiActivity";

    private ImageButton btnIndietro;
    private Button btnRegistrati;
    private TextView lblPrivacy;

    private FirebaseAuth mAuth;

    private TextView edit_email;
    private TextView edit_password;
    private TextView edit_conferma_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrati);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();


        edit_email = findViewById(R.id.edit_email);
        edit_password = findViewById(R.id.edit_password);
        edit_conferma_password = findViewById(R.id.edit_conferma_password);






        btnIndietro = findViewById(R.id.btnIndietro);
        btnIndietro.setOnClickListener(view -> {
            Intent intent = new Intent(RegistratiActivity.this, AccediActivity.class);
            startActivity(intent);
            finish();
        });

        //informativa
        lblPrivacy = findViewById(R.id.lblPrivacy);

        // 1. Definisci il testo completo e la parte cliccabile
        String fullText = "Ho letto i Termini di Servizio e acconsento al trattamento dei dati.";
        String clickableText = "i Termini di Servizio";

        SpannableString spannableString = new SpannableString(fullText);

        //inizio e fine parte cliccabile
        int startIndex = fullText.indexOf(clickableText);
        int endIndex = startIndex + clickableText.length();

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Toast.makeText(RegistratiActivity.this, "Apri i Termini di Servizio...", Toast.LENGTH_SHORT).show();
                //apri termini
            }
        };

        spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.purple_500)), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //underline
        spannableString.setSpan(new UnderlineSpan(), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        //applica
        lblPrivacy.setText(spannableString);
        lblPrivacy.setMovementMethod(LinkMovementMethod.getInstance());

        //registrazione (senza logica)
        btnRegistrati = findViewById(R.id.btnRegistrati);
        btnRegistrati.setOnClickListener(view -> {
            String email = edit_email.getText().toString().trim();
            String password = edit_password.getText().toString().trim();
            String confermaPassword = edit_conferma_password.getText().toString();
            if (!password.equals(confermaPassword)) {
                Toast.makeText(RegistratiActivity.this, "Le password non corrispondono", Toast.LENGTH_SHORT).show();
                edit_password.setText("");
                edit_conferma_password.setText("");
                return;
            }
            if (email.isEmpty() || password.isEmpty() || confermaPassword.isEmpty()) {
                Toast.makeText(RegistratiActivity.this, "Compila tutti i campi", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!checkEmail(email)) {
                Toast.makeText(RegistratiActivity.this, "Email non valida", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!checkPassword(password)) {
                Toast.makeText(RegistratiActivity.this, "Password non valida almeno 6 caratteri", Toast.LENGTH_SHORT).show();
                return;
            }
            registratiEmailPassword(email,password);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            registrationSuccessUI();
            Log.w(TAG, "Utente già loggato va su registrati, potrebbe esseerci problema log out");
        }

    }

    private void registratiEmailPassword(String email,String password){
        //Fa anche log in se registrazione ha successo
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            registrationSuccessUI();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegistratiActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void registrationSuccessUI(){
        Intent intent = new Intent(RegistratiActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    private boolean checkEmail(String email){
        return !email.isEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    private boolean checkPassword(String password){
        return !password.isEmpty() && password.length() >= 6;
    }



}