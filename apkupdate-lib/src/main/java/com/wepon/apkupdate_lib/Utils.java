package com.wepon.apkupdate_lib;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import org.json.JSONObject;

/**
 * Author: Wepon
 * Description:
 */
class Utils {
    /**
     * 获取应用程序名称
     */
    static String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getApplicationContext().getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取当前app version code
     */
    static int getAppVersionCode(Context context) {
        long appVersionCode = ApkUpdate.NOT_DEFINE;
        try {
            PackageInfo packageInfo = context.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                appVersionCode = packageInfo.getLongVersionCode();
            } else {
                appVersionCode = packageInfo.versionCode;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return (int) appVersionCode;
    }

    static void parse(ApkUpdateApiFieldBean apiFieldBean, ApkUpdateBean apkUpdateBean, JSONObject jsonObject) {
        if (!TextUtils.isEmpty(apiFieldBean.getIsNeedUpdateFieldName())) {
            apkUpdateBean.setNeedUpdate(jsonObject.optBoolean(apiFieldBean.getIsNeedUpdateFieldName()));
        }
        if (!TextUtils.isEmpty(apiFieldBean.getIsForceUpdateFieldName())) {
            apkUpdateBean.setForceUpdate(jsonObject.optBoolean(apiFieldBean.getIsForceUpdateFieldName()));
        }
        if (!TextUtils.isEmpty(apiFieldBean.getApkDownloadUrlFieldName())) {
            apkUpdateBean.setApkDownloadUrl(jsonObject.optString(apiFieldBean.getApkDownloadUrlFieldName()));
        }
        if (!TextUtils.isEmpty(apiFieldBean.getVersionCodeFieldName())) {
            apkUpdateBean.setVersionCode(jsonObject.optInt(apiFieldBean.getVersionCodeFieldName(), ApkUpdate.NOT_DEFINE));
        }
        if (!TextUtils.isEmpty(apiFieldBean.getVersionNameFieldName())) {
            apkUpdateBean.setVersionName(jsonObject.optString(apiFieldBean.getVersionNameFieldName()));
        }
        if (!TextUtils.isEmpty(apiFieldBean.getUpdateLogFieldName())) {
            apkUpdateBean.setUpdateLog(jsonObject.optString(apiFieldBean.getUpdateLogFieldName()));
        }
        if (!TextUtils.isEmpty(apiFieldBean.getUpdateTitleFieldName())) {
            apkUpdateBean.setUpdateTitle(jsonObject.optString(apiFieldBean.getUpdateTitleFieldName()));
        }
        if (!TextUtils.isEmpty(apiFieldBean.getAppNameFieldName())) {
            apkUpdateBean.setAppName(jsonObject.optString(apiFieldBean.getAppNameFieldName()));
        }
        if (!TextUtils.isEmpty(apiFieldBean.getPackageNameFieldName())) {
            apkUpdateBean.setPackageName(jsonObject.optString(apiFieldBean.getPackageNameFieldName()));
        }
        if (!TextUtils.isEmpty(apiFieldBean.getApkHashFieldName())) {
            apkUpdateBean.setApkHash(jsonObject.optString(apiFieldBean.getApkHashFieldName()));
        }
        if (!TextUtils.isEmpty(apiFieldBean.getApkSizeFieldName())) {
            apkUpdateBean.setApkSize(jsonObject.optString(apiFieldBean.getApkSizeFieldName()));
        }
    }
}
