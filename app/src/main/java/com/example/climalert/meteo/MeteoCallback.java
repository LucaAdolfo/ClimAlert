package com.example.climalert.meteo;

public interface MeteoCallback{
    void OnSuccess(String response);
    void OnFailure(String message, Exception e);
}
