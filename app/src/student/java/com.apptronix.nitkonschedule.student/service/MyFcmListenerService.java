package com.apptronix.nitkonschedule.student.service;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import timber.log.Timber;

/**
 * Created by DevOpsTrends on 6/21/2017.
 */

public class MyFcmListenerService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage message){
        String from = message.getFrom();
        Map data = message.getData();

        Timber.i("message received");
        if(data.get("action").equals("syncDB")){

            Timber.i("fcm sync started");
            FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
            Job myJob = dispatcher.newJobBuilder()
                    .setService(DbSyncService.class) // the JobService that will be called
                    .setTag("my-unique-tag")        // uniquely identifies the job
                    .build();

            dispatcher.mustSchedule(myJob);
        }
    }

}
