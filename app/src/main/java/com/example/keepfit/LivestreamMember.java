package com.example.keepfit;

public class LivestreamMember {
    private String title;
    private int maxNumberOfPeople;
    private String type;
    private String endTime;
    private String zoomRoomId;

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

    public String getZoomRoomId(){
        return zoomRoomId;
    }

    public void setZoomRoomId(String zoom){
        this.zoomRoomId = zoom;
    }

}
