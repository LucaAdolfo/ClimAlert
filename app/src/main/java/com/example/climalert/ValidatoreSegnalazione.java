package com.example.climalert;
//classe temporanea che uso come test
public class ValidatoreSegnalazione{

    public static boolean isValid(String tipo, String descrizione, Double lat, Double lon) { //lat e lon sono rispettivamente latitudine e longitudine
        if (tipo == null || tipo.trim().isEmpty()) return false;
        if (descrizione == null || descrizione.trim().isEmpty()) return false;
        if (descrizione.trim().length() < 5) return false;
        if (lat == null || lon == null) return false;

        if (lat < -90 || lat > 90) return false;
        if (lon < -180 || lon > 180) return false;

        return true;
    }
}
