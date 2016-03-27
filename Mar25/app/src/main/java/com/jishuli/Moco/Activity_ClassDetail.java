package com.jishuli.Moco;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jishuli.Moco.BusinessLogicLayer.dao.CityDao;
import com.jishuli.Moco.BusinessLogicLayer.dao.SubjectDao;
import com.jishuli.Moco.BusinessLogicLayer.manager.DatabaseManager;
import com.jishuli.Moco.PersistenceLayer.City;
import com.jishuli.Moco.PersistenceLayer.County;
import com.jishuli.Moco.PersistenceLayer.Subject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Activity_ClassDetail extends Activity {
    static private String PATH = "http://120.25.166.18/course?typeID=10&city=nanjing&subjectID=12&districtID=02";

    private TextView detailNameTextView;
    private TextView detailNumberTextView;
    private TextView spinnerTextView1;
    private TextView spinnerTextView2;
    private TextView spinnerTextView3;
    private TextView spinnerTextView4;

    private ImageButton backButton;
    private ImageButton locationButton;

    private PopupWindow popupWindow;
    private Adapter_PopupSpinner adapter;

    //4个下拉菜单
    private String[] spinner1String = {"玄武区", "秦淮区", "鼓楼区", "江宁区"};
    private String[] spinner2String;
    private String[] spinner3String = {"离我最近", "评价最好"};
    private String[] spinner4String = {"正在进行", "即将开课", "已结束"};

    //ListView的控件
    private List<ClassDetailItem> classDetailItems = new ArrayList<>();
    private ListAdapter listAdapter;
    private ListView classDetailListView = null;

    private int classNumber = 0;        //课程数量
    private int[] typeID;
    private int[] courseID;
    private String[] courseName;
    private int[] score;
    private String[] beginTime;
    private String[] endTime;
    private double[] lat;
    private double[] lng;
    private String[] institutionID;
    private String[] desctiption;
    private String[] name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classdetail);

        findViews();                //1.找到各种控件
        getData();                  //2.获得数据
        setListeners();             //3.设置监听器
        setListViewListener();      //4.设置ListView的监听器
        setListViewAdapter();       //5.设置ListView的适配器

        //返回箭头的监听器
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity_ClassDetail.this.finish();
            }
        });
    }

    //1.找到各种控件
    public void findViews(){
        detailNameTextView = (TextView)findViewById(R.id.detailNameTextView);
        detailNumberTextView = (TextView)findViewById(R.id.detailNumberTextView);
        spinnerTextView1 = (TextView)findViewById(R.id.spinnerTextView1);
        spinnerTextView2 = (TextView)findViewById(R.id.spinnerTextView2);
        spinnerTextView3 = (TextView)findViewById(R.id.spinnerTextView3);
        spinnerTextView4 = (TextView)findViewById(R.id.spinnerTextView4);
        backButton = (ImageButton)findViewById(R.id.detailBackButton);
        locationButton = (ImageButton)findViewById(R.id.detailLocationButton);
        classDetailListView = (ListView)findViewById(R.id.detailListViewLayout);
    }

    //2.获得数据
    public void getData(){
        //从前一个Activity传来的数据
        Bundle bundle = this.getIntent().getExtras();
        int id = bundle.getInt("id");
        String cityString = bundle.getString("city");

        if (cityString == null){
            cityString = "南京市";
        }

        //本市的各个区
        CityDao cityDao = DatabaseManager.getmInstance(Activity_ClassDetail.this).getCityDao();
        City city = cityDao.queryBuilder().where(CityDao.Properties.Name.eq(cityString)).unique();
        List<County> countyList = city.getCounties();
        spinner1String = new String[countyList.size()];
        for (int i = 0; i < countyList.size(); i++){
            spinner1String[i] = countyList.get(i).getName();
        }

        //课程分类的下拉菜单
        SubjectDao subjectDao = DatabaseManager.getmInstance(Activity_ClassDetail.this).getSubjectDao();
        List<Subject> subjectList = subjectDao.queryBuilder().list();
        spinner2String = new String[subjectList.size()];
        for (int i = 0; i < subjectList.size(); i++){
            spinner2String[i] = subjectList.get(i).getName();
        }


        //从服务器上下载数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(PATH);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setConnectTimeout(5000);      //超时时间，5秒
                    httpURLConnection.setRequestMethod("GET");      //方式为GET
                    httpURLConnection.setDoInput(true);

                    if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                        InputStream inputStream = httpURLConnection.getInputStream();   //获得输入流
                        byte[] data = readStream(inputStream);                          //把输入流转换成字符串组，单独一个函数：2-1
                        String JSONString = new String(data);                           //把字符串组转换成字符串

                        JSONObject jsonObject = new JSONObject(JSONString);             //得到总的JSON数据

                        classNumber = jsonObject.getInt("count");

                        JSONArray listArray = jsonObject.getJSONArray("detail");
                        for (int i = 0; i < listArray.length(); i++){
                            JSONObject temp = (JSONObject)listArray.get(i);
                            typeID[i] = temp.getInt("typeID");
                            courseID[i] = temp.getInt("courseID");
                            courseName[i] = temp.getString("courseName");
                            score[i] = temp.getInt("score");
                            beginTime[i] = temp.getString("beginTime");
                            endTime[i] = temp.getString("endTime");

                            JSONObject jsonObject1 = temp.getJSONObject("location");
                            lat[i] = jsonObject1.getDouble("lat");
                            lng[i] = jsonObject1.getDouble("lng");

                            JSONObject jsonObject2 = temp.getJSONObject("institution");
                            institutionID[i] = jsonObject2.getString("institutionID");
                            desctiption[i] = jsonObject2.getString("desctiption");
                            name[i] = jsonObject2.getString("name");

                            System.out.println("纬度："+lat[i] +"经度："+ lng[i]);
                        }
                    }
                    else {
                        System.out.println("网络有问题");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        classDetailItems = new ArrayList<ClassDetailItem>();
        for (int i = 0; i < classNumber; i++){
            classDetailItems.add(new ClassDetailItem(typeID[i], courseID[i], courseName[i], score[i], beginTime[i],
                                endTime[i], lat[i], lng[i], institutionID[i], desctiption[i], name[i]));
        }
        /*
        for (int i = 0; i < 10; i++){
            int status = 1;
            String date = "2016.5.6";
            String time = "13:00-15:00";
            int number = 120;
            String name = "育儿课堂";
            int rate = 5;
            String agentName = "ABC育儿机构";
            double distance = 0.5;
            //classDetailItems.add(new ClassDetailItem(status, date, time, number, name, rate, agentName, distance));
        }
        */
    }

    //3.设置监听器
    public void setListeners(){
        Listener listener = new Listener();

        spinnerTextView1.setOnClickListener(listener);
        spinnerTextView2.setOnClickListener(listener);
        spinnerTextView3.setOnClickListener(listener);
        spinnerTextView4.setOnClickListener(listener);
    }

    public class Listener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            setSpinnerTextView(v.getId());
        }
    }

    //4.设置ListView的监听器
    public void setListViewListener(){
        classDetailListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(Activity_ClassDetail.this, Activity_CourseDetail.class);   //跳转到课程详情列表

                Bundle bundle = new Bundle();
                bundle.putInt("id", position);                                              //点击了第几个课程分类

                intent.putExtras(bundle);

                startActivity(intent);
            }
        });
    }

    //5.设置ListView的适配器
    public void setListViewAdapter(){
        listAdapter = new Adapter_ClassDetailItem(this, classDetailItems);
        classDetailListView.setAdapter(listAdapter);
    }

    //2-1.读数据中用到的函数
    private static byte[] readStream(InputStream inputStream) throws Exception{
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = inputStream.read(buffer)) != -1){
            byteArrayOutputStream.write(buffer, 0, length);
        }
        byteArrayOutputStream.close();
        inputStream.close();
        return byteArrayOutputStream.toByteArray();
    }

    //3-1.设置四个下拉菜单
    public void setSpinnerTextView(int id){
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.popup_window, null);
        ListView listView = (ListView)view.findViewById(R.id.popup_window_listView);

        if (id == spinnerTextView1.getId()){
            adapter = new Adapter_PopupSpinner(this, spinner1String);
        }
        if (id == spinnerTextView2.getId()){
            adapter = new Adapter_PopupSpinner(this, spinner2String);
        }
        if (id == spinnerTextView3.getId()){
            adapter = new Adapter_PopupSpinner(this, spinner3String);
        }
        if (id == spinnerTextView4.getId()){
            adapter = new Adapter_PopupSpinner(this, spinner4String);
        }

        listView.setAdapter(adapter);

        popupWindow = new PopupWindow(findViewById(R.id.layout_classdetail));
        popupWindow.setContentView(view);
        popupWindow.setFocusable(true);

        //listView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        //设置PopupWindow的宽和高
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);

        //不设置背景的话下面的代码不管用
        popupWindow.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.bt_myaccount));
        popupWindow.setOutsideTouchable(true);

        popupWindow.showAsDropDown(spinnerTextView1, 0, 0);

        //下拉菜单每一项的监听器
        switch (id){
            case R.id.spinnerTextView1:{
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        spinnerTextView1.setText(spinner1String[position]);
                        popupWindow.dismiss();
                        popupWindow = null;
                    }
                });
                break;
            }

            case R.id.spinnerTextView2:{
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        spinnerTextView2.setText(spinner2String[position]);
                        popupWindow.dismiss();
                        popupWindow = null;
                    }
                });
                break;
            }

            case R.id.spinnerTextView3:{
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        spinnerTextView3.setText(spinner3String[position]);
                        popupWindow.dismiss();
                        popupWindow = null;
                    }
                });
                break;
            }

            case R.id.spinnerTextView4:{
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        spinnerTextView4.setText(spinner4String[position]);
                        popupWindow.dismiss();
                        popupWindow = null;
                    }
                });
                break;
            }
        }
    }

    //按下返回键，返回到首页
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            Activity_ClassDetail.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}