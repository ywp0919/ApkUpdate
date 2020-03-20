package com.wepon.apkupdate_lib;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

class ApkDownloadUtils {
    private DownloadManager mDownloadManager;
    private Context mContext;
    private long mDownloadId;
    private String mFileName;
    private String mDownloadApkPath;
    private volatile boolean isCancel = false;

    /**
     * 这种使用内部的DownloadManager，没有回调，通知栏会有系统级别的样式展示下载进度。
     */
    ApkDownloadUtils(Context context, String url, String fileName, String title, String description) {
        this.mContext = context;
        this.mFileName = fileName;
        downloadApkByInnerMananger(url, fileName, title, description);
    }

    /**
     * 这种使用http请求下载，并且给出回调，内部不做下载的IU显示，由调用者根据回调自行处理
     */
    ApkDownloadUtils(final Context context, final String urlStr, final String fileName, final ApkDownloadListener callback) {
        final File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName);
        if (file.exists()) {
            file.delete();
        }
        ApkUpdate.threadPool.execute(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                InputStream is = null;
                OutputStream os = null;
                try {
                    int count = 0;
                    URL url = new URL(urlStr);
                    conn = (HttpURLConnection) url.openConnection();
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        final int totalSize = conn.getContentLength();
                        is = conn.getInputStream();
                        os = new FileOutputStream(file);
                        int len;
                        byte[] bs = new byte[1024];
                        while ((len = is.read(bs)) != -1) {
                            if (isCancel) {
                                return;
                            }
                            os.write(bs, 0, len);
                            count += len;
                            final int finalCount = count;
                            ApkUpdate.mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onProgressUpdate(finalCount * 100 / totalSize);
                                }
                            });
                        }

                        ApkUpdate.mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.downloadSuc(getUriFromFile(context, file));
                            }
                        });
                    }
                } catch (final Exception e) {
                    e.printStackTrace();
                    ApkUpdate.mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.downloadFailed(e);
                        }
                    });
                } finally {
                    CloseUtils.closeQuietly(conn, is, os);
                }
            }
        });
    }

    void cancel() {
        if (mDownloadManager != null) {
            if (mContext != null) {
                mContext.unregisterReceiver(receiver);
            }
            mDownloadManager.remove(mDownloadId);
        }
        isCancel = true;
    }

    private Uri getUriFromFile(Context context, File file) {
        Uri apkUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            apkUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
        } else {
            apkUri = Uri.fromFile(file);
        }
        return apkUri;
    }

    private void downloadApkByInnerMananger(String url, String name, String title, String description) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setAllowedOverRoaming(true);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setTitle(title);
        request.setDescription(description);
        request.setVisibleInDownloadsUi(true);
        File file = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), name);
        request.setDestinationUri(Uri.fromFile(file));
        mDownloadApkPath = file.getAbsolutePath();
        if (mDownloadManager == null) {
            mDownloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        }
        if (file.exists()) {
            file.delete();
        }
        if (mDownloadManager != null) {
            mDownloadId = mDownloadManager.enqueue(request);
        }

        //监听下载状态
        mContext.registerReceiver(receiver,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkStatus();
        }
    };

    private void checkStatus() {
        DownloadManager.Query query = new DownloadManager.Query();
        //通过下载的id查找
        query.setFilterById(mDownloadId);
        Cursor cursor = mDownloadManager.query(query);
        if (cursor.moveToFirst()) {
            int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch (status) {
                //下载完成
                case DownloadManager.STATUS_SUCCESSFUL:
                    //下载完成安装APK
                    installAPK();
                    cursor.close();
                    mContext.unregisterReceiver(receiver);
                    break;
                //下载失败
                case DownloadManager.STATUS_FAILED:
                    Toast.makeText(mContext, "下载失败", Toast.LENGTH_SHORT).show();
                    cursor.close();
                    mContext.unregisterReceiver(receiver);
                    break;
            }
        }
    }

    private void installAPK() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            File file = new File(mDownloadApkPath);
            Uri apkUri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".fileprovider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(new File(Environment.DIRECTORY_DOWNLOADS, mFileName)), "application/vnd.android.package-archive");
        }
        mContext.startActivity(intent);
    }


}