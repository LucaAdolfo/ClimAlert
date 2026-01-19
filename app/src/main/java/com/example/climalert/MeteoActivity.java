package com.example.climalert;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.climalert.meteo.MeteoCallback;
import com.example.climalert.meteo.parsing.ArpavMeteo;
import com.example.climalert.meteo.parsing.Previsione;
import com.example.climalert.meteo.parsing.Previsioni;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import java.util.List;
import java.util.Locale;

public class MeteoActivity extends AppCompatActivity {

    private ImageButton btnIndietro;
    private TextView txtPosizione, txtGradi;
    private ImageView imgMeteo;
    private com.google.android.gms.location.FusedLocationProviderClient fusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_meteo);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fusedLocationClient = com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(this);
        recuperaPosizioneGPS();

        btnIndietro = findViewById(R.id.btnIndietro);

        btnIndietro.setOnClickListener(view -> {
            Intent intent = new Intent(MeteoActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        txtPosizione = findViewById(R.id.txtPosizione);
        txtGradi = findViewById(R.id.txtGradi);
        imgMeteo = findViewById(R.id.imgMeteo);
    }

    private String getDataPrevisioneCustom(int giorniDaAggiungere) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, giorniDaAggiungere);

        SimpleDateFormat sdfGiorno = new SimpleDateFormat("EEE", Locale.ITALY);    SimpleDateFormat sdfMese = new SimpleDateFormat("MMMM", Locale.ITALY);

        String giornoSett = sdfGiorno.format(cal.getTime()).toLowerCase().replace(".", "");
        int giornoMese = cal.get(Calendar.DAY_OF_MONTH);
        String mese = sdfMese.format(cal.getTime()).toLowerCase();

        //trovo periodo
        String periodo;
        if (giorniDaAggiungere == 0) {
            int ora = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            periodo = (ora < 12) ? "mattina" : "pomeriggio";
        } else {
            //forzo "pomeriggio" perché è il dato standard nell'XML ARPAV
            periodo = "pomeriggio";
        }

        return giornoSett + " " + giornoMese + " " + mese + " " + periodo;
    }

    //nome giorno
    private String getNomeGiornoSettimana(int giorniDaAggiungere) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, giorniDaAggiungere);
        return new SimpleDateFormat("EEEE", Locale.ITALIAN).format(cal.getTime());
    }

    private void aggiungiRigaPrevisione(String giorno, String urlIcona, String temp) {
        LinearLayout container = findViewById(R.id.containerPrevisioni);

        //riga
        LinearLayout riga = new LinearLayout(this);
        riga.setOrientation(LinearLayout.HORIZONTAL);
        riga.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        riga.setPadding(0, 15, 0, 15);
        riga.setGravity(android.view.Gravity.CENTER_VERTICAL);

        //nome giorno
        TextView tvGiorno = new TextView(this);
        tvGiorno.setText(giorno);
        tvGiorno.setTextSize(18);
        tvGiorno.setTypeface(null, android.graphics.Typeface.BOLD);
        tvGiorno.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        //previsioni
        ImageView ivIcona = new ImageView(this);
        ivIcona.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
        Glide.with(this).load(urlIcona).into(ivIcona);

        //temperature
        TextView tvTemp = new TextView(this);
        tvTemp.setText(temp);
        tvTemp.setTextSize(18);
        tvTemp.setPadding(40, 0, 0, 0);
        tvTemp.setTypeface(null, android.graphics.Typeface.BOLD);

        riga.addView(tvGiorno);
        riga.addView(ivIcona);
        riga.addView(tvTemp);
        container.addView(riga);
    }

    private void caricaDatiMeteoReali(String nomeCitta) {
        ArpavMeteo meteo = new ArpavMeteo();
        try {
            meteo.fetchData(new MeteoCallback() {
                @Override
                public void OnSuccess(Previsioni previsioni) {
                    runOnUiThread(() -> {
                        try {
                            LinearLayout container = findViewById(R.id.containerPrevisioni);
                            if (container != null) container.removeAllViews();

                            for (int i = 0; i < 4; i++) {
                                String dataTarget = getDataPrevisioneCustom(i);
                                var meteogramma = previsioni.getMeteogrammi(nomeCitta, dataTarget);

                                //prova la variante "Città e zone limitrofe"
                                if (meteogramma == null) {
                                    meteogramma = previsioni.getMeteogrammi(nomeCitta + " e zone limitrofe", dataTarget);
                                }

                                //altri casi speciali
                                if (meteogramma == null) {
                                    if (nomeCitta.equalsIgnoreCase("Venezia")) {
                                        meteogramma = previsioni.getMeteogrammi("Venezia e laguna", dataTarget);
                                    } else if (nomeCitta.equalsIgnoreCase("Treviso")) {
                                        meteogramma = previsioni.getMeteogrammi("Treviso e zone limitrofe", dataTarget);
                                    }
                                }

                                //metto venezia se tutto fallisce
                                if (meteogramma == null) {
                                    meteogramma = previsioni.getMeteogrammi("Venezia e laguna", dataTarget);
                                }

                                if (meteogramma != null) {
                                    android.util.Log.d("METEO_OK", "Trovato: " + dataTarget);

                                    String gradi = "--°C";
                                    for (Previsione p : meteogramma.getPrevisioni()) {
                                        if ("Temperatura".equalsIgnoreCase(p.getTitle())) {
                                            gradi = p.getValue().replace("max ", "").replace("min ", "").replace("C", "°C");
                                            break;
                                        }
                                    }

                                    String urlIcona = "";
                                    if (meteogramma.getPrevisioni("image") != null) {
                                        urlIcona = meteogramma.getPrevisioni("image").getValue();
                                    }

                                    String etichettaGiorno = (i == 0) ? "Oggi" : getNomeGiornoSettimana(i);
                                    aggiungiRigaPrevisione(etichettaGiorno, urlIcona, gradi);

                                    if (i == 0) {
                                        txtGradi.setText(gradi);
                                        Glide.with(MeteoActivity.this).load(urlIcona).into(imgMeteo);
                                    }
                                } else {
                                    android.util.Log.e("METEO_ERROR", "Non trovato nell'XML: " + dataTarget);
                                }
                            }
                        } catch (Exception e) {
                            android.util.Log.e("ARPAV", "Errore: " + e.getMessage());
                        }
                    });
                }

                @Override
                public void OnFailure(String message, Exception e) {
                    android.util.Log.e("ARPAV", "Errore: " + message);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //legge gps
    private void recuperaPosizioneGPS() {
        //permessi
        if (androidx.core.app.ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            androidx.core.app.ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                ottieniNomeCitta(location.getLatitude(), location.getLongitude());
            } else {
                txtPosizione.setText("GPS non disponibile");
            }
        });
    }

    //per trovare città
    private void ottieniNomeCitta(double lat, double lon) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            // trova l'indirizzo per le coordinate fornite
            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);

            if (addresses != null && !addresses.isEmpty()) {
                //trova città
                String citta = addresses.get(0).getLocality();

                // se è null, prendi nome dell'area urbana
                if (citta == null) {
                    citta = addresses.get(0).getSubAdminArea();
                }

                if (citta != null) {
                    txtPosizione.setText(citta.trim());
                    caricaDatiMeteoReali(citta.trim());
                } else {
                    txtPosizione.setText("Posizione sconosciuta");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            txtPosizione.setText("Errore localizzazione");
        }
    }
}