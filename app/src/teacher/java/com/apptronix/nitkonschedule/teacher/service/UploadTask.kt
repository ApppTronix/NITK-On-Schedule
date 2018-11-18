package com.apptronix.nitkonschedule.teacher.service

import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import com.apptronix.nitkonschedule.model.*
import com.apptronix.nitkonschedule.rest.ApiClient
import com.apptronix.nitkonschedule.teacher.data.DBContract
import com.apptronix.nitkonschedule.teacher.rest.ApiInterface
import com.apptronix.nitkonschedule.teacher.ui.fragments.CoursesFragment
import com.apptronix.nitkonschedule.teacher.ui.MainActivity
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
import java.util.concurrent.TimeUnit

/**
 * Created by DevOpsTrends on 7/5/2017.
 */

object UploadTask {

    val JSON = MediaType.parse("application/json; charset=utf-8")

    private var user: User? = null
    lateinit var context: Context

    @Synchronized
    fun uploadDoc(context: Context, bundle: Bundle) {
        Timber.i("uploadtask %s", bundle.getString("content"))

        this.context = context
        user = User(context)
        fetchAccessToken(context,bundle)

    }

    private fun uploadAttendance(bundle: Bundle) {

        val schedule = bundle.getSerializable("parcel") as Schedule
        Timber.i("upload Attendance %s",schedule.presentIDs)
        val apiService = ApiClient.getClient().create(ApiInterface::class.java)
        val callTT = apiService.uploadAttendance(user!!.accessToken, schedule)

        try {
            val response = callTT.execute()
            if (response.isSuccessful) {
                Timber.i("upload attendance response %s",response.body()!!.results)
                if (response.code() == 200) {
                    if(response.body()!!.results.equals("uploadSuccessful")){
                        EventBus.getDefault().post(MainActivity.MessageEvent("Attendance updated"))
                    } else {
                        EventBus.getDefault().post(MainActivity.MessageEvent("Attendance update failed"))
                    }

                } else if (response.code() == 401) {

                }
            }

        } catch (e: IOException) {
            e.printStackTrace()
            Timber.i(e.message)
        }

    }

    private fun editSchedule(bundle: Bundle) {
        Timber.i("edit schedule")
        val schedule = bundle.getSerializable("parcel") as Schedule
        val apiService = ApiClient.getClient().create(ApiInterface::class.java)
        val callTT = apiService.editSchedule(user!!.accessToken, schedule)

        try {
            val response = callTT.execute()
            if (response.isSuccessful) {
                if (response.code() == 200) {
                    EventBus.getDefault().post(MainActivity.MessageEvent(response.message()))
                } else if (response.code() == 401) {

                }
            }

        } catch (e: IOException) {
            e.printStackTrace()
            Timber.i(e.message)
        }
    }

