package com.example.hurricaneai;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.climalert.R;


public class HurricaneAIActivity extends AppCompatActivity {

    private int devModeClickCount = 0;
    private long devModeLastClickTime = 0;
    private static final String DEV_PASSWORD = "Hurricane404";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hurricane_ai);

        TextView titleTextView = findViewById(R.id.titleText);
        Button chatBtn = findViewById(R.id.chatButton);

        chatBtn.setOnClickListener(v -> {
            Intent intent = new Intent(HurricaneAIActivity.this, ChatActivity.class);
            startActivity(intent);
        });

        // Listener per la modalità sviluppatore
        titleTextView.setOnClickListener(v -> handleDevModeClick());
    }

    private void handleDevModeClick() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - devModeLastClickTime > 1000) {
            devModeClickCount = 1;
        } else {
            devModeClickCount++;
        }
        devModeLastClickTime = currentTime;

        if (devModeClickCount >= 5) {
            devModeClickCount = 0;
            showPasswordDialog();
        }
    }

    private void showPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_password, null);
        final EditText passwordInput = dialogView.findViewById(R.id.passwordInput);

        builder.setView(dialogView)
                .setPositiveButton(R.string.dev_mode_access_button, (dialog, id) -> {
                    String password = passwordInput.getText().toString();
                    if (DEV_PASSWORD.equals(password)) {
                        Toast.makeText(this, R.string.dev_mode_activated, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(HurricaneAIActivity.this, DeveloperActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, R.string.dev_mode_wrong_password, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.dev_mode_cancel_button, (dialog, id) -> dialog.cancel());
        builder.create().show();
    }
}
