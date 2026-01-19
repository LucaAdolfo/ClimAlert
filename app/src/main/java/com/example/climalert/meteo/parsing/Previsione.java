package com.example.climalert.meteo.parsing;

import com.tickaroo.tikxml.annotation.Attribute;
import com.tickaroo.tikxml.annotation.Xml;

@Xml(name="previsione")
public class Previsione{
    @Attribute(name="title")
    String title;
    @Attribute(name="type")
    String type;
    @Attribute(name="value")
    String value;

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}
