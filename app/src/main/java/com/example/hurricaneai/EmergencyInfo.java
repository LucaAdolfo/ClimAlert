package com.example.hurricaneai;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "emergency_info")
public class EmergencyInfo {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String source; // Es: "Protezione Civile - Terremoti"
    public String category; // "terremoto", "alluvione", "vulcano"
    public String content; // Testo estratto
    public String url; // URL sorgente
    public long timestamp; // Quando scaricato
    public String language; // "it", "en"

    //Costruttore
    public EmergencyInfo(String source, String category, String content,
                         String url, String language) {
        this.source = source;
        this.category = category;
        this.content = content;
        this.url = url;
        this.language = language;
        this.timestamp = System.currentTimeMillis();
    }

    // Getter e Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
}
