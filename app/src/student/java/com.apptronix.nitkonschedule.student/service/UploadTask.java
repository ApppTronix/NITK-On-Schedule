package com.apptronix.nitkonschedule.student.service;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.apptronix.nitkonschedule.model.Assignment;
import com.apptronix.nitkonschedule.model.AssignmentResponse;
import com.apptronix.nitkonschedule.model.Course;
import com.apptronix.nitkonschedule.model.Schedule;
import com.apptronix.nitkonschedule.model.ScheduleList;
import com.apptronix.nitkonschedule.model.Test;
import com.apptronix.nitkonschedule.model.TestsResponse;
import com.apptronix.nitkonschedule.model.UploadResponse;
import com.apptronix.nitkonschedule.model.User;
import com.apptronix.nitkonschedule.rest.ApiClient;
import com.apptronix.nitkonschedule.student.data.DBContract;
import com.apptronix.nitkonschedule.student.rest.ApiInterface;
import com.apptronix.nitkonschedule.student.ui.MainActivity;
import com.apptronix.nitkonschedule.student.widget.TimeTableCollectionWidget;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import com.apptronix.nitkonschedule.model.Schedule;
import com.apptronix.nitkonschedule.model.ScheduleList;
import com.apptronix.nitkonschedule.student.data.DBContract;
import com.apptronix.nitkonschedule.model.Assignment;
import com.apptronix.nitkonschedule.model.AssignmentResponse;
import com.apptronix.nitkonschedule.model.Test;
import com.apptronix.nitkonschedule.model.TestsResponse;
import com.apptronix.nitkonschedule.model.User;
import com.apptronix.nitkonschedule.rest.ApiClient;
import com.apptronix.nitkonschedule.student.rest.ApiInterface;
import com.apptronix.nitkonschedule.student.ui.MainActivity;
import com.apptronix.nitkonschedule.student.widget.TimeTableCollectionWidget;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by DevOpsTrends on 7/2/2017.
 */

public class UploadTask {

    private static Context mContext;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    static User user;
    static int percentage, attended;

    synchronized public static void sendData(Context context, Bundle bundle) {

        mContext=context;
        user = new User(mContext);

        Timber.i("upload started %s",bundle.getString("content"));
        fetchAccessToken();
        switch (bundle.getString("content")){
            case "enrollCourse":{
                handleActionEnrollCourse(bundle);
                break;
            }
            case "uploadFaces":{
                uploadFaceImages(bundle.getStringArray("filePaths"));
                break;
            }
        }
    }



    private static void handleActionEnrollCourse(Bundle bundle) {

        Course course = (Course) bundle.getSerializable("parcel");
        Timber.i(user.getAccessToken());
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<UploadResponse> callTT = apiService.enrollCourse(user.getAccessToken(),course);

        try {
            Response<UploadResponse> response = callTT.execute();
            if(response.isSuccessful()){
                if(response.code()==200){
                    if(response.body().getResults().equals("course enrolled")){ //add course to db
                        EventBus.getDefault().post(new MainActivity.MessageEvent("Course enrolled"));
                    } else { //display course not found
                        EventBus.getDefault().post(new MainActivity.MessageEvent("Course not found"));
                    }
                } else if( response.code() == 401) { //bad auth
                    EventBus.getDefault().post(new MainActivity.MessageEvent("Could not authenticate"));
                    user.setAccessToken(null, mContext); //reset access token
                    fetchAccessToken();
                }
            }

        } catch (IOException e) {
            EventBus.getDefault().post(new MainActivity.MessageEvent("Failed to add course. Check network."));
            e.printStackTrace();
        }

    }

    private static void fetchAccessToken() {

        User user = new User(mContext);
        String refreshToken = user.getRefreshToken();
        Timber.i("sending refresh token %s to server",refreshToken);

        try {

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(30, TimeUnit.SECONDS);
            builder.readTimeout(30, TimeUnit.SECONDS);
            builder.writeTimeout(30, TimeUnit.SECONDS);
            OkHttpClient client = builder.build();

            JSONObject postJSON = new JSONObject();
            postJSON.put("refreshToken",refreshToken);
            RequestBody body = RequestBody.create(JSON, String.valueOf(postJSON));
            Request request = new Request.Builder()
                    .url(new URL(ApiClient.BASE_URL+"getAccessToken"))
                    .post(body)
                    .build();


            okhttp3.Response response = client.newCall(request).execute();

            if(response.isSuccessful()){

                String responseString = response.body().string();
                Timber.i("AccessToken Response is %s",responseString);

                if(responseString.equals("fail")){

                    EventBus.getDefault().post(new MainActivity.MessageEvent("TokenUpdateRefused"));

                } else {

                    //registration, received refresh token
                    user.updateTokens(user.getRefreshToken(),responseString,mContext);

                }

            } else {

                EventBus.getDefault().post(new MainActivity.MessageEvent("ServerUnreachable"));

            }

        } catch (IOException | JSONException e1) {
            e1.printStackTrace();
            EventBus.getDefault().post(new MainActivity.MessageEvent("ServerUnreachable"));
        }
    }
    private static void uploadFaceImages(String[] filePaths) {

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);

        // Map is used to multipart the file using okhttp3.RequestBody
        // Multiple Images
        for (int i = 0; i < 9; i++) {
            File file = new File(filePaths[i]);
            builder.addFormDataPart("file[]", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file));
        }


        MultipartBody requestBody = builder.build();
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<UploadResponse> call = apiService.uploadFaceImages(user.getAccessToken(),requestBody,filePaths);
        call.enqueue(new Callback<UploadResponse>() {
            @Override
            public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                if(response.isSuccessful()){
                    if(response.code()==200){
                        if(response.body().getResults().equals("successful")){
                            EventBus.getDefault().post(new MainActivity.MessageEvent("Faces Uploaded"));
                        } else { //display course not found
                            EventBus.getDefault().post(new MainActivity.MessageEvent("Face Upload Failed"));
                        }
                    } else if( response.code() == 401) { //bad auth
                        EventBus.getDefault().post(new MainActivity.MessageEvent("Could not authenticate"));
                        user.setAccessToken(null, mContext); //reset access token
                        fetchAccessToken();
                    }
                }

            }

            @Override
            public void onFailure(Call<UploadResponse> call, Throwable t) {

                EventBus.getDefault().post(new MainActivity.MessageEvent("ServerUnreachable"));
            }
        });


    }
}
