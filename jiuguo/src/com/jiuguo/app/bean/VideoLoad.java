package com.jiuguo.app.bean;

public class VideoLoad extends Video {

    private static final long serialVersionUID = 295650701650118572L;
    public final static int MP4 = 0;
    public final static int HD2 = 1;
    public final static int HD3 = 2;
    public final static int FLV = 3;
    public final static int GPH = 4;
    private String fileUrl = "";

    //下载总片数
    private int downLoadSize = 0;
    //已完成片数
    private int downLoadPart = 0;
    private int totalSize = Integer.MAX_VALUE;
    private int currentSize = 0;

    private boolean isFinish = false;
    private boolean isStart = true;
    private int type = MP4;
    private boolean isNew = false;
    private String url;

    public VideoLoad() {}

    public VideoLoad(Video video) {
        this.setId(video.getId());
        this.setBookCount(video.getBookCount());
        this.setCheckId(video.getCheckId());
        this.setDescribe(video.getDescribe());
        this.setDuration(video.getDuration());
        this.setFavourCount(video.getFavourCount());
        this.setImage(video.getImage());
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

    public int getCurrentSize() {
        return currentSize;
    }

    public void setCurrentSize(int currentSize) {
        this.currentSize = currentSize;
    }

    public boolean isFinish() {
        return isFinish;
    }

    public void setIsFinish(boolean isFinish) {
        this.isFinish = isFinish;
    }

    public boolean isStart() {
        return isStart;
    }

    public void setIsStart(boolean isStart) {
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

    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}