package com.shur.zhiliaoweather.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Created by Shur on 2016/9/15.
 * 文件读写utils
 */
public class SharePrefrenceUtil {

    private static final String BG_PIC_PATH = "bg_pic_path";
    private SharedPreferences mSp;
    private Editor mEditor;

    public SharePrefrenceUtil(Context context) {
        mSp = context.getSharedPreferences("dreamWeather", Context.MODE_WORLD_WRITEABLE);
        mEditor = mSp.edit();
    }

    /**
     * 保存背景皮肤图片的地址
     *
     * @author: htq
     */
    public void saveBgPicPath(String path) {
        mEditor.putString(BG_PIC_PATH, path);
        mEditor.commit();
    }

    /**
     * 获取背景皮肤图片的地址
     */
    public String getPath() {
        return mSp.getString(BG_PIC_PATH, null);
    }

}
