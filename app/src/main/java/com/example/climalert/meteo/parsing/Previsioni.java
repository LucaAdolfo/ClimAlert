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
    /**
    * @param
    * name Possibili name sono -> [
    * Dolomiti Nord-Est, Dolomiti Sud-Ovest, Belluno e Prealpi orientali , Prealpi centrali ,Pedemontana orientale, Treviso e pianura orientale,
    * Veneziano orientale,Prealpi occidentali,Vicenza e pedemontana,Padova e pianura centrale,Venezia e laguna,Delta del Po
    * Rovigo e pianura meridionale, Verona e pedemontana, Area del Garda, Litorale nord , Litorale centrale , Litorale sud
    *
    * ]
     * @param data
    * Per ogni nome le date possono essere facendo finta che n è il giorno attuale.
    * -> sab n gennaio pomeriggio
    * -> dom n+1 gennaio mattina
    * -> dom n+1 gennaio pomeriggio
    * -> lun n+2 gennaio mattina
    * -> lun n+2 gennaio pomeriggio
    * -> mar n+3 gennaio
    * -> mer n+4 gennaio
    * @return scadenza
    * Quindi per i primi prossimi 3 giorni c'è la distinzione mattina pomeriggio, i successivi 2 c'è quella generale
    * //TODO
    * */
    public Scadenza getMeteogrammi(String name, String data) {
        for (Meteogramma meteogramma : meteogrammi){
            if (meteogramma.getName().equals(name)){
                List<Scadenza> scadenza = meteogramma.getScadenze();
                for (Scadenza s : scadenza) {
                    if(s.getData().equals(data))
                        return s;
                }
            }
        }
        return null;
    }
    public DataEmissione getDataEmissione() {
        return dataEmissione;
    }
}

