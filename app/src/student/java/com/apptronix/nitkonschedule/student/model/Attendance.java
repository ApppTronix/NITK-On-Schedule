package com.apptronix.nitkonschedule.student.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by DevOpsTrends on 6/28/2017.
 */

public class Attendance {

    @SerializedName("date")
    private String date;
    @SerializedName("course")
    private String course;
    @SerializedName("time")
    private String time;
    @SerializedName("presence")
    private String presence;

    public Attendance(String date, String course, String time, String presence) {
        this.date=date;
        this.course=course;
        this.time=time;
        this.presence=presence;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPresence() {
        return presence;
    }

    public void setPresence(String presence) {
        this.presence = presence;
    }
}
