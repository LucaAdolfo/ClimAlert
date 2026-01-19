package com.example.climalert;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ModificaPasswordActivity extends AppCompatActivity {

    private ImageButton btnIndietro;
    private Button btnSalva;
    private EditText edit_password, edit_conferma_password, edit_old_password;

    private FirebaseAuth mAuth;




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


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser users = mAuth.getCurrentUser();
        btnIndietro = findViewById(R.id.btnIndietro);
        btnIndietro.setOnClickListener(view -> {
           goBack();
        });

        edit_password = findViewById(R.id.edit_new_password);
        edit_conferma_password = findViewById(R.id.edit_conferma_password);
        edit_old_password = findViewById(R.id.edit_old_password);


        btnSalva = findViewById(R.id.btnSalva);
        btnSalva.setOnClickListener(view -> {

            String password = edit_password.getText().toString().trim();
            String confermaPassword = edit_conferma_password.getText().toString().trim();
            String oldPassword = edit_old_password.getText().toString().trim();
            cambiaPassword(oldPassword,password,confermaPassword);








        });
    }

    private void cambiaPassword(String oldPassword,String password,String confermaPassword) {
        FirebaseUser users = mAuth.getCurrentUser();

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
        if(oldPassword.equals(password)){
            Toast.makeText(ModificaPasswordActivity.this, "La nuova password non può essere uguale alla vecchia", Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            if(users!=null && !users.isAnonymous()) {
                AuthCredential credential = EmailAuthProvider.getCredential(users.getEmail(), oldPassword);
                users.reauthenticate(credential).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        users.updatePassword(password)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Toast.makeText(ModificaPasswordActivity.this, "Password modificata con successo", Toast.LENGTH_SHORT).show();
                                        goBack();
                                    }else{
                                        Log.e("FIREBASE", "Errore modifica password", task1.getException());

                                    }
                                });

                    } else {
                        Toast.makeText(ModificaPasswordActivity.this, "Vecchia password errata", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }



    }

    private void goBack(){
        Intent intent = new Intent(ModificaPasswordActivity.this, SicurezzaActivity.class);
        startActivity(intent);
        finish();

    }
}