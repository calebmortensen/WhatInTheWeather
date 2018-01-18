package com.calebmortensen.whatintheweather;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

public class FutureActivity extends AppCompatActivity implements LocationListener {

    WeatherAdapter weatherAdapter;

    StringBuffer response;

    ArrayList<Future> futurelist;

    ListView listView;
    ArrayAdapter<String> adapter;  //Pre defined adapter API. Customizable

    double latitude;
    double longitude;

    // LocationListener for Latititude & Longitude Geo Coordinates
    LocationManager locationManager;

    void initViews(){
        listView = (ListView)findViewById(R.id.futureListView);

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);

        response = new StringBuffer();

        futurelist = new ArrayList<>();

        if(isInternetConencted()){
            // new FutureActivity.RetrieveFutureThread().start();    // THIS IS WHERE YOU CALL YOUR URL RETRIEVAL THREAD in Parallet to MainActivty
            new RetrieveTask().execute();
        }else{
            Toast.makeText(this, "Turn on internet",Toast.LENGTH_LONG).show();
        }
    }

    boolean isInternetConencted(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo !=null && networkInfo.isConnected();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_future);
        getSupportActionBar().setTitle("10 Day Local Weather Forecast");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2da64c")));
        initViews();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        GPSTracker gpsTracker;
        gpsTracker = new GPSTracker(this);
        latitude = gpsTracker.getLatitude();
        longitude = gpsTracker.getLongitude();
        /*System.out.println("FUTURE LAT");
        System.out.println("FUTURE LONG");*/


    }



    // Android Thread
    class RetrieveTask extends AsyncTask{

        //UI Thread
        @Override
        protected void onPreExecute() {
            //Iniitializations
        }

        // NON UI Thread
        @Override
        protected Object doInBackground(Object[] objects) {



            try{
                // This URL will obtain 10 days worth of data with a paid $40 subscription using NEW API Key   &cnt=10 is for DAY COUNT
                URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?lat=" + latitude + "&lon=" + longitude + "&cnt=10&appid=d4034489aaa3556b4446ecc1b22468bc");
                // If the 10 day URL is unreachable, use the 5 day FREE API url
                //URL fiveDay = new URL("http://api.openweathermap.org/data/2.5/forecast?lat=" + latitude + "&lon=" + longitude + "&appid=819b4b03d2ae6a67c87478ee001dc0a0");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();  //Request to the Server

                InputStream inputStream = connection.getInputStream();  // Response from the server

                InputStreamReader reader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(reader);
                String line = "";
                while((line = bufferedReader.readLine()) != null ){
                    response.append(line);  //response variable is my StringBuffer
                }

            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }




        //UI Thread
        @Override
        protected void onPostExecute(Object o) {
            parseJSONResponse();
        }
    }


    void parseJSONResponse(){

        try{

            //From response you create JSONObject - the root of the forecast array list is "list"
            // This holds response to string as an object
            JSONObject jsonObject = new JSONObject(response.toString());
            //This takes the response and assigns the "list" array
            JSONArray jsonArray = jsonObject.getJSONArray("list");



            int length = jsonArray.length();
            // First Epoch dt is Today, so start counting at 2 to start output for Tomorrow
            for(int i=0;i<length;i++){
                JSONObject jObj = jsonArray.getJSONObject(i);

                // Create a new object of the Projected Weather class being (Future)
                Future future = new Future();

                // System.out.println(jObj);  // This outputs 40 objects from "list"   40/8 =  ***5*** days worth of DATA
                //  Toast.makeText(this,jObj.getString("dt"), Toast.LENGTH_LONG).show();

                // Get INNER ARRAY weather
                JSONArray weatherArray = jObj.getJSONArray("weather");
                int size = weatherArray.length();
                ArrayList<String> weather = new ArrayList<>();

                for(int w=0; w<size; w++){
                    // This outputs 4 key value pairs for ARRAY
                    //System.out.println(weatherArray)  -(id,main,description, and icon)
                    JSONObject weatherJSON = weatherArray.getJSONObject(w);
                    // This outputs 4 key value pairs for OBJECT
                    //System.out.println(weatherJSON);
                    weather.add(weatherJSON.optString("description"));
                    // System.out.println(weather);
                    // This outputs a single description -//once you create this weather object, you can create a data structure of ArrayList
                    // Takes class Ojbect and references JSON string Key
                    future.setDescription(weatherJSON.getString("description"));

                    String dateTime = jObj.getString("dt");
                    for(int Y=0; Y<size; Y++){
                        long initial = Long.parseLong(dateTime);
                        long converted = initial * 1000;
                        Date convertString = new Date(converted);
                        String dayString = convertString.toString();
                        String dateString = dayString.substring( 0, 11 );
                        String yearString = dayString.substring( 24, 28 );
                        String finalString = dateString + yearString;
                        // System.out.println(finalString);

                        future.setDt(finalString);
                    }


                    // This sets the Date/Time from the Object in the root of "list" Array
                    //future.setDt(jObj.getString("dt"));


                    //All weather will be added in this ArrayList (You can customize the adapter)
                    futurelist.add(future);
                    // System.out.println(futurelist);
                    //  adapter.add(weatherJSON.getString("description"));

                }

            }
            weatherAdapter = new WeatherAdapter(this, R.layout.list_item,futurelist);
            listView.setAdapter(weatherAdapter);
            //   listView.setAdapter(adapter); // Dynamic Data on ListView

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}

