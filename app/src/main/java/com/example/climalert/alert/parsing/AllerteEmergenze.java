package com.example.climalert.alert.parsing;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.climalert.R;
import com.tickaroo.tikxml.TikXml;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;

public class AllerteEmergenze {

    private static final String TAG = "AllerteEmergenze";
    private static final String CHANNEL_ID = "emergenze_channel";
    private static final String CHANNEL_NAME = "Allerte Emergenze";
    private static final String CHANNEL_DESCRIPTION= "Avvisi per emergenze in tempo reale";

    public static void sendNotification(Context context, String textTitle, String textContent, int notificationId){//TODO non so se bisogna chiedere autorizzazione, cioè nel manifest ci sono le permessi
        //https://developer.android.com/develop/ui/views/notifications/notification-permission?hl=it
        createNotificationChannel(context);
        Intent intent = new Intent(context, com.example.climalert.MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,  0, intent, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.notizie)// TODO IMPOSTA
                .setContentTitle(textTitle)
                .setContentText(textContent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
                ;
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "Nessuna autorizzazione per le notifiche");
            return;
        }
        notificationManager.notify(notificationId, builder.build());

    }
    private static void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            channel.setDescription(CHANNEL_DESCRIPTION);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this.
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**Converte la città in NUTS3 per il parsing valido solo per il veneto!
     * @param provincia nome della provincia o veneto in caso si voglia tutta la regione
     * @return codice NUTS3
     * @implNote non è usato ma se se volessi prendere api piu precise da meteoalarm le avrei
     */
    public static String converterInNUTS3(String provincia){
        //la corrispondeza è la seguente  Veneto	ITH3
        // Verona	ITH31
        // Vicenza	ITH32
        // Belluno	ITH33
        // Treviso	ITH34
        // Venezia	ITH35
        //Padova	ITH36
        // Rovigo	ITH37
        String provinciaLower = provincia.toLowerCase();
        if (provinciaLower.contains("veneto")){
            return "ITH3";
        }else if (provinciaLower.contains("verona")){
            return "ITH31";
            }else if (provinciaLower.contains("vicenza")){
            return "ITH32";
        }else if (provinciaLower.contains("belluno")){
            return "ITH33";
        }else if (provinciaLower.contains("treviso")){
            return "ITH34";
        }else if (provinciaLower.contains("venezia")) {
            return "ITH35";
        }else if (provinciaLower.contains("padova")) {
            return "ITH36";
        } else if (provinciaLower.contains("rovigo")) {
            return "ITH37";
        }else{
            Log.w(TAG, "Nessuna corrispondenza trovata per '" + provincia + "', uso il fallback.");
            return "ITH3";
        }
    }
    /**
     * Chiede al sito https://feeds.meteoalarm.org/feeds/meteoalarm-legacy-atom-italy tutte info delle regioni, va fatto sincrono perchè userò
     * workmanager e deve essere sincono il metodo , il link è preso atom perchè è più recente
     * @throws IOException se lancia un'eccezione di tipo IOException
     * @return null in caso che non è andato a buon fine, il body altrimenti
     */
    public static String fetchData() throws IOException{
        OkHttpClient client = new OkHttpClient();
        String url = "https://feeds.meteoalarm.org/feeds/meteoalarm-legacy-atom-italy";
        Request request = new Request.Builder()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if(!response.isSuccessful())
                return null;
            return response.body().string();
        } catch (IOException e) {
            Log.e(TAG, "Errore durante la richiesta: " + e.getMessage());
            return null;
        }
    }
    public static Feed parseFeed(String body) throws IOException {
        TikXml tikXml = new TikXml.Builder().exceptionOnUnreadXml(false).build();
        Feed feed = tikXml.read(new Buffer().writeUtf8(body), Feed.class);
        return feed;
    }

}
