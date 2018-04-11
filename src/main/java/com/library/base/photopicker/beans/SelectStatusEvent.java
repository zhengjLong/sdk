package com.library.base.photopicker.beans;

public class SelectStatusEvent {
    public int imageId;
    public boolean isSelect;

    public SelectStatusEvent(int imageId, boolean isSelect) {
        this.imageId = imageId;
        this.isSelect = isSelect;
    }

    @Override
    public String toString() {
        return "SelectStatusEvent{" +
                "imageId=" + imageId +
                ", isSelect=" + isSelect +
                '}';
    }
}
