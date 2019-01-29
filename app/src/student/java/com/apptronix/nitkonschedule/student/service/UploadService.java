package com.apptronix.nitkonschedule.student.service;


import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;

import com.apptronix.nitkonschedule.R;
import com.apptronix.nitkonschedule.student.model.Course;
import com.apptronix.nitkonschedule.student.model.UploadResponse;
import com.apptronix.nitkonschedule.student.model.User;
import com.apptronix.nitkonschedule.rest.ApiClient;
import com.apptronix.nitkonschedule.rest.ProgressRequestBody;
import com.apptronix.nitkonschedule.student.rest.ApiInterface;
import com.apptronix.nitkonschedule.student.ui.MainActivity;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import androidx.annotation.RequiresApi;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by DevOpsTrends on 7/2/2017.
 */

public class UploadService  extends IntentService implements ProgressRequestBody.UploadCallbacks {


    NotificationManager notificationManager;
    Notification.Builder mBuilder;

    int id = 28, imageCount=1; int lastProgress=0;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    static User user;
    static int percentage, attended;

    public UploadService(String name) {
        super(name);
    }

    public UploadService() {
        super("UploadService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Bundle bundle = intent.getBundleExtra("bundle");

        user = new User(this);

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

    @Override
    public void onProgressUpdate(int percentage) {


        if (lastProgress>percentage){
            imageCount++;
        }
        lastProgress=percentage;
        Timber.i("progress update %d",percentage);
        mBuilder.setProgress(100, percentage, false);
        mBuilder.setContentText("Upload " +imageCount+ " of 9");
        notificationManager.notify(id, mBuilder.build());
    }

    @Override
    public void onError() {

        Timber.i("progress error ");
        mBuilder.setContentText("Upload Failed")
                .setProgress(0, 0, false);
        notificationManager.notify(id, mBuilder.build());
    }

    @Override
    public void onFinish() {
        Timber.i("progress finish ");

    }

    private void handleActionEnrollCourse(Bundle bundle) {

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
                    user.setAccessToken(null, this); //reset access token
                    fetchAccessToken();
                }
            }

        } catch (IOException e) {
            EventBus.getDefault().post(new MainActivity.MessageEvent("Failed to add course. Check network."));
            e.printStackTrace();
        }

    }

    private void fetchAccessToken() {

        User user = new User(this);
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
                    user.updateTokens(user.getRefreshToken(),responseString,this);

                }

            } else {

                EventBus.getDefault().post(new MainActivity.MessageEvent("ServerUnreachable"));

            }

        } catch (IOException | JSONException e1) {
            e1.printStackTrace();
            EventBus.getDefault().post(new MainActivity.MessageEvent("ServerUnreachable"));
        }
    }


    private void uploadFaceImages(String[] filePaths) {


        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        mBuilder= new Notification.Builder(this,"UploadNotifications");
        mBuilder.setContentTitle("Face Image Upload")
                .setContentText("Upload 1 of 9")
                .setSmallIcon(R.drawable.ic_face_black_24dp);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String CHANNEL_ID = "my_channel_01";// The id of the channel.
            CharSequence name = getString(R.string.upload_notifications_channel);// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationManager.createNotificationChannel(mChannel);
            mBuilder.setChannelId(CHANNEL_ID);
        }
        notificationManager.notify(id,mBuilder.build());
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        // Map is used to multipart the file using okhttp3.RequestBody
        // Multiple Images
        for (int i = 0; i < 9; i++) {
            File file = new File(filePaths[i]);
            //File file = new File("/storage/emulated/0/Android/data/com.apptronix.nitkonschedule.student/files/Pictures/JPEG_pos20181127_122158_388347886.jpg");

            ProgressRequestBody fileBody = new ProgressRequestBody(file, this);
            MultipartBody.Part part = MultipartBody.Part.createFormData("file[]", file.getName(), fileBody);
            builder.addPart(part);
        }


        Timber.i("starting upload images face");

        MultipartBody requestBody = builder.build();
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<UploadResponse> call = apiService.uploadFaceImages(user.getAccessToken(),requestBody.parts());
        try {
            Response<UploadResponse> response = call.execute();
            if(response.isSuccessful()){
                if(response.code()==200){
                    if(response.body().getResults().equals("success")){
                        mBuilder.setContentText("Upload complete")
                                .setProgress(0, 0, false);
                        notificationManager.notify(id, mBuilder.build());
                        EventBus.getDefault().post(new MainActivity.MessageEvent("Faces Uploaded"));
                    } else { //display course not found
                        EventBus.getDefault().post(new MainActivity.MessageEvent("Face Upload Failed"));
                    }
                } else if( response.code() == 401) { //bad auth
                    EventBus.getDefault().post(new MainActivity.MessageEvent("Could not authenticate"));
                    user.setAccessToken(null, this); //reset access token
                    fetchAccessToken();
                }
            } else {
                Timber.i("response %s", response.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
            EventBus.getDefault().post(new MainActivity.MessageEvent("ServerUnreachable"));
        }


    }
}