package com.apptronix.nitkonschedule.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by DevOpsTrends on 6/18/2017.
 */

public class AssignmentResponse {

    @SerializedName("results")
    private List<Assignment> results;

    public List<Assignment> getResults() {
        return results;
    }

}

