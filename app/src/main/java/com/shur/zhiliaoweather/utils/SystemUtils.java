package com.shur.zhiliaoweather.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by Shur on 2016/9/18.
 * 分享app
 */
public class SystemUtils {

    public static void shareApp(Activity mActivity)
    {
        String shareAppContent="各位亲爱的小伙伴们，我发现了一款简约好用且颜值爆表的天气app，分享给大家，记得关注作者的博客http://blog.csdn.net/alanlin101，福利多多哦！";

        new File(mActivity.getFilesDir(), "share.png").deleteOnExit();
        FileOutputStream fileOutputStream=null;
        try {
            fileOutputStream = mActivity.openFileOutput("share.png", 1);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Bitmap pic= BitmapFactory.decodeResource(mActivity.getResources(),
                com.shur.zhiliaoweather.R.drawable.cool_weather_icon);
        pic.compress(CompressFormat.JPEG, 100,fileOutputStream);

        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("image/*");
        intent.putExtra("sms_body", shareAppContent);
        intent.putExtra("android.intent.extra.TEXT",shareAppContent);
        intent.putExtra("android.intent.extra.STREAM",
                Uri.fromFile(new File(mActivity.getFilesDir(), "share.png")));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mActivity.startActivity(Intent.createChooser(intent,"好东西要与小伙伴们一起分享"));
    }
}
