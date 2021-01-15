package com.example.hw82;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    // Here we create all our variables before on create
    private LatLng userLatLong = new LatLng(26.0765,-80.2521);
    private GoogleMap myMap;
    private MapView mapView;
    private TextView location, temperature;
    private EditText user_location;
    private ImageView search, gps, speech;
    private String starter_location ="Miami";
    //SPTT REQ_CODE
    private final int REQ_CODE = 100;
    //Request Queue Object / Volley Stuff
    private RequestQueue myQueue;
    //I'll go ahead and create an object of text to speech
    private TextToSpeech text_to_speech;
    //WEATHER API STUFF---------------------------------------------------------------
    private final String URL = "https://api.openweathermap.org/data/2.5/weather?q=";
    private String API_KEY = "&appid=4c474abc64a9c28d276b86d0f68f630a";
    //---------------------------------------------------------------------------------


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Bind our UI-------------------------------------------------
        search = findViewById(R.id.search);
        speech = findViewById(R.id.speech);
        temperature = findViewById(R.id.temp);
        location = findViewById(R.id.location_view);
        user_location = findViewById(R.id.user_location);
        mapView = findViewById(R.id.mapView);
        //-----------------------------------------------------------


        //Here we have our second button, the speech listener so that our user can press and have their voice translated to text
        myQueue = Volley.newRequestQueue(this);
        speech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View Z) {
                //I call my method SPTT so that we can parse and populate our android widgets from earlier
                SPTT();
                // This calls my method API_URL which populates the URL
                String url = API_URL();
                //We call JSONParse so the user doesn't have to press anything to get the information after speaking
                JSONParse(url);
                setText_to_speech();


            }
        });
        //-----------------------------------------------------------


        //Start our queue so that we can start out GET request and also listen for a onclicker
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = API_URL();
                JSONParse(url);
                setText_to_speech();

            }
        }) ;


        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        mapView.onResume();
        mapView.setVisibility(View.VISIBLE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    user_location.setText((String)result.get(0));
                }
                break;
            }
        }
    }

    public void SPTT(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Need to speak");
        try {
            startActivityForResult(intent, REQ_CODE);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), "Sorry not supported", Toast.LENGTH_SHORT).show();
        }
    }

    public void JSONParse(String url){
        StringRequest request = new StringRequest(Request.Method.GET, url,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    final JSONObject currentWeather = new JSONObject(response);
                    final JSONArray weather = currentWeather.getJSONArray("weather");
                    final JSONObject condition = weather.getJSONObject(0);
                    final String locationnamee = currentWeather.getString("name");
                    final double Lon = currentWeather.getJSONObject("coord").getDouble("lon");
                    final double Lat = currentWeather.getJSONObject("coord").getDouble("lat");
                    userLatLong = getMyLocation(Lon,Lat);

                    final double tempWeather = currentWeather.getJSONObject("main").getDouble("temp") * 9/5 - 459.67;
                    final String tempWeatherString = String.valueOf(String.format("%.2f",tempWeather)+" Fahrenheit");
                    temperature.setText(tempWeatherString);
                    location.setText(locationnamee);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        myQueue.add(request);
    }

    public String API_URL(){
        starter_location = user_location.getText().toString();
        final String url = URL+starter_location+API_KEY;
        return url;
    }

    public void setText_to_speech (){
        //setContentView(R.layout.activity_main);
        text_to_speech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

            if (status == TextToSpeech.SUCCESS){
                text_to_speech.setLanguage(Locale.CANADA);
                String let_him_speak = temperature.getText().toString();
                Toast.makeText(getApplicationContext(),let_him_speak,Toast.LENGTH_SHORT).show();
                text_to_speech.speak(let_him_speak,TextToSpeech.QUEUE_FLUSH,null);

            }

            }
        });
    }
    public void onPause(){
        if(text_to_speech != null){
            text_to_speech.stop();
            text_to_speech.shutdown();

        }
        super.onPause();
    }

    public LatLng getMyLocation(double lon, double lat){
    LatLng getMyLocationAPI = new LatLng(lon,lat);
    return getMyLocationAPI;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;
        LatLng hello = userLatLong;
        myMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        myMap.moveCamera(CameraUpdateFactory.newLatLng(hello));
        myMap.addMarker(new MarkerOptions().position(hello));
        myMap.moveCamera(CameraUpdateFactory.newLatLng(hello));
        myMap.moveCamera(CameraUpdateFactory.zoomTo(17));

    }
}

