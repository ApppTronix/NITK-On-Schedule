package com.apptronix.nitkonschedule.student.rest;


import com.apptronix.nitkonschedule.model.Assignment;
import com.apptronix.nitkonschedule.model.AssignmentResponse;
import com.apptronix.nitkonschedule.model.Course;
import com.apptronix.nitkonschedule.model.CourseResponse;
import com.apptronix.nitkonschedule.model.FcmUpdate;
import com.apptronix.nitkonschedule.model.Login;
import com.apptronix.nitkonschedule.model.Schedule;
import com.apptronix.nitkonschedule.model.ScheduleList;
import com.apptronix.nitkonschedule.model.Test;
import com.apptronix.nitkonschedule.model.TestsResponse;
import com.apptronix.nitkonschedule.model.UploadResponse;

import org.json.JSONObject;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Created by DevOpsTrends on 5/26/2017.
 */

public interface ApiInterface {

    @Multipart
    @POST("uploadFile/{filename}")
    Call<JSONObject > uploadFile(@Header("Authorization") String authorization,
                                 @Part MultipartBody.Part file, @Path("filename") String filename, @Path("course") String course);

    @GET("schedule")
    Call<ScheduleList> getSchedule(@Header("Authorization") String authorization);

    @GET("assignments")
    Call<AssignmentResponse> getAssignments(@Header("Authorization") String authorization);

    @GET("tests")
    Call<TestsResponse> getTests(@Header("Authorization") String authorization);

    @GET("courses")
    Call<CourseResponse> getCourses(@Header("Authorization") String authorization);

    @POST("enrollCourse")
    Call<UploadResponse> enrollCourse(@Header("Authorization") String authorization, @Body Course course);

    @POST("updateFCM")
    Call<UploadResponse> updateFCM(@Header("Authorization") String authorization, @Body FcmUpdate fcmUpdate);

    @POST("login")
    Call<String> googleLogin(@Body Login login);


}