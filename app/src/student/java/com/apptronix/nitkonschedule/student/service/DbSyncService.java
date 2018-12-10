package com.apptronix.nitkonschedule.student.service;

import android.app.IntentService;
import android.content.Intent;
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
