package com.apptronix.nitkonschedule.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AttendanceImageResponse {


    @SerializedName("data")
    private List<SingleMarkedStudent> data;

    @SerializedName("result")
    private String result;

    public List<SingleMarkedStudent> getData() {
        return data;
    }

    public void setData(List<SingleMarkedStudent> results) {
        this.data = results;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
