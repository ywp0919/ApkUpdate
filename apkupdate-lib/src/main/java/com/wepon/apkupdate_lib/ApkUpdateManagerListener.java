package com.wepon.apkupdate_lib;

/**
 * Author: Wepon
 * Description:
 */
public interface ApkUpdateManagerListener {

    void onNoUpdateAvailable();

    void onUpdateAvailable(ApkUpdateBean apkUpdateBean);
}

