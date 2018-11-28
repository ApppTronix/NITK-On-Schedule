package com.apptronix.nitkonschedule.teacher.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by DevOpsTrends on 7/9/2017.
 */

public class FcmUpdate {
    @SerializedName("fcmId")
    private String fcmID;

    public FcmUpdate(String fcmID) {
        this.fcmID = fcmID;
    }

}
