package com.apptronix.nitkonschedule.student.service;


import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;

import com.apptronix.nitkonschedule.student.service.DBSyncTask;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import timber.log.Timber;

/**
 * Created by DevOpsTrends on 7/2/2017.
 */

public class UploadService  extends IntentService{


    public UploadService(String name) {
        super(name);
    }

    public UploadService() {
        super("UploadService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        UploadTask.sendData(getApplicationContext(),intent.getBundleExtra("bundle"));
    }
}