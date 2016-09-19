package com.shur.zhiliaoweather.entity;

import android.net.Uri;
import android.os.Message;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;

import java.io.IOException;
import java.net.SocketTimeoutException;

/**
 * Created by Shur on 2016/9/13.
 * 网络请求
 */
@SuppressWarnings("deprecation")
public class MHttpEntity {

    private Message message;
    private HttpEntity hentity;
    public static final int succeed = 1;
    public static final int fail = 2;
    public static final int nonet = 3;

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public HttpEntity getHentity() {
        return hentity;
    }

    public void setHentity(HttpEntity hentity) {
        this.hentity = hentity;
    }

    public static MHttpEntity sendHttpRequest(String str) {
        MHttpEntity mhe = new MHttpEntity();
        Message message = Message.obtain();

        HttpEntity he = null;
        HttpClient hClient = new DefaultHttpClient();//实例化得到一个网络连接对象
        HttpConnectionParams.setConnectionTimeout(hClient.getParams(), 5000);//连接超时设置
        String mstr = Uri.decode(str);//将String类型转化为uri，有中文则必有此句
        HttpGet hget = new HttpGet(mstr);//创建请求
        try {
            HttpResponse response = hClient.execute(hget);//执行请求
            if (response.getStatusLine().getStatusCode() == 200) {//请求成功
                he = response.getEntity();
                message.arg1 = succeed;//保存网络状态
            } else {
                message.arg1 = fail;
            }
        } catch (SocketTimeoutException e) {
            message.arg1 = fail;
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            message.arg1 = fail;
            e.printStackTrace();
        } catch (IOException e) {
            message.arg1 = fail;
            e.printStackTrace();
        }
        mhe.setMessage(message);
        mhe.setHentity(he);
        return mhe;
    }
}
