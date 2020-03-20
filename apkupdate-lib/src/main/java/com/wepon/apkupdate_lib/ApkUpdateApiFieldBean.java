package com.wepon.apkupdate_lib;

/**
 * Author: Wepon
 * Description:
 */
public class ApkUpdateApiFieldBean {
    /**
     * 是否有更新的字段名称
     */
    private String isNeedUpdateFieldName;

    /**
     * 是否强制更新的字段名称
     */
    private String isForceUpdateFieldName;

    /**
     * 更新apk下载地址的字段名称
     */
    private String apkDownloadUrlFieldName;

    /**
     * 新版本的code对应的字段名称
     */
    private String versionCodeFieldName;

    /**
     * 新版本的name对应的字段名称
     */
    private String versionNameFieldName;

    /**
     * 更新内容对应的字段名称
     */
    private String updateLogFieldName;

    /**
     * 更新标题对应的字段名称
     */
    private String updateTitleFieldName;

    /**
     * app名称
     */
    private String appNameFieldName;

    /**
     * 包名
     */
    private String packageNameFieldName;

    /**
     * hash
     */
    private String apkHashFieldName;

    /**
     * size
     */
    private String apkSizeFieldName;

    public String getIsNeedUpdateFieldName() {
        return isNeedUpdateFieldName;
    }

    public void setIsNeedUpdateFieldName(String isNeedUpdateFieldName) {
        this.isNeedUpdateFieldName = isNeedUpdateFieldName;
    }

    public String getIsForceUpdateFieldName() {
        return isForceUpdateFieldName;
    }

    public void setIsForceUpdateFieldName(String isForceUpdateFieldName) {
        this.isForceUpdateFieldName = isForceUpdateFieldName;
    }

    public String getApkDownloadUrlFieldName() {
        return apkDownloadUrlFieldName;
    }

    public void setApkDownloadUrlFieldName(String apkDownloadUrlFieldName) {
        this.apkDownloadUrlFieldName = apkDownloadUrlFieldName;
    }

    public String getVersionCodeFieldName() {
        return versionCodeFieldName;
    }

    public void setVersionCodeFieldName(String versionCodeFieldName) {
        this.versionCodeFieldName = versionCodeFieldName;
    }

    public String getVersionNameFieldName() {
        return versionNameFieldName;
    }

    public void setVersionNameFieldName(String versionNameFieldName) {
        this.versionNameFieldName = versionNameFieldName;
    }

    public String getUpdateLogFieldName() {
        return updateLogFieldName;
    }

    public void setUpdateLogFieldName(String updateLogFieldName) {
        this.updateLogFieldName = updateLogFieldName;
    }

    public String getUpdateTitleFieldName() {
        return updateTitleFieldName;
    }

    public void setUpdateTitleFieldName(String updateTitleFieldName) {
        this.updateTitleFieldName = updateTitleFieldName;
    }

    public String getAppNameFieldName() {
        return appNameFieldName;
    }

    public void setAppNameFieldName(String appNameFieldName) {
        this.appNameFieldName = appNameFieldName;
    }

    public String getPackageNameFieldName() {
        return packageNameFieldName;
    }

    public void setPackageNameFieldName(String packageNameFieldName) {
        this.packageNameFieldName = packageNameFieldName;
    }

    public String getApkHashFieldName() {
        return apkHashFieldName;
    }

    public void setApkHashFieldName(String apkHashFieldName) {
        this.apkHashFieldName = apkHashFieldName;
    }

    public String getApkSizeFieldName() {
        return apkSizeFieldName;
    }

    public void setApkSizeFieldName(String apkSizeFieldName) {
        this.apkSizeFieldName = apkSizeFieldName;
    }


    // {
    //  "updateLog": "\r\n1、优化接口。\r\n2、优化更新提示界面。",
    //  "updateTitle":"更新标题",
    //  "appName": "UpdateDemo",
    //  "packageName": "com.wepon.apkupdate",
    //  "versionCode": 2,
    //  "versionName": "1.2",
    //  "force": false,
    //  "apkUrl": "https://wepon.oss-cn-hangzhou.aliyuncs.com/apkupdate_lib/apkupdate_version_2.apk",
    //  "apkHash": "",
    //  "apkSize": ""
    //}
    static ApkUpdateApiFieldBean newDefault() {
        ApkUpdateApiFieldBean bean = new ApkUpdateApiFieldBean();
        bean.updateTitleFieldName = "updateTitle";
        bean.updateLogFieldName = "updateLog";
        bean.appNameFieldName = "appName";
        bean.packageNameFieldName = "packageName";
        bean.versionCodeFieldName = "versionCode";
        bean.versionNameFieldName = "versionName";
        bean.isForceUpdateFieldName = "force";
        bean.apkDownloadUrlFieldName = "apkUrl";
        bean.apkHashFieldName = "apkHash";
        bean.apkSizeFieldName = "apkSize";
        return bean;
    }


}
