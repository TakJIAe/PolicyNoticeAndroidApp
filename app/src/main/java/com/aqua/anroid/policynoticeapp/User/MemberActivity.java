package com.aqua.anroid.policynoticeapp.User;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.aqua.anroid.policynoticeapp.MenuActivity;
import com.aqua.anroid.policynoticeapp.Parser.PublicDataDetail;
import com.aqua.anroid.policynoticeapp.Parser.PublicDataList;
import com.aqua.anroid.policynoticeapp.Parser.PublicDataParser;
import com.aqua.anroid.policynoticeapp.Parser.WantedDetail;
import com.aqua.anroid.policynoticeapp.Parser.WantedList;
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
import java.util.LinkedHashSet;

//import com.mobile.PolicyApp.R;

public class MemberActivity extends AppCompatActivity implements ParsingAdapter.OnItemClick {
    private static String IP_ADDRESS = "10.0.2.2";
    private static String TAG = "phptest";
    private static final String TAG_JSON="root";
    String userID;
    ImageView btn_menu;
    String mJsonString;
    public static Context context;

    PublicDataParser parser = new PublicDataParser();
    ArrayList<PublicDataList> publicDataArray = new ArrayList<>();;
    ArrayList<PublicDataList> publicDataList;

    ArrayList<PublicDataDetail> publicDetailArray;

    ArrayList<String> scrollServID = new ArrayList<String>();

    // Scroll
    final ArrayList<String> scrollItemList = new ArrayList<String>();
    ArrayAdapter<String> adapter = null;

    String searchServID; //?????????????????????
    String lifeArrayText;         //?????????????????????
    String trgterIndvdlArrayText; //?????????????????????
    String title_search;
    String detail_search;

    EditText input_searchWrd;

    String[] lifeArray_items = {"????????????", "?????????", "??????", "?????????", "??????","?????????", "??????", "??????????????" };
    String[] trgterIndvdlArray_items = {"????????????", "????????????????????", "?????????", "???????????????", "?????????", "?????????", "?????????????????"};
    String[] desireArray_items = { "????????????", "?????????", "??????", "????????????", "???????????? ??? ????????????", "???????????? ??? ????????????", "?????? ??? ??????????????", "?????? ??? ??????", "?????? ??? ??????", "?????? ??? ????????????",};
    String[] check_search_items = { "??????", "??????", "??????+??????"};

    Spinner check_life; //???????????? ????????? ??? ????????????
    Spinner check_trgterIndvdlArray; //???????????? ????????? ??? ????????????
    Spinner check_desireArray; //???????????? ????????? ??? ????????????
    Spinner check_search;   //???????????? ????????? ??? ????????????
    int line_index = 0; //???????????? ????????? ?????? ??????

    TextView servNm, jurMnofNm, tgtrDtlCn, slctCritCn, alwServCn, trgterIndvdlArray, lifeArray;

    ParsingAdapter parsingAdapter;

    private View layout_1;
    private View layout_2;

    ListView list;
    String test;
    String test2;

    @Override
    public void onClick(String value) {
        searchServID = value;
        Log.d("searchServID",searchServID);
        SearchDateDetail(searchServID);
        layout_1.setVisibility(View.VISIBLE);
        layout_2.setVisibility(View.INVISIBLE);
    }

    public void onClick_serch_List(View view) //??????????????????
    {
        //list.invalidateViews();
        SearchDataList();

    }

    public void  onClick_resetBtn(View view) //????????? ??????
    {
        input_searchWrd = findViewById(R.id.input_searchWrd);
        input_searchWrd.setText(null);
        input_searchWrd.clearFocus();
        check_life.setSelection(0);
        check_trgterIndvdlArray.setSelection(0);
        check_desireArray.setSelection(0);
        publicDataList.clear();
        parsingAdapter.notifyDataSetChanged();
    }
    public void back_searchlist(View view){
        layout_2.setVisibility(View.VISIBLE);
        layout_1.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);
        context = this;

        check_life = findViewById(R.id.check_life);
        check_trgterIndvdlArray = findViewById(R.id.check_trgterIndvdlArray);
        check_desireArray = findViewById(R.id.check_desireArray);
        check_search = findViewById(R.id.check_search);

        layout_1 = (LinearLayout) findViewById(R.id.layout);
        layout_2 = (LinearLayout) findViewById(R.id.main_layout);

        servNm = findViewById(R.id.servNm);
        jurMnofNm = findViewById(R.id.jurMnofNm);
        tgtrDtlCn = findViewById(R.id.tgtrDtlCn);
        slctCritCn = findViewById(R.id.slctCritCn);
        alwServCn = findViewById(R.id.alwServCn);
        trgterIndvdlArray = findViewById(R.id.trgterIndvdlArray);
        lifeArray = findViewById(R.id.lifeArray);


