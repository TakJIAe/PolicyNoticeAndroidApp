package com.aqua.anroid.policynoticeapp.Calendar;


import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

public class Event implements Serializable
{
//    public static ArrayList<Event> eventsList = new ArrayList<>(); //이벤트 목록
    public static String Event_EDIT_EXTRA = "eventEdit";

    private int id;
    public String title;
    public String startdate;
    public String enddate;
    public String date;
    public String time;


    public Event(){}


    public Event (String title, String startdate, String enddate, String date, String time)
    {
        this.title = title;
        this.startdate = startdate;
        this.enddate = enddate;
        this.date = date;
        this.time = time;

    }
    public Event (int id, String title, String startdate, String enddate, String date, String time)
    {
        this.id = id;
        this.title = title;
        this.startdate = startdate;
        this.enddate = enddate;
        this.date = date;
        this.time = time;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }

    public String setTitle(String title) {
        this.title = title;
        return title;
    }


    public String getStartdate() {
        return startdate;
    }

    public String setStartdate(String startdate) {
        this.startdate = startdate;
        return startdate;
    }

    public String getEnddate() {
        return enddate;
    }

    public void setEnddate(String enddate) {
        this.enddate = enddate;
    }
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}