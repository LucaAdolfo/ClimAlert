package com.example.hurricaneai;

public class ChatMessage {
    private final String message;
    private final boolean isUserMessage; // true = utente, false = AI

    public ChatMessage(String message, boolean isUserMessage) {
        this.message = message;
        this.isUserMessage = isUserMessage;
    }

    public String getMessage() {
        return message;
    }

    public boolean isUserMessage() {
        return isUserMessage;
    }
}