        layout_1.setVisibility(View.INVISIBLE);
        layout_2.setVisibility(View.VISIBLE);

        //???????????? ????????? ?????????
        ArrayAdapter<String> lifeArray_adapter = new ArrayAdapter<String>(
                        this, android.R.layout.simple_spinner_item,lifeArray_items);
        lifeArray_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        check_life.setAdapter(lifeArray_adapter);
        check_life.setSelection(0,false);

        check_life.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        //???????????? ????????? ?????????
        ArrayAdapter<String> trgterIndvdlArray_adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item,trgterIndvdlArray_items);
        trgterIndvdlArray_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        check_trgterIndvdlArray.setAdapter(trgterIndvdlArray_adapter);
        check_trgterIndvdlArray.setSelection(0,false);

        check_trgterIndvdlArray.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        //???????????? ????????? ?????????
        ArrayAdapter<String> desireArray_adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item,desireArray_items);
        desireArray_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        check_desireArray.setAdapter(desireArray_adapter);
        check_desireArray.setSelection(0,false);

        check_desireArray.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        //???????????? ????????? ?????????
        ArrayAdapter<String> search_adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item,check_search_items);
        search_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        check_search.setAdapter(search_adapter);
        check_search.setSelection(0,false);

        check_search.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        /*????????? id??? ?????? ??????*/
        SharedPreferences sharedPreferences = getSharedPreferences("userID",MODE_PRIVATE);
        userID  = sharedPreferences.getString("userID","");


        //???????????? ?????? ??? ?????????????????? ??????
        btn_menu = findViewById(R.id.menubtn);
        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MemberActivity.this, MenuActivity.class);
                startActivity(intent);

            }
        });

        GetData task = new GetData();
        task.execute(userID);

        // ???????????? ?????????
        InitListView();

    }

    void SearchDataList() //??????????????????????????????
    {
        new Thread(){
            public  void run(){
                try {

                    input_searchWrd = findViewById(R.id.input_searchWrd);

                    // ????????? ????????? ?????? ?????????
                    WantedList wantedList = new WantedList();
                    wantedList.searchWrd = input_searchWrd.getText().toString();        // ?????????

                    //title_search = wantedList.searchWrd;
                    if(check_search.getSelectedItem().equals("??????")){
                        title_search = wantedList.searchWrd;
                        detail_search = null;
                        Log.d(TAG, "?????????_?????? " + title_search);

                    }
                    else if(check_search.getSelectedItem().equals("??????")){
                        detail_search = wantedList.searchWrd;
                        title_search=null;
                        Log.d(TAG, "?????????_?????? " + detail_search);

                    }
                    else if(check_search.getSelectedItem().equals("??????+??????")){
                        title_search = wantedList.searchWrd;
                        detail_search = wantedList.searchWrd;
                        Log.d(TAG, "?????????_??????+?????? " + title_search + "," + detail_search);

                    }


                    //001????????? 002?????? 003????????? 004?????? 005????????? 006?????? 007??????????????
                    if(check_life.getSelectedItem().equals("?????????")){
                        wantedList.lifeArray="001";
                    }
                    else if(check_life.getSelectedItem().equals("??????")) {
                        wantedList.lifeArray = "002";
                    }
                    else if(check_life.getSelectedItem().equals("?????????")) {
                        wantedList.lifeArray = "003";
                    }
                    else if(check_life.getSelectedItem().equals("??????")) {
                        wantedList.lifeArray = "004";
                    }
                    else if(check_life.getSelectedItem().equals("?????????")) {
                        wantedList.lifeArray = "005";
                    }
                    else if(check_life.getSelectedItem().equals("??????")) {
                        wantedList.lifeArray = "006";
                    }
                    else if(check_life.getSelectedItem().equals("??????????????")) {
                        wantedList.lifeArray = "007";
                    }
                    else if(check_life.getSelectedItem().equals("????????????")) {
                        wantedList.lifeArray = "";
                    }

                    lifeArrayText=check_life.getSelectedItem().toString();

                    //010???????????????????? 020????????? 030??????????????? 040????????? 050????????? 060?????????????????
                    if(check_trgterIndvdlArray.getSelectedItem().equals("????????????????????")){
                        wantedList.trgterIndvdlArray="010";
                    }
                    else if(check_trgterIndvdlArray.getSelectedItem().equals("?????????")) {
                        wantedList.trgterIndvdlArray = "020";
                    }
                    else if(check_trgterIndvdlArray.getSelectedItem().equals("???????????????")) {
                        wantedList.trgterIndvdlArray = "030";
                    }
                    else if(check_trgterIndvdlArray.getSelectedItem().equals("?????????")) {
                        wantedList.trgterIndvdlArray = "040";
                    }
                    else if(check_trgterIndvdlArray.getSelectedItem().equals("?????????")) {
                        wantedList.trgterIndvdlArray = "050";
                    }
                    else if(check_trgterIndvdlArray.getSelectedItem().equals("?????????????????")) {
                        wantedList.trgterIndvdlArray = "060";
                    }
                    else if(check_trgterIndvdlArray.getSelectedItem().equals("????????????")) {
                        wantedList.trgterIndvdlArray = "";
                    }

                    trgterIndvdlArrayText=check_trgterIndvdlArray.getSelectedItem().toString();


                    if(check_desireArray.getSelectedItem().equals("?????????")){
                        wantedList.desireArray="100";
                    }
                    else if(check_desireArray.getSelectedItem().equals("??????")) {
                        wantedList.desireArray = "110";
                    }
                    else if(check_desireArray.getSelectedItem().equals("????????????")) {
                        wantedList.desireArray = "120";
                    }
                    else if(check_desireArray.getSelectedItem().equals("???????????? ??? ????????????")) {
                        wantedList.desireArray = "130";
                    }
                    else if(check_desireArray.getSelectedItem().equals("???????????? ??? ????????????")) {
                        wantedList.desireArray = "140";
                    }
                    else if(check_desireArray.getSelectedItem().equals("?????? ??? ??????????????")) {
                        wantedList.desireArray = "150";
                    }
                    else if(check_desireArray.getSelectedItem().equals("?????? ??? ??????")) {
                        wantedList.desireArray = "160";
                    }
                    else if(check_desireArray.getSelectedItem().equals("?????? ??? ??????")) {
                        wantedList.desireArray = "170";
                    }
                    else if(check_desireArray.getSelectedItem().equals("?????? ??? ????????????")) {
                        wantedList.desireArray = "180";
                    }
                    else if(check_desireArray.getSelectedItem().equals("????????????")) {
                        wantedList.desireArray = "";
                    }

                    //?????? ????????? ?????? ??????
                    if( !wantedList.desireArray.isEmpty()) {
                        wantedList.lifeArray = "";
                        wantedList.trgterIndvdlArray = "";
                    }
                    else if(!wantedList.lifeArray.isEmpty()&&!wantedList.trgterIndvdlArray.isEmpty()){
                        wantedList.trgterIndvdlArray = "";
                    }

                    // [?????? ??????]
                    if(parser.PulbicDataList_HttpURLConnection(wantedList)) {
                        publicDataArray = parser.XMLParserDataList();
                        ShowPublicDataList();
                    }
                }
                catch (Exception e){

                }
            }
        }.start();
    }



    void SearchDateDetail(String str){
        new Thread(){
            public  void run(){
                try {
                    // !? ????????????????????? ????????????????????? ?????? ???????????????]
                    WantedDetail wantedDetail=new WantedDetail();
                    wantedDetail.servID = str;
                    if(parser.PulbicDataDetail_HttpURLConnection(wantedDetail)){
                        publicDetailArray = parser.XMLParserDataDetail();
                        ShowPublicDetailData();

                    }
                }
                catch (Exception e){
                }
            }
        }.start();
    }

    // ?????????????????????
    void InitListView() {
        list = (ListView) findViewById(R.id.listView1);
        publicDataList = new ArrayList<>();
        parsingAdapter = new ParsingAdapter(this, publicDataList, this, this);
        list.setAdapter(parsingAdapter);


        list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });
    }


    //???????????????????????????????????? ????????? ????????? ?????????????????? ?????????//????????????????????? ?????????????????? ???????????? ????????????????????????
    // ????????? ?????? ?????? ?????? ????????? ??????
    void ShowPublicDataList()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                publicDataList.clear(); //????????? ?????????
                scrollServID.clear();
                for(int i = 0; i <publicDataArray.size(); i++) {
                    if (lifeArrayText.equals("????????????")) {
                        lifeArrayText = "";
                    }
                    if (title_search == null) {
                        title_search = "";
                    }
                    if (detail_search == null) {
                        detail_search = "";
                    }
                    if (trgterIndvdlArrayText.equals("????????????")) {
                        trgterIndvdlArrayText = "";
                    }

                    //????????? ????????? or ?????? or ??????
                    if(title_search.equals("") || detail_search.equals("")) {
                        if (publicDataArray.get(i).servNm.contains(title_search) &&
                                publicDataArray.get(i).servDgst.contains(detail_search)) {
                            if (publicDataArray.get(i).lifeArray.contains(lifeArrayText) &&
                                    publicDataArray.get(i).trgterIndvdlArray.contains(trgterIndvdlArrayText)) {//????????? ?????????????????????????????? ?????????????????? ??????

                                scrollServID.add((publicDataArray.get(i).servID));
                                publicDataList.add(publicDataArray.get(i));
                            }
                        }
                    }

                    //??????+??????
                    else {
                        if (publicDataArray.get(i).servNm.contains(title_search) ||
                                publicDataArray.get(i).servDgst.contains(detail_search)) {
                            if (publicDataArray.get(i).lifeArray.contains(lifeArrayText) &&
                                    publicDataArray.get(i).trgterIndvdlArray.contains(trgterIndvdlArrayText)) {//????????? ?????????????????????????????? ?????????????????? ??????

                                scrollServID.add((publicDataArray.get(i).servID));
                                publicDataList.add(publicDataArray.get(i));
                            }
                        }
                    }
                }
                parsingAdapter.notifyDataSetChanged();

            }
        });
    }

    // ????????? ?????? ?????? ?????? ????????? ??????
    void ShowPublicDetailData()
    {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < publicDetailArray.size(); i++) {
                    if (publicDetailArray.get(i).servNm != null)
                        servNm.setText(publicDetailArray.get(i).servNm);

                    if (publicDetailArray.get(i).jurMnofNm != null) {
                        //jurMnofNm.setText(publicDetailArray.get(i).jurMnofNm);
                        if(publicDetailArray.get(i).jurMnofNm.contains("\n\n\n")){
                            test = publicDetailArray.get(i).jurMnofNm.replace("\n\n\n\n","");
                        }
                        jurMnofNm.setText(test);
                    }
                    if (publicDetailArray.get(i).tgtrDtlCn != null) {
                        //tgtrDtlCn.setText(publicDetailArray.get(i).tgtrDtlCn);
                        if(publicDetailArray.get(i).tgtrDtlCn.contains("\n\n\n")){
                            test = publicDetailArray.get(i).tgtrDtlCn.replace("\n\n\n\n","");
                        }
                        tgtrDtlCn.setText(test);
                    }

                    if (publicDetailArray.get(i).slctCritCn != null) {
                        //slctCritCn.setText(publicDetailArray.get(i).slctCritCn);
                        if(publicDetailArray.get(i).slctCritCn.contains("\n\n\n")){
                            test = publicDetailArray.get(i).slctCritCn.replace("\n\n\n","");
                        }
                        slctCritCn.setText(test);
                    }

                    if (publicDetailArray.get(i).alwServCn != null) {
                        //alwServCn.setText(publicDetailArray.get(i).alwServCn);
                        if(publicDetailArray.get(i).alwServCn.contains("\n\n\n")){
                            test2 = publicDetailArray.get(i).alwServCn.replace("\n\n\n","");

                        }
                        alwServCn.setText(test2);
                    }
                    if (publicDetailArray.get(i).trgterIndvdlArray != null)
                        trgterIndvdlArray.setText(publicDetailArray.get(i).trgterIndvdlArray);

                    if (publicDetailArray.get(i).lifeArray != null)
                        lifeArray.setText(publicDetailArray.get(i).lifeArray);
                }
            }
        });
    }



    private class GetData extends AsyncTask<String, Void, String>{

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MemberActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "response - " + result);

            if (result != null){
                mJsonString = result;
                showResult();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String searchKeyword1 = params[0];

            String serverURL = "http://10.0.2.2/main_userinfo.php";
            String postParameters = "userID=" + searchKeyword1;
            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(10000);
                httpURLConnection.setConnectTimeout(10000);
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
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
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
    }

    private void showResult(){

        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);
                Log.d(TAG, "JSONObject : "+ item);

                String userLifearray = item.getString("userLifearray");
                String userTrgterIndvdl = item.getString("userTrgterIndvdl");

                for(int q=0; q<lifeArray_items.length ; q++){
                    if(lifeArray_items[q].equals(userLifearray)) {
                        Log.d(TAG, "???????????? ??? : " + lifeArray_items[q]);
                        check_life.setSelection(q);
                    }
                }
                for(int t=0; t<trgterIndvdlArray_items.length ; t++){
                    if(trgterIndvdlArray_items[t].equals(userTrgterIndvdl)) {
                        Log.d(TAG, "???????????? ??? : " + trgterIndvdlArray_items[t]);
                        check_trgterIndvdlArray.setSelection(t);
                    }
                }
            }

        } catch (JSONException e) {

            Log.d(TAG, "showResult_member : ", e);
        }

    }
}