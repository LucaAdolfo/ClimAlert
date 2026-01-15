package com.example.climalert.meteo.parsing;
/*
* Ho 5 tipi di classi
*
* */

import com.tickaroo.tikxml.annotation.Element;
import com.tickaroo.tikxml.annotation.Path;
import com.tickaroo.tikxml.annotation.Xml;

import java.util.List;
@Xml(name="previsioni")
public class Previsioni{


    @Element(name="data_emissione")
    DataEmissione dataEmissione;
    @Element(name="data_aggiornamento")
    DataAggiornamento dataAggiornamento;

    @Path("meteogrammi")
    @Element(name="meteogramma")
    List<Meteogramma> meteogrammi;


    public DataAggiornamento getDataAggiornamento() {
        return dataAggiornamento;
    }
    public List<Meteogramma> getMeteogrammi() {
        return meteogrammi;
    }
    public Meteogramma getMeteogrammi(String name) {
        for (Meteogramma meteogramma : meteogrammi){
            if (meteogramma.getName().equals(name)){
                return meteogramma;
            }
        }
        return null;
    }
    public DataEmissione getDataEmissione() {
        return dataEmissione;
    }
}

