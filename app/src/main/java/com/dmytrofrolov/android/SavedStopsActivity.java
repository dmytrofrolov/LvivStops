package com.dmytrofrolov.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import java.util.ArrayList;
import java.util.regex.Pattern;

public class SavedStopsActivity extends AppCompatActivity {

    ListView stopsList;
    String[] catnames;
    ArrayList<StopItem> stopItemArrayList = new ArrayList<StopItem>();

    private ArrayList<StopItem> array_sort = new ArrayList<StopItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        setTitle("Збережені зупинки");
        String loadUrl = "http://82.207.107.126:13541/SimpleRIDE/LAD/SM.WebApi/api/stops/";
        Log.d("LoadURL : ", loadUrl);
        catnames = new String[] {
                "0557 - Русових, в центр",
                "0138 - Щирецька, в АШАН",
                "0693 - Конвеєрний, в центр",
                "0532 - Кульпарків, в центр",
                "0018 - Костел, в центр"
        };
        catnames = readFromFile(this);

        for (String str:catnames
             ) {
            if(str.length() > 6)
                stopItemArrayList.add(new StopItem(str.substring(5),str.substring(0,5)));
        }

        stopsList = (ListView) findViewById(R.id.listView2);

        stopsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                Intent appInfo = new Intent(SavedStopsActivity.this, MainActivity.class);
                StopItem temp = (StopItem) parent.getItemAtPosition(position);
                appInfo.putExtra("stopstring", temp.getDescription() + temp.getTitle());
                startActivity(appInfo);
            }
        });

        final Button button = (Button) findViewById(R.id.clear_search);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View parent) {
                EditText textEdit = (EditText) findViewById(R.id.editText);
                textEdit.setText("");
            }
        });


        EditText textEdit = (EditText) findViewById(R.id.editText);

        textEdit.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (stopItemArrayList == null) return;

                int textlength = s.toString().length();
                array_sort.clear();
                for (int i = 0; i < stopItemArrayList.size(); i++) {
                    if (textlength <= stopItemArrayList.get(i).getTitle().length()) {
                        if (stopItemArrayList.get(i).getTitle().toString().toLowerCase().contains(s.toString().toLowerCase())) {
                            array_sort.add(stopItemArrayList.get(i));
                        }
                    }
                }

                if (stopItemArrayList.size() > 0)
                    stopsList.setAdapter(new StopAdapter(SavedStopsActivity.this, array_sort));
            }
        });

        SwipeRefreshLayout sw = (SwipeRefreshLayout) findViewById(R.id.swipe_to_refresh);
        sw.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                catnames = readFromFile(SavedStopsActivity.this);
                stopItemArrayList.clear();
                for (String str:catnames
                        ) {
                    stopItemArrayList.add(new StopItem(str.substring(5),str.substring(0,5)));
                }
                stopsList.setAdapter(new StopAdapter(SavedStopsActivity.this, stopItemArrayList));
                SwipeRefreshLayout sw = (SwipeRefreshLayout) findViewById(R.id.swipe_to_refresh);
                sw.setRefreshing(false);
            }
        });

        findViewById(R.id.loading).setVisibility(View.GONE);
        stopsList.setAdapter(new StopAdapter(SavedStopsActivity.this, stopItemArrayList));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main , menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


    // save / read file
    public void writeToFile(Context ctx){
        FileOutputStream stream = null;

        try {
    /* you should declare private and final FILENAME_CITY */
            stream = new FileOutputStream (new File(Environment.getExternalStorageDirectory().getPath() + "/LvivRoutes.txt"));
            ObjectOutputStream dout = new ObjectOutputStream(stream);
            dout.writeObject(catnames);

            dout.flush();
            stream.getFD().sync();
            stream.close();
        }
        catch (IOException e){

        }
    }

    public String [] readFromFile(Context ctx){
        FileInputStream stream = null;
        String [] readBack = {};
        try {
            stream = new FileInputStream (new File(Environment.getExternalStorageDirectory().getPath() + "/LvivRoutes.txt"));
            ObjectInputStream din = new ObjectInputStream(stream);
            try {
                readBack = (String[]) din.readObject();
            }catch (ClassNotFoundException e){}

            stream.close();
        }catch (IOException e) {

        }
        return readBack;

    }


    // added async loading

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
            Toast.makeText(getBaseContext(), "Received!", Toast.LENGTH_LONG).show();
//            catnames = parseJsonToStr(result);
//            stopsList.setAdapter(new ArrayAdapter<String>(SearchActivity.this, android.R.layout.simple_list_item_1, catnames));
            findViewById(R.id.loading).setVisibility(View.GONE);
            stopItemArrayList = parseJsonToStopAdapter(result);
            stopsList.setAdapter(new StopAdapter(SavedStopsActivity.this, stopItemArrayList));
        }
    }

    public String [] parseJsonToStr(String receivedStr){
        ArrayList<String> result = new ArrayList<String>();


        receivedStr = receivedStr.replace("\"[", "[");
        receivedStr = receivedStr.replace("]\"", "]");
        receivedStr = receivedStr.replace("\\\\\\\"", "*");
        receivedStr = receivedStr.replace("\\", "");
        try
        {
            //JSONObject jObject = new JSONObject(receivedStr);
            JSONArray cast = new JSONArray(receivedStr);
            for (int i=0; i<cast.length(); i++) {
                JSONObject jObject = cast.getJSONObject(i);
                String itemRes = "";
                itemRes += jObject.getString("Code")+ "_";

                if(itemRes.length()>5)continue;

                itemRes += jObject.getString("Name");
                result.add(itemRes);
            }
        }catch (JSONException e)
        {
            //
        }

        return result.toArray(new String[result.size()]);
    }

    public ArrayList<StopItem> parseJsonToStopAdapter(String receivedStr){
//        ArrayList<String> result = new ArrayList<String>();
        ArrayList<StopItem> items = new ArrayList<StopItem>();

        receivedStr = receivedStr.replace("\"[", "[");
        receivedStr = receivedStr.replace("]\"", "]");
        receivedStr = receivedStr.replace("\\\\\\\"", "*");
        receivedStr = receivedStr.replace("\\", "");
        try
        {
            //JSONObject jObject = new JSONObject(receivedStr);
            JSONArray cast = new JSONArray(receivedStr);
            for (int i=0; i<cast.length(); i++) {
                JSONObject jObject = cast.getJSONObject(i);
                String itemRes = "";
                itemRes += jObject.getString("Code")+ "_";

                if(itemRes.length()>5)continue;
                items.add(new StopItem(jObject.getString("Name"), itemRes));
//                itemRes += jObject.getString("Name");
//                result.add(itemRes);
            }
        }catch (JSONException e)
        {
            //
        }

        return items;
    }

}
