package com.example.sw_project;

public class StudentInfo {

    private String email;
    private String userName;
    private String college;
    private String department;
    private String studentId;
    private String id;
    private String contestParticipate;
    private String uid;

    public StudentInfo(String email, String userName, String college, String department, String studentId, String id, String contestParticipate, String uid){
        this.email = email;
        this.userName = userName;
        this.college = college;
        this.department = department;
        this.studentId = studentId;
        this.id = id;
        this.contestParticipate = contestParticipate;
        this.uid = uid;
    }

    public StudentInfo() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) { this.studentId = studentId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContestParticipate() {
        return contestParticipate;
    }

    public void setContestParticipate(String contestParticipate) {
        this.contestParticipate = contestParticipate;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
