package com.example.hurricaneai;

import android.util.Log;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.example.climalert.R;

public class AIEngine {
    private static final String TAG = "HurricaneAI_Engine";
    private final AppDatabase database;

    private static final Set<String> KNOWN_CATEGORIES = new HashSet<>(Arrays.asList("terremoto", "alluvione", "vulcano", "tsunami", "valanghe", "ondate di calore", "crisi idrica"));
    private static final Set<String> KEYWORDS_PRIMA = new HashSet<>(Arrays.asList("prima", "prepararsi", "prevenzione"));
    private static final Set<String> KEYWORDS_DURANTE = new HashSet<>(Arrays.asList("durante", "mentre"));
    private static final Set<String> KEYWORDS_DOPO = new HashSet<>(Arrays.asList("dopo", "successivamente"));

    private static final String RESPONSE_HEADER = "Hurricane.AI - Ecco le informazioni richieste:\n\n";
    private static final String RESPONSE_NO_INFO_FOUND = "Mi dispiace, non ho trovato informazioni specifiche per la tua domanda.";
    private static final String RESPONSE_FOOTER_WARNING = "\n\nImportante: Queste sono informazioni generali. In caso di emergenza reale, segui sempre le indicazioni delle autorità locali.";
    private static final String SOURCE_PREFIX = "Fonte: ";
    private static final String BULLET_POINT = "• ";
    private static final String SUGGESTION_PREFIX = "SUGGERIMENTO:";

    public AIEngine(AppDatabase database) {
        this.database = database;
    }

    public String getResponse(String userMessage) {
        String lowerMessage = userMessage.toLowerCase();
        String category = analyzeCategory(lowerMessage);

        // Se non troviamo una categoria, cerchiamo un suggerimento
        if ("generale".equals(category)) {
            String suggestion = findSuggestion(lowerMessage);
            if (suggestion != null) {
                return suggestion;
            }
        }

        String context = analyzeContext(lowerMessage);
        List<EmergencyInfo> relevantInfo = searchInDatabase(category);
        return buildSmartResponse(relevantInfo, context, category);
    }

    private String findSuggestion(String text) {
        JaroWinklerSimilarity similarity = new JaroWinklerSimilarity();
        for (String word : text.split("\\s+")) {
            for (String knownCategory : KNOWN_CATEGORIES) {
                if (similarity.apply(word, knownCategory) > 0.9) {
                    return SUGGESTION_PREFIX + knownCategory;
                }
            }
        }
        return null;
    }

    private String analyzeCategory(String question) {
        if (question.contains("terremot")) return "terremoto";
        if (question.contains("alluvion")) return "alluvione";
        if (question.contains("vulcan")) return "vulcano";
        if (question.contains("tsunami")) return "tsunami";
        if (question.contains("valang")) return "valanghe";
        if (question.contains("calore")) return "ondate di calore";
        if (question.contains("crisi idrica")) return "crisi idrica";
        return "generale";
    }
    
    // ... (il resto della classe rimane invariato)

    private String analyzeContext(String question) {
        for (String keyword : KEYWORDS_PRIMA) if (question.contains(keyword)) return "Prima";
        for (String keyword : KEYWORDS_DURANTE) if (question.contains(keyword)) return "Durante";
        for (String keyword : KEYWORDS_DOPO) if (question.contains(keyword)) return "Dopo";
        return "Generale";
    }

    private List<EmergencyInfo> searchInDatabase(String category) {
        if ("generale".equals(category)) return new ArrayList<>();
        return database.emergencyInfoDao().getByCategory(category);
    }

    private String buildSmartResponse(List<EmergencyInfo> infoList, String context, String category) {
        if (infoList.isEmpty()) return RESPONSE_NO_INFO_FOUND;

        String content = infoList.get(0).content;
        Pattern firstSectionPattern = Pattern.compile("(Prima|Durante|Dopo) (il |l'|lo |la )?" + category, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        Matcher firstMatcher = firstSectionPattern.matcher(content);
        if (firstMatcher.find()) {
            content = content.substring(firstMatcher.start());
        }

        String relevantText = content;
        if (!"Generale".equals(context)) {
            Pattern sectionPattern = Pattern.compile("(^|\\n)" + context + " (il |l'|lo |la )?" + category, Pattern.CASE_INSENSITIVE);
            Matcher matcher = sectionPattern.matcher(content);
            if (matcher.find()) {
                int sectionStart = matcher.start();
                Pattern nextSectionPattern = Pattern.compile("(^|\\n)(Prima|Durante|Dopo) (il |l'|lo |la )?" + category, Pattern.CASE_INSENSITIVE);
                Matcher nextMatcher = nextSectionPattern.matcher(content);
                int sectionEnd = content.length();
                if (nextMatcher.find(sectionStart + 1)) {
                    sectionEnd = nextMatcher.start();
                }
                relevantText = content.substring(sectionStart, sectionEnd).trim();
            }
        }

        String[] lines = relevantText.split("\\n");
        StringBuilder formattedResponse = new StringBuilder();
        Set<String> usedLines = new HashSet<>();
        for (String line : lines) {
            String trimmedLine = line.trim();
            if (trimmedLine.length() > 10 && !trimmedLine.matches("(?i).*(condividi|stampa|>|cookie|social).*") && usedLines.add(trimmedLine)) {
                 if (!trimmedLine.endsWith(".") && !trimmedLine.endsWith(":")) {
                    trimmedLine += ".";
                }
                if (!trimmedLine.startsWith(BULLET_POINT)) {
                     formattedResponse.append(BULLET_POINT);
                }
                formattedResponse.append(trimmedLine).append("\n");
            }
        }
        
        if (formattedResponse.length() == 0) {
            return RESPONSE_NO_INFO_FOUND + " (Nessun contenuto pertinente trovato)";
        }

        return RESPONSE_HEADER + SOURCE_PREFIX + infoList.get(0).source + "\n\n" + formattedResponse.toString() + RESPONSE_FOOTER_WARNING;
    }
}
