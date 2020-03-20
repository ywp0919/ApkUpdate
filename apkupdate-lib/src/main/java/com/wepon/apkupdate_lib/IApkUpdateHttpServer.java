/*
 * Copyright (C) 2018 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wepon.apkupdate_lib;


import androidx.annotation.NonNull;

import java.util.Map;

/**
 * Author: Wepon
 * Description: 外部实现这个http请求服务即可利用外部的网络框架。
 */
public interface IApkUpdateHttpServer {

    void asyncGet(@NonNull String url, @NonNull Map<String, Object> params, @NonNull Callback callBack);

    void asyncPost(@NonNull String url, @NonNull Map<String, Object> params, @NonNull Callback callBack);

    interface Callback {
        void onSuccess(String result);

        void onError(Throwable throwable);
    }
}
