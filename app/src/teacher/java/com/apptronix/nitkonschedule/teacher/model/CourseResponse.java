package com.apptronix.nitkonschedule.teacher.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Maha Perriyava on 4/5/2018.
 */

public class CourseResponse {
    @SerializedName("results")
    private List<Course> results;

    public List<Course> getResults() {
        return results;
    }
}
