package com.wepon.apkupdate_lib;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: Wepon
 * Description: 实现内部的网络请求，如果外部没有实现的话默认使用这个.
 */
public class DefaultApkUpdateHttpServer implements IApkUpdateHttpServer {


    private static String getParams(Map<String, Object> paramsMap) {
        StringBuilder result = new StringBuilder();
        for (HashMap.Entry<String, Object> entity : paramsMap.entrySet()) {
            result.append("&").append(entity.getKey()).append("=").append(entity.getValue());
        }
        return result.length() == 0 ? "" : result.substring(1);
    }

    @Override
    public void asyncGet(@NonNull final String urlStr, @NonNull final Map<String, Object> params, @NonNull final Callback callBack) {
        ApkUpdate.threadPool.execute(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                InputStream is = null;
                BufferedReader bf;
                try {
                    URL url = new URL(urlStr + getParams(params));
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        is = conn.getInputStream();
                        bf = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                        parseResult(is, bf, callBack);
                    }
                } catch (Exception e) {
                    callBack.onError(e);
                } finally {
                    CloseUtils.closeQuietly(conn, is);
                }
            }
        });

    }

    @Override
    public void asyncPost(@NonNull final String urlStr, @NonNull final Map<String, Object> params, @NonNull final Callback callBack) {
        ApkUpdate.threadPool.execute(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                InputStream is = null;
                BufferedReader bf;
                try {
                    URL url = new URL(urlStr);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.getOutputStream().write(getParams(params).getBytes());
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        is = conn.getInputStream();
                        bf = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                        parseResult(is, bf, callBack);
                    }
                } catch (Exception e) {
                    callBack.onError(e);
                } finally {
                    CloseUtils.closeQuietly(conn, is);
                }
            }
        });
    }

    private void parseResult(InputStream is, BufferedReader bf, final Callback callBack) throws IOException {
        StringBuilder buffer = new StringBuilder();
        String line;
        while ((line = bf.readLine()) != null) {
            buffer.append(line);
        }
        bf.close();
        is.close();
        final String result = buffer.toString();

        ApkUpdate.mainHandler.post(new Runnable() {
            @Override
            public void run() {
                callBack.onSuccess(result);
            }
        });
    }

}
