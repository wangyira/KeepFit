package com.example.keepfit;

import java.util.Date;

public class Comment {
    private String text;
    private String postingUsername;
    private Date postedTime;

    public Comment(String t, String pu, Date pt){
        text = t;
        postingUsername = pu;
        postedTime = pt;
    }

    public Comment(){}
    public String getText(){ return text; }

    public String getPostingUsername(){ return postingUsername; }

    public Date getPostedTime(){ return postedTime; }

}
