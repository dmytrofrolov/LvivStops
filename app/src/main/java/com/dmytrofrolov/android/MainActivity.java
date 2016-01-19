package com.dmytrofrolov.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;

public class MainActivity extends AppCompatActivity {

//    TextView etResponse;
	TextView tvIsConnected;
	ListView stopsList;
	ListView listViewScedule;
	String[] catnames;
    String loadUrl = "";

    ArrayList<StopItem> sceduleItemArrayList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

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


		// get reference to the views
//		etResponse = (TextView) findViewById(R.id.etResponse);
//        etResponse.setFocusable(false);
        listViewScedule = (ListView) findViewById(R.id.listViewScedule);
        listViewScedule.setClickable(false);

//		tvIsConnected = (TextView) findViewById(R.id.tvIsConnected);
		stopsList = (ListView) findViewById(R.id.listView);

		// определяем массив типа String
		Bundle recdData = getIntent().getExtras();
        String myVal = "0000_ ";
        if (recdData!=null)
            myVal = recdData.getString("stopstring");



        catnames = new String[] {
			"LET"+myVal,
            "LAD"+myVal
		};

        setTitle(catnames[0].substring(8));

        ArrayList<TransportItem> transportTypeArrayList = new ArrayList<TransportItem>();;
        transportTypeArrayList.add(new TransportItem("Електротранспорт", catnames[0].substring(0,8)));
        transportTypeArrayList.add(new TransportItem("Автобуси", catnames[1].substring(0,8)));

// используем адаптер данных
//		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, catnames);
        stopsList.setAdapter(new TransportRowAdapter(MainActivity.this, transportTypeArrayList));

        stopsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                    long id) {
                TransportItem stopItem = (TransportItem) parent.getItemAtPosition(position);
                String code = stopItem.getCode();
                String wayId = "";
                String wayType = code.substring(0, 3);

                wayId = code.substring(3, 7);
                loadUrl = "http://82.207.107.126:13541/SimpleRIDE/" + wayType + "/SM.WebApi/api/stops?code=" + wayId;
                Log.d("LoadURL : ", loadUrl);
                findViewById(R.id.loading).setVisibility(View.VISIBLE);
                new HttpAsyncTask().execute(loadUrl);
            }
        });



//		// check if you are connected or not
		if(!isConnected()){
            new AlertDialog.Builder(MainActivity.this)
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
//			tvIsConnected.setBackgroundColor(0xFF00CC00);
//			tvIsConnected.setText("You are connected");
//        }
//		else{
//			tvIsConnected.setText("You are NOT connected");
//		}
			
		// show response on the EditText etResponse 
		//etResponse.setText(GET("http://hmkcode.com/examples/index.php"));

        SwipeRefreshLayout sw = (SwipeRefreshLayout) findViewById(R.id.swipe_to_refresh);
        sw.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new HttpAsyncTask().execute(loadUrl);
            }
        });

		// call AsynTask to perform network operation on separate thread
        loadUrl = "http://82.207.107.126:13541/SimpleRIDE/"+"LAD"+"/SM.WebApi/api/stops?code="+myVal.substring(0, 4);
        findViewById(R.id.loading).setVisibility(View.VISIBLE);
        Log.d("LoadURL onStart : ", loadUrl);
        new HttpAsyncTask().execute(loadUrl);

	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.main , menu);

        return super.onCreateOptionsMenu(menu);
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
			Log.d("InputStream", e.getLocalizedMessage());
		}
		
		return result;
	}
	
    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;
        
        inputStream.close();
        return result;
        
    }
	
    public boolean isConnected(){
    	ConnectivityManager connMgr = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
    	    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
    	    if (networkInfo != null && networkInfo.isConnected()) 
    	    	return true;
    	    else
    	    	return false;	
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
            findViewById(R.id.loading).setVisibility(View.GONE);
            ArrayList<SceduleItem> ad = parseJsonToSceduleAdapter(result);
            listViewScedule.setAdapter(new SceduleAdapter(MainActivity.this, ad));

            SwipeRefreshLayout sw = (SwipeRefreshLayout) findViewById(R.id.swipe_to_refresh);
            sw.setRefreshing(false);

//            result = parseJsonToStr(result);
//            etResponse.setText(result);


       }
    }

    public String parseJsonToStr(String receivedStr){
        String result = "";

        receivedStr = receivedStr.replace("\"[", "[");
        receivedStr = receivedStr.replace("]\"", "]");
        receivedStr = receivedStr.replace("\\\\\\\"", "*");
        receivedStr = receivedStr.replace("\\", "");

        try
        {
            JSONArray cast = new JSONArray(receivedStr);
            for (int i=0; i<cast.length(); i++) {
                JSONObject jObject = cast.getJSONObject(i);
                result += jObject.getString("RouteName").replace("ЛАД А", "")+ "_";
                result += "("+String.valueOf(Integer.parseInt(jObject.getString("TimeToPoint"))/60)+" хв)_";
                result += jObject.getString("VehicleName");
                result += "\n\n";
            }
        }catch (JSONException e)
        {

        }

        return result+receivedStr;
    }

    public String [] parseJsonToStrArray(String receivedStr){
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
                itemRes += jObject.getString("RouteName").replace("ЛАД А", "")+ "_";
                itemRes += "("+String.valueOf(Integer.parseInt(jObject.getString("TimeToPoint"))/60)+" хв)_";
                itemRes += jObject.getString("VehicleName");
//                itemRes += "\n\n";
                result.add(itemRes);
            }
        }catch (JSONException e)
        {
            //
        }

        return result.toArray(new String[result.size()]);
    }

    public ArrayList<SceduleItem> parseJsonToSceduleAdapter(String receivedStr){
        ArrayList<SceduleItem> items = new ArrayList<SceduleItem>();

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
                // String waynumber, String time, String waytitle, String busnumber
                items.add(new SceduleItem(
                    "",
                    String.valueOf(Integer.parseInt(jObject.getString("TimeToPoint"))/60)+" хв",
                    jObject.getString("RouteName").replace("ЛАД А", ""),
                    jObject.getString("VehicleName")
                ));

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
