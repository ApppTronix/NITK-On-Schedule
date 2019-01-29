package com.apptronix.nitkonschedule.student.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by DevOpsTrends on 7/4/2017.
 */

public class UploadResponse {

    @SerializedName("result")
    private String result;

    public String getResults() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
