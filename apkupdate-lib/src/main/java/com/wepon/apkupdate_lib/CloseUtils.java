package com.wepon.apkupdate_lib;

import java.io.Closeable;
import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Author: Wepon
 * Description:
 */
class CloseUtils {
    static void closeQuietly(HttpURLConnection conn, Closeable... closeable) {
        if (null != conn) {
            conn.disconnect();
        }
        if (null != closeable) {
            try {
                for (Closeable value : closeable) {
                    if (value != null) {
                        value.close();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
