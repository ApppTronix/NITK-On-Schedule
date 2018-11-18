package com.apptronix.nitkonschedule.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by DevOpsTrends on 5/26/2017.
 */

public class ApiClient {

    //public static final String BASE_URL = "http://192.168.43.224:6050/";
    public static final String BASE_URL = "http://139.59.44.233:6050/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {

            OkHttpClient okClient = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(okClient)
                    .build();
        }

        return retrofit;
    }

    public static <S> S createService(
            Class<S> serviceClass) {
        return retrofit.create(serviceClass);
    }
}
