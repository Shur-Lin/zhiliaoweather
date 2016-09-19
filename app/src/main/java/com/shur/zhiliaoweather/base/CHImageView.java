package com.shur.zhiliaoweather.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Created by Shur on 2016/9/16.
 * 自定义加载图片控件
 * 继承自ImageView，用于异步加载图片，在下载图片时使用设置的loading图片占位，图片下载好后刷新View
 */
public class CHImageView extends ImageView {
    /**
     * 用于记录默认下载中状态的图片
     */
    private int downLoadingImageId = 0;
    private int downLoadingImagefailureId = 0;
    // 图片是否加载成功
    private boolean loadSuccess = false;

    public CHImageView(Context context) {
        super(context);
    }

    public CHImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CHImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 不设置将使用默认图片 设置下载中，与加载失败的图片
     *
     * @param downlding 加载图片中
     * @param failureId 加载图片失败
     */
    public void setDefultDownLoadAndFailureImage(int downlding, int failureId) {
        downLoadingImageId = downlding;
        downLoadingImagefailureId = failureId;
    }

    /**
     * 对外接口，用于调用ImageView的异步下载图片功能
     *
     * @param url 图片的URL
     */
    public void loadImage(String url) {

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showStubImage(downLoadingImageId)
                .showImageForEmptyUri(downLoadingImagefailureId)
                .cacheInMemory().cacheOnDisc()
                .showImageOnFail(downLoadingImagefailureId)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        ImageLoader.getInstance().displayImage(url, this, options);
//修改
        ImageLoader.getInstance().loadImage(url, options, new ImageLoadingListener() {

                    @Override
                    public void onLoadingStarted(String arg0, View arg1) {
                        loadSuccess = false;
                        setImageResource(downLoadingImageId);
                    }

                    @Override
                    public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
                        loadSuccess = false;
                        setImageResource(downLoadingImagefailureId);
                    }

                    @Override
                    public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {

                        if (getTag() == null || arg0.equals(getTag())) {
                            loadSuccess = true;
                            setImageBitmap(arg2);
                        }
                    }

                    @Override
                    public void onLoadingCancelled(String arg0, View arg1) {
                        loadSuccess = false;
                        setImageResource(downLoadingImagefailureId);
                    }
                });
    }

    //加载成功
    public boolean isLoadSuccess() {
        return loadSuccess;
    }
}
