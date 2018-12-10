package com.apptronix.nitkonschedule.student.rest;


import com.apptronix.nitkonschedule.student.model.AssignmentResponse;
import com.apptronix.nitkonschedule.student.model.Course;
import com.apptronix.nitkonschedule.student.model.CourseResponse;
import com.apptronix.nitkonschedule.student.model.FcmUpdate;
import com.apptronix.nitkonschedule.student.model.Login;
import com.apptronix.nitkonschedule.student.model.ScheduleList;
import com.apptronix.nitkonschedule.student.model.TestsResponse;
import com.apptronix.nitkonschedule.student.model.UploadResponse;

import org.json.JSONObject;

import java.util.List;

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


    @Multipart
    @POST("uploadFaceImages")
    Call<UploadResponse > uploadFaceImages(@Header("Authorization") String authorization, @Part List<MultipartBody.Part> file);


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