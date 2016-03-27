package com.jishuli.Moco;

public class ClassDetailItem {
    public int typeID;
    public int courseID;
    public String courseName;
    public int score;
    public String beginTime;
    public String endTime;
    public double lat;
    public double lng;
    public String institutionID;
    public String desctiption;
    public String name;

    public ClassDetailItem(int typeID, int courseID, String courseName, int score, String beginTime, String endTime,
                           double lat, double lng, String institutionID, String desctiption ,String name){
        this.typeID = typeID;
        this.courseID = courseID;
        this.courseName = courseName;
        this.score = score;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.lat = lat;
        this.lng = lng;
        this.institutionID = institutionID;
        this.desctiption = desctiption;
        this.name = name;
    }
}