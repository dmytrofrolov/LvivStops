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

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;

public class MainActivity extends Activity {

    TextView etResponse;
	TextView tvIsConnected;
	ListView stopsList;
	String[] catnames;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		// get reference to the views
		etResponse = (TextView) findViewById(R.id.etResponse);
        etResponse.setFocusable(false);


		tvIsConnected = (TextView) findViewById(R.id.tvIsConnected);
		stopsList = (ListView) findViewById(R.id.listView);

		// определяем массив типа String
		Bundle recdData = getIntent().getExtras();
		String myVal = recdData.getString("stopstring");


		catnames = new String[] {
			"LET"+myVal,
            "LAD"+myVal
		};

// используем адаптер данных
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, catnames);

		stopsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
									long id)
			{
				String item = (String) parent.getItemAtPosition(position);
                String wayId = "";
				String wayType = item.substring(0, 3);
				String loadUrl = "";
                wayId = item.substring(3, 7);
                loadUrl = "http://82.207.107.126:13541/SimpleRIDE/"+wayType+"/SM.WebApi/api/stops?code="+wayId;
                Log.d("LoadURL : ", loadUrl);
                new HttpAsyncTask().execute(loadUrl);
			}
		});

		stopsList.setAdapter(adapter);


		// check if you are connected or not
		if(isConnected()){
			tvIsConnected.setBackgroundColor(0xFF00CC00);
			tvIsConnected.setText("You are connected");
        }
		else{
			tvIsConnected.setText("You are NOT connected");
		}
			
		// show response on the EditText etResponse 
		//etResponse.setText(GET("http://hmkcode.com/examples/index.php"));
		
		// call AsynTask to perform network operation on separate thread

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
        	Toast.makeText(getBaseContext(), "Received!", Toast.LENGTH_LONG).show();
        	result = parseJsonToStr(result);
            etResponse.setText(result);
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

	
}
