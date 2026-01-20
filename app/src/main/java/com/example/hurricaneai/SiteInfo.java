package com.example.hurricaneai;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.example.climalert.R;


@Entity(tableName = "sites")
public class SiteInfo {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String url;
    public String category;
    public String language;

    public SiteInfo(String name, String url, String category, String language) {
        this.name = name;
        this.url = url;
        this.category = category;
        this.language = language;
    }
}
