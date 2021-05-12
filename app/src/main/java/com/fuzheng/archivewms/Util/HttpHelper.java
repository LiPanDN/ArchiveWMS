package com.fuzheng.archivewms.Util;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class HttpHelper {

    public static final String BASE_URL = "http://localhost:58843/AndroidService.svc/";
    private static final String USER_AGENT = "Mozilla/4.5";

    private static HttpPost getHttpPost(String url, String json) {
        HttpPost request = null;
        try {
            request = new HttpPost(url);
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-Type", "application/json");
            request.setHeader("User-Agent", USER_AGENT);
        } catch (IllegalArgumentException e) {
            System.out.println("网络调用出现异常，请检查访问的URL地址是否正确");
            return null;
        } catch (Exception ex) {
            System.out.println("网络调用出现异常，请检查网络是否开启！");
            return null;
        }
        if (json != null) {
            try {
                StringEntity entity = new StringEntity(json, "UTF-8");
                request.setEntity(entity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return request;
    }

    public static String Post(String url, String json) {
        HttpPost request = HttpHelper.getHttpPost(url, json);
        String result = null;
        if (request == null) {
            result = "Post调用网络失败，网络有问题";
            return result;
        }
        try {
            HttpResponse response = new DefaultHttpClient().execute(request);
            if (response.getStatusLine().getStatusCode() == 200) {
                result = EntityUtils.toString(response.getEntity());
            } else {
                result = "网络Post异常:请求失败！状态码："+response.getStatusLine().getStatusCode() ;
            }
        } catch (ClientProtocolException e) {
            result = "网络Post异常ClientProtocolException错误！";

        } catch (IOException e) {
            e.printStackTrace();
            result = "网络Post异常！IOException错误";
        }catch (Exception e) {
            Log.i("mytag", e.toString());
            e.printStackTrace();
            result = "异常！出现错误！";
        }
        return result;
    }

    // ����Get���󣬻����Ӧ��ѯ���
    public static String Get(String url) {
        // ���HttpGet����
        HttpGet request = null;
        String result = null;
        try {
            request = new HttpGet(url);
        } catch (IllegalArgumentException e) {
            result = "网络Get调用出现异常，请检查访问的URL地址是否正确";
            System.out.println(result);
            return result;
        } catch (Exception ex) {
            result = "网络Get调用出现异常，请检查网络是否开启！";
            System.out.println(result);
            return result;
        }
        try {
            HttpResponse response = new DefaultHttpClient().execute(request);
            if (response.getStatusLine().getStatusCode() == 200) {
                result = EntityUtils.toString(response.getEntity());
                return result;
            }
        } catch (ClientProtocolException e) {
            result = "网络Get异常！ClientProtocolException错误！";
            return result;
        } catch (IOException e) {
            result = "网络Get异常！IOException错误！";
            return result;
        }
        return null;
    }
}