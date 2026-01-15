package com.example.climalert.meteo.parsing;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.climalert.meteo.MeteoCallback;
import com.tickaroo.tikxml.TikXml;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/*
* Lo scopo è quello di estrarre e parsare direttamente qua tutti i file
* Estraggo dal sito dell arpav con okhttp e poi parso
* -->Da aggiugnere permesso ad internet su manifest intanto preparo tutto
*
*
*
*
*
* */


public class ArpavMeteo {
    private static final String BASE_URL ="https://www.arpa.veneto.it/risorse/data-bollettini/meteo/bollettini/it/xml/bollettino_utenti.xml";
    private final TikXml tikXml = new TikXml.Builder().exceptionOnUnreadXml(false).build();
    public void fetchData(MeteoCallback callback) {
        //Istanzio quello che fa call
        OkHttpClient client = new OkHttpClient();
        //Ora creo la request
        Request request = new Request.Builder()
                .url(BASE_URL)
                .build();
        //valore
        client.newCall(request).enqueue(new Callback() {//Metodo asincrono
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("ArpavMeteo", "Errore durante la richiesta: " + e.getMessage());
                callback.OnFailure("Errore di connessione", e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()){ // Questo significa che la risposta c'è stata (cioè si è connesso) e il response code è positivo
                    Log.e("ArpavMeteo", "Server ha risposto ma con codice negativo: "+response.code());
                    callback.OnFailure("Risposta negativa dal server", new IOException("Unexpected code " + response));
                    return;
                }
                try {
                    Previsioni previsioni = tikXml.read(response.body().source(), Previsioni.class);
                    callback.OnSuccess(previsioni);
                }catch (Exception e){
                    Log.e("ArpavMeteo", "Errore durante il parsing XML: " + e.getMessage());
                    callback.OnFailure("Errore durante il parsing XML", e);
                    return;
                }
                finally {
                    response.close();
                }





            }
        });



    }





}
