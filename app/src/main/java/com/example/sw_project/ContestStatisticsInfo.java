package com.example.sw_project;

import java.util.ArrayList;
import java.util.HashMap;

public class ContestStatisticsInfo {

    //{a,b} a: 공모전 참여횟수, b: 학생

    private HashMap<String, ArrayList> schoolNum;
    private HashMap<String, ArrayList> major;

    public HashMap<String, ArrayList> getSchoolNum() {
        return schoolNum;
    }

    public void setSchoolNum(HashMap<String, ArrayList> schoolNum) {
        this.schoolNum = schoolNum;
    }

    public HashMap<String, ArrayList> getMajor() {
        return major;
    }

    public void setMajor(HashMap<String, ArrayList> major) {
        this.major = major;
    }
}
