package com.apptronix.nitkonschedule.teacher.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by DevOpsTrends on 6/28/2017.
 */

public class TestsResponse {

    @SerializedName("results")
    private List<Test> results;

    public List<Test> getResults() {
        return results;
    }
}
