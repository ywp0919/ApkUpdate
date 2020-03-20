package com.wepon.apkupdate_lib;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;


/**
 * Author: Wepon
 * Description: 使用入口
 */
public class ApkUpdate {

    /**
     * 这里就只设置2个限制的线程池，没必要太多。
     * 并且线程池也可以给外部配置。
     */
    static ExecutorService threadPool;

    static Handler mainHandler = new Handler(Looper.getMainLooper());

    private static Context applicationContext;

    static final int NOT_DEFINE = -1;
    static String TAG = "ApkUpdate";

    private static Map<String, WeakReference<ApkUpdate>> CACHE = new HashMap<>();

    private WeakReference<Context> contextRef;

    /**
     * 获取更新信息的url地址
     */
    private String updateInfoUrl;

    /**
     * 默认是get请求，参数和url外部传入。
     */
    private boolean isGetHttp;

    /**
     * 是否强制更新，和传入的字段一起使用。
     */
    private boolean isForceUpdate;

    /**
     * 请求更新信息的参数
     */
    private Map<String, Object> updateInfoParams;

    /**
     * http请求服务，为了外部能使用共用的网络框架，这里通过接口的方式交给外部去实现。
     */
    private IApkUpdateHttpServer updateHttp;

    /**
     * 错误回调信息
     */
    private ErrorInfoCallback errorInfoCallback;

    /**
     * 保存解析后的对象信息
     */
    private ApkUpdateBean apkUpdateBean;

    /**
     * 有无更新事件的回调监听
     */
    private ApkUpdateManagerListener apkUpdateManagerListener;

    /**
     * 外部调用download的话就必须实现这个listener才有回调。
     */
    private ApkDownloadListener apkDownloadListener;

    /**
     * 解析升级接口需要的字段名称结构，如果外部不自定义的话就会默认一个，格式见注释固定。
     */
    private ApkUpdateApiFieldBean apiFieldBean = ApkUpdateApiFieldBean.newDefault();

    /**
     * 下载的apk的命名 getContext().getPackageName() + "-" + newVersionCode + ".apk";
     */
    private String downloadApkFileName;


    private ApkUpdate(Builder builder) {
        this.apkDownloadListener = builder.apkDownloadListener;
        this.apkUpdateManagerListener = builder.apkUpdateManagerListener;
        this.isGetHttp = builder.isGetHttp;
        if (builder.apiFieldBean != null) {
            this.apiFieldBean = builder.apiFieldBean;
        }
        this.updateInfoParams = builder.updateInfoParams;
        this.updateHttp = builder.updateHttp;
        this.updateInfoUrl = builder.updateInfoUrl;
        this.isForceUpdate = builder.isForceUpdate;
        this.errorInfoCallback = builder.errorInfoCallback;

        synchronized (ApkUpdate.class) {
            if (threadPool == null) {
                threadPool = Executors.newFixedThreadPool(2);
            }
        }
    }

    private ApkUpdate() {
    }


    /**
     * 外部也可以自己获取到数据后直接传入json.
     */
    public void updateByJsonStr(String result, @NonNull Context context) {
        saveContext(context);
        parseUpdateInfo(result);
    }

    /**
     * 先进行网络请求，获取接口信息。
     * 需要显示弹窗的话就要传activity的context，不需要的话随便传。
     */
    public void update(@NonNull Context context) {
        try {
            saveContext(context);
            if (TextUtils.isEmpty(updateInfoUrl)) {
                throw new IllegalArgumentException("ApkUpdate : updateInfoUrl must not empty.");
            }
            // 获取接口信息，再判断是否之后需要进行更新下载.
            // 返回需要是json信息.
            if (updateHttp == null) {
                updateHttp = new DefaultApkUpdateHttpServer();
            }
            IApkUpdateHttpServer.Callback callback = new IApkUpdateHttpServer.Callback() {
                @Override
                public void onSuccess(String result) {
                    // 异步请求成功之后进行是否要更新的逻辑判断。
                    parseUpdateInfo(result);
                }

                @Override
                public void onError(Throwable throwable) {
                    ApkUpdateLogUtils.e("callback error : " + throwable.getLocalizedMessage());
                }
            };
            Map<String, Object> stringObjectMap =
                    updateInfoParams == null ? new HashMap<String, Object>() : updateInfoParams;
            if (isGetHttp) {
                updateHttp.asyncGet(updateInfoUrl, stringObjectMap, callback);
            } else {
                updateHttp.asyncPost(updateInfoUrl, stringObjectMap, callback);
            }
        } catch (Throwable throwable) {
            if (errorInfoCallback != null) {
                errorInfoCallback.onError(throwable);
            } else {
                ApkUpdateLogUtils.e("" + throwable.getLocalizedMessage());
            }
        }
    }

