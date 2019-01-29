package com.apptronix.nitkonschedule.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Maha Perriyava on 4/5/2018.
 */

public class Course implements Serializable {

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("regIDs")
    private List<String> enrolled;

    @SerializedName("sem")
    private int sem;

    public Course(String name, String description, List<String> enrolled, int sem){
        this.name=name;
        this.description=description;
        this.enrolled=enrolled;
        this.sem=sem;
    }

    public Course(String name){
        this.name=name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public int getSem() {
        return sem;
    }

    public void setSem(int sem) {
        this.sem = sem;
    }

    public List<String> getEnrolled() {
        return enrolled;
    }

    public void setEnrolled(List<String> enrolled) {
        this.enrolled = enrolled;
    }
}
