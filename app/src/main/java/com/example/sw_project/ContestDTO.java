package com.example.sw_project;

public class ContestDTO {

    //공모전 제목
    private String title;
    //공모전 d-day
    private String name;
    //공모전 마감기한
    private String due;
    //공모전 이미지
    private String imageUrl;
    //공모전 세부 링크
   private String albumID;

    public ContestDTO() {
        this.title = title;
        this.name = name;
        this.due = due;
        this.imageUrl = imageUrl;
        this.albumID = albumID;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDue(String due) {
        this.due = due;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setAlbumID(String albumID) { this.albumID = albumID; }

    public String getTitle() {
        return title;
    }

    public String getName() {
        return name;
    }

    public String getDue() {
        return due;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getAlbumID() {
        return albumID;
    }
}

