package com.aqua.anroid.policynoticeapp.Calendar;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import com.aqua.anroid.policynoticeapp.R;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;

import java.util.ArrayList;
import java.util.Date;

//public class EventEditActivity extends AppCompatActivity implements EventListener {
public class EventEditActivity extends AppCompatActivity {

    private static String IP_ADDRESS = "10.0.2.2";
    private static String EDITTAG = "editphp";
    private static String SAVETAG = "savephp";

    private EditText eventTitleET;
    private TextView Date, Time, startDateTV, endDateTV;
    private Button eventDatePickerBtn, eventSaveBtn;

    private LocalTime time; // 현지 시간으로 시간 호출
    Event event = new Event();

    static int result;
    String userID;
    String editTitle,editStartdate,editEnddate,editDate,editTime;
    String passedEventTime;
    String setTime, setDate;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit);
        initWidgets();

        time = LocalTime.now(); // 지금 현지 시간으로 초기화
        setTime = CalendarUtils.formattedTime(time);
        setDate = String.valueOf(CalendarUtils.selectedDate);

        Date.setText(setDate);
        Time.setText(setTime);

        SharedPreferences sharedPreferences = getSharedPreferences("userID",MODE_PRIVATE);
        userID  = sharedPreferences.getString("userID","");

        checkForEditEvent();
//        saveEventBtn();
    }

    private void initWidgets()
    {
        eventTitleET = findViewById(R.id.eventTitleET);
        startDateTV = findViewById(R.id.startDateTV);
        endDateTV = findViewById(R.id.endDateTV);
        eventDatePickerBtn = findViewById(R.id.eventDatePickerBtn);
        eventSaveBtn = findViewById(R.id.eventSaveBtn);
        Date = findViewById(R.id.Date);
        Time = findViewById(R.id.Time);
    }


    public void checkForEditEvent(){
        Intent previousIntent = getIntent();
        Intent intent = getIntent();
        passedEventTime =  previousIntent.getStringExtra(Event.Event_EDIT_EXTRA);

        // 받아온 시간과 같은 time이 존재한다면 = 1, 아니면 0
        result = CalendarActivity.eventsForTime(passedEventTime);

        if(result == 1)
        {

            Log.e("intentresult" , String.valueOf(result));

            editTitle = intent.getStringExtra("title");
            editStartdate =intent.getStringExtra("startdate");
            editEnddate = intent.getStringExtra("enddate");
            editDate = intent.getStringExtra("date");
            editTime = intent.getStringExtra("time");

            // 수정할 리스트뷰 정보 가져옴
            eventTitleET.setText(editTitle);
            startDateTV.setText(editStartdate);
            endDateTV.setText(editEnddate);
            Date.setText(editDate);
            Time.setText(editTime);


        }
//        startActivity(new Intent(this, CalendarActivity.class));

    }

    public void saveEventBtn(View view) {

        // 새로 생성
        if(result != 1) {
            String eventTitle = eventTitleET.getText().toString();
            String eventStartDate = startDateTV.getText().toString();
            String eventEndDate = endDateTV.getText().toString();
            String eventDate = Date.getText().toString();
            String eventTime = Time.getText().toString();
            Log.e("result(0)_save..", "save data..");
            InsertEvent inserttask = new InsertEvent();
            inserttask.execute("http://" + IP_ADDRESS + "/event_insert.php", userID, eventTitle, eventStartDate, eventEndDate, eventDate, eventTime);

        }

        if(result == 1)
        {
//            eventTitleET.setText(editTitle);
//            startDateTV.setText(editStartdate);
//            endDateTV.setText(editEnddate);
//            Date.setText(editDate);
//            Time.setText(editTime);

            String updateTitle = eventTitleET.getText().toString();
            String updateStartDate = startDateTV.getText().toString();
            String updateEndDate = endDateTV.getText().toString();
            String updateDate = Date.getText().toString();
            String updateTime = Time.getText().toString();

            Log.e("result(1)_edit..","edit data..");
            Log.e("edit..passedtime","edit data.."+ passedEventTime);
            UpdateEvent updatetask = new UpdateEvent();
            updatetask.execute("http://" + IP_ADDRESS + "/event_update.php",
                    updateTitle, updateStartDate,updateEndDate,updateDate,updateTime, passedEventTime);


            //새로 저장할땐 time, date 새롭게 저장하도록 수정해야함 !!! ***********************

         /*   eventSaveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UpdateEvent updatetask = new UpdateEvent();
                    updatetask.execute("http://" + IP_ADDRESS + "/event_update.php",
                            updateTitle, updateStartDate,updateEndDate,updateDate,updateTime, passedEventTime);

                }
            });
*/
        }

        startActivity(new Intent(this, CalendarActivity.class));
    }

    //날짜 선택
    public void datepickerAction(View view) {
        MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.dateRangePicker();

        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("날짜를 선택해주세요");
        long today = MaterialDatePicker.todayInUtcMilliseconds();
        materialDateBuilder.setSelection(today);

        //미리 날짜 선택
//        builder.setSelection(Pair.create(MaterialDatePicker.thisMonthInUtcMilliseconds(), MaterialDatePicker.todayInUtcMilliseconds()));
        final MaterialDatePicker materialDatePicker = builder.build();
        materialDatePicker.show(getSupportFragmentManager(), "Date_PICKER");

        materialDatePicker.addOnPositiveButtonClickListener(
                new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                    @Override
                    public void onPositiveButtonClick(Pair<Long, Long> selection) {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Date startdate = new Date();
                        Date enddate = new Date();

                        startdate.setTime(selection.first);
                        enddate.setTime(selection.second);

                        String startdateString = simpleDateFormat.format(startdate);
                        String enddateString = simpleDateFormat.format(enddate);

                        startDateTV.setText(startdateString);
                        endDateTV.setText(enddateString);

                        //선택한 날짜 객체 저장
                        event.setStartdate(startdateString);
                        event.setEnddate(enddateString);
                    }
                });

    }


 /*   @Override
    public void onEventSave() {
        String eventTitle = eventTitleET.getText().toString();
        String eventStartDate = startDateTV.getText().toString();
        String eventEndDate = endDateTV.getText().toString();
        String eventDate = Date.getText().toString();
        String eventTime = Time.getText().toString();


        if (selectedEvent == null) {
            //int id = Event.eventsList.size();
            InsertData task = new InsertData();
            task.execute("http://" + IP_ADDRESS + "/event_insert.php", userID, eventTitle, eventStartDate,eventEndDate,eventDate,eventTime);


        }*/
    /*
        // 편집 모드
        else
        {
            //선택한 메모에 제목을 가져와 동일하게 지정
            selectedEvent.setTitle(eventTitle);
            selectedEvent.setStartdate(eventStartDate);
            selectedEvent.setEnddate(eventEndDate);

            // db 업데이트 하기
            // ...
        }
        startActivity(new Intent(this, CalendarActivity.class));
    }
    */

