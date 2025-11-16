package com.example.climalert.meteo;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.*;

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

interface MeteoCallback{
    void OnSuccess(String response);
    void OnFailure(String message, Exception e);
}


public class ArpavMeteo {
    private static final String BASE_URL ="https://www.arpa.veneto.it/risorse/data-bollettini/meteo/bollettini/it/xml/bollettino_utenti.xml";
    public void fetchData(MeteoCallback callback) throws IOException {
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
                callback.OnFailure("Errore di connessione",null);
                return;
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()){ // Questo significa che la risposta c'è stata (cioè si è connesso) e il response code è positivo
                    Log.e("ArpavMeteo", "Server ha risposto ma con codice negativo: "+response.code());
                    callback.OnFailure("Risposta negativa dal server",null);
                    return;
                }
                String responseBody = response.body().string();
                Log.d("ArpavMeteo", "Risposta ricevuta: " + responseBody);
                callback.OnSuccess(responseBody);
            }
        });



    }





}
