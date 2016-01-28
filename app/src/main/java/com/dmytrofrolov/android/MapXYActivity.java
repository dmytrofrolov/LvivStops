package com.dmytrofrolov.android;

/**
 * Created by dmytrofrolov on 1/26/16.
 */


import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Vector;

public class MapXYActivity extends Activity implements OnMapReadyCallback {

    private String x_coord;
    private String y_coord;
    private String transportTitle;
    private String transportCode;
    private String urls;
    private GoogleMap gMap = null;
    private boolean isFirstStart = true;

    private class PointItem{
        PointItem(String x_coord, String y_coord, String title, int state){
            this.x_coord = x_coord;
            this.y_coord = y_coord;
            this.title = title;
            this.state = state;
        }

        public String x_coord;
        public String y_coord;
        public String title;
        public int state;
    }

    private Vector<PointItem> points;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_xy_activity);

        points = new Vector<PointItem>();

        Bundle recdData = getIntent().getExtras();
        transportCode = recdData.getString("transportCode");
        transportTitle = recdData.getString("transportTitle");
        if(transportCode==null)transportCode="";

        Button addBtn = (Button) findViewById(R.id.button);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HttpAsyncTask().execute(urls);
            }
        });

        urls = getResources().getString(R.string.api_url_bus_on_way);
        if(transportTitle.toLowerCase().contains("трамвай") || transportTitle.toLowerCase().contains("тролейбус")){
            urls = getResources().getString(R.string.api_url_electro_on_way);
        }
        urls += transportCode;

        Log.d("MapXYActivityURL", urls);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        gMap = map;
        new HttpAsyncTask().execute(urls);

        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(49.83, 24.014167));
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(20.4f);
        gMap.animateCamera(zoom);
        gMap.moveCamera(center);
//        LatLng currentBus = new LatLng(Double.parseDouble(y_coord),Double.parseDouble(x_coord));
//        Log.d("x_coord ",x_coord);
//        Log.d("y_coord ",y_coord);
//        map.setMyLocationEnabled(true);
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentBus, 13));

//        map.addMarker(new MarkerOptions()
//                .title(title)
////                .snippet("The most populous city in Australia.")
//                .position(currentBus));

    }


    // added async loading

    public static String GET(String url){
        InputStream inputStream = null;
        String result = "";
        url = url.replace("|","%7C");
        try {

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", "");
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Отримано!", Toast.LENGTH_LONG).show();
            parseJsonToAndSaveToPoints(result);
            gMap.clear();
            Log.d("Size", String.valueOf(points.size()));
            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            for(int i = 0; i < points.size(); i++){
                LatLng currentBus = new LatLng(Double.parseDouble(points.get(i).y_coord),Double.parseDouble(points.get(i).x_coord));

                BitmapDescriptor icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                if(points.get(i).state==0)
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);

                gMap.addMarker(new MarkerOptions()
                        .title(points.get(i).title)
                        .snippet(transportTitle)
                        .icon(icon)
                        .position(currentBus));
                builder.include(currentBus);
            }
            if ( points.size() > 0 && isFirstStart){
                LatLngBounds bounds = builder.build();
                gMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 30));
                isFirstStart = false;
            }
        }
    }

    public void parseJsonToAndSaveToPoints(String receivedStr){
//        ArrayList<String> result = new ArrayList<String>();
//        ArrayList<StopItem> items = new ArrayList<StopItem>();

        receivedStr = receivedStr.replace("\"[", "[");
        receivedStr = receivedStr.replace("]\"", "]");
        receivedStr = receivedStr.replace("\\\\\\\"", "*");
        receivedStr = receivedStr.replace("\\", "");
        points.clear();
        Log.d("receivedStr", String.valueOf(receivedStr.length()) );
        try
        {
            //JSONObject jObject = new JSONObject(receivedStr);

            JSONArray cast = new JSONArray(receivedStr);
            for (int i=0; i<cast.length(); i++) {
                JSONObject jObject = cast.getJSONObject(i);

                if(Float.parseFloat(jObject.getString("X")) < 1 || Float.parseFloat(jObject.getString("Y")) < 1)
                    continue;

                int state = Integer.parseInt(jObject.getString("State"));

                points.add(new PointItem(
                        jObject.getString("X"),
                        jObject.getString("Y"),
                        jObject.getString("VehicleName"),
                        state
                ));

//                if(itemRes.length()>5)continue;
//                items.add(new StopItem(jObject.getString("Name"), itemRes));
//                itemRes += jObject.getString("Name");
//                result.add(itemRes);
            }
        }catch (JSONException e)
        {
            //
        }

//        return items;
    }
}

