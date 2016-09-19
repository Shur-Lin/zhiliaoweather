package com.shur.zhiliaoweather.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.shur.zhiliaoweather.R;
import com.shur.zhiliaoweather.activity.MainActivity;
import com.shur.zhiliaoweather.adapter.GridTodayCAdapter;
import com.shur.zhiliaoweather.entity.LivingIndexEntity;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Shur on 2016/9/15.
 * 生活指数
 */
public class LifeIndexFragment extends Fragment {

    public static final String TAG = "TodayCan";
    public TextView descTv;
    public List<LivingIndexEntity> listsib;
    private TextView dateTv;
    private View baseView;
    private GridView todayInfoGrid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MainActivity.TAG_H = TAG;
        baseView = inflater.inflate(R.layout.gridview_todaycan, null);

        initView();
        initData();
        return baseView;
    }

    /**
     * 初始化界面
     */
    private void initData() {

        initDate();//获取更新日期

        if (MainActivity.response.getResults() == null) {
            Toast.makeText(getActivity(), "获取指数信息失败，请检查网络连接", Toast.LENGTH_SHORT).show();
        } else {
            setData();//时间设置
        }

    }

    private void setData() {
        listsib = MainActivity.response.getResults().get(0).getIndex();
        if (MainActivity.response.getResults().get(0).getIndex().toString() == "[]") {
        } else {
            todayInfoGrid.setAdapter(new GridTodayCAdapter(getActivity(), listsib));
        }
        todayInfoGrid.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                descTv.setText(listsib.get(position).getDes());//显示描述

            }
        });
    }

    private void initDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int mon = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        dateTv.setText(year + "年" + mon + "月" + day + "日");
    }

    /**
     * 初始化界面
     */
    private void initView() {
        todayInfoGrid = (GridView) baseView.findViewById(R.id.gridview);
        dateTv = (TextView) baseView.findViewById(R.id.date_tv);
        descTv = (TextView) baseView.findViewById(R.id.todaycan_dec);
    }


}
