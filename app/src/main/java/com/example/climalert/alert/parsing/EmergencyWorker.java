package com.example.climalert.alert.parsing;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

public class EmergencyWorker extends Worker {
    public String TAG = "EmergencyWorker";
    public EmergencyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }
    /** Vuole che gli passi due variabili: target_region e updated_time, target_region la regione che deve andare a visitare e updated time è il tempo del ultimo aggiornamento
     * @return La data di aggiornamento se è nuova, null altriemnti!
    * **/
    @NonNull
    @Override
    public Result doWork() {
        /*Result.failure() oppure Result.success()*/
        //Deve fare pulling
        Log.d(TAG, "Il worker ha iniziato il suo lavoro in background.");

        try {
            String body = AllerteEmergenze.fetchData();
            if (body == null){
                return Result.retry();
            }
            Feed feed = AllerteEmergenze.parseFeed(body);
            if(feed==null){
                return Result.retry();
            }
            String last_fetched_time = getInputData().getString("updated_time");

            if(!isAlertNew(last_fetched_time, feed.getUpdated())){//da quello che ho gia ritorna subito
                return Result.success();
            }
            String targetRegion = getInputData().getString("target_region");
            Entry entry = feed.getEntry(targetRegion);
            if (entry == null){
                Log.e("EmergencyWorker", "Nessuna regione trovata per EmergencyWorker dal parsing");
                return Result.success(); // Non c'è la regione che cerchiamo
            } else if (!isAlertNew(last_fetched_time,entry.getUpdated())) { //Se l'aggiornamento è piu recente di quello ultimo
                AllerteEmergenze.sendNotification(getApplicationContext(), "Allerta per"+entry.getAreaDesc(),entry.getEvent(), entry.getId().hashCode());
                Data dataoutput= new Data.Builder().putString("new_fetched_time", entry.getUpdated()).build();
                return Result.success(dataoutput); //C'è qualcosa da aggiornare!

            }else{//Non ha trovato nulla da aggiornare!
                return Result.success();
            }

        } catch (IOException e) {
            return Result.retry();
        }
    }
    private boolean isAlertNew(String last_fetched_time, String lastTime){
        try{
            if(last_fetched_time==null || lastTime==null){ // Se l'updateTIme è null c'è errore casting, quindi rifa tutto, se lastTime è null allora è il primo aggiornamento
                return true;
            }
            OffsetDateTime ultima_data_salvata = OffsetDateTime.parse(last_fetched_time);
            OffsetDateTime data_adesso = OffsetDateTime.parse(lastTime);
            return data_adesso.isAfter(ultima_data_salvata);
        }catch (DateTimeParseException e) {
            Log.e("DATE_PARSING", "Errore nel parsing della data: " + last_fetched_time, e);
            return !last_fetched_time.equals(lastTime); // Se fallisce lascio che confronto le stringhe
        }
    }

}