    private fun deleteSchedule(bundle: Bundle) {
        Timber.i(" delete schedule")
        val id = bundle.getInt("id")
        val cursor = this.context.contentResolver.query(DBContract.TimeTableEntry.buildScheduleUri(id),
                null, null, null, null)
        if(cursor.moveToFirst()){
            val schedule = Schedule(
                    cursor.getInt(cursor.getColumnIndex(DBContract.TimeTableEntry.COLUMN_DATE)),
                    cursor.getString(cursor.getColumnIndex(DBContract.TimeTableEntry.COLUMN_COURSE)),
                    cursor.getInt(cursor.getColumnIndex(DBContract.TimeTableEntry.COLUMN_TIME)),
                    cursor.getString(cursor.getColumnIndex(DBContract.TimeTableEntry.COLUMN_DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndex(DBContract.TimeTableEntry.COLUMN_PRESENT_IDS))

            )

            val apiService = ApiClient.getClient().create(ApiInterface::class.java)
            val callTT = apiService.deleteSchedule(user!!.accessToken, schedule)

            Timber.i(" delete schedule call created ")
            try {
                val response = callTT.execute()
                if (response.isSuccessful) {
                    if (response.code() == 200) {
                        if(response.body()!!.results.equals("deleteSuccessful")){
                            Timber.i(" delete schedule success")
                            this.context.contentResolver.delete(DBContract.TimeTableEntry.buildScheduleUri(id),null,null)
                        }
                        EventBus.getDefault().post(MainActivity.MessageEvent(response.message()))
                    } else if (response.code() == 401) {

                        Timber.i(" delete schedule fail")
                    }
                }

            } catch (e: IOException) {
                e.printStackTrace()
                Timber.i(e.message)
            }
        } else {
            Timber.i("delete schedule cursor empty")
        }


    }


    private fun uploadCourse(bundle: Bundle) {

        Timber.i("upload course")
        val course = bundle.getSerializable("parcel") as Course
        val apiService = ApiClient.getClient().create(ApiInterface::class.java)
        val callTT = apiService.uploadCourse(user!!.accessToken, course)

        try {
            val response = callTT.execute()
            if (response.isSuccessful) {
                if (response.code() == 200) {
                    var cv =  ContentValues()
                    cv.put(DBContract.CourseEntry.COLUMN_COURSE,course.name)
                    cv.put(DBContract.CourseEntry.COLUMN_COURSE_DESCRIPTION,course.description)
                    context.contentResolver.insert(DBContract.CourseEntry.CONTENT_URI,cv)
                    EventBus.getDefault().post(MainActivity.MessageEvent(response.message()))
                    EventBus.getDefault().post(CoursesFragment.MessageEvent("reload"))
                } else if (response.code() == 401) {

                }
            }

        } catch (e: IOException) {
            e.printStackTrace()
            Timber.i(e.message)
        }

    }


    private fun uploadSchedule(bundle: Bundle) {

        Timber.i("uploading new schedule")
        val schedule = bundle.getSerializable("parcel") as Schedule
        val apiService = ApiClient.getClient().create(ApiInterface::class.java)
        val callTT = apiService.uploadSchedule(user!!.accessToken, schedule)

        try {
            val response = callTT.execute()
            if (response.isSuccessful) {
                Timber.i("upload new schedule response is %s",response.body()!!.results)
                if (response.code() == 200) {
                    EventBus.getDefault().post(MainActivity.MessageEvent(response.message()))
                } else if (response.code() == 401) {

                }
            } else {
                Timber.i("upload reponse unsuccesful")
            }

        } catch (e: IOException) {
            e.printStackTrace()
            Timber.i(e.message)
        }

    }


    private fun deleteTest(bundle: Bundle) {
        Timber.i("test")
        val id = bundle.getInt("id")
        val cursor = this.context.contentResolver.query(DBContract.TestsEntry.buildTestUri(id),
                null, null, null, null)
        if(cursor.moveToFirst()){
            val test = Test(
                    cursor.getString(cursor.getColumnIndex(DBContract.TestsEntry.COLUMN_TITLE)),
                    cursor.getString(cursor.getColumnIndex(DBContract.TestsEntry.COLUMN_SYLLABUS)),
                    cursor.getString(cursor.getColumnIndex(DBContract.TestsEntry.COLUMN_COURSE_CODE)),
                    cursor.getInt(cursor.getColumnIndex(DBContract.TestsEntry.COLUMN_TEST_DATE)),
                    cursor.getInt(cursor.getColumnIndex(DBContract.TestsEntry.COLUMN_WEIGHTAGE)),
                    cursor.getInt(cursor.getColumnIndex(DBContract.TestsEntry.COLUMN_TIME))
            )

            val apiService = ApiClient.getClient().create(ApiInterface::class.java)
            val callTT = apiService.deleteTest(user!!.accessToken, test)

            try {
                val response = callTT.execute()
                if (response.isSuccessful) {
                    if (response.code() == 200) {
                        if(response.body()!!.results.equals("deleteSuccessful")){
                            this.context.contentResolver.delete(DBContract.TestsEntry.buildTestUri(id),null,null)
                        }
                        EventBus.getDefault().post(MainActivity.MessageEvent(response.message()))
                    } else if (response.code() == 401) {

                    }
                }

            } catch (e: IOException) {
                e.printStackTrace()
                Timber.i(e.message)
            }
        }


    }

    private fun editTest(bundle: Bundle,context: Context) {
        Timber.i("test")

        val test = bundle.getSerializable("parcel") as Test
        val apiService = ApiClient.getClient().create(ApiInterface::class.java)
        val callTT = apiService.editTest(user!!.accessToken, test)

        try {
            val response = callTT.execute()
            if (response.isSuccessful) {
                if (response.code() == 200) {
                    EventBus.getDefault().post(MainActivity.MessageEvent("editSuccessful"))
                } else if (response.code() == 401) {

                }
            }

        } catch (e: IOException) {
            e.printStackTrace()
            Timber.i(e.message)
        }

    }

    private fun deleteAssignment(bundle: Bundle) {
        Timber.i("assignment")
        val id = bundle.getInt("id")
        val cursor = this.context.contentResolver.query(DBContract.AssignmentsEntry.buildAssignmentUri(id),
                null, null, null, null)
        if(cursor.moveToFirst()){
            val assignment = Assignment(
                    cursor.getString(cursor.getColumnIndex(DBContract.AssignmentsEntry.COLUMN_TITLE)),
                    cursor.getString(cursor.getColumnIndex(DBContract.AssignmentsEntry.COLUMN_DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndex(DBContract.AssignmentsEntry.COLUMN_COURSE_CODE)),
                    cursor.getInt(cursor.getColumnIndex(DBContract.AssignmentsEntry.COLUMN_SUBMISSION_DATE)),
                    cursor.getInt(cursor.getColumnIndex(DBContract.AssignmentsEntry.COLUMN_WEIGHTAGE)),
                    cursor.getInt(cursor.getColumnIndex(DBContract.AssignmentsEntry.COLUMN_MAX_SCORE))
            )

            val apiService = ApiClient.getClient().create(ApiInterface::class.java)
            val callTT = apiService.deleteAssignment(user!!.accessToken, assignment)

            try {
                val response = callTT.execute()
                if (response.isSuccessful) {
                    if (response.code() == 200) {
                        if(response.body()!!.results.equals("deleteSuccessful")){
                            this.context.contentResolver.delete(DBContract.AssignmentsEntry.buildAssignmentUri(id),null,null)
                        }
                        EventBus.getDefault().post(MainActivity.MessageEvent(response.message()))
                    } else if (response.code() == 401) {

                    }
                }

            } catch (e: IOException) {
                e.printStackTrace()
                Timber.i(e.message)
            }
        }

    }

    private fun editAssignment(bundle: Bundle) {

        val assignment = bundle.getSerializable("parcel") as Assignment

        val apiService = ApiClient.getClient().create(ApiInterface::class.java)
        val callTT = apiService.editAssignment(user!!.accessToken, assignment)

        try {
            val response = callTT.execute()
            if (response.isSuccessful) {
                if (response.code() == 200) {
                    Timber.i("upload 200")
                    EventBus.getDefault().post(MainActivity.MessageEvent("editSuccessful"))
                } else if (response.code() == 401) {
                    Timber.i("upload 401")
                }
            }

        } catch (e: IOException) {
            e.printStackTrace()
            Timber.i(e.message)
        }

    }

    private fun fetchAccessToken(mContext: Context,bundle: Bundle) {
        try {
            val refreshToken = user!!.refreshToken
            Timber.i("sending refresh token %s to server", refreshToken)

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

            //for emulator "http://10.0.2.2:5000/login"
            val response = client.newCall(request).execute()

            if (response.isSuccessful) {

                val responseString = response.body()!!.string()
                Timber.i("AccessToken Response is %s", responseString)

                if (responseString == "fail") {

                    EventBus.getDefault().post(MainActivity.MessageEvent("TokenUpdateRefused"))

                } else {

                    //registration, received refresh token
                    user!!.updateTokens(user!!.refreshToken, responseString, mContext)
                    when (bundle.getString("content")) {

                        "Upload Assignment" -> {
                            uploadAssignment(bundle)
                        }
                        "Upload Attendance" -> {
                            uploadAttendance(bundle)
                        }
                        "Upload Course" -> {
                            uploadCourse(bundle)
                        }
                        "Upload Test" -> {
                            uploadTest(bundle)
                        }
                        "Edit Assignment" -> {
                            editAssignment(bundle)
                        }
                        "Edit Test" -> {
                            editTest(bundle,mContext)
                        }

                        "Delete Assignment" -> {
                            deleteAssignment(bundle)
                        }
                        "Delete Test" -> {
                            deleteTest(bundle)
                        }
                        "Upload Schedule" -> {
                            uploadSchedule(bundle)
                        }
                        "Delete Schedule" -> {
                            deleteSchedule(bundle)
                        }
                        "Edit Schedule" -> {
                            editSchedule(bundle)
                        }
                    }

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

    private fun uploadAssignment(bundle: Bundle) {
        Timber.i("assignment")

        val assignment = bundle.getSerializable("parcel") as Assignment

        val apiService = ApiClient.getClient().create(ApiInterface::class.java)
        val callTT = apiService.uploadAssignment(user!!.accessToken, assignment)

        try {
            val response = callTT.execute()
            if (response.isSuccessful) {
                if (response.code() == 200) {
                    Timber.i("upload 200")
                    EventBus.getDefault().post(MainActivity.MessageEvent(response.message()))
                } else if (response.code() == 401) {
                    Timber.i("upload 401")
                }
            }

        } catch (e: IOException) {
            e.printStackTrace()
            Timber.i(e.message)
        }

    }

    private fun uploadTest(bundle: Bundle) {
        Timber.i("test")


        val test = bundle.getSerializable("parcel") as Test

        val apiService = ApiClient.getClient().create(ApiInterface::class.java)
        val callTT = apiService.uploadTest(user!!.accessToken, test)

        try {
            val response = callTT.execute()
            if (response.isSuccessful) {
                if (response.code() == 200) {
                    EventBus.getDefault().post(MainActivity.MessageEvent(response.message()))
                } else if (response.code() == 401) {

                }
            }

        } catch (e: IOException) {
            e.printStackTrace()
            Timber.i(e.message)
        }


    }

}
