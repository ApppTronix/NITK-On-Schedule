package com.apptronix.nitkonschedule.teacher.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by DevOpsTrends on 6/10/2017.
 */

public class ScheduleList {

    @SerializedName("results")
    private List<Schedule> results;

    public List<Schedule> getResults() {
        return results;
    }

}
