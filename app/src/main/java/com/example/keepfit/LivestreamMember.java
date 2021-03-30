package com.example.keepfit;

public class LivestreamMember {
    private String title;
    private int maxNumberOfPeople;
    private String type;
    private String endTime;
    private String zoomLink;
    private String referenceTitle = "noThumbnail";
    private String uploadingUser;

    public LivestreamMember(){

    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public int getMaxNumberOfPeople(){
        return maxNumberOfPeople;
    }

    public void setMaxNumberOfPeople(int ppl){
        this.maxNumberOfPeople = ppl;
    }

    public String getExerciseType(){
        return type;
    }

    public void setExerciseType(String type){
        this.type = type;
    }

    public String getEndTime(){
        return endTime;
    }

    public void setEndTime(String time){
        this.endTime = time;
    }

    public String getZoomLink(){
        return zoomLink;
    }

    public void setZoomLink(String zoom){
        this.zoomLink = zoom;
    }

    public String getReferenceTitle(){
        return referenceTitle;
    }

    public void setReferenceTitle(String referenceTitle){
        this.referenceTitle = referenceTitle;
    }

    public String getUploadingUser(){
        return uploadingUser;
    }

    public void setUploadingUser(String username){
        this.uploadingUser = username;
    }
}
