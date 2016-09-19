package com.shur.zhiliaoweather.base;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/**
 * Created by Shur on 2016/9/15.
 * 我的application
 */
public class MyApplication extends Application{

    @Override
    public void onCreate() {
        initImageLoader(this);
        super.onCreate();
    }

    /**
     * 加载图片 使用第三方图片
     * @param context
     */
    private void initImageLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY-2)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .discCacheSize(32 * 1024 * 1024)
                .memoryCacheSize(4 * 1024 * 1024)
                .build();
        ImageLoader.getInstance().init(config);
    }

    public static boolean isNetWorkConnect(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo == null || !networkInfo.isAvailable()){
            return false;
        }else{
            return true;
        }
    }
}
