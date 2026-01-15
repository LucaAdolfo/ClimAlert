package com.example.climalert.meteo;

import com.example.climalert.meteo.parsing.Previsioni;

public interface MeteoCallback{
    void OnSuccess(Previsioni previsioni);
    void OnFailure(String message, Exception e);
}
