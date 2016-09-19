package com.shur.zhiliaoweather.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.google.gson.GsonBuilder;
import com.shur.zhiliaoweather.R;
import com.shur.zhiliaoweather.entity.MHttpEntity;
import com.shur.zhiliaoweather.entity.ResponseWrapper;
import com.shur.zhiliaoweather.entity.SendDataEntity;

import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * @author Shur
 *         欢迎界面
 */
public class SplashActivity extends AppCompatActivity {

    public static ResponseWrapper response;// 数据结构的对象
    public static final int succeed = 1;
    public static final int fail = 2;
    public static final int nonet = 3;
    public String normalDistrict;
    public String locationCity = "广州";
    public LocationClient mLocationClient = null;
    public BDLocationListener mListener;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initDate();
    }

    //初始化数据
    private void initDate() {

        showProgressDialog("自动定位中...");
        initBaiduMapLocation();//初始化百度定位
        //子线程中请求网络数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);//睡眠2000
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sendRequest();
            }
        }).start();
    }

    //网络请求天气数据
    @SuppressWarnings("deprecation")
    private void sendRequest() {
        String getData = null;
        MHttpEntity mhe = null;

        try {
            SendDataEntity.setCity(normalDistrict);
            Log.d("TAG", normalDistrict + "=====>>>>>>>normalDistrict");
            mhe = MHttpEntity.sendHttpRequest(SendDataEntity.getData());

            if (mhe.getHentity() != null) {//可以获取的实体
                getData = EntityUtils.toString(mhe.getHentity());
                GsonBuilder gson = new GsonBuilder();//json解析
                response = gson.create().fromJson(getData, ResponseWrapper.class);
                Log.i("TAG", response.getError() + "=======>>>>>>response.getError()");

                if (response.getError() == -3) {//连接成功
                    SendDataEntity.setCity(normalDistrict);
                    mhe = MHttpEntity.sendHttpRequest(SendDataEntity.getData());

                    if (mhe.getHentity() != null) {//获取到实体
                        getData = EntityUtils.toString(mhe.getHentity());
                        Log.i("weather_info", getData + "-->getData");
                    }

                    if (response.getError() == -3) {
                        SendDataEntity.setCity(locationCity);
                        mhe = MHttpEntity.sendHttpRequest(SendDataEntity.getData());

                        if (mhe.getHentity() != null) {
                            Log.e("TAG", mhe.getHentity() + "==>>mhe.getHentity()");
                            getData = EntityUtils.toString(mhe.getHentity());
                        }
                    }
                }
                mhe.getMessage().obj = getData;//获取数据
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        handler.sendMessage(mhe.getMessage());//发送异步处理消息

    }

    /**
     * 处理网络连接状态
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {//处理消息

            if (pDialog != null) {
                pDialog.dismiss();//先关闭对话框
            }
            if (msg != null) {
                switch (msg.arg1) {
                    case succeed:// 与服务器连接成功，则传递数据并跳转
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        if (msg.obj != null) {
                            intent.putExtra("weather_data", (String) msg.obj);
                            intent.putExtra("normal_city", locationCity);
                            startActivity(intent);
                            finish();
                        }
                        break;
                    case fail:// 与服务器连接失败，弹出错误提示Toast
                        Toast.makeText(SplashActivity.this, getString(R.string.net_fail), Toast.LENGTH_SHORT).show();
                        intent = new Intent(SplashActivity.this, MainActivity.class);
                        intent.putExtra("weather_data", (String) msg.obj);
                        startActivity(intent);
                        finish();
                        break;
//                    case nonet:
//                        break;
                }
            }
        }
    };


    //显示定位的对话框
    private void showProgressDialog(String s) {

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage(s + "...");
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.show();
    }

    //使用百度定位
    private void initBaiduMapLocation() {
        mLocationClient = new LocationClient(this.getApplicationContext());
        mListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mListener);//注册监听函数
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationMode.Hight_Accuracy);
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
        mLocationClient.start();//开始定位
    }


    //百度定位监听器
    private class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {

            if (bdLocation != null) {
                normalDistrict = bdLocation.getDistrict();//获取位置
                locationCity = bdLocation.getCity();//城市
            }
            if (locationCity == null) {
                Toast.makeText(SplashActivity.this, "定位失败，请检查网络", Toast.LENGTH_SHORT).show();
            } else {
                String[] str = locationCity.split("市");
                locationCity = str[0];
                if ("".equals(locationCity)) {
                    Toast.makeText(SplashActivity.this, "定位失败，默认为广州", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    /**
     * 拦截返回键
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
