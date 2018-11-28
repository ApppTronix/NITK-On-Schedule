package com.apptronix.nitkonschedule.teacher.service

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import com.apptronix.nitkonschedule.teacher.model.*
import com.apptronix.nitkonschedule.rest.ApiClient
import com.apptronix.nitkonschedule.teacher.data.DBContract
import com.apptronix.nitkonschedule.teacher.rest.ApiInterface
import com.apptronix.nitkonschedule.teacher.ui.MainActivity
import com.apptronix.nitkonschedule.teacher.ui.fragments.ScheduleFragment
import com.apptronix.nitkonschedule.teacher.widget.TimeTableCollectionWidget
import com.google.firebase.messaging.FirebaseMessaging
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.io.IOException
import java.net.URL
import java.util.*
import java.util.concurrent.TimeUnit

@SuppressLint("StaticFieldLeak")
/**
 * Created by DevOpsTrends on 7/2/2017.
 */

object DBSyncTask {

    private var mContext: Context? = null
    val JSON = MediaType.parse("application/json; charset=utf-8")
    private var user: User? = null

    @Synchronized
    fun syncData(context: Context) {
        mContext = context

        user=User(context)
        fetchAccessToken(true)
        handleActionFetchTests()
        handleActionFetchCourses()
        handleActionFetchTimeTable()
        handleActionFetchAssignments()
        handleActionFetchResources()

        val intent = Intent(context, TimeTableCollectionWidget::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val ids = AppWidgetManager.getInstance(context.applicationContext).getAppWidgetIds(ComponentName(context.applicationContext, TimeTableCollectionWidget::class.java))
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        context.sendBroadcast(intent)
    }

    private fun handleActionFetchTests() {

        Timber.i(user!!.accessToken)
        val apiService = ApiClient.getClient().create(ApiInterface::class.java)
        val callTT = apiService.getTests(user!!.accessToken)

        try {
            val response = callTT.execute()
            if (response.isSuccessful) {
                if (response.code() == 200) {
                    val testsList = response.body()!!.results
                    insertTestsList(testsList)
                } else if (response.code() == 401|| response.code() == 500) { //bad auth
                    user!!.setAccessToken(null, mContext!!) //reset access token
                    fetchAccessToken(true)
                }
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun handleActionFetchAssignments() {

        val apiService = ApiClient.getClient().create(ApiInterface::class.java)
        val callTT = apiService.getAssignments(user!!.accessToken)

        try {
            val response = callTT.execute()
            if (response.isSuccessful) {
                if (response.code() == 200) {
                    val assgnList = response.body()!!.results
                    insertAssignmentsList(assgnList)
                } else if (response.code() == 401|| response.code() == 500) { //bad auth
                    user!!.setAccessToken(null, mContext!!) //reset access token
                    fetchAccessToken(true)
                }
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }

    }


    private fun handleActionFetchResources() {

        val apiService = ApiClient.getClient().create(ApiInterface::class.java)
        val callTT = apiService.getResources(user!!.accessToken)

        try {
            val response = callTT.execute()
            if (response.isSuccessful) {
                if (response.code() == 200) {
                    val resourcesList = response.body()!!.results
                    insertResourcesList(resourcesList)
                } else if (response.code() == 401|| response.code() == 500) { //bad auth
                    user!!.setAccessToken(null, mContext!!) //reset access token
                    fetchAccessToken(true)
                }
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun handleActionFetchCourses() {

        val apiService = ApiClient.getClient().create(ApiInterface::class.java)
        val callTT = apiService.getCourses(user!!.accessToken)

        try {
            val response = callTT.execute()
            if (response.isSuccessful) {
                Timber.i(response.body()!!.results.toString())
                if (response.code() == 200 && response.body()!!.results != null) {
                    val courseList = response.body()!!.results
                    insertCourseList(courseList)
                } else if (response.code() == 401|| response.code() == 500) { //bad auth
                    user!!.setAccessToken(null, mContext!!) //reset access token
                    fetchAccessToken(true)
                }
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }


    }

    private fun fetchAccessToken(force:Boolean) {


        if(!force){
            if(!(user!!.accessTokenTime == null || 0.equals(user!!.accessTokenTime))){
                if((user!!.accessTokenTime!! +300)>(System.currentTimeMillis()/1000)) { //not expired yet
                    return
                }
            }

        }

        val refreshToken = user!!.refreshToken
        Timber.i("sending refresh token %s to server", refreshToken)

        try {

            val builder = OkHttpClient.Builder()
            builder.connectTimeout(30, TimeUnit.SECONDS)
            builder.readTimeout(30, TimeUnit.SECONDS)
            builder.writeTimeout(30, TimeUnit.SECONDS)
            val client = builder.build()

            val postJSON = JSONObject()
            postJSON.put("refreshToken", refreshToken)
            val body = RequestBody.create(JSON, postJSON.toString())
            val request = Request.Builder()
                    .url(URL(ApiClient.BASE_URL + "getAccessToken"))
                    .post(body)
                    .build()


            val response = client.newCall(request).execute()

            if (response.isSuccessful) {

                val responseString = response.body()!!.string()
                Timber.i("AccessToken Response is %s", responseString)

                if (responseString == "fail") {

                    EventBus.getDefault().post(MainActivity.MessageEvent("TokenUpdateRefused"))

                } else {

                    //registration, received refresh token
                    user!!.updateTokens(user!!.refreshToken, responseString, mContext!!)

                }

            } else {

                EventBus.getDefault().post(MainActivity.MessageEvent("ServerUnreachable"))

            }

        } catch (e1: IOException) {
            e1.printStackTrace()
            EventBus.getDefault().post(MainActivity.MessageEvent("ServerUnreachable"))
        } catch (e1: JSONException) {
            e1.printStackTrace()
            EventBus.getDefault().post(MainActivity.MessageEvent("ServerUnreachable"))
        }

    }

    private fun handleActionFetchTimeTable() {

        val apiService = ApiClient.getClient().create(ApiInterface::class.java)
        val callTT = apiService.getSchedule(user!!.accessToken)

        try {
            val response = callTT.execute()
            if (response.isSuccessful) {
                if (response.code() == 200) {
                    val timeTableList = response.body()!!.results
                    insertTimeTableList(timeTableList)
                } else if (response.code() == 401|| response.code() == 500) { //bad auth
                    user!!.setAccessToken(null, mContext!!) //reset access token
                    fetchAccessToken(true)
                }
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun insertTestsList(testsList: List<Test>?) {

        val cVVector = Vector<ContentValues>(testsList!!.size)

        for (test in testsList) {
            val assgnCV = ContentValues()
            assgnCV.put(DBContract.TestsEntry.COLUMN_COURSE_CODE, test.course)
            assgnCV.put(DBContract.TestsEntry.COLUMN_TITLE, test.title)
            assgnCV.put(DBContract.TestsEntry.COLUMN_SYLLABUS, test.portions)
            assgnCV.put(DBContract.TestsEntry.COLUMN_TEST_DATE, test.testDate)
            assgnCV.put(DBContract.TestsEntry.COLUMN_WEIGHTAGE, test.weightage)
            cVVector.add(assgnCV)
        }

        if (cVVector.size > 0) {
            val cvArray = cVVector.toTypedArray()
            val inserts = mContext!!.contentResolver.bulkInsert(DBContract.TestsEntry.CONTENT_URI, cvArray)
            Timber.i("%d tests bulk insert success", inserts)
        }

    }


    private fun insertAssignmentsList(assgnList: List<Assignment>?) {

        val cVVector = Vector<ContentValues>(assgnList!!.size)

        for (assgn in assgnList) {
            val assgnCV = ContentValues()
            assgnCV.put(DBContract.AssignmentsEntry.COLUMN_COURSE_CODE, assgn.course)
            assgnCV.put(DBContract.AssignmentsEntry.COLUMN_TITLE, assgn.title)
            assgnCV.put(DBContract.AssignmentsEntry.COLUMN_DESCRIPTION, assgn.description)
            assgnCV.put(DBContract.AssignmentsEntry.COLUMN_SUBMISSION_DATE, assgn.submissionDate)
            assgnCV.put(DBContract.AssignmentsEntry.COLUMN_WEIGHTAGE, assgn.weightage)
            cVVector.add(assgnCV)
        }

        if (cVVector.size > 0) {
            val cvArray = cVVector.toTypedArray()
            val inserts = mContext!!.contentResolver.bulkInsert(DBContract.AssignmentsEntry.CONTENT_URI, cvArray)
            Timber.i("%d assignments bulk insert success", inserts)
        }

    }

    private fun insertCourseList(courseList: List<Course>?) {

        val cVVector = Vector<ContentValues>(courseList!!.size)

        if (courseList.size > 0) {

            Timber.i("insert course %d",courseList.size)
            for (course in courseList) {
                val courseCV = ContentValues()
                var regIDs :String?=""
                for(id in course.enrolled){
                    regIDs+=id+";"
                }
                courseCV.put(DBContract.CourseEntry.COLUMN_COURSE, course.name)
                courseCV.put(DBContract.CourseEntry.COLUMN_COURSE_ENROLLED_IDS, regIDs)
                courseCV.put(DBContract.CourseEntry.COLUMN_COURSE_DESCRIPTION, course.description)
                cVVector.add(courseCV)

                FirebaseMessaging.getInstance().subscribeToTopic(course.name)
            }
            val cvArray = cVVector.toTypedArray()
            val inserts = mContext!!.contentResolver.bulkInsert(DBContract.CourseEntry.CONTENT_URI, cvArray)
            Timber.i("%d course bulk insert success", inserts)
        }
    }

    private fun insertTimeTableList(timeTableList: List<Schedule>?) {

        val cVVector = Vector<ContentValues>(timeTableList!!.size)

        for (dayTable in timeTableList) {

            val timeTableCV = ContentValues()
            timeTableCV.put(DBContract.TimeTableEntry.COLUMN_DATE, dayTable.getDate())
            timeTableCV.put(DBContract.TimeTableEntry.COLUMN_COURSE, dayTable.course)
            timeTableCV.put(DBContract.TimeTableEntry.COLUMN_TIME, dayTable.time)
            timeTableCV.put(DBContract.TimeTableEntry.COLUMN_DESCRIPTION, dayTable.description)
            timeTableCV.put(DBContract.TimeTableEntry.COLUMN_PRESENT_IDS, dayTable.presentIDs)

            Timber.i("%s %d",dayTable.description,dayTable.date)
            cVVector.add(timeTableCV)

        }

        if (cVVector.size > 0) {
            val cvArray = cVVector.toTypedArray()
            val inserts = mContext!!.contentResolver.bulkInsert(DBContract.TimeTableEntry.CONTENT_SCHEDULE_URI, cvArray)
            Timber.i("%d day's time table bulk insert success", inserts)
            EventBus.getDefault().post(ScheduleFragment.MessageEvent("reload"))
        }
    }

    private fun insertResourcesList(resourceList: List<ResourceModel>?) {

        val cVVector = Vector<ContentValues>(resourceList!!.size)

        for (res in resourceList) {

            for (file in res.resources) {
                val resourceCV = ContentValues()
                resourceCV.put(DBContract.RepositoryEntry.COLUMN_FILE_NAME, file)
                resourceCV.put(DBContract.RepositoryEntry.COLUMN_COURSE, res.course)

                cVVector.add(resourceCV)
            }


        }

        if (cVVector.size > 0) {
            val cvArray = cVVector.toTypedArray()
            val inserts = mContext!!.contentResolver.bulkInsert(DBContract.RepositoryEntry.CONTENT_URI, cvArray)
            Timber.i("%d resource insert success", inserts)
        }
    }

}
