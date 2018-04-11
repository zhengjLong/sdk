package com.library.base.viewPageCycle;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 广告数据
 */
public class Adverts implements Parcelable {
    private int popupType;
    private String picUrl;
    private int flag;
    private int memberUnitId;
    private String startTime;
    private int id;
    private String endTime;
    private String title;
    private String jumpUrl;
    private String createDate;

    public int getPopupType() {
        return this.popupType;
    }

    public void setPopupType(int popupType) {
        this.popupType = popupType;
    }

    public String getPicUrl() {
        return this.picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public int getFlag() {
        return this.flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public int getMemberUnitId() {
        return this.memberUnitId;
    }

    public void setMemberUnitId(int memberUnitId) {
        this.memberUnitId = memberUnitId;
    }

    public String getStartTime() {
        return this.startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEndTime() {
        return this.endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getJumpUrl() {
        return this.jumpUrl;
    }

    public void setJumpUrl(String jumpUrl) {
        this.jumpUrl = jumpUrl;
    }

    public String getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.popupType);
        dest.writeString(this.picUrl);
        dest.writeInt(this.flag);
        dest.writeInt(this.memberUnitId);
        dest.writeString(this.startTime);
        dest.writeInt(this.id);
        dest.writeString(this.endTime);
        dest.writeString(this.title);
        dest.writeString(this.jumpUrl);
        dest.writeString(this.createDate);
    }

    public Adverts() {
    }

    protected Adverts(Parcel in) {
        this.popupType = in.readInt();
        this.picUrl = in.readString();
        this.flag = in.readInt();
        this.memberUnitId = in.readInt();
        this.startTime = in.readString();
        this.id = in.readInt();
        this.endTime = in.readString();
        this.title = in.readString();
        this.jumpUrl = in.readString();
        this.createDate = in.readString();
    }

    public static final Parcelable.Creator<Adverts> CREATOR = new Parcelable.Creator<Adverts>() {
        @Override
        public Adverts createFromParcel(Parcel source) {
            return new Adverts(source);
        }

        @Override
        public Adverts[] newArray(int size) {
            return new Adverts[size];
        }
    };
}
