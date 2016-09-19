package com.shur.zhiliaoweather.entity;

/**
 * Created by Shur on 2016/9/13.
 * 通过百度地图定位得到的城市名来获取该城市的天气信息的实体类
 */
public class SendDataEntity {

    public static String city = "";
    public static String json = "json";
    public static String ak = "N7Y24lnMMYHOXGZEgKPXKp7WNhLmipUh";

    public static void setCity(String city) {
        SendDataEntity.city = city;
    }
    public static void setJson(String json) {
        SendDataEntity.json = json;
    }
    public static void setAk(String ak) {
        SendDataEntity.ak = ak;
    }
    public static String getCity() {
        return city;
    }
    public static String getJson() {
        return json;
    }
    public static String getAk() {
        return ak;
    }
    public static String getData() {
        return "http://api.map.baidu.com/telematics/v3/weather?location=" + city + "&output="+ json +"&ak="+ ak;
    }
//http://api.map.baidu.com/telematics/v3/weather?location=广州&output=json&ak=N7Y24lnMMYHOXGZEgKPXKp7WNhLmipUh
}
