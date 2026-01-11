//test in cui controllo se i dati inseriti all'interno dei campi della segnalazione sono validi
package com.example.climalert;

import org.junit.Test;
import static org.junit.Assert.*;

public class ValidatoreSegnalazioneTest{

    @Test
    public void tipoVuoto_nonValido(){
        assertFalse(ValidatoreSegnalazione.isValid("   ", "Descrizione ok", 45.0, 12.0));
    }

    @Test
    public void descrizioneVuota_nonValida() {
        assertFalse(ValidatoreSegnalazione.isValid("Incendio", "   ", 45.0, 12.0));
    }

    @Test
    public void coordinateNull_nonValido() {
        assertFalse(ValidatoreSegnalazione.isValid("Incendio", "Descrizione ok", null, 12.0));
        assertFalse(ValidatoreSegnalazione.isValid("Incendio", "Descrizione ok", 45.0, null));
    }

    @Test
    public void coordinateFuoriRange_nonValido() {
        assertFalse(ValidatoreSegnalazione.isValid("Incendio", "Descrizione ok", 999.0, 12.0));
        assertFalse(ValidatoreSegnalazione.isValid("Incendio", "Descrizione ok", 45.0, 999.0));
    }

    @Test
    public void datiCorretti_valido() {
        assertTrue(ValidatoreSegnalazione.isValid("Frana", "Strada bloccata", 45.43, 12.33));
    }
}
