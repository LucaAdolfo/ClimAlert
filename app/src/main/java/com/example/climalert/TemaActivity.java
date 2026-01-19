package com.example.climalert;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TemaActivity extends AppCompatActivity{

    private ImageButton btnIndietro;
    private ImageView imgSole, imgLuna;
    private RadioButton rdbChiaro, rdbScuro;
    private RadioGroup radioGroupTema;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tema);

        btnIndietro = findViewById(R.id.btnIndietro);
        imgSole = findViewById(R.id.light_theme);
        imgLuna = findViewById(R.id.dark_theme);
        rdbChiaro = findViewById(R.id.rdbChiaro);
        rdbScuro = findViewById(R.id.rdbScuro);
        radioGroupTema = findViewById(R.id.radioGroupTema);

        //Controllo tema per impostare i radio buttom
        SharedPreferences sharedPreferences=getSharedPreferences("ImpostazioniTema", Context.MODE_PRIVATE);
        boolean isDarkMode=sharedPreferences.getBoolean("isDarkMode",false);
        if(isDarkMode){
            rdbScuro.setChecked(true);
        }else{
            rdbChiaro.setChecked(true);
        }

        //per tema notte
        imgLuna.setOnClickListener(v ->{
            rdbScuro.setChecked(true);
            cambiaTema(true);
        });

        //per tema chiaro
        imgSole.setOnClickListener(v ->{
            rdbChiaro.setChecked(true);
            cambiaTema(false);
        });

        radioGroupTema.setOnCheckedChangeListener((group, checkedId) ->{
            if (checkedId==R.id.rdbScuro){
                cambiaTema(true);
            }else if(checkedId==R.id.rdbChiaro){
                cambiaTema(false);
            }
        });

        btnIndietro.setOnClickListener(view -> {
            Intent intent=new Intent(TemaActivity.this, ImpostazioniActivity.class);
            startActivity(intent);
            finish();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main),(v, insets) ->{
            Insets systemBars=insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void cambiaTema(boolean scuro){
        SharedPreferences sharedPreferences=getSharedPreferences("ImpostazioniTema", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean("isDarkMode",scuro);
        editor.apply();

        if(scuro){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        //ricreo l'activity oer applicare tema
        recreate();
    }
}