package com.example.climalert;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TerminiServizioActivity extends AppCompatActivity {

    private ImageButton btnIndietro;
    private TextView lblTermini;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_termini_servizio);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnIndietro = findViewById(R.id.btnIndietro);
        btnIndietro.setOnClickListener(view -> {
            Intent intent = new Intent(TerminiServizioActivity.this, SicurezzaActivity.class);
            startActivity(intent);
            finish();
        });

        lblTermini = findViewById(R.id.lblTermini);
        String testoHtml = getString(R.string.testoTerminiDiServizio);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            lblTermini.setText(android.text.Html.fromHtml(testoHtml, android.text.Html.FROM_HTML_MODE_LEGACY));
        } else {
            lblTermini.setText(android.text.Html.fromHtml(testoHtml));
        }
    }
}