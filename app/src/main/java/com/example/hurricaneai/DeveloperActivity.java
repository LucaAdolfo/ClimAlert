package com.example.hurricaneai;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.example.climalert.R;


public class DeveloperActivity extends AppCompatActivity {

    private static final String TAG = "DeveloperActivity";
    private AppDatabase database;
    private WebScraper scraper;
    private TextView statusTextView;

    private final ExecutorService backgroundExecutor = Executors.newSingleThreadExecutor();
    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer);

        database = AppDatabase.getDatabase(this);
        scraper = new WebScraper(database);

        // UI
        MaterialToolbar toolbar = findViewById(R.id.toolbar_developer);
        statusTextView = findViewById(R.id.statusText_developer);
        Button testBtn = findViewById(R.id.testButton_developer);
        Button scrapeBtn = findViewById(R.id.scrapeButton_developer);
        Button testSingleBtn = findViewById(R.id.testSingleButton_developer);

        // Toolbar
        toolbar.setNavigationOnClickListener(v -> finish());

        // Listeners
        testBtn.setOnClickListener(v -> testDatabase());
        scrapeBtn.setOnClickListener(v -> startScraping());
        testSingleBtn.setOnClickListener(v -> testSingleSite());
    }

    private void testDatabase() {
        statusTextView.setText(R.string.database_test_running);
        backgroundExecutor.execute(() -> {
            try {
                database.emergencyInfoDao().deleteAll();
                EmergencyInfo testData = new EmergencyInfo("Test Source", "test", "Test content", "http://example.com", "it");
                database.emergencyInfoDao().insert(testData);
                int count = database.emergencyInfoDao().getCount();
                mainThreadHandler.post(() -> statusTextView.setText(getString(R.string.database_test_success, count)));
            } catch (Exception e) {
                mainThreadHandler.post(() -> statusTextView.setText(R.string.database_test_error));
            }
        });
    }

    private void startScraping() {
        statusTextView.setText(R.string.scraping_running);
        backgroundExecutor.execute(() -> {
            try {
                int before = database.emergencyInfoDao().getCount();
                scraper.scrapeAllSitesAndWait();
                int after = database.emergencyInfoDao().getCount();
                int added = after - before;
                mainThreadHandler.post(() -> statusTextView.setText("Scraping completato! Aggiunti: " + added));
            } catch (Exception e) {
                Log.e(TAG, "Scraping error", e);
                mainThreadHandler.post(() -> statusTextView.setText(R.string.scraping_error));
            }
        });
    }

    private void testSingleSite() {
        statusTextView.setText(R.string.single_test_running);
        backgroundExecutor.execute(() -> {
            try {
                final String testUrl = "https://www.protezionecivile.gov.it/it/approfondimento/in-caso-di-terremoto/";
                final String testContent = scraper.scrapeContentFromUrl(testUrl);
                final int length = (testContent != null) ? testContent.length() : 0;

                final String preview = (testContent != null)
                        ? (testContent.length() > 200 ? testContent.substring(0, 200) + "..." : testContent)
                        : "NULL";

                mainThreadHandler.post(() -> statusTextView.setText(getString(R.string.single_test_result, testUrl, length, preview)));

            } catch (Exception e) {
                mainThreadHandler.post(() -> statusTextView.setText(getString(R.string.single_test_error, e.getMessage())));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (scraper != null) scraper.shutdown();
        backgroundExecutor.shutdown();
    }
}
