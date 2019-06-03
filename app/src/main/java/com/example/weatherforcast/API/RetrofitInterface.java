package com.example.weatherforcast.API;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitInterface {

    @GET("2.5/forecast/daily?id=524901&cnt=14&APPID=" + RetrofitClient.APIKEY)
    Call<ResponseBody> getData();


    @GET("2.5/weather?&APPID=" + RetrofitClient.APIKEY)
    Call<ResponseBody> getCitywiseData(@Query("q") String q);


}
