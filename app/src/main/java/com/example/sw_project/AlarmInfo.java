package com.example.sw_project;

import java.text.SimpleDateFormat;

public class AlarmInfo implements Comparable<AlarmInfo>{

    private String destinationUid;
    private String userNickname;
    private String uid;
    private int kind;
    private String message;
    private Long timeStamp;
    private boolean isRead;
    private String alarmDocument;
    private boolean isCheck = false;
    private String postId;

    public AlarmInfo() {
    }

   @Override
    public int compareTo(AlarmInfo alarmInfo) {

       SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy.MM.dd.HH.mm.ss");

       String a = simpleDateFormat.format(alarmInfo.getTimeStamp());
       String b = simpleDateFormat.format(timeStamp);
        if(a.compareTo(b) > 0){
            return 1;
        }else if(a.compareTo(b) < 0){
            return -1;
        }else{
            return 0;
        }
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getDestinationUid() {
        return destinationUid;
    }

    public void setDestinationUid(String destinationUid) {
        this.destinationUid = destinationUid;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getKind() {
        return kind;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(boolean read) {
        isRead = read;
    }

    public String getAlarmDocument() {
        return alarmDocument;
    }

    public void setAlarmDocument(String alarmDocument) {
        this.alarmDocument = alarmDocument;
    }

    public boolean getIsCheck() {
        return isCheck;
    }

    public void setIsCheck(boolean check) {
        isCheck = check;
    }
}
