package com.example.climalert.meteo.parsing;

import com.tickaroo.tikxml.annotation.Attribute;
import com.tickaroo.tikxml.annotation.Element;
import com.tickaroo.tikxml.annotation.Xml;

import java.util.List;

@Xml(name="scadenza")
public class Scadenza{
    @Attribute(name="data")
    String data;
    @Element(name = "previsione")
    List<Previsione> previsioni;

    public String getData() {
        return data;
    }

    public List<Previsione> getPrevisioni() {
        return previsioni;
    }
    /**@param type può essere o "image" o "text"
     * @return previsione or null se non trovata
     * */
    public Previsione getPrevisioni(String type) {
        for (Previsione previsione : previsioni){
            if (previsione.getType().equals(type)){
                return previsione;
            }
        }
        return null;
    }
}
