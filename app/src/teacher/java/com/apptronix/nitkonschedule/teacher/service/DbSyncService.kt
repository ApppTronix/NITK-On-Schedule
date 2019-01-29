package com.apptronix.nitkonschedule.teacher.service

import android.annotation.SuppressLint
import android.app.IntentService
import android.content.Intent
import android.os.AsyncTask
/**
 * Created by DevOpsTrends on 7/2/2017.
 */

class DbSyncService : IntentService("DbSyncService") {

    override fun onHandleIntent(p0: Intent?) {

        DBSyncTask.syncData(this)
    }

}
