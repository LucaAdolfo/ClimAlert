package com.example.hurricaneai;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.FutureCallback;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import com.example.climalert.BuildConfig;

public class AIEngine {
    private final GenerativeModelFutures model;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public AIEngine(AppDatabase database) {
        String apiKey = BuildConfig.GEMINI_API_KEY;
        GenerativeModel gm = new GenerativeModel("gemini-2.5-flash", apiKey);
        this.model = GenerativeModelFutures.from(gm);
    }

    //Interfaccia per gestire la risposta
    public interface AICallback {
        void onResponse(String response);
        void onError(Throwable t);
    }

    public void getResponseAsynchronous(String userMessage, AICallback callback) {
        String prompt = "Sei HurricaneAi, un assistente esperto in sicurezza e calamità naturali. " +
                "Rispondi in modo conciso e utile alla seguente domanda: " + userMessage;

        Content content = new Content.Builder()
                .addText(prompt)
                .build();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                callback.onResponse(result.getText());
            }

            @Override
            public void onFailure(Throwable t) {
                callback.onError(t);
            }
        }, executor);
    }
}