package com.wepon.apkupdate;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.wepon.apkupdate_lib.ApkDownloadListener;
import com.wepon.apkupdate_lib.ApkUpdate;
import com.wepon.apkupdate_lib.ApkUpdateApiFieldBean;
import com.wepon.apkupdate_lib.ApkUpdateBean;
import com.wepon.apkupdate_lib.ApkUpdateManagerListener;
import com.wepon.apkupdate_lib.IApkUpdateHttpServer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 打开日志开关
        ApkUpdate.openLog(true);
        // 设置咱们外部的线程池来管理请求线程的创建。
        ApkUpdate.setExThreadPool(Executors.newSingleThreadExecutor());
    }

    /**
     * 代理网络请求方式
     */
    public void testLocalData1(View view) {
        // demo 这里实际使用时需要用app的网络请求框架来请求真实数据，demo用的假数据，没做网络请求。
        getApkUpdateBuilder()
                // 这里可以设置成app调用方的网络请求框架。
                .setUpdateHttpServer(new IApkUpdateHttpServer() {
                    @Override
                    public void asyncGet(@NonNull String url, @NonNull Map<String, Object> params, @NonNull Callback callBack) {
                        // 这里回传了一份假json数据，实际应用时需要做网络请求获取到数据。
                        // 通过调用者的网络框架获取到数据后回调  callBack.onSuccess();
                        success(callBack);
                    }

                    @Override
                    public void asyncPost(@NonNull String url, @NonNull Map<String, Object> params, @NonNull Callback callBack) {
                        success(callBack);
                    }
                })
                .build()
                .update(this);// 需要传入一个activity来显示dialog信息。
    }

    /**
     * 传入调用者已经生成的json string，内部可以解析这个json，走后面的流程。
     */
    public void testLocalData2(View view) {
        // 有些时候如果外部拿到了升级接口的json数据，也可以转换后传入直接调用。
        getApkUpdateBuilder()
                .build()
                .updateByJsonStr(getTestUpdateJsonString(), this);// 需要传入一个activity来显示dialog信息。
    }

    /**
     * 只配置更新接口url，一键式调用。
     */
    public void testNetApiData(View view) {
        ApkUpdate.newBuilder()
                .setUpdateInfoUrl("https://wepon.oss-cn-hangzhou.aliyuncs.com/apkupdate_lib/updateInfo") // 获取升级信息接口，demo这步是乱填的。
                .build()
                .update(this);
    }

    /**
     * 可根据回调自定义UI或者自定义操作。
     */
    public void testNetApiCustomData(View view) {
        ApkUpdate.newBuilder()
                .setUpdateInfoUrl("https://wepon.oss-cn-hangzhou.aliyuncs.com/apkupdate_lib/updateInfo") // 获取升级信息接口，demo这步是乱填的。
                // 设置了是否有更新的监听处理，如果设置了这个，内部就不会进行弹窗以及之后的流程了。
                // 用户可以根据以下回调自定义 升级显示的UI. 如果使用内置的则不需要设置此方法 。
                .setApkUpdateManagerListener(new ApkUpdateManagerListener() {
                    @Override
                    public void onNoUpdateAvailable() {
                        // 没有更新
                    }

                    @Override
                    public void onUpdateAvailable(final ApkUpdateBean apkUpdateBean) {
                        // 有新的升级
                        // 在这里用户可以自行下载，也可以调用sdk进行下载，如：
                        // 注意，这里的url只能使用onUpdateAvailable回调的这个对象里面的url，不支持其他的。

                        // show update dialog
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle(apkUpdateBean.getUpdateTitle())
                                .setMessage(apkUpdateBean.getUpdateLog())
                                .setCancelable(true)
                                .setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // 去下载更新。
                                        ApkUpdate.downloadApk(apkUpdateBean.getApkDownloadUrl());
                                        // 同时可以显示下载的进度ui
                                    }
                                })
                                .create()
                                .show();

                    }
                })
                // 用户可以根据以下回调自定义下载的UI. 如果使用内置的则不需要设置此方法.
                // 如果不接收回调，内部则会在下载完成后自动调用安装逻辑
                // 主线程回调
                .setApkDownloadListener(new ApkDownloadListener() {
                    @Override
                    public void downloadFailed(Throwable throwable) {
                        // 下载失败
                        // 可以取消进度ui
                    }

                    @Override
                    public void downloadSuc(Uri uri) {
                        // 下载成功
                        // 可以取消进度ui
                        // 可以使用sdk的方法进行安装
                        ApkUpdate.installApk(uri);
                    }

                    @Override
                    public void onProgressUpdate(int progress) {
                        // 进度回调，值为0-100.
                        Log.d("Wepon", "progress:" + progress);
                        // 可以刷新进度ui
                    }
                })
                .build()
                .update(this);
    }


    private ApkUpdate.Builder getApkUpdateBuilder() {
        ApkUpdateApiFieldBean bean = new ApkUpdateApiFieldBean();
        bean.setApkDownloadUrlFieldName("apkDownloadUrl"); // apk下载地址的字段
        bean.setIsForceUpdateFieldName("isForceUpdate");// 是否强制升级的字段
//        bean.setIsNeedUpdateFieldName("isNeedUpdate"); // 此次是否需要更新
        bean.setVersionCodeFieldName("versionCode");// 新版本VersionCode对应的字段
        bean.setUpdateLogFieldName("updateLog"); // 升级窗口需要显示的内容
        bean.setUpdateTitleFieldName("updateTitle");// 升级显示需要显示的标题

        return ApkUpdate.newBuilder()
                .setForceUpdate(false) // 本地设置是否强制更新 （升级接口也可以配置字段来实现是否强制升级，两者有一个强制即为强制）
                .setGetHttp(true) // get请求，否则post请求。
                .setUpdateInfoUrl("https://xxx.yyyy") // 获取升级信息接口，demo本地数据示例的这一步是乱填的。
                .setUpdateInfoParams(new HashMap<String, Object>()) // 获取升级信息接口需要的参数，不需要可以不传。
                .setApiFieldBean(bean) // 设置一些字段名称
                // ApkUpdate里面的异常信息回调，不捕获的话就不加这个就行。
                .setErrorInfoCallback(new ApkUpdate.ErrorInfoCallback() {
                    @Override
                    public void onError(Throwable throwable) {
                        Toast.makeText(MainActivity.this, throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 下面的数据都是测试时用的假数据，测试apkDownloadUrl下载地址也需要自己弄一个...
     */
    private void success(IApkUpdateHttpServer.Callback callBack) {
        callBack.onSuccess(getTestUpdateJsonString());
    }

    private String getTestUpdateJsonString() {
        return "{\n" +
                "\"isNeedUpdate\": true,\n" +
                "\"isForceUpdate\": false,\n" +
                "\"versionCode\": 2,\n" +
                "\"versionName\": \"1.2\",\n" +
                "\"updateTitle\": \"重大更新\",\n" +
                "\"updateLog\": \"\\r\\n1、优化接口。\\r\\n2、优化更新提示界面。\",\n" +
                "\"apkDownloadUrl\": \"https://wepon.oss-cn-hangzhou.aliyuncs.com/apkupdate_lib/apkupdate_version_2.apk\"\n" +
                "}";
    }


}
