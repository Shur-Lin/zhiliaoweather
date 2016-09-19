package com.shur.zhiliaoweather.adapter;

import android.app.Dialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.shur.zhiliaoweather.R;
import com.shur.zhiliaoweather.base.CHImageView;
import com.shur.zhiliaoweather.entity.CityManagerEntity;
import com.shur.zhiliaoweather.entity.SQLiteCityManager;

import java.util.List;

/**
 * Created by Shur on 2016/9/17.
 * 城市数据适配器
 */
public class GridCityMAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<CityManagerEntity> citymanager;
    private Context context;
    private Dialog mDialog;

    public GridCityMAdapter(Context context, List<CityManagerEntity> citymanager) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.citymanager = citymanager;
    }

    @Override
    public int getCount() {
        return citymanager == null ? 0 : citymanager.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_gridview_citymanager, parent, false);
        }
        //控件
        final TextView grid_city = (TextView) convertView.findViewById(R.id.grid_city);
        TextView grid_temp = (TextView) convertView.findViewById(R.id.grid_temp);
        CHImageView grid_weatherimage = (CHImageView) convertView.findViewById(R.id.grid_weatherimage);
        TextView grid_weather = (TextView) convertView.findViewById(R.id.grid_weather);
        Button grid_set_normal = (Button) convertView.findViewById(R.id.grid_set_normal);
        TextView city_item_layout = (TextView) convertView.findViewById(R.id.city_item_layout);
        TextView grid_item_delete = (TextView) convertView.findViewById(R.id.grid_item_delete);

        //删除城市
        grid_item_delete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mDialog = new Dialog(context, android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
                View deleteview = LayoutInflater.from(context).inflate(R.layout.exitapp_dialog, null);
                TextView exitapp_text = (TextView) deleteview.findViewById(R.id.exitapp_text);
                Button leftbutton = (Button) deleteview.findViewById(R.id.leftbutton);
                Button rightbutton = (Button) deleteview.findViewById(R.id.rightbutton);

                exitapp_text.setText("删除城市");
                leftbutton.setText("确定");
                rightbutton.setText("取消");
                //确定对话框
                leftbutton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SQLiteCityManager sqlite = new SQLiteCityManager(context, "weatherdb", null, 1);
                        SQLiteDatabase db = sqlite.getWritableDatabase();
                        String mcityname = grid_city.getText().toString();
                        int index = db.delete("coolWeather", "cityname = ?", new String[]{mcityname});
                        if (index == 0) {
                            Toast.makeText(context, "删除失败，请重试", Toast.LENGTH_SHORT).show();
                        }
                        mDialog.dismiss();
                        for (int i = 0; i < citymanager.size(); i++) {
                            if (mcityname.equals(citymanager.get(i).getCity())) {
                                citymanager.remove(i);
                            }
                        }
                        notifyDataSetChanged();
                    }
                });
                //取消对话框
                rightbutton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                    }
                });
                mDialog.setContentView(deleteview);
                mDialog.show();
            }
        });
        //
        grid_set_normal.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 设为默认的点击事件
            }
        });
        if (position == citymanager.size() - 1) {
            grid_city.setText("");
            grid_temp.setText("");
            grid_weather.setText("");
            grid_set_normal.setText("");
            grid_weatherimage.setImageDrawable(null);
            grid_set_normal.setBackgroundColor(Color.TRANSPARENT);
            city_item_layout.setBackgroundResource(R.drawable.cityadd_bg);
            grid_item_delete.setText("");
        } else {
            grid_item_delete.setText("×");
            grid_city.setText(citymanager.get(position).getCity());
            grid_temp.setText(citymanager.get(position).getTemp());
            grid_weatherimage.loadImage(citymanager.get(position).getWeatherimage());
            grid_weather.setText(citymanager.get(position).getWeather());
            grid_set_normal.setBackgroundResource(R.drawable.citym_normal_bg);
            if (position == 0) {
                grid_set_normal.setText("默认");
            } else {
                grid_set_normal.setText("设为默认");
            }
            city_item_layout.setBackgroundColor(Color.TRANSPARENT);//修改过
        }
        return convertView;
    }

    public void setCitymanager(List<CityManagerEntity> citymanager) {
        this.citymanager = citymanager;
    }

}
