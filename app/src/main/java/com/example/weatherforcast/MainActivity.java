package com.example.weatherforcast;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.weatherforcast.adapter.NoteAdapter;
import com.example.weatherforcast.model.Note;
import com.example.weatherforcast.model.Temp;
import com.example.weatherforcast.model.Weather;
import com.example.weatherforcast.model.main;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.weatherforcast.API.RetrofitClient.getAPIService;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    NoteAdapter mAdapter;
    private ArrayList<main> notesList = new ArrayList<>();


    Button btn_go;
    EditText et_citname;
    TextView tvNote;

    Note note = new Note();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.rv);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        mAdapter = new NoteAdapter(this, notesList);
        recyclerView.setAdapter(mAdapter);
        getNotes();


        btn_go = findViewById(R.id.btn_go);
        et_citname = findViewById(R.id.et_cityname);
        tvNote = findViewById(R.id.tv_note);


        btn_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /* CityAdapter mAdapter = new CityAdapter(MainActivity.this, note);
                recyclerView.setAdapter(mAdapter);*/
                getCityWiseData();

            }
        });
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
    public void getCityWiseData() {
        Call<ResponseBody> call = getAPIService().getCitywiseData("" + et_citname.getText().toString());

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
                    tvNote.setText(
                            "city : " + json.getString("name") +

                                    "\n\nWeather : " + json.getJSONArray("weather").getJSONObject(0).getString("description") +

                                    "\nbase :" + json.getString("base") +

                                    "\nTemp : " + json.getJSONObject("main").getString("temp") +

                                    "\nPressure : " + json.getJSONObject("main").getString("pressure") +
                                    "\nhumidity : " + json.getJSONObject("main").getString("humidity") +
                                    "\ntemp_min : " + json.getJSONObject("main").getString("temp_min") +
                                    "\ntemp_max : " + json.getJSONObject("main").getString("temp_max") +

                                    "\nWind Speed : " + json.getJSONObject("wind").getString("speed") +
                                    "\nWind deg : " + json.getJSONObject("wind").getString("deg") +
                                    rain_data


                    );
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
        if (id == R.id.refresh_icon) {
            Intent intent = new Intent(MainActivity.this, DaysActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


}
