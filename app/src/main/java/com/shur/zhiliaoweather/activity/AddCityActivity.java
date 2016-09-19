package com.shur.zhiliaoweather.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.shur.zhiliaoweather.R;
import com.shur.zhiliaoweather.adapter.GridAddCityAdapter;
import com.shur.zhiliaoweather.entity.SQLiteCityManager;

/**
 * 添加城市
 */
public class AddCityActivity extends Activity {

    private GridView addCityGrid;
    private static TextView cityTv;
    private boolean ishas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_city);
        initView();//初始化界面
    }

    //初始化界面
    private void initView() {
        addCityGrid = (GridView) findViewById(R.id.addcity_gridview);
        GridAddCityAdapter ad = new GridAddCityAdapter(this);
        addCityGrid.setAdapter(ad);

        addCityGrid.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cityTv = (TextView) view.findViewById(R.id.citytext);
                cityTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.city_checkbox_selected, 0);
                querydata(cityTv.getText().toString());
                // 如果数据库中没有该城市，则添加到数据库。反之则提示。
                if (!ishas) {
                    insertdata();
                    finish();
                } else {
                    Toast.makeText(AddCityActivity.this, "不可重复添加", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //获取城市数据库管理
    private SQLiteCityManager sqlite = new SQLiteCityManager(AddCityActivity.this, "weatherdb",
            null, 1);

    //插入数据库
    private void insertdata() {
        SQLiteDatabase db = sqlite.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("cityname", cityTv.getText().toString());
        cv.put("imageurl", "");
        cv.put("weather", "点击更新");
        cv.put("temp", "0℃");
        //插入语句
        db.insert("dreamWeather", "cityname", cv);
    }

    //查询数据库
    public void querydata(String str) {
        // 读写数据库
        SQLiteDatabase db = sqlite.getReadableDatabase();
        Cursor cursor = db.query("dreamWeather", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String cityname = cursor.getString(cursor.getColumnIndex("cityname"));
            cityname = cityname.substring(0, 2);
            str = str.substring(0, 2);
            // 与当前按下的城市名做比较
            if (ishas = cityname.equals(str)) {
                return;
            }
        }
    }

}
