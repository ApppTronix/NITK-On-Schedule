package com.apptronix.nitkonschedule.student.service;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;

import com.apptronix.nitkonschedule.student.service.DBSyncTask;
/**
 * Created by DevOpsTrends on 7/2/2017.
 */

public class DbSyncService  extends IntentService {


    public DbSyncService(String name) {
        super(name);
    }

    public DbSyncService() {
        super("DbSyncService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        DBSyncTask.syncData(getApplicationContext());
    }
}