    private void saveContext(Context context) {
        this.contextRef = new WeakReference<>(apkUpdateManagerListener == null ? context : context.getApplicationContext());
        if (applicationContext == null)
            applicationContext = context.getApplicationContext();
    }


    /**
     * 需要解析成APkUpdateBean的格式，传入的json的格式要符合要求。
     */
    private void parseUpdateInfo(String result) {
        try {
            apkUpdateBean = new ApkUpdateBean();
            JSONObject jsonObject = new JSONObject(result);
            // 一个一个解析字段的value
            Utils.parse(apiFieldBean, apkUpdateBean, jsonObject);
            // 为后面的下载做的准备工作。
            if (TextUtils.isEmpty(apiFieldBean.getApkDownloadUrlFieldName())) {
                ApkUpdateLogUtils.e("ApkUpdate : apkDownloadUrlFieldName is can not empty.");
                return;
            }
            int newVersionCode = apkUpdateBean.getVersionCode();
            if (newVersionCode == NOT_DEFINE) {
                ApkUpdateLogUtils.e("ApkUpdate : versionCodeFieldName is not define.");
                return;
            }
            downloadApkFileName = getContext().getPackageName() + "-" + newVersionCode + ".apk";

            String downloadUrl = apkUpdateBean.getApkDownloadUrl();
            if (!TextUtils.isEmpty(downloadUrl)) {
                CACHE.put(downloadUrl, new WeakReference<>(ApkUpdate.this));
                Log.d("Wepon", "put url:" + downloadUrl);
                Log.d("Wepon", "put apkupdate:" + this);
            }

            // 判断是否需要更新。
            boolean needUpdate = apkUpdateBean.isNeedUpdate() || Utils.getAppVersionCode(getContext())
                    < jsonObject.optInt(apiFieldBean.getVersionCodeFieldName(), NOT_DEFINE);

            if (apkUpdateManagerListener != null) {
                if (needUpdate) {
                    apkUpdateManagerListener.onUpdateAvailable(apkUpdateBean);
                } else {
                    apkUpdateManagerListener.onNoUpdateAvailable();
                }
                return;
            }

            if (!needUpdate) {
                return;
            }

            // 显示内部的升级窗口
            showInnerUpdateDialog();

        } catch (JSONException e) {
            if (errorInfoCallback != null) {
                errorInfoCallback.onError(e);
            } else {
                ApkUpdateLogUtils.e("ApkUpdate : parseUpdateInfo result error : " + e.getLocalizedMessage());
            }
        }
    }

