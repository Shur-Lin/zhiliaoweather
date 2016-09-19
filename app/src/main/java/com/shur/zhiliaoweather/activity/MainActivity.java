package com.shur.zhiliaoweather.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.shur.zhiliaoweather.R;
import com.shur.zhiliaoweather.base.MyApplication;
import com.shur.zhiliaoweather.entity.CityManagerEntity;
import com.shur.zhiliaoweather.entity.MHttpEntity;
import com.shur.zhiliaoweather.entity.ResponseWrapper;
import com.shur.zhiliaoweather.entity.SendDataEntity;
import com.shur.zhiliaoweather.fragment.AboutMeFragment;
import com.shur.zhiliaoweather.fragment.ChangeBackgroundFragment;
import com.shur.zhiliaoweather.fragment.CityManagerFragment;
import com.shur.zhiliaoweather.fragment.HomePageFragment;
import com.shur.zhiliaoweather.fragment.LifeIndexFragment;
import com.shur.zhiliaoweather.utils.SharePrefrenceUtil;
import com.shur.zhiliaoweather.utils.SystemUtils;

import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Shur
 *         天气显示界面
 */
public class MainActivity extends FragmentActivity implements OnClickListener, FragmentAndActivity {

    private long nowtime;
    public static ResponseWrapper response = new ResponseWrapper();// 数据结构的对象
    public static ResponseWrapper response2;
    private DrawerLayout mainDrawerLayout;
    private View leftDrawer;
    private EditText inputcity;
    public boolean netErrorFlag = false;
    public static final int succeed = 1;
    public static final int fail = 2;
    public static final int nonet = 3;
    private static int tag = 0;
    public static String TAG_H = null;
    private ProgressDialog pDialog;
    public static HomePageFragment homecontent = new HomePageFragment();
    public CityManagerFragment citymanager = new CityManagerFragment();
    public static CityManagerEntity cmb2 = new CityManagerEntity();
    private ChangeBgReceiver mReceiver;//广播

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getIntentData();//获取从上个activity传过来的数据
        registerBroadCast();//动态注册广播
        initview();//初始化界面

    }

    /**
     * 初始化界面
     */
    private void initview() {

        mainDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout_main);
        initBgPic();//设置背景图片
        leftDrawer = findViewById(R.id.left_drawer);
        mainDrawerLayout.setScrimColor(0x00000000);// 设置底部页面背景透明度
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();//开始事务
        ft.replace(R.id.fragmentlayout, homecontent, HomePageFragment.TAG);
        ft.commit();//事务一定要提交

    }

    /**
     * 设置主界面背景
     */
    private void initBgPic() {
        String path = new SharePrefrenceUtil(this).getPath();//获取图片路径
        Intent intent = new Intent("change_background");//发送广播
        intent.putExtra("path", path);
        sendBroadcast(intent);
    }

    /**
     * 动态注册广播
     */
    private void registerBroadCast() {
        mReceiver = new ChangeBgReceiver();
        IntentFilter filter = new IntentFilter("change_background");
        registerReceiver(mReceiver, filter);
    }

    //定义更换背景的广播
    private class ChangeBgReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String path = intent.getStringExtra("path");
            if (path == null) {
                Log.i("weather", "path is null");
            } else {
                Log.i("weather", path);
            }
            boolean auto = intent.getBooleanExtra("auto", false);//是否自动更新
            Bitmap bitmap = getBitmapByPath(path, auto);//获取图片
            if (bitmap != null) {
                mainDrawerLayout.setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));
            }
        }
    }

    /**
     * 获取图片
     *
     * @param path
     * @param auto
     * @return
     */
    private Bitmap getBitmapByPath(String path, boolean auto) {
        AssetManager am = this.getAssets();//获取资产
        Bitmap bitmap = null;
        InputStream is = null;
        try {
            if (auto == false) {
                is = am.open("bkgs/" + path);
            } else if (auto == true) {
                is = am.open("autobkgs/" + path);
            }
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 获取数据
     */
    private void getIntentData() {
        Intent intent = getIntent();
        String weatherData = intent.getStringExtra("weather_data");
        GsonBuilder gson = new GsonBuilder();
        response2 = gson.create().fromJson(weatherData, ResponseWrapper.class);
        if (response2 != null) {
            if (response.getError() == 0) {
                response = response2;
            }
        } else {
            netErrorFlag = true;//连接错误
        }
    }

    /**
     * 发送数据
     *
     * @param inputcity
     */
    @Override
    public void senddata(EditText inputcity) {
        this.inputcity = inputcity;
    }

    /**
     * 发送城市信息
     *
     * @param inputcitytext
     */
    @Override
    public void sendcitytext(final String inputcitytext) {
//        this.inputcitytext = inputcitytext;
        tag = 4;
        if ("".equals(inputcitytext)) {
            showToast(getString(R.string.edittext_hint));
            if (HomePageFragment.pDialog != null) {
                HomePageFragment.pDialog.dismiss();
            }
        } else {
            SendDataEntity.setCity(inputcitytext);// 获取用户输入城市
            new Thread(new Runnable() {

                @Override
                public void run() {
                    sendRequest(inputcitytext);
                }
            }).start();
        }
    }


    /**
     * 显示对话框
     */
    @Override
    public void showDialog() {
        pDialog = new ProgressDialog(MainActivity.this);
        pDialog.setCancelable(true);// 点击可以取消Dialog的展现
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("正在更新...");
        pDialog.show();
    }

    /**
     * 界面点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.homep_menu://打开侧滑界面
                switchLeftLayout();
                break;

            case R.id.homep_refresh://点击手动更新
                if (MyApplication.isNetWorkConnect(getApplicationContext())) {
                    refresh();
                } else {
                    Toast.makeText(getApplicationContext(), "网络未连接，请检查网络设置",
                            Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btn_city_manager://城市管理
                switchLeftLayout();
                switchFragment(citymanager, null);
                break;

            case R.id.btn_life://生活指数
                switchLeftLayout();
                switchFragment(new LifeIndexFragment(), null);
                break;

            case R.id.btn_change_bag://更换背景
                switchLeftLayout();
                switchFragment(new ChangeBackgroundFragment(), null);
                break;

            case R.id.btn_about://关于我
                switchLeftLayout();
                switchFragment(new AboutMeFragment(), null);
                break;

            case R.id.btn_share_app://分享app
                SystemUtils.shareApp(this);
                break;

            case R.id.exitapp://退出app
                showExitDialog();
                break;
        }
    }

    /**
     * 选择使用哪个fragment
     *
     * @param fragment
     * @param str
     */
    public void switchFragment(Fragment fragment, String str) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragmentlayout, fragment, null);
        ft.commit();
    }

    /**
     * 更新bg
     */
    private void refresh() {
        showDialog();
        switchFragment(homecontent, null);
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendRequest(HomePageFragment.currentcity.getText().toString());//根据城市刷新天气列表
            }
        }).start();
    }

    /**
     * 发送网络请求
     *
     * @param cityName
     */
    public void sendRequest(String cityName) {
        String getData = null;
        MHttpEntity mhe = null;
        try {
            SendDataEntity.setCity(cityName);// 获取用户输入的城市名
            mhe = MHttpEntity.sendHttpRequest(SendDataEntity.getData());
            if (mhe.getHentity() != null) {
                getData = EntityUtils.toString(mhe.getHentity());
                mhe.getMessage().obj = getData;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        handler.sendMessage(mhe.getMessage());// 使用Handler对网络状态做处理
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (pDialog != null)
                pDialog.dismiss();
            if (msg != null)
                switch (msg.arg1) {
                    case succeed:// 与服务器连接成功
                        if (msg.obj != null) {
                            fromJson(msg.obj.toString());
                        }
                        break;
                    case fail:// 与服务器连接失败
                        showToast(getString(R.string.net_fail));
                        break;
                }
        }
    };

    /**
     * 接续json
     *
     * @param wetherdata
     */
    private void fromJson(String wetherdata) {
        GsonBuilder gson = new GsonBuilder();//
        response2 = gson.create().fromJson(wetherdata, ResponseWrapper.class);
        if (response2.getError() == 0) {
            response = response2;
            homecontent.setpagedata();
            if (tag == 4 && inputcity != null) {
                closeinput(inputcity);
            }
        } else if (response2.getError() == -3 || response2.getError() == -2) {
            showToast(getString(R.string.input_truename));
        } else {
            showToast(getString(R.string.getdata_fail));
        }
        if (HomePageFragment.pDialog != null) {
            HomePageFragment.pDialog.dismiss();
        }
    }


    /**
     * 关闭输入法键盘
     */
    public void closeinput(EditText editText) {
        editText.setText("");
        InputMethodManager imm = (InputMethodManager)
                getSystemService(MainActivity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    /**
     * 管理菜单界面
     */
    private void switchLeftLayout() {
        if (mainDrawerLayout.isDrawerOpen(leftDrawer)) {
            mainDrawerLayout.closeDrawer(leftDrawer);
        } else {
            mainDrawerLayout.openDrawer(leftDrawer);
        }
    }

    /**
     * 推出app的对话框
     */
    private void showExitDialog() {
        final Dialog dialog = new Dialog(this,
                android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
        View exitappview = getLayoutInflater().inflate(R.layout.exitapp_dialog, null);
        TextView exitapp_text = (TextView) exitappview.findViewById(R.id.exitapp_text);
        Button leftbutton = (Button) exitappview.findViewById(R.id.leftbutton);
        Button rightbutton = (Button) exitappview.findViewById(R.id.rightbutton);
        exitapp_text.setText("退出程序");
        leftbutton.setText("确定");
        rightbutton.setText("取消");
        leftbutton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rightbutton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setContentView(exitappview);
        dialog.show();
    }


    /**
     * 点击多次bt，Toast只显示一次的解决方案
     */
    public Toast toast = null;

    public void showToast(String text) {
        if (toast == null) {
            toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        } else {
            toast.setText(text);
        }
        toast.show();
    }

    /**
     * 连续按两次返回则退出程序
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - nowtime > 2000) {
                Toast.makeText(this, R.string.click_exit, Toast.LENGTH_SHORT).show();
                nowtime = System.currentTimeMillis();
                return true;
            } else {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 一定要反注册广播
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }


}
