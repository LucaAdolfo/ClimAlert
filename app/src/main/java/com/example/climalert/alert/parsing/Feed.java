package com.example.climalert.alert.parsing;


import android.util.Log;

import com.tickaroo.tikxml.annotation.Element;
import com.tickaroo.tikxml.annotation.PropertyElement;
import com.tickaroo.tikxml.annotation.Xml;

import java.util.List;

@Xml(name = "feed")
public class Feed {
    @PropertyElement(name = "updated")
    String updated;

    public List<Entry> getEntry() {
        return entry;
    }
    public Entry getEntry(String name) {
        if(name==null)
            name= "Veneto";
        for (Entry entry : entry){
            if (entry.getAreaDesc().toLowerCase().contains(name.toLowerCase()) || entry.getAreaDesc().contains(name)){
                return entry;
            }
        }
        Log.e("EmergencyWorker - FEED", "Nessuna regione trovata con " + name);
        return getEntry().get(0);
    }

    public String getUpdated() {
        return updated;
    }
    @Element(name="entry")
    List<Entry> entry;
}
