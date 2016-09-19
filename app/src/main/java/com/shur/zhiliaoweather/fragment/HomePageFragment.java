package com.shur.zhiliaoweather.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.shur.zhiliaoweather.R;
import com.shur.zhiliaoweather.activity.FragmentAndActivity;
import com.shur.zhiliaoweather.activity.MainActivity;
import com.shur.zhiliaoweather.adapter.ListWeatherAdapter;
import com.shur.zhiliaoweather.entity.CityManagerEntity;
import com.shur.zhiliaoweather.entity.SQLiteCityManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shur on 2016/9/14.
 * 天气主界面显示
 */
public class HomePageFragment extends Fragment {

    public static final String TAG = "HomeContent";
    public static List<CityManagerEntity> mcmb = new ArrayList<CityManagerEntity>();
    public static TextView currentcity;// 当前城市
    private TextView pm25;// PM值
    private TextView temp;// 温度
    private TextView pollution;// 污染程度
    private ListView weatherInfolist;//
    private EditText inputcity;
    private Button searchWeatherBtn;
    private View homeContent;
    public MainActivity mainActivity;
    public FragmentAndActivity mActivity;
    public static ProgressDialog pDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MainActivity.TAG_H = TAG;
        if (!mainActivity.netErrorFlag) {
            homeContent = inflater.inflate(R.layout.include_content_activity, null);
            initView();
            if (MainActivity.response.getResults() != null) {
                setpagedata();//设置页面数据
            }
        } else {
            homeContent = inflater.inflate(R.layout.activtiy_main_net_error, null);//返回错误的页面
        }
        return homeContent;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) getActivity();
        mActivity = (FragmentAndActivity) activity;
    }

    /**
     * 设置页面数据
     */
    public void setpagedata() {
        if (pDialog != null) {
            pDialog.dismiss();//关闭对话框
        }
        autoSetBgPic();//自动设置背景图片
        weatherInfolist.setAdapter(new ListWeatherAdapter(getActivity(),
                MainActivity.response.getResults().get(0).getWeather_data()));//设置适配器
        searchWeatherBtn.setOnClickListener(searchWeatherOnClickListener);
        currentcity.setText(MainActivity.response.getResults().get(0).getCurrentCity());//设置城市

        if ("".equals(MainActivity.response.getResults().get(0).getPm25())) {
            pm25.setText("PM2.5：");
            pollution.setText(R.string.no_data);
            pollution.setBackgroundColor(Color.TRANSPARENT);
        } else {//设置pm值
            pm25.setText("PM2.5："+MainActivity.response.getResults().get(0).getPm25());
            int pm = Integer.parseInt(MainActivity.response.getResults().get(0).getPm25());
            Log.i("TAG", pm + " <-- pm");
            if (pm < 75) {//根据pm值设置图片
                pollution.setText(R.string.pollution_no);
                pollution.setBackgroundResource(R.drawable.ic_dl_b);
            } else if (pm > 75 && pm < 100) {
                pollution.setText(R.string.pollution_little);
                pollution.setBackgroundResource(R.drawable.ic_dl_c);
            } else if (pm > 100 && pm < 150) {
                pollution.setText(R.string.pollution_mild);
                pollution.setBackgroundResource(R.drawable.ic_dl_d);
            } else if (pm > 150 && pm < 200) {
                pollution.setText(R.string.polltion_moderate);
                pollution.setBackgroundResource(R.drawable.ic_dl_e);
            } else if (pm > 200) {
                pollution.setText(R.string.polltion_severe);
                pollution.setBackgroundResource(R.drawable.ic_dl_f);
            }
        }

        //时间
        String todaydata = MainActivity.response.getResults().get(0)
                .getWeather_data().get(0).getDate();
        //温度
        String temperature = MainActivity.response.getResults().get(0)
                .getWeather_data().get(0).getTemperature();
        String subs = null;
        if (todaydata.length() > 14) {
            subs = todaydata.substring(14, todaydata.length() - 1);
            temp.setText(subs);
        } else if (temperature.length() > 5) {
            String[] str = temperature.split("~ ", 2);
            subs = str[1];
            temp.setText(subs);
        } else {
            temp.setText(temperature);
        }

//        创建SQLite对象并不会创建数据库
        SQLiteCityManager sqlite = new SQLiteCityManager(getActivity(), "weatherdb", null, 1);
        // 读写数据库
        SQLiteDatabase db = sqlite.getWritableDatabase();
        // ContentValues键值对，类似HashMap
        ContentValues cv = new ContentValues();
        // key为字段名，value为所存数据
        cv.put("cityname", MainActivity.response.getResults().get(0).getCurrentCity());
        cv.put("imageurl", MainActivity.response.getResults().get(0)
                .getWeather_data().get(0).getDayPictureUrl());
        cv.put("weather", MainActivity.response.getResults().get(0)
                .getWeather_data().get(0).getWeather());
        cv.put("temp", subs);
        //查询数据库全部数据
        Cursor cursor = db.query("dreamWeather", null, null, null, null, null, null);
        int i = 0;
        while (cursor.moveToNext()) {
            i++;
            Log.i("TAG", i + "==>>i");
            String cityname = cursor.getString(cursor.getColumnIndex("cityname"));
            String weathertext = cursor.getString(cursor.getColumnIndex("weather"));
            cityname = cityname.substring(0, 2);
            String citytext = currentcity.getText().toString().substring(0, 2);
            if (citytext.equals(cityname)) {
                if ("点击更新".equals(weathertext)) {
                    db.update("dreamWeather", cv, "weather = ?", new String[]{"点击更新"});
                    db.close();
                }
                return;
            }
        }
        // 插入，第二个参数:不能为null的字段
        db.insert("dreamWeather", "cityname", cv);
        db.close();
    }

    /**
     * 根据天气自动设置背景图片
     */
    private void autoSetBgPic() {
        String path = null;
        String weather = MainActivity.response.getResults().get(0)
                .getWeather_data().get(0).getWeather();
        if (weather.contains("多云") && !weather.contains("转多云")) {
            path = "cloudy.jpg";
        } else if (weather.contains("晴") && !weather.contains("转晴")) {
            path = "fine.jpg";
        } else if (weather.contains("雨")) {
            path = "rain.jpg";
        }
        if (path != null) {
            Intent intent = new Intent("change_background");
            intent.putExtra("path", path);
            intent.putExtra("auto", true);
            getActivity().sendBroadcast(intent);//发送广播
        }
    }

    /**
     * 初始化界面
     */
    private void initView() {
        currentcity = (TextView) homeContent.findViewById(R.id.currentcity);
        pm25 = (TextView) homeContent.findViewById(R.id.pm25);
        temp = (TextView) homeContent.findViewById(R.id.temp);
        searchWeatherBtn = (Button) homeContent.findViewById(R.id.btn_search);
        pollution = (TextView) homeContent.findViewById(R.id.pollution_level);
        inputcity = (EditText) homeContent.findViewById(R.id.inputcity);
        weatherInfolist = (ListView) homeContent.findViewById(R.id.weather_infor_list);
    }


    /**
     * 按钮点击查询事件
     */
    private View.OnClickListener searchWeatherOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            pDialog = new ProgressDialog(getActivity());
            pDialog.setCancelable(true);// 点击可以取消Dialog的展现
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setMessage("正在查询，请稍后...");
            pDialog.show();
            mActivity.senddata(inputcity);
            mActivity.sendcitytext(inputcity.getText().toString());
        }
    };
}
