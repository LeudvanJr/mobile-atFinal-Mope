package br.edu.fateczl.mope.control;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class LatLngHelper {
    public static LatLng StringToLatLng(String s){
        LatLng latLng = null;
        try {
            String[] parteS = s
                    .replace("(", "")
                    .replace(")","")
                    .split(",");
            double lat = Double.parseDouble(parteS[0].trim());
            double lng = Double.parseDouble(parteS[1].trim());
            latLng = new LatLng(lat,lng);
        } catch (NumberFormatException e) {
            latLng = new LatLng(0,0);
        }
        return latLng;
    }
    public static String latLngToString(LatLng latLng){
        String[] aux = latLng.toString().split(" ");
        return aux[1]
                .replace("(","")
                .replace(")","");
    }
}
