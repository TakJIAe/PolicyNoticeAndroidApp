package com.aqua.anroid.policynoticeapp.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.aqua.anroid.policynoticeapp.Favorite.FavoriteActivity;
import com.aqua.anroid.policynoticeapp.Favorite.FavoriteAdapter;
import com.aqua.anroid.policynoticeapp.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;

import java.util.List;

/* 이벤트 유형의 배열 어댑터 확장 */
public class EventAdapter extends BaseAdapter{

    private static String TAG = "phptest";
    public static ArrayList<Event> eventsList = new ArrayList<>(); //이벤트 목록

    ArrayList<Event> events= new ArrayList<Event>();
    ArrayList<Event> items;
    Context context;
    Activity activity;
//    public EventAdapter(@NonNull Context context, ArrayList<Event> events, ArrayList<Event> items)
//    {
//        super(context, 0, events); //super 호출 위해 resource 0 지정
//        this.context = context;
//        this.events = events;
//        this.items = items;
//    }

    public EventAdapter(Context context, Activity activity, ArrayList<Event> events)
    {
        //super(context, 0, events); //super 호출 위해 resource 0 지정
        this.context = context;
        this.activity = activity;
        this.events = events;

        //this.items = items;
//        Log.d(TAG, "selectedDate : " + CalendarUtils.selectedDate.toString());

    }


    //뷰홀더 추가
    class ViewHolder {
        TextView eventTitleTV;
        TextView eventStartDateTV;
        TextView eventEndDateTV;
        TextView eventDateTV;
        TextView eventTimeTV;

    }

    //Adapter에 사용되는 데이터의 개수를 리턴
    @Override
    public int getCount() {
        return events.size();
    }

    //지정한 위치(i)에 있는 데이터 리턴턴
    @Override
    public Object getItem(int i) {
        return events.get(i);
    }

    //지정한 위치(i)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴
    @Override
    public long getItemId(int i) {
        return i;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        Log.d("어뎁터 갱신", "어뎁터 갱신");

        LocalDate date;
//        ViewHolder holder = new ViewHolder();
        final ViewHolder holder;//아이템 내 view들을 저장할 holder 생성

        final Event event_item = events.get(position);

        Log.d(TAG, "events_adapter : " + events.toString());


        //이벤트 항목 가져옴
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.event_cell, parent, false);

            holder = new ViewHolder();

//            convertView = LayoutInflater.from(getContext()).inflate(R.layout.event_cell, parent, false);
            /*convertView.findViewById(R.id.eventTitleTV);*/

            holder.eventTitleTV = (TextView) convertView.findViewById(R.id.eventTitleTV);
            holder.eventStartDateTV = (TextView) convertView.findViewById(R.id.eventStartDateTV);
            holder.eventEndDateTV = (TextView) convertView.findViewById(R.id.eventEndDateTV);
            holder.eventDateTV = (TextView) convertView.findViewById(R.id.eventDateTV);
            holder.eventTimeTV = (TextView) convertView.findViewById(R.id.eventTimeTV);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        //if(CalendarUtils.selectedDate.equals(event_item.getStartdate())){
        holder.eventTitleTV.setText(event_item.getTitle());
        holder.eventStartDateTV.setText(event_item.getStartdate());
        holder.eventEndDateTV.setText(event_item.getEnddate());
        holder.eventDateTV.setText(event_item.getDate());
        holder.eventTimeTV.setText(event_item.getTime());

        //}

        ImageButton eventDeleteBtn = (ImageButton) convertView.findViewById(R.id.eventDeleteBtn);
        eventDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteEvent task = new DeleteEvent();
                task.execute(holder.eventTimeTV.getText().toString());
//                eventsList.clear();
//                CalendarActivity.eventsList.remove(selectedEvent);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                alertDialogBuilder
                        .setMessage("이벤트 삭제")
                        .setCancelable(true)
                        .setPositiveButton("확인",
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int arg1) {
                                        context.startActivity(new Intent(context, CalendarActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

                                    }
                                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
//                eventsList.clear();
            }
        });


     /*   Log.d(TAG, "selectedDate : " + CalendarUtils.selectedDate.toString());

        Log.d(TAG, "holder.eventTitleTV : " + holder.eventTitleTV.getText().toString());
        Log.d(TAG, "holder.eventStartDateTV : " + holder.eventStartDateTV.getText().toString());
        Log.d(TAG, "holder.eventEndDateTV : " + holder.eventEndDateTV.getText().toString());
*/
        return convertView;
    }

    // 아이템 데이터 추가를 위한 함수.
    public void addItem(Event items) {
        events.add(items);
    }


    class DeleteEvent extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }

        @Override
        protected String doInBackground(String... params) {
            String searchKeyword1 = params[0];

            String serverURL = "http://10.0.2.2/event_delete.php";
            String postParameters = "time=" + searchKeyword1;

            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString();


            } catch (Exception e) {

                // Log.d(TAG, "DeleteData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }

}