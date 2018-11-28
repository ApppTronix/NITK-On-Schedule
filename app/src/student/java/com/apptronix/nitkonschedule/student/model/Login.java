package com.apptronix.nitkonschedule.student.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by DevOpsTrends on 7/11/2017.
 */

public class Login {


    @SerializedName("idToken")
    private String idToken;
    @SerializedName("fcmID")
    private String fcmID;

    public Login(String idToken, String fcmID) {
        this.idToken=idToken;
        this.fcmID=fcmID;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getFcmID() {
        return fcmID;
    }

    public void setFcmID(String fcmID) {
        this.fcmID = fcmID;
    }
}
