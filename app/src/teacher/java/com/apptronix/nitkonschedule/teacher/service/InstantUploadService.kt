package com.apptronix.nitkonschedule.teacher.service

import android.app.IntentService
import android.app.Notification
import android.app.NotificationManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import com.apptronix.nitkonschedule.R
import com.apptronix.nitkonschedule.model.User
import com.apptronix.nitkonschedule.rest.ApiClient
import com.apptronix.nitkonschedule.rest.ProgressRequestBody
import com.apptronix.nitkonschedule.teacher.data.DBContract
import com.apptronix.nitkonschedule.teacher.rest.ApiInterface
import com.apptronix.nitkonschedule.teacher.ui.MainActivity
import com.apptronix.nitkonschedule.teacher.ui.MarkStudents
import okhttp3.MultipartBody
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import java.io.File
import java.io.IOException


class InstantUploadService : IntentService("InstantUploadService"), ProgressRequestBody.UploadCallbacks {


    lateinit var mNotifyManager: NotificationManager
    lateinit var mBuilder: Notification.Builder
    internal var id = 28
    var course: String = ""

    override fun onProgressUpdate(percentage: Int) {
        mBuilder.setProgress(100, percentage, false)
        mNotifyManager.notify(id, mBuilder.build())
    }

    override fun onError() {
        mBuilder.setContentText("Upload Failed")
                .setProgress(0, 0, false)
        mNotifyManager.notify(id, mBuilder.build())
    }

    override fun onFinish() {
        mBuilder.setContentText("Upload complete")
                .setProgress(0, 0, false)
        mNotifyManager.notify(id, mBuilder.build())
    }

    override fun onHandleIntent(intent: Intent?) {

        if (intent != null) {
            val bundle = intent.getBundleExtra("bundle")
            val content = bundle.getString("content")
            if (content == "Upload File") {

                course=bundle.getString("course")
                val path = bundle.getString("file_paths")
                val file = File(path)
                val fileBody = ProgressRequestBody(file, this)

                val filePart = MultipartBody.Part.createFormData("file", file.name, fileBody)

                mNotifyManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                mBuilder= Notification.Builder(this)
                mBuilder.setContentTitle("File Upload")
                         .setContentText("Upload in progress")
                        .setSmallIcon(R.drawable.ic_folder)
                mNotifyManager.notify(id,mBuilder.build())

                val apiService = ApiClient.getClient().create(ApiInterface::class.java)
                val user = User(this);
                val callTT = apiService.uploadFile(user.accessToken, filePart,file.name,course)

                try {
                    val response = callTT.execute()

                    Timber.i("response msg %s",response.message())
                    if (response.isSuccessful) {
                        if (response.code() == 200) {

                            var values = ContentValues()
                            values.put(DBContract.RepositoryEntry.COLUMN_FILE_NAME,file.name)
                            values.put(DBContract.RepositoryEntry.COLUMN_FILE_LOCATION,path)
                            values.put(DBContract.RepositoryEntry.COLUMN_COURSE,course)
                            contentResolver.insert(DBContract.RepositoryEntry.CONTENT_URI,values)
                            mBuilder.setContentText("Upload complete")
                                    .setProgress(0, 0, false)
                            mNotifyManager.notify(id, mBuilder.build())
                            Timber.i("response 200 file")
                            EventBus.getDefault().post(MainActivity.MessageEvent("FileUploadSuccess"))
                        } else if (response.code() == 401) {

                        }
                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                    Timber.i(e.message)
                }

            } else if (content == "Upload Attendance Image") {

                course=bundle.getString("course")
                val path = bundle.getString("file_paths")
                val file = File(path)
                val fileBody = ProgressRequestBody(file, this)

                val filePart = MultipartBody.Part.createFormData("file", file.name, fileBody)

                mNotifyManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                mBuilder= Notification.Builder(this)
                mBuilder.setContentTitle("Attendance Image Upload")
                        .setContentText("Upload in progress")
                        .setSmallIcon(R.drawable.ic_folder)
                mNotifyManager.notify(id,mBuilder.build())

                val apiService = ApiClient.getClient().create(ApiInterface::class.java)
                val user = User(this);
                val callTT = apiService.uploadAttendanceImage(user.accessToken, filePart,file.name,course)

                try {
                    val response = callTT.execute()

                    Timber.i("response msg %s",response.message())
                    if (response.isSuccessful) {
                        if (response.code() == 200) {

                            mBuilder.setContentText("Face Recognition complete")
                                    .setProgress(0, 0, false)
                            mNotifyManager.notify(id, mBuilder.build())

                            EventBus.getDefault().post(MarkStudents.MessageEvent("FaceRecognitionComplete",response.body()!!))
                        } else if (response.code() == 401) {

                        }
                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                    Timber.i(e.message)
                }

            } else {
                Timber.i("starting upload task")
                UploadTask.uploadDoc(this, bundle)
            }


        }

    }


}


