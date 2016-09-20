package com.tcc.tccpinut.tccpinut.fragments;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tcc.tccpinut.tccpinut.classes.Localizador;

import java.io.IOException;
import java.util.List;

/**
 * Created by muffinmad on 13/09/2016.
 */
public class PinutMapFragment extends SupportMapFragment implements OnMapReadyCallback{

    private LatLng initLatLng;
    private GoogleMap gMap;

    public LatLng getInitLatLng() {
        return initLatLng;
    }

    public void setInitLatLng(LatLng initLatLng) {
        this.initLatLng = initLatLng;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Bundle b = getArguments();
        double i,j;
        i = b.getDouble("LAT");
        j = b.getDouble("LGN");
        initLatLng = new LatLng(i, j);
        getMapAsync(this);
    }

    public void moveCamera(LatLng latLng){
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(initLatLng, 12);
        gMap.moveCamera(cameraUpdate);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        if (initLatLng != null){
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(initLatLng, 12);
            googleMap.moveCamera(cameraUpdate);
            setMarkers(googleMap);
        }

    }


    // Função que vai pegar os markers pelo webservice e colocar no mapa
    // TODO: Looping que vai buscar o resultado do webservice
    private boolean setMarkers(GoogleMap googleMap){
        LatLng latLng = new LatLng(40.785091,-73.968285);
        MarkerOptions mk = new MarkerOptions();
        mk.position(latLng);
        mk.title("VISH");
        googleMap.addMarker(mk);
        return true;
    }

    // Pega as coordenadas de um endereço através do texto do endereço.
    private LatLng getCoord(String adress) {
        Geocoder geocoder = new Geocoder(getContext());
        try {
            List<Address> fromLocationName = geocoder.getFromLocationName(adress, 1);
            if (!fromLocationName.isEmpty()){
                LatLng pos = new LatLng(fromLocationName.get(0).getLatitude(), fromLocationName.get(0).getLongitude());
                return pos;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
