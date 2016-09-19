package com.shur.zhiliaoweather.adapter;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.shur.zhiliaoweather.R;
import com.shur.zhiliaoweather.entity.BgPicEntity;
import com.shur.zhiliaoweather.utils.SharePrefrenceUtil;

import java.util.List;

/**
 * Created by Shur on 2016/9/18.
 * 更换背景图的适配器
 */
public class BgPicGridAdapter extends BaseAdapter {

    private List<BgPicEntity> bgList;
    private Resources resources;
    private Activity mActivity;
    private String mDefaultBgPath;
    private SharePrefrenceUtil sharePrefrenceUtil;

    public BgPicGridAdapter(Activity mActivity, List<BgPicEntity> list) {
        this.bgList = list;
        this.mActivity = mActivity;
        this.resources = mActivity.getResources();
        sharePrefrenceUtil = new SharePrefrenceUtil(mActivity);
    }

    @Override
    public int getCount() {
        return bgList.size();
    }

    @Override
    public Object getItem(int position) {
        return bgList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.bg_pic_grid_item, null);
            viewHolder.backgroundIv = (ImageView) convertView.findViewById(R.id.gridview_item_iv);
            viewHolder.checkedIv = (ImageView) convertView.findViewById(R.id.gridview_item_checked_iv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.backgroundIv.setBackgroundDrawable(
                new BitmapDrawable(resources, ((BgPicEntity) getItem(position)).bitmap));

        mDefaultBgPath = sharePrefrenceUtil.getPath();
        if (((BgPicEntity) getItem(position)).path.equals(mDefaultBgPath)) {
            viewHolder.checkedIv.setVisibility(View.VISIBLE);
        } else {
            viewHolder.checkedIv.setVisibility(View.GONE);
        }

        return convertView;
    }

    //listview的优化
    private class ViewHolder {
        ImageView checkedIv, backgroundIv;
    }
}
