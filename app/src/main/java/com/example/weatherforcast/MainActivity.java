package com.example.weatherforcast;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weatherforcast.adapter.NoteAdapter;
import com.example.weatherforcast.model.Note;
import com.example.weatherforcast.model.Temp;
import com.example.weatherforcast.model.Weather;
import com.example.weatherforcast.model.main;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.weatherforcast.API.RetrofitClient.getAPIService;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    RecyclerView recyclerView;
    NoteAdapter mAdapter;

    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.tv_temp)
    TextView tvTemp;
    @BindView(R.id.tv_city)
    TextView tvCity;
    @BindView(R.id.img_sun)
    ImageView imgSun;
    @BindView(R.id.tv_icon)
    TextView tvIcon;
    private ArrayList<main> notesList = new ArrayList<>();


    //    Button btn_go;
//    EditText et_citname;
    TextView tvNote;

    LocationManager locationManager;
    double mLatitude = 28.6018425;
    double mLongitude = 77.352016;
    private GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;

    Note note = new Note();
    private String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        tvNote = findViewById(R.id.tv_note);
        tvDate.setTypeface(GlobalElements.getInstance().getAugustSansBold());
        tvTemp.setTypeface(GlobalElements.getInstance().getAugustSansRegular());
        tvCity.setTypeface(GlobalElements.getInstance().getAugustSansRegular());
        tvNote.setTypeface(GlobalElements.getInstance().getAugustSansMedium());

        recyclerView = findViewById(R.id.rv);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        mAdapter = new NoteAdapter(this, notesList);
        recyclerView.setAdapter(mAdapter);
        getNotes();


