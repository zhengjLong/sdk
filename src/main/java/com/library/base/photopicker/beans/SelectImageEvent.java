package com.library.base.photopicker.beans;

import java.util.List;

public class SelectImageEvent {
    public final static int STATUS_OK=1;
    public final static int STATUS_FAIL=0;
    public final static int STATUS_CANCEL=-1;
    public String flag;
    public int status;
    public List<MediaBean> selectMediaBeans;

    public SelectImageEvent(String flag, int status, List<MediaBean> selectMediaBeans) {
        this.flag = flag;
        this.status = status;
        this.selectMediaBeans = selectMediaBeans;
    }

    public SelectImageEvent() {

    }
}
