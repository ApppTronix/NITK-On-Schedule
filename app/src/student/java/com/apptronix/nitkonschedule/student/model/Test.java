package com.apptronix.nitkonschedule.student.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by DevOpsTrends on 6/28/2017.
 */

public class Test implements Serializable {

    @SerializedName("maxScore")
    private int maxScore;

    @SerializedName("score")
    private float score;

    @SerializedName("testDate")
    private int testDate;
    @SerializedName("testTime")
    private int testTime;
    @SerializedName("course")
    private String course;
    @SerializedName("weightage")
    private float weightage;
    @SerializedName("title")
    private String title;
    @SerializedName("portions")
    private String portions;

    public Test(String title, String portions, String course, int testDate, int weightage,int testTime) {
        this.testDate=testDate;
        this.course=course;
        this.weightage=weightage;
        this.title=title;
        this.portions=portions;
        this.testTime=testTime;
    }


    public Test(String title, String portions, String course, int testDate, int weightage,int testTime, float score, int maxScore) {
        this.testDate=testDate;
        this.course=course;
        this.weightage=weightage;
        this.title=title;
        this.portions=portions;
        this.testTime=testTime;
        this.score=score;
        this.maxScore=maxScore;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPortions() {
        return portions;
    }

    public void setPortions(String portions) {
        this.portions = portions;
    }

    public int getWeightage() {
        return (int) weightage;
    }

    public void setWeightage(float weightage) {
        this.weightage = weightage;
    }

    public int getTestDate() {
        return testDate;
    }

    public void setTestDate(int testDate) {
        this.testDate = testDate;
    }

    public int getTestTime() {
        return testTime;
    }

    public void setTestTime(int testTime) {
        this.testTime = testTime;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(int maxScore) {
        this.maxScore = maxScore;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }
}
