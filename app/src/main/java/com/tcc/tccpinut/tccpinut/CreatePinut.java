package com.tcc.tccpinut.tccpinut;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageHelper;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.tcc.tccpinut.tccpinut.classes.Pinut;
import com.tcc.tccpinut.tccpinut.helpers.AudioHelper;
import com.tcc.tccpinut.tccpinut.helpers.ImageHelper;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.sql.Time;
import java.util.concurrent.TimeUnit;


public class CreatePinut extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        View.OnClickListener {

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private LatLng latLng;
    private EditText edtTxtTitulo, edtTxtTexto, edtTxtDuracaoPin;
    private Button btCriarPinut, btCancelarPinut, gravar, parar, tocar;
    private Switch switchPriva;

    private final int CODIGO_CAMERA = 1;
    private String caminhoFoto, caminhoAudio;
    private AudioHelper aHelper;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pinut);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        btCriarPinut = (Button) findViewById(R.id.btCriarPinut);
        btCancelarPinut = (Button) findViewById(R.id.btCancelarPinut);
        switchPriva = (Switch)findViewById(R.id.switchPrivacidade);


        gravar = (Button)findViewById(R.id.gravar);
        parar = (Button)findViewById(R.id.parar);
        tocar = (Button)findViewById(R.id.tocar);


        gravar.setOnClickListener(this);
        parar.setOnClickListener(this);
        tocar.setOnClickListener(this);


        switchPriva.setOnClickListener(this);
        btCriarPinut.setOnClickListener(this);
        btCancelarPinut.setOnClickListener(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);  /*(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLocationPermission();
                buildGoogleApiClient();
                if (latLng != null) {
                    Toast.makeText(CreatePinut.this, "Pinut criada na posição "+latLng.latitude+", "+latLng.longitude, Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    Toast.makeText(CreatePinut.this, "Ainda não foi possível definir a localização do dispositivo", Toast.LENGTH_SHORT).show();
                }

            }
        });*/

        checkLocationPermission();
        buildGoogleApiClient();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onClick(View v) {
        int item = v.getId();

        if (item == R.id.btCriarPinut) {

            if(latLng != null) {
                Pinut pinut = new Pinut();

                edtTxtTitulo = (EditText) findViewById(R.id.edtTxtTitulo);
                edtTxtTexto = (EditText) findViewById(R.id.edtTxtTexto);
                edtTxtDuracaoPin = (EditText) findViewById(R.id.edtTxtDuracaoHoras);

                if (switchPriva.isChecked()) {
                    pinut.setPrivacy(1);
                } else {
                    pinut.setPrivacy(0);
                }

                pinut.setTitle(edtTxtTitulo.getText().toString());
                pinut.setText(edtTxtTexto.getText().toString());

                long time = System.currentTimeMillis();
                pinut.setCreatedOn(time);
                pinut.setExpireOn(time + TimeUnit.MILLISECONDS.convert(
                        Integer.parseInt(edtTxtDuracaoPin.getText().toString()),
                        TimeUnit.HOURS
                ));

                pinut.setLocation(latLng.latitude, latLng.longitude);
                pinut.setImagepath(caminhoFoto);
                pinut.setAudiopath(caminhoAudio);

                Intent result = new Intent();
                result.putExtra("pin", pinut);

                setResult(RESULT_OK, result);
                finish();
            } else {
                Toast.makeText(this, "Erro ao carregar sua localização!", Toast.LENGTH_LONG).show();
            }
        }

        if (item == R.id.btCancelarPinut) {
            setResult(RESULT_CANCELED, null);
            finish();
        }

        if (item == R.id.fab) {
            Intent intentTiraFoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            caminhoFoto = getExternalFilesDir(null) + "/" + System.currentTimeMillis() + ".jpg";
            File arquivoFoto = new File(caminhoFoto);
            intentTiraFoto.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(arquivoFoto));
            startActivityForResult(intentTiraFoto, CODIGO_CAMERA);
        }

        if(item == R.id.switchPrivacidade){
            if(switchPriva.isChecked()){
                switchPriva.setText(R.string.privada);
            } else {
                switchPriva.setText(R.string.publica);
            }
        }

        if(item == R.id.gravar){
            caminhoAudio = Environment.getExternalStorageDirectory().
                    getAbsolutePath() + "/javacodegeeksRecording.3gpp";


            aHelper = new AudioHelper(caminhoAudio);
            aHelper.start();
        }

        if(item == R.id.parar){
            aHelper.stop();
        }

        if(item == R.id.tocar){
            aHelper.play();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CODIGO_CAMERA:
                if(resultCode == RESULT_OK){
                    ImageHelper imageHelper = new ImageHelper();

                    Bitmap bitmap = imageHelper.lessResolution(caminhoFoto, 150, 150);

                    ImageView campoFoto = (ImageView) findViewById(R.id.ciarPinutFoto);
                    campoFoto.setImageBitmap(bitmap);
                }
                break;
        }
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;

        //Place current location marker
        latLng = new LatLng(location.getLatitude(), location.getLongitude());

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
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
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("CreatePinut Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
