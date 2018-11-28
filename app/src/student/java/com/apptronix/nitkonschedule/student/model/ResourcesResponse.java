package com.apptronix.nitkonschedule.student.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResourcesResponse {

    @SerializedName("result")
    private List<com.apptronix.nitkonschedule.student.model.ResourceModel> results;

    public List<com.apptronix.nitkonschedule.student.model.ResourceModel> getResults() {
        return results;
    }

    public void setResults(List<com.apptronix.nitkonschedule.student.model.ResourceModel> results) {
        this.results = results;
    }
}

