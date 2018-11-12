package com.apptronix.nitkonschedule.teacher.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import androidx.core.app.NotificationCompat;

import com.apptronix.nitkonschedule.R;
import com.apptronix.nitkonschedule.teacher.data.DBContract;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class DownloadService extends IntentService {

    public static final int UPDATE_PROGRESS = 8344;

    public DownloadService() {
        super("DownloadService");
    }

    NotificationManager mNotifyManager;
    NotificationCompat.Builder mBuilder;
    String locn;

    @Override
    protected void onHandleIntent(Intent intent) {
        String urlToDownload = intent.getStringExtra("url");
        String course = intent.getStringExtra("course");
        String fileName = intent.getStringExtra("fileName");

        mNotifyManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("Picture Download")
                .setContentText("Download in progress")
                .setSmallIcon(R.mipmap.ic_launcher);
        try {
            URL url = new URL(urlToDownload);
            URLConnection connection = url.openConnection();
            connection.connect();
            // this will be useful so that you can show a typical 0-100% progress bar
            int fileLength = connection.getContentLength();

            // download the file
            InputStream input = new BufferedInputStream(connection.getInputStream());

            locn = Environment.DIRECTORY_DOWNLOADS+course+fileName;
            OutputStream output = new FileOutputStream(locn);

            byte data[] = new byte[1024];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                mBuilder.setProgress(100, (int) (total * 100 / fileLength), false);
                // Displays the progress bar for the first time.
                mNotifyManager.notify(UPDATE_PROGRESS, mBuilder.build());
            }

            output.flush();
            output.close();
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            ContentValues contentValues = new ContentValues();
            contentValues.put(DBContract.RepositoryEntry.COLUMN_FILE_NAME,fileName);
            contentValues.put(DBContract.RepositoryEntry.COLUMN_COURSE,course);
            contentValues.put(DBContract.RepositoryEntry.COLUMN_FILE_LOCATION,locn);
            getContentResolver().update(DBContract.RepositoryEntry.CONTENT_URI,contentValues,null,null);

            int requestID = (int) System.currentTimeMillis();

            Intent myIntent = new Intent(Intent.ACTION_VIEW);
            myIntent.setData(Uri.parse(locn));
            Intent j = Intent.createChooser(myIntent, "Choose an application to open with:");
            startActivity(j);

            j.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);


            PendingIntent contentIntent = PendingIntent.getActivity(this,
                    requestID, j, PendingIntent.FLAG_UPDATE_CURRENT);

            mBuilder.setContentText("Download complete")
                    // Removes the progress bar
                    .setProgress(0,0,false)
                    .setContentIntent(contentIntent);
            mNotifyManager.notify(UPDATE_PROGRESS, mBuilder.build());
        }

    }
}
