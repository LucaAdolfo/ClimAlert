package com.example.climalert.meteo.parsing;

import com.tickaroo.tikxml.annotation.Attribute;
import com.tickaroo.tikxml.annotation.Xml;

@Xml(name="data_aggiornamento")
public class DataAggiornamento{
    @Attribute(name="data")
    String data;
    public String getData() {
        return data;
    }
}
