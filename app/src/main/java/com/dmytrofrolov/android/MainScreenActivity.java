package com.dmytrofrolov.android;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.UUID;
import java.util.regex.Pattern;

public class MainScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen_activity);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        setTitle("Львівський транспорт. Онлайн.");

        // Search by stop button
        Button search_by_stop_button = (Button) findViewById(R.id.search_by_stops);
        search_by_stop_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View parent) {
                Intent appInfo = new Intent(MainScreenActivity.this, SearchActivity.class);
                startActivity(appInfo);
            }
        });

        // Search by bus button
        Button search_by_bus_button = (Button) findViewById(R.id.search_by_bus);
        search_by_bus_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View parent) {
                Intent appInfo = new Intent(MainScreenActivity.this, TransportScreenActivity.class);
                appInfo.putExtra("title", "Автобуси");
                appInfo.putExtra("loadurl", "http://82.207.107.126:13541/SimpleRIDE/LAD/SM.WebApi/api/CompositeRoute/");
                startActivity(appInfo);
            }
        });

        // Search by electic button
        Button search_by_electric_button = (Button) findViewById(R.id.search_by_electric);
        search_by_electric_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View parent) {
                Intent appInfo = new Intent(MainScreenActivity.this, TransportScreenActivity.class);
                appInfo.putExtra("title", "Електротранспорт");
                appInfo.putExtra("loadurl", "http://82.207.107.126:13541/SimpleRIDE/LET/SM.WebApi/api/CompositeRoute/");
                startActivity(appInfo);
            }
        });

        // Saved stops button
        Button saved_stops_button = (Button) findViewById(R.id.saved_stops);
        saved_stops_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View parent) {
                Intent appInfo = new Intent(MainScreenActivity.this, SavedStopsActivity.class);
                startActivity(appInfo);
            }
        });


        if(!isConnected()){
            new AlertDialog.Builder(MainScreenActivity.this)
                    .setTitle("Проблемс(")
                    .setMessage("Інформації немає, спробуйте вибрати щось інше. \nА також перевірте наявність інтернету.")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

        String query="";
        try{query = URLEncoder.encode(getDeviceName(), "utf-8");}catch (UnsupportedEncodingException e){}
        new HttpAsyncTask().execute("http://lvivtransport.udesgo.com/save.php?model="+query);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main , menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.saved_stops:
                Intent appInfo = new Intent(MainScreenActivity.this, SavedStopsActivity.class);
                startActivity(appInfo);
                return true;
            case R.id.about_program:

                return true;

            case R.id.send_feedback:
                return true;

            case R.id.exit:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    // device numbers
    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;

        final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String deviceId = deviceUuid.toString();

        if (model.startsWith(manufacturer)) {
            return capitalize(model)+ " "+deviceId;
        } else {
            return capitalize(manufacturer) + " " + model + " "+deviceId;
        }
    }


    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    // check connection
    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }


    public static String GET(String url){
        InputStream inputStream = null;
        String result = "";
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
//            Log.d("onPostExecute ", result);
        }
    }

}
