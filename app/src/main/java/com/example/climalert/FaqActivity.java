package com.example.climalert;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class FaqActivity extends AppCompatActivity {

    private ImageButton btnIndietro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_faq);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnIndietro = findViewById(R.id.btnIndietro);
        btnIndietro.setOnClickListener(view -> {
            Intent intent = new Intent(FaqActivity.this, ImpostazioniActivity.class);
            startActivity(intent);
            finish();
        });

        //per le faq a comparsa
        setupFaq(R.id.lblDomanda1, R.id.lblRisposta1);
        setupFaq(R.id.lblDomanda2, R.id.lblRisposta2);
        setupFaq(R.id.lblDomanda3, R.id.lblRisposta3);
        setupFaq(R.id.lblDomanda4, R.id.lblRisposta4);
        setupFaq(R.id.lblDomanda5, R.id.lblRisposta5);

    }
    private void setupFaq(int domandaId, int rispostaId){
        TextView domanda=findViewById(domandaId);
        TextView risposta=findViewById(rispostaId);

        domanda.setOnClickListener(v ->{
            if (risposta.getVisibility()==View.GONE){
                risposta.setVisibility(View.VISIBLE);
            } else {
                risposta.setVisibility(View.GONE);
            }
        });
    }
}