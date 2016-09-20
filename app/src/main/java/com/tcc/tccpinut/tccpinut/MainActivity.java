package com.tcc.tccpinut.tccpinut;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.tcc.tccpinut.tccpinut.classes.Localizador;
import com.tcc.tccpinut.tccpinut.fragments.AmigosFragment;
import com.tcc.tccpinut.tccpinut.fragments.ContaFragment;
import com.tcc.tccpinut.tccpinut.fragments.PinutMapFragment;
import com.tcc.tccpinut.tccpinut.fragments.TopPinutsFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        TopPinutsFragment.OnFragmentInteractionListener,
        AmigosFragment.OnFragmentInteractionListener,
        ContaFragment.OnFragmentInteractionListener,
        GoogleApiClient.ConnectionCallbacks,
        LocationListener {

    private FragmentManager fragManager;
    private FloatingActionButton fab;
    private Localizador localizador;
    private PinutMapFragment pinutMapFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentCreatePinut = new Intent(MainActivity.this, CreatePinut.class);
                startActivity(intentCreatePinut);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // permição de gps
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                String[] permissoes = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
                requestPermissions(permissoes, 1);
                //localizador = new Localizador(this);
                criaLocalizador();
            }else{
                //localizador = new Localizador(this);
                criaLocalizador();
            }
        }else{
            //localizador = new Localizador(this);
            criaLocalizador();
        }

        fragManager = getSupportFragmentManager();
        // Cria o frag do mapa
        FragmentTransaction fragTran = fragManager.beginTransaction();
        createMapFrag(fragTran);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void createMapFrag(FragmentTransaction fragTran) {
        pinutMapFragment = new PinutMapFragment();
        Bundle bundle = new Bundle();
        //if (localizador != null){
//            bundle.putDouble("LAT", localizador.getLatLng().latitude);
//            bundle.putDouble("LGT", localizador.getLatLng().longitude);
        if (atualLatLng !=null){
            bundle.putDouble("LAT", atualLatLng.latitude);
            bundle.putDouble("LNG", atualLatLng.longitude);
        }else{
            bundle.putDouble("LAT", 40.785091);
            bundle.putDouble("LNG", -73.968285);
        }

        pinutMapFragment.setArguments(bundle);
        fragTran.replace(R.id.main_frame, pinutMapFragment);
        fragTran.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentTransaction fragTran = fragManager.beginTransaction();

        if (id == R.id.nav_mapa) {
            if(fab.getVisibility() != View.VISIBLE){
                fab.show();
                createMapFrag(fragTran);
            }
        } else if (id == R.id.nav_toppinuts) {
            if (fab.getVisibility() == View.VISIBLE){
                fab.hide();
            }
            fragTran.replace(R.id.main_frame, new TopPinutsFragment());
            fragTran.commit();
        } else if (id == R.id.nav_amigos) {
            if (fab.getVisibility() == View.VISIBLE){
                fab.hide();
            }
            fragTran.replace(R.id.main_frame, new AmigosFragment());
            fragTran.commit();
        } else if (id == R.id.nav_compartilhar) {

            // TODO: Colocar a opção de compartilhar

        } else if (id == R.id.nav_conta) {
            if (fab.getVisibility() == View.VISIBLE){
                fab.hide();
            }
            fragTran.replace(R.id.main_frame, new ContaFragment());
            fragTran.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    // --------------------- Funções do GPS

    private GoogleApiClient client;
    private LatLng atualLatLng;

    private void criaLocalizador(){
        client = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .build();
        atualLatLng = new LatLng(40.785091,-73.968285);
        client.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest request = new LocationRequest();

        // intervalos para solicitação do serviço
        request.setSmallestDisplacement(30);
        request.setInterval(3000);

        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(client);
        if (lastLocation != null){
            atualLatLng = (new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()));
        }else{
            atualLatLng = (new LatLng(40.785091,-73.968285));
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(client, request, this);
        }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        atualLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        pinutMapFragment.moveCamera(atualLatLng);
    }
}
