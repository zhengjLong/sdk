package com.library.base.photopicker.beans;

/**
 * 接收相册或者照相机裁剪来的图片
 */
public class SelectPhotoEvent {
    public final static int STATUS_OK=1;
    public final static int STATUS_FAIL=0;
    public final static int STATUS_CANCEL=-1;
    public String flag;
    public int status;
    public String imagePath;

    public SelectPhotoEvent(String flag, int status, String imagePath) {
        this.flag = flag;
        this.status = status;
        this.imagePath = imagePath;
    }

    public SelectPhotoEvent() {

    }
}
