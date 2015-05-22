package com.jiuguo.bean;

import java.util.ArrayList;

public class VideoLoad extends Video {
    private static final long serialVersionUID = 295650701650118572L;
    public final static int MP4 = 0;
    public final static int HD2 = 1;
    public final static int HD3 = 2;
    public final static int FLV = 3;
    public final static int GPH = 4;
    private String fileUrl = "";

    //������Ƭ��
    private int downLoadSize = 0;
    //�����Ƭ��
    private int downLoadPart = 0;
    private int totalSize = Integer.MAX_VALUE;
    private boolean isFinish = false;
    private boolean isStart = true;
    private int type = MP4;
    private boolean isNew = false;
    private String url;

    public VideoLoad() {

    }

    public VideoLoad(Video video) {
        this.setId(video.getId());
        this.setBookCount(video.getBookCount());
        this.setCheckId(video.getCheckId());
        this.setDescribe(video.getDescribe());
        this.setDuration(video.getDuration());
        this.setFavourCount(video.getFavourCount());
        this.setImageUrl(video.getImageUrl());
        this.setTitle(video.getTitle());
        this.setPosterName(video.getPosterName());
        this.setPostDate(video.getPostDate());
        this.setPosterId(video.getPosterId());
        this.setPlayCount(video.getPlayCount());
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public int getDownLoadSize() {
        return downLoadSize;
    }

    public void setDownLoadSize(int downLoadSize) {
        this.downLoadSize = downLoadSize;
    }

    public int getDownLoadPart() {
        return downLoadPart;
    }

    public void setDownLoadPart(int downLoadPart) {
        this.downLoadPart = downLoadPart;
    }

    public int getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }

    public boolean isFinish() {
        return isFinish;
    }

    public void setFinish(boolean isFinish) {
        this.isFinish = isFinish;
    }

    public boolean isStart() {
        return isStart;
    }

    public void setStart(boolean isStart) {
        this.isStart = isStart;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}