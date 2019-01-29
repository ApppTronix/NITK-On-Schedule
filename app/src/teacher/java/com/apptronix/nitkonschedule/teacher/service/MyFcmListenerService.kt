package com.apptronix.nitkonschedule.teacher.service

import android.app.NotificationManager
import android.content.ContentValues
import androidx.core.app.NotificationCompat
import com.apptronix.nitkonschedule.R
import com.apptronix.nitkonschedule.Utils
import com.apptronix.nitkonschedule.teacher.data.DBContract
import com.apptronix.nitkonschedule.teacher.ui.fragments.AssignmentsFragment
import com.apptronix.nitkonschedule.teacher.ui.fragments.ScheduleFragment
import com.apptronix.nitkonschedule.teacher.ui.fragments.TestsFragment
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.greenrobot.eventbus.EventBus
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.util.*

/**
 * Created by DevOpsTrends on 6/21/2017.
 */

class MyFcmListenerService : FirebaseMessagingService() {

    val notifyId = 202
    lateinit var shortName:String
    lateinit var desc:String
    override fun onMessageReceived(message: RemoteMessage?) {
        val from = message!!.from
        val data = message.data

        Timber.i("message received %s",data.toString())


        val cVVector = Vector<ContentValues>(1)
        var mBuilder = NotificationCompat.Builder(this)
        mBuilder.setSmallIcon(R.drawable.ic_school_black_24dp)

        val title=data.get("title")

        try {
            if (title!!.toLowerCase().contains("new")) {
                if (title!!.contains("Schedule")) {

                    val dayTable = JSONObject(data.getValue("content"))
                    val timeTableCV = ContentValues()
                    val date =  dayTable.getInt("date")
                    val course =  dayTable.getString("course")
                    val time = dayTable.getInt("time")

                    mBuilder
                            .setSmallIcon(R.drawable.ic_timetable)
                            .setContentTitle("New Schedule")
                            .setContentText("You have a "+ course + " class scheduled at " + Utils.setTime(time) + " on " + Utils.convertToDate(date))

                    timeTableCV.put(DBContract.TimeTableEntry.COLUMN_DATE,date)
                    timeTableCV.put(DBContract.TimeTableEntry.COLUMN_COURSE,course)
                    timeTableCV.put(DBContract.TimeTableEntry.COLUMN_TIME, time)



                    try {
                        shortName =  dayTable.getString("shortName")
                    } catch (e:JSONException){
                        shortName = ""
                    }
                    try {
                        desc =  dayTable.getString("desc")
                    } catch (e:JSONException){
                        desc = ""
                    }
                    timeTableCV.put(DBContract.TimeTableEntry.COLUMN_SHORT_NAME,shortName)
                    timeTableCV.put(DBContract.TimeTableEntry.COLUMN_DESCRIPTION, desc)
                    cVVector.add(timeTableCV)
                    val cvArray = cVVector.toTypedArray()

                    val inserts = this.contentResolver.bulkInsert(DBContract.TimeTableEntry.CONTENT_URI, cvArray)
                    Timber.i("%d day's time table bulk insert success", inserts)
                    EventBus.getDefault().post(ScheduleFragment.MessageEvent("reload"))

                } else if (title!!.contains("Assignment")) {

                    val assgn = JSONObject(data.getValue("content"))
                    val assgnCV = ContentValues()

                    val date =  assgn.getString("submissionDate")
                    val course =  assgn.getString("course")
                    val time = assgn.getString("time")
                    val title = assgn.getString("title")

                    mBuilder.setContentTitle("New Assignment")
                            .setSmallIcon(R.drawable.ic_assignment_black_24dp)
                            .setContentText("You have a "+ course + " assignment to be submitted on " + date + " at " + time)

                    assgnCV.put(DBContract.AssignmentsEntry.COLUMN_SUBMISSION_DATE,date)
                    assgnCV.put(DBContract.AssignmentsEntry.COLUMN_COURSE_CODE,course)
                    assgnCV.put(DBContract.AssignmentsEntry.COLUMN_TIME, time)
                    assgnCV.put(DBContract.AssignmentsEntry.COLUMN_TITLE, title)
                    assgnCV.put(DBContract.AssignmentsEntry.COLUMN_DESCRIPTION, assgn.getString("description"))
                    assgnCV.put(DBContract.AssignmentsEntry.COLUMN_WEIGHTAGE, assgn.getString("weightage"))
                    assgnCV.put(DBContract.AssignmentsEntry.COLUMN_MAX_SCORE, assgn.getString("description"))
                    cVVector.add(assgnCV)
                    val cvArray = cVVector.toTypedArray()
                    val inserts = this.contentResolver.bulkInsert(DBContract.TimeTableEntry.CONTENT_URI, cvArray)
                    Timber.i("%d assgn bulk insert success", inserts)
                }else if (title!!.contains("Test")) {

                    val test = JSONObject(data.getValue("content"))
                    val testCV = ContentValues()

                    val date =  test.getString("testDate")
                    val course =  test.getString("course")
                    val time = test.getString("testTime")
                    val title = test.getString("title")

                    mBuilder.setContentTitle("New Test")
                            .setContentText("You have a "+ course + " test scheduled for " + date + " at " + time)

                    testCV.put(DBContract.AssignmentsEntry.COLUMN_SUBMISSION_DATE,date)
                    testCV.put(DBContract.AssignmentsEntry.COLUMN_COURSE_CODE,course)
                    testCV.put(DBContract.AssignmentsEntry.COLUMN_TIME, time)
                    testCV.put(DBContract.AssignmentsEntry.COLUMN_TITLE, title)
                    testCV.put(DBContract.AssignmentsEntry.COLUMN_DESCRIPTION, test.getString("description"))
                    testCV.put(DBContract.AssignmentsEntry.COLUMN_WEIGHTAGE, test.getString("weightage"))
                    testCV.put(DBContract.AssignmentsEntry.COLUMN_MAX_SCORE, test.getString("description"))
                    cVVector.add(testCV)
                    val cvArray = cVVector.toTypedArray()
                    val inserts = this.contentResolver.bulkInsert(DBContract.TimeTableEntry.CONTENT_URI, cvArray)
                    Timber.i("%d test bulk insert success", inserts)
                } else if (title.contains("newResource")) {

                    val filename = data.getValue("filename")
                    val resourceCV = ContentValues()


                    mBuilder.setContentTitle("New Resource added")
                            .setContentText("You have a new resource for "+ from)

                    resourceCV.put(DBContract.RepositoryEntry.COLUMN_COURSE,from)
                    resourceCV.put(DBContract.RepositoryEntry.COLUMN_FILE_NAME,filename)
                    cVVector.add(resourceCV)
                    val cvArray = cVVector.toTypedArray()
                    val inserts = this.contentResolver.bulkInsert(DBContract.RepositoryEntry.CONTENT_URI, cvArray)
                    Timber.i("%d resource bulk insert success", inserts)
                } else {
                    return
                }
            } else if (title.contains("delete")){
                if (title!!.contains("Assignment")) {
                    val assgnCV = ContentValues()


                    val assgn = JSONObject(data.getValue("content"))
                    val date =  assgn.getString("submissionDate")
                    val course =  assgn.getString("course")
                    val title = assgn.getString("title")

                    mBuilder.setContentTitle("Assignment Deleted")
                            .setSmallIcon(R.drawable.ic_assignment_black_24dp)
                            .setContentText("Your "+ course + " assignment to be submitted on " + date + " was removed")

                    EventBus.getDefault().post(AssignmentsFragment.MessageEvent("reload"))
                }else if (title!!.contains("Test")) {

                    val test = JSONObject(data.getValue("content"))
                    val testCV = ContentValues()

                    val date =  test.getString("testDate")
                    val course =  test.getString("course")


                    mBuilder.setContentTitle("Test Deleted")
                            .setSmallIcon(R.drawable.ic_assignment_black_24dp)
                            .setContentText("Your "+ course + " test scheduled on " + date + " was removed")

                    EventBus.getDefault().post(TestsFragment.MessageEvent("reload"))
                } else if (title.contains("Schedule")) {

                    val dayTable = JSONObject(data.getValue("content"))
                    val timeTableCV = ContentValues()
                    val date =  dayTable.getInt("date")
                    val course =  dayTable.getString("course")
                    val time = dayTable.getInt("time")

                    mBuilder
                            .setSmallIcon(R.drawable.ic_timetable)
                            .setContentTitle("Scheduled Class Cancelled")
                            .setContentText("Your "+ course + " class scheduled at " + Utils.setTime(time) + " on " + Utils.convertToDate(date) + " was cancelled")

                    EventBus.getDefault().post(ScheduleFragment.MessageEvent("reload"))

                }
            }

            val mNotifyMgr =  getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            mNotifyMgr.notify(notifyId, mBuilder.build())
        } catch (e: NoSuchElementException){
            Timber.e("Key missing in map")
        }

    }



}
