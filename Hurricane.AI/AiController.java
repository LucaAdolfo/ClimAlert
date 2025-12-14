package com.hurricane404.climalert.controller;

import com.hurricane404.climalert.service.HurricaneAiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final HurricaneAiService aiService;

    public AiController(HurricaneAiService aiService) {
        this.aiService = aiService;
    }

    // Endpoint 1: Restituisce il messaggio di benvenuto
    @GetMapping("/welcome")
    public ResponseEntity<Map<String, String>> getWelcome() {
        return ResponseEntity.ok(Map.of(
            "message", aiService.getWelcomeMessage(),
            "sender", "Hurricane.Ai"
        ));
    }

    // Endpoint 2: Riceve la domanda dell'utente e restituisce la risposta AI
    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> chat(@RequestBody Map<String, String> payload) {
        String userMessage = payload.get("message");
        
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Messaggio vuoto"));
        }

        String aiResponse = aiService.getResponse(userMessage);

        return ResponseEntity.ok(Map.of(
            "response", aiResponse,
            "sender", "Hurricane.Ai"
        ));
    }
}

//Descrizione: Gestisce le chiamate dall'App (Messaggio di Benvenuto e Chat).