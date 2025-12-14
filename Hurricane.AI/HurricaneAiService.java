package com.hurricane404.climalert.service;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import jakarta.annotation.PostConstruct;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class HurricaneAiService {

    private final ChatLanguageModel chatLanguageModel;
    private Assistant assistant;

    // SITOGRAFIA UFFICIALE ( dal PDF)
    // L'applicazione leggerà questi siti in tempo reale all'avvio
    private final List<String> urls = List.of(
        // Fonti Italiane (Protezione Civile, INGV)
        "https://www.protezionecivile.gov.it/it/emergenza-terremoti",
        "https://www.ingv.it/it/scienza-terremoti.html",
        "https://www.protezionecivile.gov.it/it/emergenza-alluvioni",
        "https://www.protezionecivile.gov.it/it/emergenza-rischio-vulcanico",
        "https://www.salute.gov.it/portale/emergenze/dettaglioContenutiEmergenze.jsp?lingua=italiano&id=5144&area=emergenze&menu=emergenze", // Caldo/Freddo estremo

        // Fonti Internazionali (Inglese - L'AI le tradurrà)
        "https://www.efas.eu/en",          // Alluvioni Europa
        "https://www.meteoalarm.org/en/live/" // Allerte meteo Europa
    );

    // Interfaccia interna dell'assistente
    interface Assistant {
        String chat(String userMessage);
    }

    public HurricaneAiService(ChatLanguageModel chatLanguageModel) {
        this.chatLanguageModel = chatLanguageModel;
    }

    @PostConstruct
    public void init() {
        System.out.println("--- HURRICANE.AI: Avvio procedura di Web Scraping ---");
        
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        List<Document> documents = new ArrayList<>();

        // 1. SCARICAMENTO AUTOMATICO DAI SITI
        for (String url : urls) {
            try {
                System.out.println("Leggendo fonte ufficiale: " + url);
                
                // Connessione al sito simulando un browser (per non essere bloccati)
                org.jsoup.nodes.Document webPage = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                        .timeout(10000) // Aspetta max 10 secondi per sito
                        .get();
                
                // Estrae solo il testo utile, ignorando menu e pubblicità
                String bodyText = webPage.body().text();
                
                // Aggiunge metadati per far capire all'AI da dove arriva l'info
                String contentWithSource = "FONTE: " + url + "\n\nCONTENUTO:\n" + bodyText;
                documents.add(Document.from(contentWithSource));
                
            } catch (IOException e) {
                System.err.println("Errore lettura sito " + url + ": " + e.getMessage());
                // Continua con gli altri siti senza bloccare l'app
            }
        }

        if (documents.isEmpty()) {
            System.err.println("ATTENZIONE: Nessun sito è stato scaricato correttamente.");
        } else {
            System.out.println("--- DATI ACQUISITI: " + documents.size() + " fonti ufficiali caricate in memoria. ---");
        }

        // 2. ELABORAZIONE DEI DATI (Embedding)
        // Divide i testi lunghi in pezzi più piccoli per l'AI
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(DocumentSplitters.recursive(1000, 200))
                .embeddingStore(embeddingStore)
                .embeddingModel(new AllMiniLmL6V2EmbeddingModel())
                .build();

        ingestor.ingest(documents);

        // 3. CREAZIONE INTELLIGENZA ARTIFICIALE
        this.assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(chatLanguageModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .contentRetriever(embeddingStore.asRetriever())
                .systemMessageProvider(chatId -> """
                    Sei Hurricane.Ai, l'assistente ufficiale dell'app ClimAlert.
                    
                    REGOLE FONDAMENTALI:
                    1. Rispondi SOLO basandoti sulle informazioni estratte dai siti web forniti.
                    2. TRADUZIONE OBBLIGATORIA: Se l'informazione viene da una fonte Inglese (es. EFAS, Meteoalarm), devi tradurre la risposta in ITALIANO.
                    3. Se l'utente chiede indicazioni di sicurezza (es. terremoto), fornisci una lista puntata chiara.
                    4. Se non trovi la risposta nei dati scaricati, rispondi: "Mi dispiace, ma non trovo questa informazione nelle fonti ufficiali monitorate (Protezione Civile, INGV, ecc)."
                    """)
                .build();
    }

    public String getResponse(String userQuestion) {
        return assistant.chat(userQuestion);
    }

    public String getWelcomeMessage() {
        return "Ciao! Sono Hurricane.Ai 🌪️.\n" +
               "Ho appena analizzato i dati aggiornati dalla Protezione Civile, INGV e Meteoalarm.\n" +
               "Posso rispondere alle tue domande su come gestire le emergenze climatiche. Chiedimi pure!";
    }
}