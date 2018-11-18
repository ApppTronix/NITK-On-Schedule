package com.apptronix.nitkonschedule.model;

import com.google.gson.annotations.SerializedName;

public class SingleMarkedStudent {


    @SerializedName("collID")
    private String collId;
    @SerializedName("present")
    private Boolean present;

    public String getCollId() {
        return collId;
    }

    public void setCollId(String collId) {
        this.collId = collId;
    }

    public Boolean getPresent() {
        return present;
    }

    public void setPresent(Boolean present) {
        this.present = present;
    }

}