    private void showInnerUpdateDialog() {
        //需要更新的话，获取更新信息展示弹窗。
        ApkUpdatePromptBean updatePromptBean = new ApkUpdatePromptBean();
        if (!TextUtils.isEmpty(apkUpdateBean.getUpdateLog())) {
            updatePromptBean.setContent(apkUpdateBean.getUpdateLog());
        } else {
            updatePromptBean.setContent("发现新的版本！");
        }
        if (!TextUtils.isEmpty(apkUpdateBean.getUpdateTitle())) {
            updatePromptBean.setTitle(apkUpdateBean.getUpdateTitle());
        } else {
            updatePromptBean.setTitle("更新提示");
        }
        boolean isForce = isForceUpdate || apkUpdateBean.isForceUpdate();
        updatePromptBean.setIsForceUpdate(isForce);

        if (!(getContext() instanceof Activity)) {
            if (errorInfoCallback != null) {
                errorInfoCallback.onError(new IllegalArgumentException("ApkUpdate : the context for dialog must instanceof activity."));
            }
            ApkUpdateLogUtils.e("ApkUpdate : the context for dialog must instanceof activity.");
            return;
        }
        // show update dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setTitle(updatePromptBean.getTitle())
                .setMessage(updatePromptBean.getContent())
                .setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 去下载更新。
                        goInnerDownload();
                    }
                });
        if (!updatePromptBean.getIsForceUpdate()) {
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
        }
        builder.create().show();
    }

    /**
     * 内部使用DownloadManager的下载流程
     */
    private void goInnerDownload() {
        // 触发下载 DownloadManager.
        if (TextUtils.isEmpty(apkUpdateBean.getApkDownloadUrl())) {
            ApkUpdateLogUtils.e("ApkUpdate : apkDownloadUrl is can not empty.");
            return;
        }
        new ApkDownloadUtils(getContext(),
                apkUpdateBean.getApkDownloadUrl(),
                downloadApkFileName,
                Utils.getAppName(getContext()),
                "正在下载新版本...");
    }

    /**
     * 外部调用下载的话就走下载请求的流程，并在有回调的时候给到外部。
     */
    private void goCustomDownloadApk() {
        new ApkDownloadUtils(getContext(), apkUpdateBean.getApkDownloadUrl(),
                downloadApkFileName, apkDownloadListener);
    }


    private Context getContext() {
        if ((contextRef == null ? null : contextRef.get()) == null)
            throw new IllegalArgumentException("ApkUpdate : context is can not null.");
        return contextRef.get();
    }


    /**
     * 外部调用，传的这个url必须是内部通过升级接口解析后回调出去的url。
     */
    public static void downloadApk(String apkDownloadUrl) {
        if (TextUtils.isEmpty(apkDownloadUrl)) {
            ApkUpdateLogUtils.e("ApkUpdate : apkDownloadUrl is empty.");
            return;
        }
        if (CACHE.containsKey(apkDownloadUrl)
                && CACHE.get(apkDownloadUrl) != null
                && CACHE.get(apkDownloadUrl).get() != null) {
            ApkUpdate apkUpdate = CACHE.get(apkDownloadUrl).get();
            if (apkUpdate.apkDownloadListener != null) {
                apkUpdate.goCustomDownloadApk();
            } else {
                apkUpdate.goInnerDownload();
            }
        } else {
            ApkUpdateLogUtils.e("ApkUpdate : not found record by apkDownloadUrl. ");
        }
    }

    /**
     * 日志开关
     */
    public static void openLog(boolean open) {
        ApkUpdateLogUtils.apkUpdateLogOpen = open;
    }

    /**
     * 使用外部的线程池
     */
    public static void setExThreadPool(@NonNull ExecutorService executor) {
        threadPool = executor;
    }

    public static void installApk(Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        if (applicationContext == null) {
            ApkUpdateLogUtils.e("ApkUpdate : ApkUpdate.update function is not used.");
            return;
        }
        applicationContext.startActivity(intent);
    }

    public interface ErrorInfoCallback {
        void onError(Throwable throwable);
    }

    public static Builder newBuilder() {
        return new Builder();
    }


    public static class Builder {
        private ApkDownloadListener apkDownloadListener;
        private ApkUpdateManagerListener apkUpdateManagerListener;
        private boolean isGetHttp = true;
        private ApkUpdateApiFieldBean apiFieldBean;
        private Map<String, Object> updateInfoParams;
        private IApkUpdateHttpServer updateHttp;
        private String updateInfoUrl;
        private boolean isForceUpdate;
        private ErrorInfoCallback errorInfoCallback;

        private Builder() {
        }

        public Builder setApkDownloadListener(ApkDownloadListener apkDownloadListener) {
            this.apkDownloadListener = apkDownloadListener;
            return this;
        }

        public Builder setApkUpdateManagerListener(ApkUpdateManagerListener apkUpdateManagerListener) {
            this.apkUpdateManagerListener = apkUpdateManagerListener;
            return this;
        }

        public Builder setApiFieldBean(ApkUpdateApiFieldBean apiFieldBean) {
            this.apiFieldBean = apiFieldBean;
            return this;
        }

        public Builder setUpdateInfoParams(Map<String, Object> updateInfoParams) {
            this.updateInfoParams = updateInfoParams;
            return this;
        }

        public Builder setUpdateInfoUrl(String updateInfoUrl) {
            this.updateInfoUrl = updateInfoUrl;
            return this;
        }

        public Builder setForceUpdate(boolean forceUpdate) {
            this.isForceUpdate = forceUpdate;
            return this;
        }

        public Builder setGetHttp(boolean getHttp) {
            this.isGetHttp = getHttp;
            return this;
        }


        public Builder setUpdateHttpServer(IApkUpdateHttpServer updateHttp) {
            this.updateHttp = updateHttp;
            return this;
        }

        public Builder setErrorInfoCallback(ErrorInfoCallback errorInfoCallback) {
            this.errorInfoCallback = errorInfoCallback;
            return this;
        }

        /**
         * 外部也可以自己获取到数据后直接传入json.
         */
        public ApkUpdate build(){
            return new ApkUpdate(this);
        }
    }
}
