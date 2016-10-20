package com.tcc.tccpinut.tccpinut.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.vision.text.Text;
import com.tcc.tccpinut.tccpinut.CreatePinut;
import com.tcc.tccpinut.tccpinut.DAOs.DBControl;
import com.tcc.tccpinut.tccpinut.DAOs.PinutDAO;
import com.tcc.tccpinut.tccpinut.MainActivity;
import com.tcc.tccpinut.tccpinut.MostrarDetalhesPinut;
import com.tcc.tccpinut.tccpinut.R;
import com.tcc.tccpinut.tccpinut.classes.Pinut;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by muffinmad on 13/09/2016.
 */
public class PinutMapFragment extends SupportMapFragment implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.InfoWindowAdapter, GoogleMap.OnInfoWindowClickListener {

    private LatLng initLatLng;
    private GoogleMap gMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private Marker mCurrLocationMarker;
    private boolean firstExecution = true;
    private CameraPosition cameraPosition;
    //private List<Marker> markerList;
    private WeakHashMap<Marker, Pinut> markerHashMap;


    // variáveis para auxiliar no carregamentos das pints
    private LatLng currLatLng;
    private final double CHUNCK_VALUE = 0.05;

    public PinutMapFragment() {
    }

    public LatLng getInitLatLng() {
        return initLatLng;
    }

    public void setInitLatLng(LatLng initLatLng) {
        this.initLatLng = initLatLng;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        firstExecution = true;
        //markerList = new ArrayList<Marker>();
        markerHashMap = new WeakHashMap<Marker, Pinut>();
        getMapAsync(this);
    }

    public void moveCamera(LatLng latLng) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(initLatLng, 12);
        gMap.moveCamera(cameraUpdate);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.setOnCameraMoveListener(this);
        gMap.setInfoWindowAdapter(this);
        gMap.setOnInfoWindowClickListener(this);
        //gMap.setOnMyLocationChangeListener(myLocationChangeListener);

        checkLocationPermission();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                gMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            gMap.setMyLocationEnabled(true);
        }
        loadMarkers();
    }
/*
    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            System.out.println("\n Mudou carai: " + location.getLongitude() + "\n" +
                    location.getLatitude());
        }
    };
*/

    // Função que vai pegar os markers pelo webservice e colocar no mapa
    public boolean loadMarkers() {
        PinutDAO dao = new PinutDAO(getContext());
        List<Pinut> lista = dao.buscaPinuts(); //// TODO: 21/09/2016 Trocar pela função do webservice
        dao.close();
        gMap.clear();
        if (lista != null) {
            if (!lista.isEmpty()) {
                for (Pinut pinut : lista) {
                    MarkerOptions mk = new MarkerOptions();
                    mk.position(new LatLng(pinut.getLatitude(), pinut.getLongitude()));
                    mk.title(pinut.getTitle());
                    Marker a = gMap.addMarker(mk);
                    a.setTag(pinut);
                    markerHashMap.put(a, pinut);
                    //markerList.add(a);
                }
            }
        }

        return true;
    }

    public void testePinut() {
        Pinut pinut = new Pinut();
        //pinut.setLatLng(gMap.getCameraPosition().target);
        pinut.setLatitude(gMap.getCameraPosition().target.latitude);
        pinut.setLongitude(gMap.getCameraPosition().target.longitude);
        pinut.setTitle("VISH PINUT NOVA!");
        pinut.setCreatedOn(System.currentTimeMillis());
        pinut.setExpireOn(TimeUnit.MILLISECONDS.convert(3, TimeUnit.DAYS));
        PinutDAO dao = new PinutDAO(getContext());
        dao.insert(pinut);
        dao.close();
        Toast.makeText(getContext(), "Pinut Criada", Toast.LENGTH_SHORT).show();
        loadMarkers();
    }

    // Pega as coordenadas de um endereço através do texto do endereço.
    private LatLng getCoord(String adress) {
        Geocoder geocoder = new Geocoder(getContext());
        try {
            List<Address> fromLocationName = geocoder.getFromLocationName(adress, 1);
            if (!fromLocationName.isEmpty()) {
                LatLng pos = new LatLng(fromLocationName.get(0).getLatitude(), fromLocationName.get(0).getLongitude());
                return pos;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onCameraMove() {
        cameraPosition = gMap.getCameraPosition();
        TextView t1 = (TextView) getActivity().findViewById(R.id.lat);
        TextView t2 = (TextView) getActivity().findViewById(R.id.lng);
        t1.setText(Double.toString(cameraPosition.target.latitude));
        t2.setText(Double.toString(cameraPosition.target.longitude));
        if (currLatLng == null) {
            currLatLng = cameraPosition.target;
        } else {
            if ((Math.abs(cameraPosition.target.latitude) + Math.abs(cameraPosition.target.longitude))
                    - (Math.abs(currLatLng.latitude) + Math.abs(currLatLng.longitude)) > CHUNCK_VALUE) {
                currLatLng = cameraPosition.target;
                loadMarkers();
            }
        }
    }

    // ------------- rotinas das markers customizadas
    @Override
    public View getInfoWindow(Marker marker) {

        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = getLayoutInflater(null).inflate(R.layout.pinut_info_window, null);
        LatLng l = marker.getPosition();
        TextView lat = (TextView) view.findViewById(R.id.info_lat);
        TextView lng = (TextView) view.findViewById(R.id.info_lng);
        lat.setText(Double.toString(l.latitude));
        lng.setText(Double.toString(l.longitude));
        return view;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Pinut pinut = (Pinut) marker.getTag();
        /*
        Toast.makeText(getContext(), "Janela da pinut clickada!" +
                "\n Lat: " + pinut.getLatitude()
                + "\nLng: " + pinut.getLongitude(), Toast.LENGTH_SHORT).show();
        */
        //TODO: ao clicar no marker exibir uma tela com as informações
        Intent intentPinutDetalhes = new Intent(getActivity(), MostrarDetalhesPinut.class);
        intentPinutDetalhes.putExtra("pinut", pinut);
        startActivity(intentPinutDetalhes);
    }

    // ---------------- Rotinas dos servições de localização
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //PRIORITY_BALANCED_POWER_ACCURACY
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onStart() {
        if(mGoogleApiClient != null) {
            if (!mGoogleApiClient.isConnected()) {
                mGoogleApiClient.connect();
            }
        }
        super.onStart();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(getActivity(), "AEEEEEEEEHAHAHAHA", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getActivity(), "Falha ao conectar!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        setInitLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        if(firstExecution){
            gMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(getInitLatLng().latitude, getInitLatLng().longitude)));
            gMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            firstExecution = false;
        }

        //Place current location marker
        //LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        //move map camera
        /*
        gMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        gMap.animateCamera(CameraUpdateFactory.zoomTo(15));
*/


        //stop location updates
        /*
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
        */
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

//                        if (mGoogleApiClient == null) {
//                            buildGoogleApiClient();
//                        }
//                        gMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(getActivity(), "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }


}
