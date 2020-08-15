package com.example.animalwelfare;

public class Posts {

    private String Uid;
    private String Username;
    private String Date;
    private String Time;
    private String Postimage;
    private String Issue;
    private String Location;
    private String Landmark;
    private String Description ;


    public Posts()
    {

    }


    public Posts(String uid, String username, String date, String time, String postimage, String issue, String location, String landmark, String description) {
        Uid = uid;
        Username = username;
        Date = date;
        Time = time;
        Postimage = postimage;
        Issue = issue;
        Location = location;
        Landmark = landmark;
        Description = description;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getPostimage() {
        return Postimage;
    }

    public void setPostimage(String postimage) {
        Postimage = postimage;
    }

    public String getIssue() {
        return Issue;
    }

    public void setIssue(String issue) {
        Issue = issue;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getLandmark() {
        return Landmark;
    }

    public void setLandmark(String landmark) {
        Landmark = landmark;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }


}
