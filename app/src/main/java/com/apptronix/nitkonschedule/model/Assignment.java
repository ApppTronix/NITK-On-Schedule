package com.apptronix.nitkonschedule.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by DevOpsTrends on 6/18/2017.
 */
public class Assignment implements Serializable{

    @SerializedName("maxScore")
    private int maxScore;
    @SerializedName("submissionDate")
    private int submissionDate;
    @SerializedName("course")
    private String course;
    @SerializedName("weightage")
    private int weightage;
    @SerializedName("title")
    private String title;
    @SerializedName("description")
    private String description;

    public Assignment(String title, String description, String course, int submissionDate, int weightage,int maxScore) {
        this.submissionDate=submissionDate;
        this.course=course;
        this.weightage=weightage;
        this.title=title;
        this.description=description;
        this.maxScore=maxScore;
    }
    public int getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(int submissionDate) {
        this.submissionDate = submissionDate;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public int getWeightage() {
        return weightage;
    }

    public void setWeightage(int weightage) {
        this.weightage = weightage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
