package com.example.hurricaneai;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.climalert.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatActivity extends AppCompatActivity implements ChatAdapter.OnMessageInteractionListener {

    private static final String TAG = "ChatActivity";

    private RecyclerView chatRecyclerView;
    private EditText messageInput;
    private MaterialButton sendButton;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;
    private AIEngine aiEngine;

    private final ExecutorService backgroundExecutor = Executors.newSingleThreadExecutor();
    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        toolbar.setNavigationOnClickListener(v -> finish());

        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages, this);
        
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        chatRecyclerView.setLayoutManager(layoutManager);
        chatRecyclerView.setAdapter(chatAdapter);

        addMessage(new ChatMessage(getString(R.string.chat_welcome_message), false));
        final ChatMessage loadingMessage = new ChatMessage("Caricamento dati in corso...", false);
        addMessage(loadingMessage);

        messageInput.setEnabled(false);
        sendButton.setEnabled(false);

        DataInitializer dataInitializer = new DataInitializer(this);
        dataInitializer.initializeData(new DataInitializer.OnDataReadyCallback() {
            @Override
            public void onDataReady() {
                mainThreadHandler.post(() -> {
                    removeMessage(loadingMessage);
                    addMessage(new ChatMessage("Sono pronto! Chiedimi pure.", false));
                    messageInput.setEnabled(true);
                    sendButton.setEnabled(messageInput.getText().length() > 0);
                });
            }
            @Override
            public void onError(String message) {
                mainThreadHandler.post(() -> {
                    removeMessage(loadingMessage);
                    addMessage(new ChatMessage("Errore nel caricamento dei dati. Riprova più tardi.", false));
                });
            }
        });
        
        aiEngine = new AIEngine(AppDatabase.getDatabase(this));

        sendButton.setOnClickListener(v -> sendMessage());
        messageInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                sendButton.setEnabled(messageInput.isEnabled() && TextUtils.getTrimmedLength(s) > 0);
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    public void onEditMessageRequest(ChatMessage messageToEdit) {
        int userMessagePosition = chatMessages.indexOf(messageToEdit);
        if (userMessagePosition != -1 && userMessagePosition < chatMessages.size() - 1) {
            ChatMessage nextMessage = chatMessages.get(userMessagePosition + 1);
            if (!nextMessage.isUserMessage()) {
                removeMessage(nextMessage);
            }
        }
        messageInput.setText(messageToEdit.getMessage());
        messageInput.requestFocus();
        messageInput.setSelection(messageToEdit.getMessage().length());
        removeMessage(messageToEdit);
    }

    @Override
    public void onRegenerateResponseRequest(int position) {
        if (position > 0 && position < chatMessages.size()) {
            ChatMessage aiResponse = chatMessages.get(position);
            ChatMessage userQuestion = chatMessages.get(position - 1);
            if (!aiResponse.isUserMessage() && userQuestion.isUserMessage()) {
                removeMessage(aiResponse);
                generateResponseFor(userQuestion.getMessage());
            }
        }
    }

    @Override
    public void onSuggestionClick(String suggestion) {
        // Rimuovi il messaggio di suggerimento precedente
        // L'ultimo messaggio è sicuramente il suggerimento, quindi possiamo rimuoverlo con certezza
        if (!chatMessages.isEmpty()) {
            removeMessage(chatMessages.get(chatMessages.size() - 1));
        }
        // Invia la domanda corretta come se l'avesse scritta l'utente
        messageInput.setText(suggestion);
        sendMessage();
    }

    private void sendMessage() {
        String messageText = messageInput.getText().toString().trim();
        if (messageText.isEmpty()) return;
        addMessage(new ChatMessage(messageText, true));
        messageInput.setText("");
        generateResponseFor(messageText);
    }

    private void generateResponseFor(String text) {
        final ChatMessage typingIndicator = new ChatMessage(getString(R.string.chat_typing_indicator), false);
        addMessage(typingIndicator);

        backgroundExecutor.execute(() -> {
            try {
                final String response = aiEngine.getResponse(text);
                mainThreadHandler.post(() -> {
                    removeMessage(typingIndicator);
                    // Gestisce la logica per mostrare una risposta normale o un suggerimento
                    if (response.startsWith("SUGGERIMENTO:")) {
                        addMessage(new ChatMessage(response, false)); // Il tipo di vista gestirà il layout
                    } else {
                        addMessage(new ChatMessage(response, false));
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Errore durante la generazione della risposta AI", e);
                mainThreadHandler.post(() -> {
                    removeMessage(typingIndicator);
                    addMessage(new ChatMessage(getString(R.string.chat_error_message), false));
                });
            }
        });
    }

    private void addMessage(ChatMessage message) {
        chatMessages.add(message);
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        chatRecyclerView.scrollToPosition(chatMessages.size() - 1);
    }

    private void removeMessage(ChatMessage message) {
        int position = chatMessages.indexOf(message);
        if (position != -1) {
            chatMessages.remove(position);
            chatAdapter.notifyItemRemoved(position);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        backgroundExecutor.shutdown();
    }
}
