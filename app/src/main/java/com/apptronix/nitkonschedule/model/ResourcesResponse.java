package com.apptronix.nitkonschedule.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResourcesResponse {

    @SerializedName("result")
    private List<ResourceModel> results;

    public List<ResourceModel> getResults() {
        return results;
    }

    public void setResults(List<ResourceModel> results) {
        this.results = results;
    }
}

