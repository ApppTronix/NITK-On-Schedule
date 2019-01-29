package com.apptronix.nitkonschedule.student.service;

import android.util.Log;

import com.apptronix.nitkonschedule.student.model.FcmUpdate;
import com.apptronix.nitkonschedule.student.model.UploadResponse;
import com.apptronix.nitkonschedule.student.model.User;
import com.apptronix.nitkonschedule.rest.ApiClient;
import com.apptronix.nitkonschedule.student.rest.ApiInterface;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONException;

import java.io.IOException;

import okhttp3.MediaType;
import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by DevOpsTrends on 6/21/2017.
 */

public class MyInstanceIdListenerService extends FirebaseInstanceIdService{

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private String message;
    User user;

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        user = new User(this);
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("FCM", "Refreshed token: " + refreshedToken);
        // TODO: Implement this method to send any registration to your app's servers.
        try {
            user.setFcmID(refreshedToken,this);
            sendRegistrationToServer(refreshedToken);

        } catch (JSONException | IOException e) {
            e.printStackTrace();
            Timber.i(e.getMessage());
        }
    }

    private void sendRegistrationToServer(String refreshedToken) throws JSONException, IOException {

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<UploadResponse> callTT = apiService.updateFCM(user.getAccessToken(),new FcmUpdate(refreshedToken));

        try {
            Response<UploadResponse> response = callTT.execute();
            if(response.isSuccessful()){
                if(response.code()==200){
                    String results = response.body().getResults();
                } else if( response.code() == 401) { //bad auth
                    user.setAccessToken(null, this); //reset access token
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
