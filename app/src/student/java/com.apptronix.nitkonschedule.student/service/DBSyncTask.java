package com.apptronix.nitkonschedule.student.service;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.apptronix.nitkonschedule.model.Course;
import com.apptronix.nitkonschedule.model.CourseResponse;
import com.apptronix.nitkonschedule.model.Schedule;
import com.apptronix.nitkonschedule.model.ScheduleList;
import com.apptronix.nitkonschedule.student.data.DBContract;
import com.apptronix.nitkonschedule.model.Assignment;
import com.apptronix.nitkonschedule.model.AssignmentResponse;
import com.apptronix.nitkonschedule.model.Test;
import com.apptronix.nitkonschedule.model.TestsResponse;
import com.apptronix.nitkonschedule.model.User;
import com.apptronix.nitkonschedule.rest.ApiClient;
import com.apptronix.nitkonschedule.student.rest.ApiInterface;
import com.apptronix.nitkonschedule.student.ui.MainActivity;
import com.apptronix.nitkonschedule.student.widget.TimeTableCollectionWidget;
import com.google.firebase.messaging.FirebaseMessaging;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by DevOpsTrends on 7/2/2017.
 */

public class DBSyncTask {

    private static Context mContext;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    static User user;
    static int percentage, attended;

    synchronized public static void syncData(Context context) {

        mContext=context;
        user = new User(mContext);

        fetchAccessToken();

        handleActionFetchCourses();
        handleActionFetchTimeTable();
        handleActionFetchTests();
        handleActionFetchAssignments();
        
        calculatePercentages();
        
        Intent intent = new Intent(context,TimeTableCollectionWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(context.getApplicationContext()).getAppWidgetIds(new ComponentName(context.getApplicationContext(),TimeTableCollectionWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        context.sendBroadcast(intent);
        
    }

    private static void calculatePercentages() {
        Cursor coursesCursor = mContext.getContentResolver().query(DBContract.CourseEntry.CONTENT_URI, null, null,null,null);
        if(coursesCursor!=null && coursesCursor.moveToFirst()){
            for(int i=0;i<coursesCursor.getCount();i++){
                coursesCursor.moveToPosition(i);
                String enrolled = coursesCursor.getString(coursesCursor.getColumnIndex(DBContract.CourseEntry.COLUMN_COURSE_ENROLLED));
                if(enrolled!=null && enrolled!="false"){
                    String course = coursesCursor.getString(coursesCursor.getColumnIndex(DBContract.CourseEntry.COLUMN_COURSE));
                    Cursor scheduleCursor = mContext.getContentResolver().query(DBContract.TimeTableEntry.ATTENDANCE_URI,null, DBContract.TimeTableEntry.COLUMN_COURSE+" = ? ",new String[]{course},null);
                    if(scheduleCursor.moveToFirst()){
                        percentage =0;
                        attended=0;
                        for(int j=0;j<scheduleCursor.getCount();j++){
                            scheduleCursor.moveToPosition(j);
                            String present = scheduleCursor.getString(scheduleCursor.getColumnIndex(DBContract.TimeTableEntry.COLUMN_PRESENT));
                            if(present!=null && Objects.equals(present, "false")){
                                attended++;
                            }
                        }
                        percentage=(attended*100)/scheduleCursor.getCount();
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(DBContract.CourseEntry.COLUMN_COURSE,course);
                        contentValues.put(DBContract.CourseEntry.COLUMN_ATT_PERCENT,percentage);
                        mContext.getContentResolver().update(DBContract.CourseEntry.CONTENT_URI,contentValues,
                                DBContract.CourseEntry.COLUMN_COURSE+" = ? ",new String[]{course});
                    }


                }
            }
        }
    }

    private  static void  handleActionFetchCourses() {

        user = new User(mContext);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<CourseResponse> callTT = apiService.getCourses(user.getAccessToken());

        try {
            Response<CourseResponse> response = callTT.execute();
            if(response.isSuccessful()){
                if(response.code()==200){
                    List<Course> courseList = response.body().getResults();
                    insertCourseList(courseList);
                } else if (response.code() == 401) { //bad auth
                    user.setAccessToken(null, mContext); //reset access token
                    fetchAccessToken();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    private static void handleActionFetchTests() {

        Timber.i(user.getAccessToken());
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<TestsResponse> callTT = apiService.getTests(user.getAccessToken());

        try {
            Response<TestsResponse> response = callTT.execute();
            if(response.isSuccessful()){
                if(response.code()==200){
                    List<Test> testsList = response.body().getResults();
                    insertTestsList(testsList);
                } else if( response.code() == 401) { //bad auth
                    user.setAccessToken(null, mContext); //reset access token
                    fetchAccessToken();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void insertCourseList(List<Course> courseList) {

        Vector<ContentValues> cVVector = new Vector<ContentValues>(courseList.size());

        if (courseList.size() > 0) {

            Timber.i("insert course %d",courseList.size());
            for (Course course : courseList) {
                ContentValues courseCV = new ContentValues();
                courseCV.put(DBContract.CourseEntry.COLUMN_COURSE, course.getName());
                courseCV.put(DBContract.CourseEntry.COLUMN_COURSE_DESCRIPTION, course.getDescription());
                cVVector.add(courseCV);

                FirebaseMessaging.getInstance().subscribeToTopic(course.getName());
            }
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            int inserts = mContext.getContentResolver().bulkInsert(DBContract.CourseEntry.CONTENT_URI, cvArray);
            Timber.i("%d course bulk insert success", inserts);
        }
    }

    private static void handleActionFetchAssignments() {

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<AssignmentResponse> callTT = apiService.getAssignments(user.getAccessToken());

        try {
            Response<AssignmentResponse> response = callTT.execute();
            if(response.isSuccessful()){
                if(response.code()==200){
                    List<Assignment> assgnList = response.body().getResults();
                    insertAssignmentsList(assgnList);
                } else if( response.code() == 401) { //bad auth
                    user.setAccessToken(null, mContext); //reset access token
                    fetchAccessToken();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void fetchAccessToken() {

        User user = new User(mContext);
        String refreshToken = user.getRefreshToken();
        Timber.i("sending refresh token %s to server",refreshToken);

        try {

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(30, TimeUnit.SECONDS);
            builder.readTimeout(30, TimeUnit.SECONDS);
            builder.writeTimeout(30, TimeUnit.SECONDS);
            OkHttpClient client = builder.build();

            JSONObject postJSON = new JSONObject();
            postJSON.put("refreshToken",refreshToken);
            RequestBody body = RequestBody.create(JSON, String.valueOf(postJSON));
            Request request = new Request.Builder()
                    .url(new URL(ApiClient.BASE_URL+"getAccessToken"))
                    .post(body)
                    .build();


            okhttp3.Response response = client.newCall(request).execute();

            if(response.isSuccessful()){

                String responseString = response.body().string();
                Timber.i("AccessToken Response is %s",responseString);

                if(responseString.equals("fail")){

                    EventBus.getDefault().post(new MainActivity.MessageEvent("TokenUpdateRefused"));

                } else {

                    //registration, received refresh token
                    user.updateTokens(user.getRefreshToken(),responseString,mContext);

                }

            } else {

                EventBus.getDefault().post(new MainActivity.MessageEvent("ServerUnreachable"));

            }

        } catch (IOException | JSONException e1) {
            e1.printStackTrace();
            EventBus.getDefault().post(new MainActivity.MessageEvent("ServerUnreachable"));
        }
    }

    private static void handleActionFetchTimeTable(){

        user = new User(mContext);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ScheduleList> callTT = apiService.getSchedule(user.getAccessToken());
        Timber.i("user AT %s",user.getAccessToken());
        try {
            Response<ScheduleList> response = callTT.execute();
            if(response.isSuccessful()){
                if(response.code()==200){
                    List<Schedule> timeTableList = response.body().getResults();
                    insertTimeTableList(timeTableList);
                } else if( response.code() == 401) { //bad auth
                    user.setAccessToken(null, mContext); //reset access token
                    fetchAccessToken();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void insertTestsList(List<Test> testsList) {

        Vector<ContentValues> cVVector = new Vector<ContentValues>(testsList.size());

        for(Test test:testsList){
            ContentValues testCV = new ContentValues();
            testCV.put(DBContract.TestsEntry.COLUMN_COURSE_CODE,test.getCourse());
            testCV.put(DBContract.TestsEntry.COLUMN_TITLE,test.getTitle());
            testCV.put(DBContract.TestsEntry.COLUMN_SYLLABUS,test.getPortions());
            testCV.put(DBContract.TestsEntry.COLUMN_TEST_DATE,test.getTestDate());
            testCV.put(DBContract.TestsEntry.COLUMN_WEIGHTAGE,test.getWeightage());
            cVVector.add(testCV);
        }

        if ( cVVector.size() > 0 ) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            int inserts=mContext.getContentResolver().bulkInsert(DBContract.TestsEntry.CONTENT_URI, cvArray);
            Timber.i("%d tests bulk insert success", inserts);
        }

    }

    private static void insertAssignmentsList(List<Assignment> assgnList) {

        Vector<ContentValues> cVVector = new Vector<ContentValues>(assgnList.size());

        for(Assignment assgn:assgnList){
            ContentValues assgnCV = new ContentValues();
            assgnCV.put(DBContract.AssignmentsEntry.COLUMN_COURSE_CODE,assgn.getCourse());
            assgnCV.put(DBContract.AssignmentsEntry.COLUMN_TITLE,assgn.getTitle());
            assgnCV.put(DBContract.AssignmentsEntry.COLUMN_DESCRIPTION,assgn.getDescription());
            assgnCV.put(DBContract.AssignmentsEntry.COLUMN_SUBMISSION_DATE,assgn.getSubmissionDate());
            assgnCV.put(DBContract.AssignmentsEntry.COLUMN_WEIGHTAGE,assgn.getWeightage());
            cVVector.add(assgnCV);
        }

        if ( cVVector.size() > 0 ) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            int inserts=mContext.getContentResolver().bulkInsert(DBContract.AssignmentsEntry.CONTENT_URI, cvArray);
            Timber.i("%d assignments bulk insert success", inserts);
        }

    }


    private static void insertTimeTableList(List<Schedule> timeTableList) {

        Vector<ContentValues> cVVector = new Vector<ContentValues>(timeTableList.size()*8);

        for(Schedule dayTable:timeTableList){

            ContentValues timeTableCV = new ContentValues();
            timeTableCV.put(DBContract.TimeTableEntry.COLUMN_DATE,dayTable.getDate());
            timeTableCV.put(DBContract.TimeTableEntry.COLUMN_COURSE,dayTable.getCourse());
            timeTableCV.put(DBContract.TimeTableEntry.COLUMN_TIME,dayTable.getTime());
            timeTableCV.put(DBContract.TimeTableEntry.COLUMN_DESCRIPTION,dayTable.getDescription());
            timeTableCV.put(DBContract.TimeTableEntry.COLUMN_PRESENT,dayTable.getPresentIDs());
            cVVector.add(timeTableCV);


        }

        if ( cVVector.size() > 0 ) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            int inserts=mContext.getContentResolver().bulkInsert(DBContract.TimeTableEntry.CONTENT_URI, cvArray);
            Timber.i("%d day's time table bulk insert success", inserts);
        }
    }

}
