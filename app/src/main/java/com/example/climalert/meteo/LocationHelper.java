package com.example.climalert.meteo;

import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;

/*
* Ho bisogno di un metodo che mi permetta di date la posizione capire
* altre info come provincia e comune gestisco tutto con un unica classe per comodita
*
* */

//TODO --> da completare per avere il meteo riferito alla posizione
public class LocationHelper {
    private GpsMyLocationProvider myLocation = null;
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private String indirizzo;
    private String provincia;
    private String comune;
    private double latitudine;
    private double longitudine;



//    private void checkLocationPermissions() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            // Abbiamo già il permesso, procediamo
//        } else {
//            // Chiediamo il permesso
//            requestPermissionLauncher.launch(new String[]{
//                    Manifest.permission.ACCESS_FINE_LOCATION,
//                    Manifest.permission.ACCESS_COARSE_LOCATION
//            });
//        }
//    }
//    public String getProvincia(double latitudine, double longitudine) {
//        Geocoder geocoder = new Geocoder(this, Locale.ITALY);
//        try {
//            List<Address> listaIndirizzi = geocoder.getFromLocation(latitudine, longitudine, 1);
//
//            if (listaIndirizzi != null && !listaIndirizzi.isEmpty()) {
//                Address indirizzo = listaIndirizzi.get(0);
//
//                return indirizzo.getSubAdminArea(); // Restituisce es: "Padova"
//            }
//        } catch (IOException e) {
//            Log.e("GEO", "Server non raggiungibile o errore di rete", e);
//        }
//        return "Provincia non trovata";
//    }



}
