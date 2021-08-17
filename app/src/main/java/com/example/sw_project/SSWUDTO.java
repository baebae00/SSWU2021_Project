package com.example.sw_project;

public class SSWUDTO {
    // private String title;
    private String name;
    private String reportDate;
    private String albumID;

    public SSWUDTO() {
        // this.title = title;
        this.name = name;
        this.reportDate = reportDate;
        this.albumID = albumID;
    }

    /* public void setTitle(String title) {
        this.title = title;
    } */

    public void setName(String name) {
        this.name = name;
    }

    public void setReportDate(String reportDate) {this.reportDate = reportDate; }

    public void setAlbumID(String albumID) { this.albumID = albumID; }

    /* public String getTitle() {
        return title;
    } */

    public String getName() {
        return name;
    }

    public String getReportDate() {return reportDate; }

    public String getAlbumID() {
        return albumID;
    }
}

