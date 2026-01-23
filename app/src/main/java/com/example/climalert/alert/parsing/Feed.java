package com.example.climalert.alert.parsing;


import com.tickaroo.tikxml.annotation.Element;
import com.tickaroo.tikxml.annotation.Xml;

import java.util.List;

@Xml(name = "feed")
public class Feed {
    @Element(name = "updated")
    String updated;

    public List<Entry> getEntry() {
        return entry;
    }
    public Entry getEntry(String name) {
        for (Entry entry : entry){
            if (entry.getAreaDesc().contains(name)){
                return entry;
            }
        }
        return null;
    }

    public String getUpdated() {
        return updated;
    }
    @Element(name="entry")
    List<Entry> entry;
}
