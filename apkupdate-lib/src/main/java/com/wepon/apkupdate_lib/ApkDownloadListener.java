package com.wepon.apkupdate_lib;

import android.net.Uri;

/**
 * Author: Wepon
 * Description:
 */
public interface ApkDownloadListener {
    void downloadFailed(Throwable throwable);

    void downloadSuc(Uri uri);

    void onProgressUpdate(int progress);
}
