package com.example.keepfit;

public class VideoReference {
    private String difficulty;
    private int numLikes;
    private String referenceTitle;
    private String tag;
    private String time;
    private String title;
    private String uploadingUser;
    private Boolean commentsAllowed;

    public VideoReference(String diff, Integer nl, String rt, String ta, String tim, String titl, String uu, Boolean ac){
        difficulty = diff;
        numLikes = nl;
        referenceTitle = rt;
        tag = ta;
        time = tim;
        title = titl;
        uploadingUser = uu;
        commentsAllowed = ac;

    }

    public VideoReference(){}

    public String getDifficulty(){
        return difficulty;
    }

    public int getNumLikes() {
        return numLikes;
    }

    public String getReferenceTitle() {
        return referenceTitle;
    }

    public String getTag() {
        return tag;
    }

    public String getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }

    public String getUploadingUser() {
        return uploadingUser;
    }

    public Boolean getCommentsAllowed(){ return commentsAllowed; }

}
