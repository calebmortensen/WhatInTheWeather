package com.calebmortensen.whatintheweather;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;

import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, LocationListener {

    //import android.support.design.widget.Snackbar;

    boolean isInternetConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo !=null && networkInfo.isConnected();
    }


    // 16Day API KEY:  d4034489aaa3556b4446ecc1b22468bc
    private WebView webView;
    private static final String INTELLICAST_URL = "http://images.intellicast.com/WxImages/CurrentConditions/world.jpg";
    private static final String GOOGLE_URL = "https://www.google.com";
    // LocationListener for Latititude & Longitude Geo Coordinates
    LocationManager locationManager;

    double latitude;
    double longitude;

    // Both from Volley
    StringRequest stringRequest;
    RequestQueue requestQueue;

    // This button calls the action to move to the next Activity
    Button projectedActivityButton;
    Button refreshMainButton;
    Button urlButton;

    void initViews(){
        /*projectedActivityButton = (Button)findViewById(R.id.projectedActivityButton);
        projectedActivityButton.setOnClickListener(this);
        refreshMainButton = (Button)findViewById(R.id.refreshMainButton);
        refreshMainButton.setOnClickListener(this);*/
        getSupportActionBar().setTitle("WhatInTheWeather");

        /*urlButton = (Button)findViewById(R.id.urlButton);
        urlButton.setOnClickListener(this);*/

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);






        if(isInternetConnected()){

        }else{
            Toast.makeText(this,"Check Internet Connection",Toast.LENGTH_LONG).show();
        }
    }

    // Use ButterKnife to Inject data to views
    @BindView(R.id.cityTextView)
    TextView cityTextView;
    @BindView(R.id.countryTextView)
    TextView countryTextView;
    @BindView(R.id.dateTextView)
    TextView dateTextView;
    @BindView(R.id.descriptionTextView)
    TextView descriptionTextView;
    @BindView(R.id.humidityTextView)
    TextView humidityTextView;
    @BindView(R.id.latitudeTextView)
    TextView latitudeTextView;
    @BindView(R.id.longitudeTextView)
    TextView longitudeTextView;
    @BindView(R.id.temperatureTextView)
    TextView temperatureTextView;
    @BindView(R.id.windTextView)
    TextView windTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#4DB6AC")));

        /*fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initViews();
        ButterKnife.bind(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        GPSTracker gpsTracker;
        gpsTracker = new GPSTracker(this);
        latitude = gpsTracker.getLatitude();
        longitude = gpsTracker.getLongitude();
        /*System.out.println(latitude);
        System.out.println(longitude);*/

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        //Initialize the request queue
        requestQueue = Volley.newRequestQueue(this);


        webView = (WebView)findViewById(R.id.webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        String mStringUrl;
        // mStringUrl = "https://www.nasa.gov/sites/default/files/1-bluemarble_west.jpg";
        // mStringUrl ="https://en.wikipedia.org/wiki/The_Blue_Marble#/media/File:The_Earth_seen_from_Apollo_17.jpg";
        mStringUrl = "https://pixabay.com/photo-1617121/";
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(mStringUrl);
       // webView.getSettings().setBuiltInZoomControls(true);
        //webView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
        //webView.setBackgroundColor(0);
        //webView.setBackgroundResource(android.R.color.holo_green_light);

        fetchWeather();


    }


    private void fetchWeather(){

        // 5 DAY FREE API KEY:  819b4b03d2ae6a67c87478ee001dc0a0 (Use for CURRENT Location in MAIN Activity)
        // 16 DAY ($40/month) API KEY: d4034489aaa3556b4446ecc1b22468bc  (Use for 10 Day Weather Forecast in FUTURE Activity)

        String url = "http://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&appid=819b4b03d2ae6a67c87478ee001dc0a0";

        stringRequest = new StringRequest(Request.Method.GET, url, new com.android.volley.Response.Listener<String>() {

            @Override
            public void onResponse(String response) {



                if( response != null) {
                    // This is for a SINGLE OBJECT {} in the JSON

                    try {
                        JSONObject jsonObject = new JSONObject(response.toString());
                        //Weather Array
                        JSONArray weatherArray = jsonObject.getJSONArray("weather");
                        // System.out.println(weatherArray);
                        String description = weatherArray.getJSONObject(0).get("description").toString();
                        //City Name
                        String name = jsonObject.getString("name");
                        // Inner Sys Object for Country string
                        String country = jsonObject.getJSONObject("sys").getString("country");
                        // Date String
                        String dt = jsonObject.getString("dt");
                        // Inner Main Object for Humidity string
                        String humidity = jsonObject.getJSONObject("main").getString("humidity");
                        // Temp
                        String temp = jsonObject.getJSONObject("main").getString("temp");
                        // Inner Coord Object Lat/Lon
                        String lat = jsonObject.getJSONObject("coord").getString("lat");
                        String lon = jsonObject.getJSONObject("coord").getString("lon");
                        // Wind
                        String speed= jsonObject.getJSONObject("wind").getString("speed");

                        // WeatherInfo for city(name) & date -values in root of JSON
                        cityTextView.setText(String.valueOf(name));
                        //First convert STRING to LONG
                        long initial = Long.parseLong(dt);
                        // Next, since java.date.util interprets in Milliseconds and WeatherAPI.org uses EPOCH time in Seconds
                        long converted = initial * 1000;
                        // Format the string: EX:  Tue Dec 12 18:07:13 PST 2017
                        Date convertString = new Date(converted);
                        //System.out.println(convertString);
                        // NOTE: This code was developed using a Samsung S3 on API 18 - thus, SimpleDateFormat is not used/supported
                        String dayString = convertString.toString();
                        String dateString = dayString.substring( 0, 11 );
                        //System.out.println(dateString);
                        String yearString = dayString.substring( 24, 28 );
                        //System.out.println(yearString);
                        String finalString = dateString + yearString;


                        dateTextView.setText(String.valueOf(finalString));

                        //Coord
                        latitudeTextView.setText(String.valueOf(lat));
                        longitudeTextView.setText(String.valueOf(lon));
                        //Sys
                        countryTextView.setText(String.valueOf(country));

                        //Weather - This is the ONLY JSON returned as type Array
                        descriptionTextView.setText(String.valueOf(description));
                        //System.out.println(temp);
                        //Main
                        double kelvin = Double.parseDouble(temp);
                        double fahrenheit = Math.round( 100.0 *((kelvin * (9.0/5.0)) - 459.67)) / 100.0;

                        //System.out.println(fahrenheit);

                        temperatureTextView.setText(String.valueOf(fahrenheit));
                        humidityTextView.setText(String.valueOf(humidity));

                        //Wind
                        windTextView.setText(String.valueOf(speed));


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }




                }



            }
        },new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(stringRequest);
    }





    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.refresh) {
            Intent currentActivity = getIntent();
            finish();
            startActivity(currentActivity);
        } else if(id == R.id.world){
            webView.loadUrl(INTELLICAST_URL);
        } else if(id == R.id.google) {
            webView.loadUrl(GOOGLE_URL);
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void onClick(View v) {

        switch(v.getId()){

                case R.id.fab:
                Intent intent = new Intent("com.calebmortensen.whatintheweather.futureactivity");
                startActivity(intent);
                break;


            default:
                break;

        }
    }

    @Override
    public void onLocationChanged(Location location) {

        // Geographical Coordinates
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        // Once Location is fetched, remove the updates
        locationManager.removeUpdates(this);


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
