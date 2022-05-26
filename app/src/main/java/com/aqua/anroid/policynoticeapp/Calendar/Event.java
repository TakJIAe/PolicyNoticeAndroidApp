package com.aqua.anroid.policynoticeapp.Calendar;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;

public class Event implements Serializable
{
    public static ArrayList<Event> eventsList = new ArrayList<>(); //이벤트 목록
    public static String Event_EDIT_EXTRA = "eventEdit";

    private int id;
    public  String title;
    private LocalDate date;
    private LocalTime time;
    public  String startdate;
    public  String enddate;
    private Date deleted;

    // 주어진 날짜에 대한 모든 이벤트 반환
    public static ArrayList<Event> eventsForDate(LocalDate date) {
        ArrayList<Event> events = new ArrayList<>();

        for (Event event : eventsList)
        {
            // 삭제되지 않은 이벤트만 채우기
            if(event.getDeleted()==null)
            {
                //이벤트 날짜가 지난 날짜와 같으면 이벤트 목록에서 해당 이벤트 반환
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String selectDate1 = date.toString();   //현재누른날짜
                String startDate1 = event.getStartdate();  //시작날짜
                String endDate1 = event.getEnddate();  //종료날짜
                Date selectDate = null;


                try {
                    selectDate = dateFormat.parse(selectDate1);

                    Date startDate  = dateFormat.parse(startDate1);
                    Date endDate    = dateFormat.parse(endDate1);

                    int result1 = selectDate.compareTo(startDate);       // curr > d1
                    int result2 = selectDate.compareTo(endDate);

                    // 조건이 맞을때
                    if((result1>=0)&&(result2<=0))
                        events.add(event);
                }
                catch (ParseException e) {
                    e.printStackTrace();
                }
                /*if (event.getDate().equals(date))
                {
                    events.add(event);
                }*/
            }
        }

        return events;
    }

    public Event(){}
    public Event (int id, String title, LocalDate date, LocalTime time, String startdate, String enddate, Date deleted)
    {
        this.id = id;
        this.title = title;
        this.date = date;
        this.time = time;
        this.startdate = startdate;
        this.enddate = enddate;
        this.deleted= deleted;

    }

    public Event (int id, String title, LocalDate date,  String startdate, String enddate, LocalTime time)
    {
        this.id = id;
        this.title = title;
        this.date = date;
        this.time = time;
        this.startdate = startdate;
        this.enddate = enddate;
        deleted = null;

    }

    public static Event getEventForID(int passedEventID)
    {
        for(Event event : eventsList)
        {
            if(event.getId() == passedEventID)
                return event;
        }
        return null;
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public Date getDeleted() {
        return deleted;
    }

    public void setDeleted(Date deleted) {
        this.deleted = deleted;
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
}