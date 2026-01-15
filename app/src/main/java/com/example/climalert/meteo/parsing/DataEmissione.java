package com.example.climalert.meteo.parsing;

import com.tickaroo.tikxml.annotation.Attribute;
import com.tickaroo.tikxml.annotation.Xml;

@Xml(name="data_emissione")
public class DataEmissione{
    @Attribute(name="data")
    String data;

    public String getData() {
        return data;
    }
}
