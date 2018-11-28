package com.apptronix.nitkonschedule.student.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Maha Perriyava on 4/5/2018.
 */

public class ResourceModel implements Serializable {

    @SerializedName("resources")
    private List<String> resources;

    @SerializedName("courseCode")
    private String course;

    public ResourceModel(List<String> resources, String course){
        this.resources=resources;
        this.course=course;
    }

    public List<String> getResources() {
        return resources;
    }

    public void setResources(List<String> resources) {
        this.resources = resources;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }
}
