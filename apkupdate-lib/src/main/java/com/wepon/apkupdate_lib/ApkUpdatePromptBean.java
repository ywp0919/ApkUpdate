package com.wepon.apkupdate_lib;

/**
 * Author: Wepon
 * Description: 更新的弹窗的一些显示用的属性
 */
class ApkUpdatePromptBean {

    private String title;
    private String content;
    private boolean isForceUpdate;

    String getTitle() {
        return title;
    }

    void setTitle(String title) {
        this.title = title;
    }

    String getContent() {
        return content;
    }

    void setContent(String content) {
        this.content = content;
    }

    boolean getIsForceUpdate() {
        return isForceUpdate;
    }

    void setIsForceUpdate(boolean isForceUpdate) {
        this.isForceUpdate = isForceUpdate;
    }
}
