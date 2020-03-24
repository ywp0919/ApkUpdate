package com.wepon.apkupdate_lib;

import android.util.Log;

/**
 * Author: Wepon
 * Description:
 */
class ApkUpdateLogUtils {

    static boolean apkUpdateLogOpen = false;


    static void e(String tag, String msg) {
        if (apkUpdateLogOpen) {
            Log.e(tag, msg);
        }
    }

    static void e(String msg) {
        if (apkUpdateLogOpen) {
            Log.e(ApkUpdate.TAG, msg);
        }
    }

    static void d(String msg) {
        if (apkUpdateLogOpen) {
            Log.d(ApkUpdate.TAG, msg);
        }
    }
}
