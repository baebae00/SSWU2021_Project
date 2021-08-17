package com.example.sw_project;

import android.app.Activity;
import android.content.SharedPreferences;

public class MySharedPreferences {

    private String MY_ACCOUNT = "myAccount";
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public MySharedPreferences(Activity context){
        prefs = context.getSharedPreferences(MY_ACCOUNT, Activity.MODE_PRIVATE);
    }

    public void setUserId(String userId){
        editor = prefs.edit();
        editor.putString("userId",userId);
        editor.commit();
    }

    public String getUserId(){
        return prefs.getString("userId","");
    }

    public void setUserPw(String userPw){
        editor = prefs.edit();
        editor.putString("userPw",userPw);
        editor.commit();
    }

    public String getUserPw(){
        return prefs.getString("userPw","");
    }

    public void clearUser(){
        editor = prefs.edit();
        editor.clear();
        editor.commit();
    }

}
