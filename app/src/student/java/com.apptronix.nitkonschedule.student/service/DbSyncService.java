package com.apptronix.nitkonschedule.student.service;

import android.os.AsyncTask;

import com.apptronix.nitkonschedule.student.service.DBSyncTask;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * Created by DevOpsTrends on 7/2/2017.
 */

public class DbSyncService  extends JobService{

    private AsyncTask<Void,Void,Void> mFetchDataTask;
    @Override
    public boolean onStartJob(final JobParameters job) {

        mFetchDataTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                DBSyncTask.syncData(getApplicationContext());
                jobFinished(job,false);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                jobFinished(job,false);
            }
        };
        mFetchDataTask.execute();
        return true;// Answers the question: "Is there still work going on?"
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if (mFetchDataTask != null) {
            mFetchDataTask.cancel(true);
        }
        return true;// Answers the question: "Should this job be retried?"
    }

}
