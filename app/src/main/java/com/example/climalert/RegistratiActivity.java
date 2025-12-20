package com.example.climalert;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
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

public class RegistratiActivity extends AppCompatActivity {

    private ImageButton btnIndietro;
    private Button btnRegistrati;
    private TextView lblPrivacy;

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
            Intent intent = new Intent(RegistratiActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}