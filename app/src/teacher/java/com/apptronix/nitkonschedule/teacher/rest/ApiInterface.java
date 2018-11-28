package com.apptronix.nitkonschedule.teacher.rest;


import com.apptronix.nitkonschedule.student.model.Assignment;
import com.apptronix.nitkonschedule.student.model.AssignmentResponse;
import com.apptronix.nitkonschedule.student.model.Course;
import com.apptronix.nitkonschedule.student.model.CourseResponse;
import com.apptronix.nitkonschedule.student.model.FcmUpdate;
import com.apptronix.nitkonschedule.student.model.Login;
import com.apptronix.nitkonschedule.student.model.ResourcesResponse;
import com.apptronix.nitkonschedule.student.model.Schedule;
import com.apptronix.nitkonschedule.student.model.ScheduleList;
import com.apptronix.nitkonschedule.student.model.Test;
import com.apptronix.nitkonschedule.student.model.TestsResponse;
import com.apptronix.nitkonschedule.student.model.UploadResponse;

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
    @POST("uploadFile/{course}/{filename}")
    Call<JSONObject > uploadFile(@Header("Authorization") String authorization,
                                 @Part MultipartBody.Part file,@Path("filename") String filename,@Path("course") String course );


    @Multipart
    @POST("uploadAttendanceImage/{course}/{filename}")
    Call<JSONObject > uploadAttendanceImage(@Header("Authorization") String authorization,
                                 @Part MultipartBody.Part file,@Path("filename") String filename,@Path("course") String course );

    // Schedule

    @GET("schedule")
    Call<ScheduleList> getSchedule(@Header("Authorization") String authorization);

    @POST("editSchedule")
    Call<UploadResponse> editSchedule(@Header("Authorization") String authorization, @Body Schedule schedule);

    @POST("deleteSchedule")
    Call<UploadResponse> deleteSchedule(@Header("Authorization") String authorization, @Body Schedule schedule);

    @POST("uploadSchedule")
    Call<UploadResponse> uploadSchedule(@Header("Authorization") String authorization, @Body Schedule schedule);


    @POST("uploadAttendance")
    Call<UploadResponse> uploadAttendance(@Header("Authorization") String authorization, @Body Schedule schedule);

    // get

    @GET("assignments")
    Call<AssignmentResponse> getAssignments(@Header("Authorization") String authorization);

    @GET("tests")
    Call<TestsResponse> getTests(@Header("Authorization") String authorization);

    @GET("courses")
    Call<CourseResponse> getCourses(@Header("Authorization") String authorization);

    @GET("resources")
    Call<ResourcesResponse> getResources(@Header("Authorization") String authorization);


    // Uploads

    @POST("uploadAssignment")
    Call<UploadResponse> uploadAssignment(@Header("Authorization") String authorization, @Body Assignment assignment);

    @POST("uploadTest")
    Call<UploadResponse> uploadTest(@Header("Authorization") String authorization, @Body Test test);

    @POST("uploadCourse")
    Call<UploadResponse> uploadCourse(@Header("Authorization") String authorization, @Body Course course);

    @POST("editAssignment")
    Call<UploadResponse> editAssignment(@Header("Authorization") String authorization, @Body Assignment assignment);

    @POST("editTest")
    Call<UploadResponse> editTest(@Header("Authorization") String authorization, @Body Test test);

    @POST("deleteAssignment")
    Call<UploadResponse> deleteAssignment(@Header("Authorization") String authorization, @Body Assignment assignment);

    @POST("deleteTest")
    Call<UploadResponse> deleteTest(@Header("Authorization") String authorization, @Body Test test);

    @POST("updateFCM")
    Call<UploadResponse> updateFCM(@Header("Authorization") String authorization, @Body FcmUpdate fcmUpdate);

    @POST("login")
    Call<String> googleLogin(@Body Login login);


}