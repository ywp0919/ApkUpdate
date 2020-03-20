package com.wepon.apkupdate_lib;

/**
 * Author: Wepon
 * Description:
 */
public class ApkUpdateBean {

    /**
     * 是否需要更新
     */
    private boolean isNeedUpdate;

    /**
     * 是否强制更新
     */
    private boolean isForceUpdate;

    /**
     * 下载url
     */
    private String apkDownloadUrl;

    /**
     * 新apk的版本code
     */
    private int versionCode;

    /**
     * 新apk的版本名称
     */
    private String versionName;

    /**
     * 更新日志
     */
    private String updateLog;

    /**
     * 更新标题
     */
    private String updateTitle;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 应用包名
     */
    private String packageName;

    /**
     * apk文件的hash
     */
    private String apkHash;

    /**
     * apk文件的字节大小
     */
    private String apkSize;

    //{
    //  "updateLog": "update log...",
    //  "appName": "UpdateTest",
    //  "packageName": "com.wepon.apkupdate",
    //  "versionCode": 2,
    //  "versionName": "1.2",
    //  "force": false,
    //  "apkUrl": "http://iot-smart-earphone.oss-cn-hangzhou.aliyuncs.com/autoupdate/apk/a14b2b65887fc1ecae3eff4fb66c50e3/apkupdate-version-2.apk",
    //  "apkHash": "a3af65bc5b7da503b2630657559e4121",
    //  "apkSize": "1681770"
    //}

    public boolean isNeedUpdate() {
        return isNeedUpdate;
    }

    public void setNeedUpdate(boolean needUpdate) {
        isNeedUpdate = needUpdate;
    }

    public boolean isForceUpdate() {
        return isForceUpdate;
    }

    public void setForceUpdate(boolean forceUpdate) {
        isForceUpdate = forceUpdate;
    }

    public String getApkDownloadUrl() {
        return apkDownloadUrl;
    }

    public void setApkDownloadUrl(String apkDownloadUrl) {
        this.apkDownloadUrl = apkDownloadUrl;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getUpdateLog() {
        return updateLog;
    }

    public void setUpdateLog(String updateLog) {
        this.updateLog = updateLog;
    }

    public String getUpdateTitle() {
        return updateTitle;
    }

    public void setUpdateTitle(String updateTitle) {
        this.updateTitle = updateTitle;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getApkHash() {
        return apkHash;
    }

    public void setApkHash(String apkHash) {
        this.apkHash = apkHash;
    }

    public String getApkSize() {
        return apkSize;
    }

    public void setApkSize(String apkSize) {
        this.apkSize = apkSize;
    }


    @Override
    public String toString() {
        return "ApkUpdateBean{" +
                "isNeedUpdate=" + isNeedUpdate +
                ", isForceUpdate=" + isForceUpdate +
                ", apkDownloadUrl='" + apkDownloadUrl + '\'' +
                ", versionCode=" + versionCode +
                ", versionName='" + versionName + '\'' +
                ", updateLog='" + updateLog + '\'' +
                ", updateTitle='" + updateTitle + '\'' +
                ", appName='" + appName + '\'' +
                ", packageName='" + packageName + '\'' +
                ", apkHash='" + apkHash + '\'' +
                ", apkSize='" + apkSize + '\'' +
                '}';
    }
}
