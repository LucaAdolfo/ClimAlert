package com.example.climalert.meteo.parsing;


import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* start tag -> previsioni
* meteogrammi -> meteogramma (con zona) -> scadenza (data)
* bollettini
*
*
* end tag -> /previsioni
* */
class DataEmissione{
    private String date = "";
    public DataEmissione(String d){
        this.date = d;
    }
    public String getDate(){
        return this.date;
    }
}

class Meteogramma {
    private String zona = "";
    private String id = "";

    public Meteogramma(String z, String s) {
        this.zona = z;
        this.id = s;

    }
    public String getZona(){
        return this.zona;
    }
    public String getId(){
        return this.id;
    }
}
class Scadenza{
    private String date = "";
    public Scadenza(String d){
        this.date = d;
    }
    public String getDate(){
        return this.date;
    }
}
//<previsione title="Simbolo" type="image"
// value="https://www.arpa.veneto.it/risorse/data-bollettini/meteo/icone/meteo/d3.png"/>
class Previsione{
    private String title = "";
    private String type = "";
    private String value = "";
    public Previsione(String t, String ty, String v){
        this.title = t;
        this.type = ty;
        this.value = v;
    }
    public String getTitle(){
        return this.title;
    }
    public String getType() {
        return this.type;
    }
    public String getValue() {
        return this.value;
    }

}

public class xmlParser {
    /*

    */
    public Map parseXml(String s){
        // Strutture per raccogliere i dati estratti
        DataEmissione dataEmissione = null;
        List<Meteogramma> meteogramma = new ArrayList<>();
        List<Scadenza> listaScadenze = new ArrayList<>();
        List<Previsione> listaPrevisioniCorrenti = new ArrayList<>(); // Lista per i figli diretti

        Map<String, Object> tot = new HashMap<>();
        // map è key - value  -> key è Nome oggetto , value è oggetto
        try{
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new java.io.StringReader(s));
            int eventType = parser.getEventType();
            while(eventType != XmlPullParser.END_DOCUMENT){
                String tagName = parser.getName();
                switch (eventType){
                    case XmlPullParser.START_TAG:
                        if (tagName.equalsIgnoreCase("data_emissione")){
                            dataEmissione = new DataEmissione(parser.getAttributeValue(null, "data"));

                        }
                        else if(tagName.equalsIgnoreCase("previsione")) {
                            String title = parser.getAttributeValue(null, "title");
                            String type = parser.getAttributeValue(null, "type");
                            String value = parser.getAttributeValue(null, "value");

                            Previsione p = new Previsione(title,type,value);
                            listaPrevisioniCorrenti.add(p);


//                            tot.put(tagName, new Previsione(title,type,value));
                        }
                        else if (tagName.equalsIgnoreCase("meteogramma")){
                            String zona = parser.getAttributeValue(null, "zona");
                            String id = parser.getAttributeValue(null, "id");

                            meteogramma.add(new Meteogramma(zona,id));

                            //tot.put(tagName, new Meteogramma(zona,id));
                        }

                        else if (tagName.equalsIgnoreCase("scadenza")){
                            String date = parser.getAttributeValue(null, "data");
                            //tot.put(tagName, new Scadenza(date));
                            listaScadenze.add(new Scadenza(date));


                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if(tagName.equalsIgnoreCase("previsioni")){
                            break;
                        }
                    default:
                        break;

                }



            parser.next();
            }
            tot.put("data_emissione", dataEmissione);
            tot.put("meteogramma", meteogramma);
            tot.put("scadenze", listaScadenze); // Lista di scadenze
            tot.put("previsioni_correnti", listaPrevisioniCorrenti);



        }catch (Exception e){

            Log.e("Parsing","Errore parsing xml : "+e.getMessage());
        }





        return tot;
    }




}
