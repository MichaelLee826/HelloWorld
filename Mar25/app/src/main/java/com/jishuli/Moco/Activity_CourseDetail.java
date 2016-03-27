package com.jishuli.Moco;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Activity_CourseDetail extends Activity {
    private String PATH = "http://120.25.166.18/coursedetail?courseID=1&city=nanjing";

    private ViewPager viewPager;
    private List<View> viewList;

    private TextView courseName;
    private TextView coursePrice;
    private TextView rateNumber;
    private TextView enrollNumber;
    private TextView showTime;
    private TextView showLocation;

    private Button saveButton;
    private Button signupButton;

    private TextView introductionTitile;
    private TextView categoryTitile;
    private TextView commentTitile;

    private View view1;
    private View view2;
    private View view3;

    private int currentIndex = 0;
    private int offset = 0;
    private int width = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coursedetail);

        findViews();            //1.找到各种控件
        getData();              //2.下载数据
        setListeners();         //3.设置监听器
        setViewPager();         //4.设置ViewPager

        Bundle bundle = this.getIntent().getExtras();
        int id = bundle.getInt("id");
    }

    //1.找到各种控件
    public void findViews(){
        viewPager = (ViewPager)findViewById(R.id.coursedetail_viewpager);
        courseName = (TextView)findViewById(R.id.coursedetail_coursename);
        coursePrice = (TextView)findViewById(R.id.coursedetail_price);
        rateNumber = (TextView)findViewById(R.id.coursedetail_ratenumber);
        enrollNumber = (TextView)findViewById(R.id.coursedetail_enrollnumber);
        showTime = (TextView)findViewById(R.id.coursedetail_showtime);
        showLocation = (TextView)findViewById(R.id.coursedetail_showlocaton);
        saveButton = (Button)findViewById(R.id.coursedetail_saveButton);
        signupButton = (Button)findViewById(R.id.coursedetail_signupButton);

        introductionTitile = (TextView)findViewById(R.id.viewpager_title1);
        categoryTitile = (TextView)findViewById(R.id.viewpager_title2);
        commentTitile = (TextView)findViewById(R.id.viewpager_title3);
    }

    //2.下载数据
    public void getData(){
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

                        int num = jsonObject.getInt("count");

                        JSONArray listArray = jsonObject.getJSONArray("detail");
                        for (int i = 0; i < listArray.length(); i++){
                            JSONObject temp = (JSONObject)listArray.get(i);
                            int typeID = temp.getInt("typeID");
                            int courseID = temp.getInt("courseID");
                            String courseName = temp.getString("courseName");
                            int score = temp.getInt("score");
                            String beginTime = temp.getString("beginTime");
                            String endTime = temp.getString("endTime");

                            JSONObject jsonObject1 = temp.getJSONObject("location");
                            double lat = jsonObject1.getDouble("lat");
                            double lng = jsonObject1.getDouble("lng");

                            JSONObject jsonObject2 = temp.getJSONObject("institution");
                            String institutionID = jsonObject2.getString("institutionID");
                            String desctiption = jsonObject2.getString("desctiption");
                            String name = jsonObject2.getString("name");

                            System.out.println(lat + lng + institutionID + desctiption + name);
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
    }

    //3.设置监听器
    public void setListeners(){
        introductionTitile.setOnClickListener(new TitleTextViwOnClickListener(0));
        categoryTitile.setOnClickListener(new TitleTextViwOnClickListener(1));
        commentTitile.setOnClickListener(new TitleTextViwOnClickListener(2));
    }

    //4.设置ViewPager
    public void setViewPager(){
        viewList = new ArrayList<View>();
        LayoutInflater layoutInflater = getLayoutInflater().from(this);

        view1 = layoutInflater.inflate(R.layout.viewpager1, null);
        view2 = layoutInflater.inflate(R.layout.viewpager2, null);
        view3 = layoutInflater.inflate(R.layout.viewpager3, null);

        viewList.add(view1);
        viewList.add(view2);
        viewList.add(view3);

        viewPager.setAdapter(new Adapter_CourseIntro_ViewPager(viewList));
        viewPager.setCurrentItem(0);
        viewPager.setOnPageChangeListener(new PageChangeListener());
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

    //3-1.点击标题时切换ViewPager
    public class TitleTextViwOnClickListener implements View.OnClickListener{
        private int index = 0;

        public TitleTextViwOnClickListener(int i){
            index = i;
        }

        @Override
        public void onClick(View v) {
            viewPager.setCurrentItem(index);
        }
    }


    public class PageChangeListener implements ViewPager.OnPageChangeListener{
        int one = offset * 2 + width;           // 页卡1 -> 页卡2 偏移量
        int two = one * 2;                      // 页卡1 -> 页卡3 偏移量

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            switch (position){
                case 0: {
                    introductionTitile.setTextColor(Color.rgb(71, 187, 150));
                    categoryTitile.setTextColor(Color.BLACK);
                    commentTitile.setTextColor(Color.BLACK);
                    break;
                }
                case 1: {
                    introductionTitile.setTextColor(Color.BLACK);
                    categoryTitile.setTextColor(Color.rgb(71, 187, 150));
                    commentTitile.setTextColor(Color.BLACK);
                    break;
                }
                case 2: {
                    introductionTitile.setTextColor(Color.BLACK);
                    categoryTitile.setTextColor(Color.BLACK);
                    commentTitile.setTextColor(Color.rgb(71, 187, 150));
                    break;
                }
            }
            //构造函数：TranslateAnimation(float fromXDelta, float toXDelta, float fromYDelta, floattoYDelta)
            //Animation animation = new TranslateAnimation(one * currentIndex, one * position, 0, 0);

            currentIndex = position;

            //animation.setFillAfter(true);           // True:图片停在动画结束位置
            //animation.setDuration(300);
            //imageView.startAnimation(animation);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
