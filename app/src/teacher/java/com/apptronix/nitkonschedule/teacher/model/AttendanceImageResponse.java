package com.apptronix.nitkonschedule.teacher.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AttendanceImageResponse {


    @SerializedName("results")
    private List<SingleMarkedStudent> results;

    public List<SingleMarkedStudent> getResults() {
        return results;
    }

}