//        btn_go = findViewById(R.id.btn_go);
//        et_citname = findViewById(R.id.et_cityname);
//


       /* btn_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               *//* CityAdapter mAdapter = new CityAdapter(MainActivity.this, note);
                recyclerView.setAdapter(mAdapter);*//*


            }
        });*/

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        checkForPlayServices();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            showGPSDisabledAlertToUser();
        createLocationRequest();
        mGoogleApiClient.connect();


    }

    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, R.style.Theme_AppCompat_DayNight_Dialog);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it for accurate data?")
                .setCancelable(false)
                .setPositiveButton("Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    public void getNotes() {
        Call<ResponseBody> call = getAPIService().getData();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String json_response = response.body().string();
                    JSONObject json = new JSONObject(json_response);
                    JSONObject city_object = json.getJSONObject("city");
                    JSONArray list_array = json.getJSONArray("list");

                    if (list_array.length() > 0) {
                        for (int i = 0; i < list_array.length(); i++) {
                            JSONObject object = list_array.getJSONObject(i);
                            main note = new main();

                            note.setDate(object.getString("dt"));
                            note.setPressure(object.getString("pressure"));
                            note.setHumidity(object.getString("humidity"));
                            note.setSpeed(object.getString("speed"));
                            note.setDeg(object.getString("deg"));
                            note.setClouds(object.getString("clouds"));

                            JSONObject obj1 = object.getJSONObject("temp");
                            Temp temp = new Temp();
                            temp.setDay(obj1.getString("day"));
                            temp.setMin(obj1.getString("min"));
                            temp.setMax(obj1.getString("max"));
                            temp.setNight(obj1.getString("night"));
                            temp.setEve(obj1.getString("eve"));
                            temp.setMorn(obj1.getString("morn"));
                            note.setTemp(temp);

                            JSONArray obj = object.getJSONArray("weather");
                            JSONObject obj2 = obj.getJSONObject(0);
                            Weather wea = new Weather();
                            wea.setId(obj2.getString("id"));
                            wea.setMain(obj2.getString("main"));
                            wea.setDesc(obj2.getString("description"));
                            wea.setIcon(obj2.getString("icon"));
                            note.setWeather(wea);

                            notesList.add(note);
                        }

                        mAdapter.notifyDataSetChanged();
                    } else {

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                System.out.print("error" + t.getMessage());
            }
        });


    }

    public void getCityWiseData(String city) {
        Call<ResponseBody> call = getAPIService().getCitywiseData("" + city);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String json_response = response.body().string();
                    JSONObject json = new JSONObject(json_response);
                    Note note = new Note();
                    note.setNote(json);
                    String rain_data;
                    if (json.has("rain")) {
                        rain_data = "\nrain : " + json.getJSONObject("rain").getString("3h");
                    } else {
                        rain_data = "";
                    }
                    tvNote.setText("" + json.getJSONArray("weather").
                            getJSONObject(0).getString("description")
                    );
                    tvCity.setText(json.getString("name") + "," + json.getJSONObject("sys").getString("country"));
                    tvDate.setText("Today  " + support.getTodayDate());

                    tvTemp.setText(String.format("%.2f", support.convertFahrenheitToCelcius(Float.parseFloat(
                            json.getJSONObject("main").getString("temp")))) + "" + " ℃");

                    setWeatherIcon(json.getJSONArray("weather").getJSONObject(0).getInt("id"),
                            json.getJSONObject("sys").getLong("sunrise") * 1000,
                            json.getJSONObject("sys").getLong("sunset") * 1000);
                    /* tvNote.setText(
                     *//* "city : " + json.getString("name") +*//*

                            "\n\nWeather : " + json.getJSONArray("weather").getJSONObject(0).getString("description") +

                                    "\nbase :" + json.getString("base") +

                                    *//*   "\nTemp : " + json.getJSONObject("main").getString("temp") +*//*

                                    "\nPressure : " + json.getJSONObject("main").getString("pressure") +
                                    "\nhumidity : " + json.getJSONObject("main").getString("humidity") +
                                    "\ntemp_min : " + json.getJSONObject("main").getString("temp_min") +
                                    "\ntemp_max : " + json.getJSONObject("main").getString("temp_max") +

                                    "\nWind Speed : " + json.getJSONObject("wind").getString("speed") +
                                    "\nWind deg : " + json.getJSONObject("wind").getString("deg") +
                                    rain_data


                    );*/
                    tvNote.setVisibility(View.VISIBLE);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                System.out.print("error" + t.getMessage());
            }
        });


    }

    private void setWeatherIcon(int actualId, long sunrise, long sunset) {
        int id = actualId / 100;
        String icon = "";
        if (actualId == 800) {
            long currentTime = new Date().getTime();
            if (currentTime >= sunrise && currentTime < sunset) {
                icon = this.getString(R.string.weather_sunny);
            } else {
                icon = this.getString(R.string.weather_clear_night);
            }
        } else {
            switch (id) {
                case 2:
                    icon = this.getString(R.string.weather_thunder);
                    break;
                case 3:
                    icon = this.getString(R.string.weather_drizzle);
                    break;
                case 7:
                    icon = this.getString(R.string.weather_foggy);
                    break;
                case 8:
                    icon = this.getString(R.string.weather_cloudy);
                    break;
                case 6:
                    icon = this.getString(R.string.weather_snowy);
                    break;
                case 5:
                    icon = this.getString(R.string.weather_rainy);
                    break;
            }
        }
        tvIcon.setTypeface(GlobalElements.getInstance().getWeather());
        tvIcon.setText(icon);
    }



    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(60000 * 5);
        mLocationRequest.setFastestInterval(60000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    private void checkForPlayServices() {
        int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getBaseContext());
        if (status != ConnectionResult.SUCCESS) {
            int requestCode = 10;
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, status, requestCode);
            dialog.show();
        }
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            mLatitude = mLastLocation.getLatitude();
            mLongitude = mLastLocation.getLongitude();
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = null;
            try {
                //----------------------------------------------------------
                addresses = geocoder.getFromLocation(mLatitude, mLongitude, 1);
                int maxAddressLine = addresses.get(0).getMaxAddressLineIndex();
//            String countryName = addresses.get(0).getAddressLine(maxAddressLine);
                String cityName = addresses.get(0).getLocality();
                String stateName = addresses.get(0).getAdminArea();
                String countryName = addresses.get(0).getCountryName();

                if (GlobalElements.isConnectingToInternet(this)) {
                    getCityWiseData(cityName);
                } else {
                    GlobalElements.showDialog(this);
                }
            } catch (Exception e) {

            }
//            getDoctors(mLastLocation);
        }
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (!checkPremissions()) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        } else {
            getLocation();
        }
    }

    private boolean checkPremissions() {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            return false;
        } else return true;
    }

    private boolean permissionToRecordAccepted = false;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                try {
                    permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (permissionToRecordAccepted)
                        getLocation();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        if (!permissionToRecordAccepted)
            Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }


}
