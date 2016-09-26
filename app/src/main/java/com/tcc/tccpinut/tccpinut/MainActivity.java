package com.tcc.tccpinut.tccpinut;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import com.tcc.tccpinut.tccpinut.classes.Pinut;
import com.tcc.tccpinut.tccpinut.fragments.AmigosFragment;
import com.tcc.tccpinut.tccpinut.fragments.ContaFragment;
import com.tcc.tccpinut.tccpinut.fragments.PinutMapFragment;
import com.tcc.tccpinut.tccpinut.fragments.TopPinutsFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        TopPinutsFragment.OnFragmentInteractionListener,
        AmigosFragment.OnFragmentInteractionListener,
        ContaFragment.OnFragmentInteractionListener
{

    private FragmentManager fragManager;
    private FloatingActionButton fab;
    private PinutMapFragment pinutMapFragment;
    private int CREAT_PINUT_REQUEST = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                pinutMapFragment.testePinut();

                Intent intentCreatePinut = new Intent(MainActivity.this, CreatePinut.class);
                startActivityForResult(intentCreatePinut, CREAT_PINUT_REQUEST);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fragManager = getSupportFragmentManager();
        // Cria o frag do mapa
        FragmentTransaction fragTran = fragManager.beginTransaction();
        createMapFrag(fragTran);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == CREAT_PINUT_REQUEST){
            if(resultCode == RESULT_OK){
                Pinut pinut = (Pinut) data.getSerializableExtra("pin");
                String res = "Titulo: " + pinut.getTitle() + "\n" +
                        "Texto: " + pinut.getText() + "\n" +
                        "Foto: " + pinut.getImagepath() + "\n";
                Toast.makeText(this, res, Toast.LENGTH_LONG).show();
            }
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void createMapFrag(FragmentTransaction fragTran) {
        pinutMapFragment = new PinutMapFragment();
        Bundle bundle = new Bundle();

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
            if (fab.getVisibility() != View.VISIBLE) {
                fab.show();
                createMapFrag(fragTran);
            }
        } else if (id == R.id.nav_toppinuts) {
            if (fab.getVisibility() == View.VISIBLE) {
                fab.hide();
            }
            fragTran.replace(R.id.main_frame, new TopPinutsFragment());
            fragTran.commit();
        } else if (id == R.id.nav_amigos) {
            if (fab.getVisibility() == View.VISIBLE) {
                fab.hide();
            }
            fragTran.replace(R.id.main_frame, new AmigosFragment());
            fragTran.commit();
        } else if (id == R.id.nav_compartilhar) {

            // TODO: Colocar a opção de compartilhar

        } else if (id == R.id.nav_conta) {
            if (fab.getVisibility() == View.VISIBLE) {
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

}