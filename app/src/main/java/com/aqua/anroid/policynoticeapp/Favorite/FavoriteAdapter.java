package com.aqua.anroid.policynoticeapp.Favorite;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;


import com.aqua.anroid.policynoticeapp.Parser.PublicDataList;
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
import java.util.ArrayList;

public class FavoriteAdapter extends BaseAdapter {
    private static String TAG = "phptest";

    private Context context;
    //private ArrayList<FavoriteData> favoriteDatalist; //Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<PublicDataList> publicDataLists = new ArrayList<PublicDataList>(); //목록조회 데이터
    ArrayList<FavoriteData> favoriteData= new ArrayList<FavoriteData>();

    private static String IP_ADDRESS = "10.0.2.2";
    private Activity activity;

    public FavoriteAdapter(Activity activity) {
        this.activity = activity;

    }

//    public FavoriteAdapter(Context context, ArrayList<PublicDataList> publicDataLists, Activity activity) {
//        this.context = context;
//        this.publicDataLists = publicDataLists;
//        //다이얼로그 때문에 activity 선언
//        this.activity = activity;
//    }

    //Adapter에 사용되는 데이터의 개수를 리턴
    @Override
    public int getCount() {
        return favoriteData.size();
    }

    //뷰홀더 추가
    class ViewHolder {
        TextView textview_list_name;
        TextView textview_list_content;
    }

    //i에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴
    @Override
    public View getView(int i, View view, ViewGroup parent) {
        //int pos = i;
        Context context = parent.getContext();
        final ViewHolder holder;//아이템 내 view들을 저장할 holder 생성

        final FavoriteData favoriteData_item = favoriteData.get(i);

        Log.d(TAG, "items_adapter : " + favoriteData.toString());


        //"item_list" Layout을 inflate하여 view 참조 획득
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            //최초 생성 view인 경우, inflation -> ViewHolder 생성 -> 해당 View에 setTag 저장
            view = inflater.inflate(R.layout.favorite_item_list, parent, false);

            holder = new ViewHolder();

            //화면에 표시될 View(Layoutㅇ inflate된)으로부터 위젯에 대한 참조 획득
            holder.textview_list_name = (TextView) view.findViewById(R.id.textView_list_name);
            holder.textview_list_content = (TextView) view.findViewById(R.id.textView_list_content);



            //해당 view에 setTag로 Holder 객체 저장
            view.setTag(holder);
        } else {
            //view가 이미 생성된 적이 있다면, 저장되어 있는 Holder 가져오기
            holder = (ViewHolder) view.getTag();
        }
        holder.textview_list_name.setText(favoriteData_item.getItem_name());
        holder.textview_list_content.setText(favoriteData_item.getItem_content());


        //삭제 버튼 클릭 시
        ImageView deletebutton = (ImageView) view.findViewById(R.id.deletebutton);
        //ViewHolder finalHolder = holder;
        deletebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteData task = new DeleteData();
                task.execute(holder.textview_list_name.getText().toString());
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                alertDialogBuilder
                        .setMessage( " 삭제 완료")
                        .setCancelable(true)
                        .setPositiveButton("확인",
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int arg1) {
                                        context.startActivity(new Intent(context, FavoriteActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

                                    }
                                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }

        });
        //해당 view 반납
        return view;
    }

    //지정한 위치(i)에 있는 데이터 리턴턴
    @Override
    public Object getItem(int i) {
        return favoriteData.get(i);
    }



    //지정한 위치(i)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴
    @Override
    public long getItemId(int i) {
        return i;
    }


    // 아이템 데이터 추가를 위한 함수.
    public void addItem(FavoriteData items) {
        favoriteData.add(items);
    }


    class DeleteData extends AsyncTask<String, Void, String> {
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

            String serverURL = "http://10.0.2.2/favorite_delete.php";
            String postParameters = "item_name=" + searchKeyword1;

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