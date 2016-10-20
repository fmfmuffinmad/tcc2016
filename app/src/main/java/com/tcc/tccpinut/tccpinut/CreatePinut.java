package com.tcc.tccpinut.tccpinut;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
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
    private String caminhoFoto = null, caminhoAudio = null;
    private AudioHelper aHelper;

    private RequestQueue queue = null;

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

        double latitude = getIntent().getDoubleExtra("latitude", 0);
        double longitude = getIntent().getDoubleExtra("longitude", 0);
        this.latLng = new LatLng(latitude, longitude);

        btCriarPinut = (Button) findViewById(R.id.btCriarPinut);
        btCancelarPinut = (Button) findViewById(R.id.btCancelarPinut);
        switchPriva = (Switch) findViewById(R.id.switchPrivacidade);

        gravar = (Button) findViewById(R.id.gravar);
        parar = (Button) findViewById(R.id.parar);
        tocar = (Button) findViewById(R.id.tocar);

        gravar.setOnClickListener(this);
        parar.setOnClickListener(this);
        tocar.setOnClickListener(this);

        switchPriva.setOnClickListener(this);
        btCriarPinut.setOnClickListener(this);
        btCancelarPinut.setOnClickListener(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        checkLocationPermission();
        buildGoogleApiClient();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    @Override
    public void onClick(View v) {
        int item = v.getId();
        switch(item) {
            case R.id.btCriarPinut:
                if (latLng != null) {
                    queue = Volley.newRequestQueue(this);

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
                    pinut.setExpireOn(time + TimeUnit.HOURS.toMillis(Long.parseLong(edtTxtDuracaoPin.getText().toString())));

                    pinut.setLatitude(latLng.latitude);
                    pinut.setLongitude(latLng.longitude);
                    pinut.setImagepath(caminhoFoto);
                    pinut.setAudiopath(caminhoAudio);

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    Date date = new Date(pinut.getCreatedOn());
                    String dataFormCriada = sdf.format(date);

                    Date date2 = new Date(pinut.getExpireOn());
                    String dataFormExpira = sdf.format(date2);

                    final String url = "http://webservices.pinut.com.br/PinutWebServices/testeinsertpinut.php";

                    HashMap<String, String> hash = new HashMap<String, String>();
                    hash.put("createdby", "2");
                    hash.put("expireon", dataFormExpira);
                    hash.put("privacy", String.valueOf(pinut.getPrivacy()));
                    hash.put("createdon", dataFormCriada);
                    hash.put("latitude", String.valueOf(pinut.getLatitude()));
                    hash.put("longitude", String.valueOf(pinut.getLongitude()));

                    JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(hash),
                            new Response.Listener<JSONObject>()
                            {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        response = response.getJSONObject("0");
                                        String resp = response.getString("0");
                                        Toast.makeText(CreatePinut.this, resp, Toast.LENGTH_SHORT).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener()
                            {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    error.printStackTrace();
                                    //Log.d("Error.Response", error.toString());
                                    System.out.println("\n\n" + error.toString() + "\n\n");
                                    Toast.makeText(CreatePinut.this, error.toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                    );
                    queue.add(getRequest);

                    Intent result = new Intent();
                    result.putExtra("pin", pinut);
                    setResult(RESULT_OK, result);
                    finish();
                } else {
                    Toast.makeText(this, "Erro ao carregar sua localização!", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btCancelarPinut:
                setResult(RESULT_CANCELED, null);
                finish();
                break;
            case R.id.fab:
                Intent intentTiraFoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                caminhoFoto = getExternalFilesDir(null) + "/" + System.currentTimeMillis() + ".jpg";
                File arquivoFoto = new File(caminhoFoto);
                intentTiraFoto.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(arquivoFoto));
                startActivityForResult(intentTiraFoto, CODIGO_CAMERA);
                break;

            case R.id.switchPrivacidade:
                if (switchPriva.isChecked()) {
                    switchPriva.setText(R.string.privada);
                } else {
                    switchPriva.setText(R.string.publica);
                }
                break;

            case R.id.gravar:
                caminhoAudio = Environment.getExternalStorageDirectory().
                        getAbsolutePath() + "/javacodegeeksRecording.3gpp";
                aHelper = new AudioHelper(caminhoAudio);
                aHelper.start();
                break;

            case R.id.parar:
                if(aHelper != null) {
                    aHelper.stop();
                } else {
                    Toast.makeText(this, "Não foi gravado um áudio ainda.", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.tocar:
                if(aHelper != null) {
                    aHelper.play();
                } else {
                    Toast.makeText(this, "Não foi gravado um áudio ainda.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CODIGO_CAMERA:
                if (resultCode == RESULT_OK) {
                    ImageHelper imageHelper = new ImageHelper();
                    Bitmap bitmap = imageHelper.lessResolution(caminhoFoto, 300, 300);
                    ImageView campoFoto = (ImageView) findViewById(R.id.ciarPinutFoto);
                    campoFoto.setImageBitmap(bitmap);
                    campoFoto.setTag(caminhoFoto);
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
