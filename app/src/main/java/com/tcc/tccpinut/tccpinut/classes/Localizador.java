package com.tcc.tccpinut.tccpinut.classes;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by muffinmad on 20/09/2016.
 */

public class Localizador implements GoogleApiClient.ConnectionCallbacks {

    private final GoogleApiClient client;

    public Localizador(Context context) {
        client = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .build();

        client.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest request = new LocationRequest();

        // intervalos para solicitação do serviço
        request.setSmallestDisplacement(30);
        request.setInterval(3000);

        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
