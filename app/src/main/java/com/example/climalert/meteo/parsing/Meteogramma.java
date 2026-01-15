package com.example.climalert.meteo.parsing;

import com.tickaroo.tikxml.annotation.Attribute;
import com.tickaroo.tikxml.annotation.Element;
import com.tickaroo.tikxml.annotation.Xml;

import java.util.List;

@Xml(name="meteogramma")
public class Meteogramma{
    @Attribute(name="name")
    String name;
    @Attribute(name="zoneid")
    String zoneid;
    @Element (name="scadenza")
    List<Scadenza> scadenze;

    public String getName() {
        return name;
    }

    public String getZoneid() {
        return zoneid;
    }
    public List<Scadenza> getScadenze() {
        return scadenze;
    }
}
