package com.aqua.anroid.policynoticeapp.Calendar;


import static com.aqua.anroid.policynoticeapp.Calendar.CalendarUtils.daysInMonthArray;
import static com.aqua.anroid.policynoticeapp.Calendar.CalendarUtils.monthYearFromDate;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aqua.anroid.policynoticeapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CalendarActivity extends AppCompatActivity implements CalendarAdapter.OnItemListener {
    private static String IP_ADDRESS = "10.0.2.2";
    private static String TAG = "phptest";
    private static final String TAG_JSON = "root";

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private ListView eventListView;

    String userID;
    String mJsonString;

    public static ArrayList<Event> eventsList = new ArrayList<>(); //이벤트 목록
    static ArrayList<Event> events;

    String title;
    String startdate;
    String enddate;
    String date;
    String time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        SharedPreferences sharedPreferences = getSharedPreferences("userID", MODE_PRIVATE);
        userID = sharedPreferences.getString("userID", "");

        GetData task = new GetData();
        task.execute(userID);
        initWidgets();
        CalendarUtils.selectedDate = LocalDate.now(); //현재 날짜
        setMonthView(); //화면 설정
//        Edit();
    }

 /*   private void EventEdit() {
        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int po = events.get(position);

            }
        });
    }*/

    private void initWidgets() {
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthYearText = (TextView) findViewById(R.id.monthYearTV);
        eventListView = findViewById(R.id.eventListView);
    }


    private void setMonthView() {
        //년 월 텍스트뷰
        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));

        //해당 월 날짜 가져오기
        ArrayList<LocalDate> daysInMonth = daysInMonthArray(CalendarUtils.selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this);

        //레이아웃 설정, 열 7개
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);

        setEventAdapter();
    }

    public void previousMonthAction(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusMonths(1);
        setMonthView();
    }

    public void nextMonthAction(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusMonths(1);
        setMonthView();
    }


    @Override
    public void onItemClick(int position, LocalDate date) {
        //현재 날짜로 변경하는 함수를 호출하여
        //CalendarUtils.selectedDate 가 날짜와 같도록 설정
        if (date != null) {
            CalendarUtils.selectedDate = date;
            setMonthView();
        }
    }

    // 이벤트 Adapter 제공
    private void setEventAdapter() {
        //ID 로 목록 찾고 리스트 호출
        ArrayList<Event> dailyEvents = eventsForDate(CalendarUtils.selectedDate);
        EventAdapter eventAdapter = new EventAdapter(this, this, dailyEvents);
        eventListView.setAdapter(eventAdapter);
//        eventAdapter.notifyDataSetChanged();

    }

    public void newEventAction(View view) {
        startActivity(new Intent(CalendarActivity.this, EventEditActivity.class));
    }

