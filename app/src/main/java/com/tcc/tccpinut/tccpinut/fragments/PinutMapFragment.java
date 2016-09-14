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

import java.io.IOException;
import java.util.List;

/**
 * Created by muffinmad on 13/09/2016.
 */
public class PinutMapFragment extends SupportMapFragment implements OnMapReadyCallback{

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        LatLng latLng = getCoord("Sao Paulo");
        if (latLng != null){
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16);
            googleMap.moveCamera(cameraUpdate);
            MarkerOptions mk = new MarkerOptions();
            mk.position(latLng);
            mk.title("VISH");
            googleMap.addMarker(mk);

        }



    }

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
