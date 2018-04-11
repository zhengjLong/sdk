package com.library.base.photopicker.beans;

import java.io.Serializable;

/**
 * 媒体实体
 */
public class MediaBean implements Serializable {

    private int id;
    private String floderName;
    private String name; //图片名字
    private String realPath;  //真实路径
    private String size;//大小
    private long date;//日期
    private boolean isSelected;//是否是选中
    private boolean isPhoto;//是图片



    public MediaBean(int id, boolean isPhoto, boolean isSelected, long date, String size,String floderName, String realPath, String name) {
        this.id = id;
        this.isPhoto = isPhoto;
        this.isSelected = isSelected;
        this.date = date;
        this.size = size;
        this.realPath = realPath;
        this.floderName=floderName;
        this.name = name;
    }

    public MediaBean() {

    }
    public MediaBean(String realPath) {
        this.realPath=realPath;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRealPath() {
        return realPath;
    }

    public void setRealPath(String realPath) {
        this.realPath = realPath;
    }

    public boolean isPhoto() {
        return isPhoto;
    }

    public void setIsPhoto(boolean isPhoto) {
        this.isPhoto = isPhoto;
    }
    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

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

    public String getFloderName() {
        return floderName;
    }

    public void setFloderName(String floderName) {
        this.floderName = floderName;
    }

    @Override
    public String toString() {
        return "MediaBean{" +
                "id=" + id +
                ", floderName='" + floderName + '\'' +
                ", name='" + name + '\'' +
                ", realPath='" + realPath + '\'' +
                ", size='" + size + '\'' +
                ", date=" + date +
                ", isSelected=" + isSelected +
                ", isPhoto=" + isPhoto +
                '}';
    }
}
