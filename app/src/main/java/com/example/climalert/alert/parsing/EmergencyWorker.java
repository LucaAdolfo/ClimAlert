package com.example.climalert.alert.parsing;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.gson.Gson;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
public class EmergencyWorker extends Worker {
    public String TAG = "EmergencyWorker";
    public EmergencyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }
    /** Vuole che gli passi due variabili: target_region e updated_time, target_region la regione che deve andare a visitare e updated time è il tempo del ultimo aggiornamento
     * @return La data di aggiornamento se è nuova, null altriemnti!
    * **/
    public static Entry ultimo_Aggiornamento=null;
    @NonNull
    @Override
    public Result doWork() {
        /*Result.failure() oppure Result.success()*/
        //Deve fare pulling
        Log.d(TAG, "Il worker ha iniziato il suo lavoro in background.");

        try {
            if (getRunAttemptCount() > 3) {
                Log.e(TAG, "Troppi tentativi falliti, interrompo.");
                return Result.failure();
            }
            String body = AllerteEmergenze.fetchData();
            if (body == null){
                Log.e(TAG, "Fetch data non riuscito , retry");
                return Result.retry();
            }
            Feed feed = AllerteEmergenze.parseFeed(body);
            if(feed==null){
                Log.e(TAG, "Parsing non riuscito");
                return Result.retry();
            }
            String last_fetched_time = getLastFetchedTime();

            if(!isAlertNew(last_fetched_time, feed.getUpdated())){//da quello che ho gia ritorna subito
                return Result.success();
            }
            String targetRegion = getLastRegione();
            Entry entry = feed.getEntry(targetRegion);
            ultimo_Aggiornamento = entry;
            String currentTime = OffsetDateTime.now().toString();
            if (entry == null){
                Log.e(TAG, "Nessuna regione trovata per EmergencyWorker dal parsing");
                return Result.success(); // Non c'è la regione che cerchiamo
            } else if (isAlertNew(last_fetched_time,entry.getUpdated()) && !isAlertNew(entry.getOnset(), currentTime)) { //Se l'aggiornamento è piu recente di quello ultimo
                Log.d(TAG, "Nuova allerta disponibile");
                String allerta = String.format(
                        "Data: %s\nTipo: %s\nUrgenza: %s\nPrevista per: %s",
                        castData(entry.getUpdated()),
                        entry.getEvent() != null ? entry.getEvent() : "Non specificato",
                        entry.getUrgency() != null ? entry.getUrgency() : "Ordinaria",
                        castData(entry.getOnset())
                );
                setEntryUpdate(entry);
                AllerteEmergenze.sendNotification(getApplicationContext(), "Allerta per "+entry.getAreaDesc(),allerta, entry.getId().hashCode());
                setTimeUpdate(entry.getUpdated());
                return Result.success(); //C'è qualcosa da aggiornare!
            }else{//Non ha trovato nulla da aggiornare!
                Log.d(TAG, "Non ci sono nuovi aggiornamenti");
                return Result.success();
            }

        } catch (IOException e) {
            Log.e(TAG, "Errore: "+ e.getMessage());
            return Result.retry();
        }
    }
    /**
     * @param last_fetched_time a
     * @param lastTime b
     * @return true se b e dopo a, false altrimenti
     *
     * */
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
    private String castData(String data){
        try {
            OffsetDateTime odt = OffsetDateTime.parse(data);
            OffsetDateTime dataLocale = odt.atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE d MMMM, HH:mm", Locale.ITALIAN);
            return dataLocale.format(formatter);
        } catch (Exception e) {
            return data; // In caso di errore ritorna l'originale
        }

    }
    private void setEntryUpdate(Entry entry) {
        Gson gson = new Gson();
        String json = gson.toJson(entry);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("EmergencyAlert", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("ultima_entry", json).apply();
    }

    private String getLastFetchedTime() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("EmergencyAlert", MODE_PRIVATE);
        return sharedPreferences.getString("last_fetched_time", null);
    }
    private String getLastRegione() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("EmergencyAlert", MODE_PRIVATE);
        return sharedPreferences.getString("regione", "Veneto");
    }
    private void setTimeUpdate(String date) {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("EmergencyAlert", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("last_fetched_time",date);
        editor.apply();

    }

}
