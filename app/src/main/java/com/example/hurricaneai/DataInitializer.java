package com.example.hurricaneai;

import android.content.Context;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataInitializer {
    private static final String TAG = "DataInitializer";
    private final AppDatabase database;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public interface OnDataReadyCallback {
        void onDataReady();
        void onError(String message);
    }

    public DataInitializer(Context context) {
        this.database = AppDatabase.getDatabase(context);
    }

    // Metodo unico che gestisce tutta l'inizializzazione del database in modo sequenziale
    public void initializeData(OnDataReadyCallback callback) {
        executor.execute(() -> {
            try {
                // FASE 1: Popola la lista dei siti. Questo garantisce che i siti ci siano sempre.
                populateSitesIfNeeded();

                // FASE 2: Avvia lo scraping vero e proprio
                scrapeSites(callback);

            } catch (Exception e) {
                Log.e(TAG, "Errore critico durante l'inizializzazione.", e);
                callback.onError(e.getMessage());
            }
        });
    }

    private void populateSitesIfNeeded() {
        SiteInfoDao dao = database.siteInfoDao();
        if (dao.getCount() == 0) {
            Log.d(TAG, "La tabella dei siti è vuota. Inizio popolamento.");
            dao.insertAll(
                new SiteInfo("Protezione Civile - Terremoto", "https://www.protezionecivile.gov.it/it/approfondimento/in-caso-di-terremoto/", "terremoto", "it"),
                new SiteInfo("Protezione Civile - Alluvione", "https://www.protezionecivile.gov.it/it/approfondimento/in-caso-di-alluvione/", "alluvione", "it"),
                new SiteInfo("Sass Puglia - Eruzioni Vulcaniche", "https://www.sasspuglia.it/cosa-fare-in-caso-di-eruzione-vulcanica/", "vulcano", "it"),
                new SiteInfo("Sass Puglia - Tsunami", "https://www.sasspuglia.it/cosa-fare-in-caso-di-maremoto-tsunami/", "tsunami", "it"),
                new SiteInfo("Provincia Bolzano - Valanghe", "https://protezione-civile.provincia.bz.it/it/valanghe", "valanghe", "it"),
                new SiteInfo("Protezione Civile - Ondate di Calore", "https://www.protezionecivile.gov.it/it/approfondimento/in-caso-di-ondate-di-calore/", "ondate di calore", "it"),
                new SiteInfo("Protezione Civile - Crisi Idriche", "https://www.protezionecivile.gov.it/it/approfondimento/in-caso-di-crisi-idriche/", "crisi idrica", "it")
            );
            Log.d(TAG, "Popolamento siti completato.");
        }
    }

    private void scrapeSites(OnDataReadyCallback callback) {
        database.emergencyInfoDao().deleteAll(); // Pulisce solo i dati vecchi
        List<SiteInfo> sites = database.siteInfoDao().getAll();
        Log.d(TAG, "Trovati " + sites.size() + " siti. Inizio scraping.");

        for (SiteInfo site : sites) {
            if (site.url.endsWith(".pdf")) continue;

            try {
                Document doc = Jsoup.connect(site.url).get();
                String content = extractAllContent(doc);
                if (!content.trim().isEmpty()) {
                    EmergencyInfo info = new EmergencyInfo(site.name, site.category, content, site.url, site.language);
                    database.emergencyInfoDao().insert(info);
                    Log.d(TAG, "Contenuto salvato per: " + site.name);
                }
            } catch (IOException e) {
                Log.e(TAG, "Errore scraping per " + site.url, e);
            }
        }
        Log.d(TAG, "Scraping completato.");
        callback.onDataReady();
    }

    private String extractAllContent(Document doc) {
        Element body = doc.body();
        if (body == null) return "";
        body.select("p, br, li, h1, h2, h3").after("{{NEWLINE}}");
        String text = body.text();
        return text.replace("{{NEWLINE}} ", "\n").replace("{{NEWLINE}}", "\n");
    }
}
