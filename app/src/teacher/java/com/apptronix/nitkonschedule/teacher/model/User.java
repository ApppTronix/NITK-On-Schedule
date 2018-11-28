package com.apptronix.nitkonschedule.teacher.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import androidx.annotation.Nullable;

/**
 * Created by DevOpsTrends on 6/10/2017.
 */

public class User {
    private String userName;
    private String email;
    private String picture;
    private String userID;
    private String fcmID;
    private String refreshToken;
    private String accessToken;
    private Long accessTokenTime;

    public User(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        setUserName(prefs.getString("userName",null));
        setEmail(prefs.getString("email",null));
        setPicture(prefs.getString("picture",null));
        setUserID(prefs.getString("userID",null));
        setRefreshToken(prefs.getString("refreshToken",null));
        setAccessToken(prefs.getString("accessToken",null));
        setFcmID(prefs.getString("fcmID",null));
    }

    public String getFcmID() {
        return fcmID;
    }

    public void setFcmID(String fcmID) {
        this.fcmID=fcmID;
    }

    public void setFcmID(String fcmID, Context context) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString("fcmID",fcmID)
                .apply();
    }

    public void makeUser(Context context, GoogleSignInAccount account){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString("userName", account.getDisplayName())
                .putString("email", account.getEmail())
                .putString("picture", String.valueOf(account.getPhotoUrl()))
                .putString("userID",account.getId())
                .apply();

    }
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updateTokens(String refreshToken,@Nullable String accessToken, Context context){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString("refreshToken", refreshToken)
                .putString("accessToken", accessToken)
                .apply();
        this.accessToken=accessToken;
        this.refreshToken=refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(@Nullable String accessToken, Context context) {
        updateTokens(refreshToken,accessToken, context);
    }

    public void setAccessToken(String accessToken) {
        this.accessToken=accessToken;
    }

    public void signOutUser(Context context){
        userName=null;
        email=null;
        picture=null;
        userID=null;
        accessToken=null;
        refreshToken=null;
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString("userName", null)
                .putString("email", null)
                .putString("picture", null)
                .putString("userID",null)
                .putString("refreshToken", null)
                .putString("accessToken", null)
                .apply();
    }

    public Long getAccessTokenTime() {
        return accessTokenTime;
    }

    public void setAccessTokenTime(Long accessTokenTime) {
        this.accessTokenTime = accessTokenTime;
    }
}