package com.example.hurricaneai;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import com.example.climalert.R;


public class WebScraper {
    private static final String TAG = "HurricaneAI_Scraper";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";
    private static final int CONNECTION_TIMEOUT_MS = 30000;
    private static final int SCRAPE_PAUSE_MS = 3000;
    private static final int MAX_CONTENT_LENGTH = 15000;
    private static final int MIN_TEXT_LENGTH = 10;
    private static final String CONTENT_TRUNCATED_MSG = "\n\n[CONTENUTO TRONCATO PER LUNGHEZZA]";
    private static final String TITLE_PREFIX = "TITOLO: ";

    private static final ExecutorService scraperExecutor = Executors.newSingleThreadExecutor();

    // Selettori e Pattern
    private static final String[] CONTENT_SELECTORS = {"main", "article", "#content", ".content"};
    private static final String ELEMENTS_TO_REMOVE = "script, style, nav, footer, header";
    private static final String TEXT_ELEMENT_SELECTORS = "p, h1, h2, h3, li, td";
    private static final Pattern PATTERN_LONG_NUMBER = Pattern.compile(".*\\d{10,}.*");
    private static final Pattern PATTERN_ONLY_SYMBOLS = Pattern.compile("^\\s*[\\d\\W]+\\s*$");

    private final AppDatabase database;

    public WebScraper(AppDatabase database) {
        this.database = database;
    }

    public void scrapeAllSites() {
        scraperExecutor.submit(this::performScraping);
    }

    public void scrapeAllSitesAndWait() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        scraperExecutor.submit(() -> {
            performScraping();
            latch.countDown();
        });
        latch.await();
    }

    private void performScraping() {
        List<SiteInfo> allSites = database.siteInfoDao().getAll();
        Log.d(TAG, "Inizio scraping di " + allSites.size() + " siti dal database...");
        int successCount = 0;
        for (SiteInfo site : allSites) {
            if (Thread.currentThread().isInterrupted()) break;
            if (scrapeSingleSite(site)) successCount++;
            try {
                Thread.sleep(SCRAPE_PAUSE_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        Log.d(TAG, "Scraping completato! " + successCount + "/" + allSites.size() + " siti scaricati");
    }

    @SuppressLint("Security")
    @SuppressWarnings("BroadCatchBlock")
    private boolean scrapeSingleSite(SiteInfo site) {
        try {
            Log.d(TAG, "Scraping: " + site.name);
            String content = scrapeContentFromUrl(site.url);
            if (TextUtils.isEmpty(content)) {
                Log.w(TAG, "Nessun contenuto per: " + site.name);
                return false;
            }

            EmergencyInfo info = new EmergencyInfo(site.name, site.category, content, site.url, site.language);
            database.emergencyInfoDao().insert(info);
            Log.d(TAG, "Salvato: " + site.name);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Errore generico per " + site.name, e);
            return false;
        }
    }

    public String scrapeContentFromUrl(String url) throws IOException {
        Document doc = Jsoup.connect(url)
                .ignoreHttpErrors(true)
                .timeout(CONNECTION_TIMEOUT_MS)
                .userAgent(USER_AGENT)
                .get();
        return extractMainContent(doc);
    }

    private String extractMainContent(Document doc) {
        doc.select(ELEMENTS_TO_REMOVE).remove();
        Element contentElement = null;
        for (String selector : CONTENT_SELECTORS) {
            contentElement = doc.selectFirst(selector);
            if (contentElement != null) break;
        }
        if (contentElement == null) contentElement = doc.body();

        Elements textElements = contentElement.select(TEXT_ELEMENT_SELECTORS);
        StringBuilder content = new StringBuilder();
        String pageTitle = doc.title();
        if (!TextUtils.isEmpty(pageTitle)) {
            content.append(TITLE_PREFIX).append(pageTitle).append("\n\n");
        }

        for (Element element : textElements) {
            String text = element.text().trim();
            if (text.length() > MIN_TEXT_LENGTH && !PATTERN_LONG_NUMBER.matcher(text).matches() && !PATTERN_ONLY_SYMBOLS.matcher(text).matches()) {
                if (content.length() == 0 || !content.substring(Math.max(0, content.length() - 50)).contains(text.substring(0, Math.min(30, text.length())))) {
                    content.append(text).append("\n\n");
                }
            }
        }
        String result = content.toString().trim();
        if (result.length() > MAX_CONTENT_LENGTH) {
            result = result.substring(0, MAX_CONTENT_LENGTH) + CONTENT_TRUNCATED_MSG;
        }
        return result;
    }

    // Metodo per arrestare il thread dello scraper
    public void shutdown() {
        Log.d(TAG, "Arresto dello scraper executor.");
        scraperExecutor.shutdownNow();
    }
}
