package com.example.hurricaneai;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import com.example.climalert.R;


public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

    public interface OnMessageInteractionListener {
        void onEditMessageRequest(ChatMessage message);
        void onRegenerateResponseRequest(int position);
        void onSuggestionClick(String suggestion);
    }

    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_AI = 2;
    private static final int VIEW_TYPE_SUGGESTION = 3; // Nuovo tipo di vista

    private final List<ChatMessage> chatMessages;
    private final OnMessageInteractionListener listener;

    public ChatAdapter(List<ChatMessage> chatMessages, OnMessageInteractionListener listener) {
        this.chatMessages = chatMessages;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = chatMessages.get(position);
        if (message.getMessage().startsWith("SUGGERIMENTO:")) {
            return VIEW_TYPE_SUGGESTION;
        }
        return message.isUserMessage() ? VIEW_TYPE_USER : VIEW_TYPE_AI;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId;
        if (viewType == VIEW_TYPE_USER) {
            layoutId = R.layout.item_chat_message_user;
        } else if (viewType == VIEW_TYPE_AI) {
            layoutId = R.layout.item_chat_message_ai;
        } else { // VIEW_TYPE_SUGGESTION
            layoutId = R.layout.item_chat_message_suggestion;
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new MessageViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        holder.bind(chatMessages.get(position), listener, position, chatMessages);
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        private TextView messageTextView;
        private ImageButton editButton, regenerateButton;
        private Button suggestionButton;
        private TextView suggestionPrefixTextView;

        public MessageViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            if (viewType == VIEW_TYPE_SUGGESTION) {
                suggestionButton = itemView.findViewById(R.id.suggestionButton);
                suggestionPrefixTextView = itemView.findViewById(R.id.suggestionPrefixTextView);
            } else {
                messageTextView = itemView.findViewById(R.id.messageTextView);
                if (viewType == VIEW_TYPE_USER) {
                    editButton = itemView.findViewById(R.id.editButton);
                } else {
                    regenerateButton = itemView.findViewById(R.id.regenerateButton);
                }
            }
        }

        public void bind(final ChatMessage message, final OnMessageInteractionListener listener, final int position, final List<ChatMessage> messages) {
            if (getItemViewType() == VIEW_TYPE_SUGGESTION) {
                String suggestionText = message.getMessage().replace("SUGGERIMENTO:", "").trim();
                suggestionButton.setText(suggestionText);
                suggestionButton.setOnClickListener(v -> listener.onSuggestionClick(suggestionText));
            } else {
                messageTextView.setText(message.getMessage());
                if (editButton != null) {
                    editButton.setOnClickListener(v -> listener.onEditMessageRequest(message));
                }
                if (regenerateButton != null) {
                    if (position > 0 && messages.get(position - 1).isUserMessage()) {
                        regenerateButton.setVisibility(View.VISIBLE);
                        regenerateButton.setOnClickListener(v -> listener.onRegenerateResponseRequest(position));
                    } else {
                        regenerateButton.setVisibility(View.GONE);
                    }
                }
            }
        }
    }
}
