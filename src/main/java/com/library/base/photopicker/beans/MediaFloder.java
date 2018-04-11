package com.library.base.photopicker.beans;

import java.io.Serializable;
import java.util.List;

/**
 * 媒体文件夹实体类
 */
public class MediaFloder implements Serializable {

    /* 文件夹名 */
    private String name;
    /* 文件夹路径 */
    private String dirPath;
    /* 该文件夹下图片列表 */
    private List<MediaBean> mediaBeanList;
    /* 标识是否选中该文件夹 */
    private boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDirPath() {
        return dirPath;
    }

    public void setDirPath(String dirPath) {
        this.dirPath = dirPath;
    }

    public List<MediaBean> getMediaBeanList() {
        return mediaBeanList;
    }

    public void setMediaBeanList(List<MediaBean> mediaBeanList) {
        this.mediaBeanList = mediaBeanList;
    }
}