/*    private void Edit(){

        ArrayList<Event> events= new ArrayList<Event>();
        EventAdapter eventAdapter = new EventAdapter(this,  this, events);
        eventListView.setAdapter(eventAdapter);

        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                *//*Event item = (Event) eventListView.getItemAtPosition(position);
                String title = item.getTitle();
                Log.e("title", title);*//*

                Intent intent = new Intent(CalendarActivity.this, EventEditActivity.class);

                startActivity(intent);
            }
        });
    }*/

    // 재개될 때마다 다시 로드되도록 EventAdapter 호출
    //Activity가 사용자와 상호작용하기 바로 전에 호출됨
    @Override
    protected void onResume() {
        super.onResume();
        setEventAdapter();
//        eventsList.clear();
    }

    // 주어진 날짜에 대한 모든 이벤트 반환
    public static ArrayList<Event> eventsForDate(LocalDate date) {
//        ArrayList<Event> events = new ArrayList<>();
        events = new ArrayList<>();

        for (int k = 0; k < eventsList.size(); k++) {
//            items.add(new Event(titles.get(k),startdates.get(k),enddates.get(k)));

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String selectDate1 = date.toString();   //현재누른날짜
            String startDate1 = eventsList.get(k).getStartdate();  //시작날짜
            String endDate1 = eventsList.get(k).getEnddate();  //종료날짜
            Date selectDate = null;

            /*Log.d("시작날짜", startDate1);*/


            try {
                selectDate = dateFormat.parse(selectDate1);

                Date startDate = dateFormat.parse(startDate1);
                Date endDate = dateFormat.parse(endDate1);

                int result1 = selectDate.compareTo(startDate);       // curr > d1
                int result2 = selectDate.compareTo(endDate);

                // 조건이 맞을때
                if ((result1 >= 0) && (result2 <= 0))  //선택한 날짜가 시작날짜랑 같거나 크고 & 앤드날짜보다 작거나 같으면
                    events.add(eventsList.get(k));   //items.get(k)가  events 어레이에 더해짐
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return events;
    }

    // 주어진 날짜에 대한 time 반환
    public static int eventsForTime(String passedtime) {
        for (int i = 0; i < eventsList.size(); i++) {
            String time = eventsList.get(i).getTime();  //시작날짜
//            Log.e("time"+i+"번째", time);

            if(time != null && passedtime != null) {
                String[] savetime = time.split(":");
                String save1 = savetime[0];
//            Log.e("save" +i+"번째", save1);
                String save2 = savetime[1];
                String save3 = savetime[2];


                String[] passtime = passedtime.split(":");
                String pass1 = passtime[0];
//            Log.e("parse" +i+"번째", parse1);
                String pass2 = passtime[1];
                String pass3 = passtime[2];

                //각각 일치하면 0 리턴하므로 합계 0일경우 모두 일치한다.
                if (save1.compareTo(pass1) + save2.compareTo(pass2) + save3.compareTo(pass3) == 0) {
                    Log.e("compare", "ok");

                    return 1;
                }
            }

//            int result = time.compareTo(date); // 맞으면 0
            // 조건이 맞을때
//            if (date.equals(time) && time.equals(date))

            /*
            if(date.equals(time)) {
                Log.e("test", String.valueOf(date == time));
                return 1;
            } //가로 치우면 작동됨.....무조건 1돼서 그런가

            */
        }
        return 0;
    }


    //시간 정하면 호출되는 메소드
    public void onTimeSet(int hourOfDay, int minute){
        Log.d("alert", "alert set");
        Calendar c = Calendar.getInstance();

        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE,minute);
        c.set(Calendar.SECOND,0);
//        updateTimeText(c);

        startAlarm(c);
    }

    private void startAlarm(Calendar c) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent,0);

        if(c.before(Calendar.getInstance())){
            c.add(Calendar.DATE,1);
        }

        //RTC_WAKE : 지정된 시간에 기기의 절전 모드 해제하여 대기 중인 intent 실행
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),pendingIntent);
    }
/*

    //사용자가 선택한 시간
    private void updateTimeText(Calendar c) {
        String TimeText ="알람시간"
    }
*/


    // 이벤트 가져오기
    class GetData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(CalendarActivity.this,
                    "Please Wait", null, true, true);


        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "response - " + result);

            if (result != null) {
                mJsonString = result;

                showResult();
            }

        }


        @Override
        protected String doInBackground(String... params) {

            String searchKeyword1 = params[0];

            String serverURL = "http://10.0.2.2/event_query.php";
            String postParameters = "userID=" + searchKeyword1;

            Log.d(TAG, "userID_event : " + searchKeyword1);

            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString().trim();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }

        }


        public void showResult() {

            try {
                JSONObject jsonObject = new JSONObject(mJsonString);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
                eventsList.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    if (jsonArray.length() != 0) {
                        JSONObject item = jsonArray.getJSONObject(i);
                        Log.d(TAG, "JSONObject : " + item);

                        title = item.getString("title");
                        startdate = item.getString("startdate");
                        enddate = item.getString("enddate");
                        date = item.getString("date");
                        time = item.getString("time");

//                    titles.add(title); //items_name ArrayList에 php에서 받아온 item_name 추가
//                    startdates.add(startdate); //items_name ArrayList에 php에서 받아온 item_name 추가
//                    enddates.add(enddate); //items_content ArrayList에 php에서 받아온 item_content 추가

                        eventsList.add(new Event(title, startdate, enddate, date, time));

                        Log.d(TAG, "eventsList : " + eventsList.toString());

                    }
                }
            } catch (JSONException e) {

                Log.d(TAG, "showResult_member : ", e);
            }

        }
    }

}