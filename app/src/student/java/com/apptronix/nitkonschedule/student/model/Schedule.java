package com.apptronix.nitkonschedule.student.model;

/**
 * Created by DevOpsTrends on 6/10/2017.
 */
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class Schedule implements Serializable {

    @SerializedName("date")
    private int date;

    @SerializedName("course")
    private String course;

    @SerializedName("time")
    private int time;

    @SerializedName("desc")
    private String description;

    @SerializedName("present")
    private String presentIDs;

    public Schedule(int date, String course, int time, String desc, String presIds){
        this.date=date;
        this.course=course;
        this.time=time;
        this.description=desc;
        this.presentIDs=presIds;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPresentIDs() {
        return presentIDs;
    }

    public void setPresentIDs(String presentIDs) {
        this.presentIDs = presentIDs;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}