/*
    ArrayList<Event> dailyEvents = eventsForDate(CalendarUtils.selectedDate);
    EventAdapter eventAdapter = new EventAdapter(this, this, dailyEvents);*/

/*
    @Override
    public void onEventEdit(String title, String startdate, String enddate, String date, String time) {

//        showEditActivity();

        eventTitleET.setText(title);
        startDateTV.setText(startdate);
        endDateTV.setText(enddate);
        Date.setText(date);
        Time.setText(time);

    }
    */

/*
    private void showEditActivity() {

        Intent intent = new Intent(this, EventEditActivity.class);
        startActivity(intent);
    }*/


    //이벤트 저장 - InsertEvent
    class InsertEvent extends AsyncTask<String, String, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(EventEditActivity.this,
                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
//            mTextViewResult.setText(result);
            Log.d(SAVETAG, "POST response  - " + result);
        }

        @Override
        protected String doInBackground(String... params) {

            String userID = (String)params[1];
            String eventTitle = (String) params[2];
            String eventStartDate = (String) params[3];
            String eventEndDate = (String) params[4];
            String eventDate = (String) params[5];
            String eventTime = (String) params[6];

            String serverURL = (String) params[0];
            String postParameters = "userID=" + userID + "&title=" + eventTitle + "&startdate=" + eventStartDate + "&enddate=" + eventEndDate
                    + "&date=" + eventDate + "&time=" + eventTime;


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(10000);
                httpURLConnection.setConnectTimeout(10000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(SAVETAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString();

            } catch (Exception e) {

                Log.d(SAVETAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }





    //이벤트 수정
    class UpdateEvent extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(EDITTAG, "update_event "+ result);
        }

        @Override
        protected String doInBackground(String... params) {
            String eventTitle = (String) params[1];
            String eventStartDate = (String) params[2];
            String eventEndDate = (String) params[3];
            String eventDate = (String) params[4];
            String eventTime = (String) params[5];
            String passedEventTime = (String) params[6];

            Log.e("doInBackground 1", eventTime);
            Log.e("doInBackground 2", passedEventTime);

            //PHP 파일을 실행시킬 수 있는 주소와 전송할 데이터를 준비
            //POST 방식으로 데이터 전달시에는 데이터가 주소에 직접 입력되지 않는다.
            String serverURL = (String)params[0];

            //HTTP 메시지 본문에 포함되어 전송되기 때문에 따로 데이터를 준비해야 한다,
            //전송할 데이터는 '이름=값' 형식이며 여러개를 보내야 할 경우에는 항목 사이에 &를 추가한다.
            //여기에 적어준 이름을 나중에 PHP에서 사용하여 값을 얻게 된다.
            String postParameters = "title=" + eventTitle + "&startdate=" + eventStartDate + "&enddate=" + eventEndDate
                    + "&date=" + eventDate + "&time=" + eventTime + "&passedEventTime=" + passedEventTime;



            try {

                //HttpURLConnection 클래스를 사용하여 POST방식으로 데이터를 전송
                URL url = new URL(serverURL);   //주소가 저장된 변수를 입력
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000); //5초안에 응답이 오지 않으면 예외가 발생
                httpURLConnection.setConnectTimeout(5000);  //5초안에 연결이 안되면 예외가 발생
                httpURLConnection.setRequestMethod("POST"); //요청방식으로 POST로 한다.
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                //전송할 데이터가 저장된 변수를 이곳에 입력한다. 인코딩을 고려해야한다.
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                //응답 읽기
                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(EDITTAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                //StringBuilder를 사용하여 수신되는 데이터를 저장
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();

                return sb.toString();   //저장된 데이터를 스트링으로 변환하여 리턴턴


            } catch (Exception e) {

                Log.d(EDITTAG, "UpdateEvent: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }
}
