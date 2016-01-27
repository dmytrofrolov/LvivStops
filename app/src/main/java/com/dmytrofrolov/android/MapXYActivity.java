package com.dmytrofrolov.android;

/**
 * Created by dmytrofrolov on 1/26/16.
 */


import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class MapXYActivity extends Activity implements OnMapReadyCallback {

    private String x_coord;
    private String y_coord;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_xy_activity);

        Bundle recdData = getIntent().getExtras();
        x_coord = recdData.getString("x_coord");
        y_coord = recdData.getString("y_coord");
        title = recdData.getString("title");



        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        LatLng currentBus = new LatLng(Double.parseDouble(y_coord),Double.parseDouble(x_coord));
//        Log.d("x_coord ",x_coord);
//        Log.d("y_coord ",y_coord);
//        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentBus, 13));

        map.addMarker(new MarkerOptions()
                .title(title)
//                .snippet("The most populous city in Australia.")
                .position(currentBus));

    }
}

