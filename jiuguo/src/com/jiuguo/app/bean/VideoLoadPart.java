package com.jiuguo.app.bean;

public class VideoLoadPart extends BaseBean {

	private static final long serialVersionUID = -7564123757741413864L;

	public static final Integer STATE_FINISHED = 1;
	public static final Integer STATE_UNFINISHED = 0;

	private Integer id;
	private Long videoId;
	private Integer part;
	private Integer state;
	private String url;
    private String suffix;
	private long totalSize = 0;
	private long downloadSize = 0;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Long getVideoId() {
		return videoId;
	}

	public void setVideoId(Long videoId) {
		this.videoId = videoId;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Integer getPart() {
		return part;
	}

	public void setPart(Integer part) {
		this.part = part;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public long getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(long totalSize) {
		this.totalSize = totalSize;
	}

	public long getDownloadSize() {
		return this.downloadSize;
	}

	public void setDownloadSize(long downloadSize) {
		this.downloadSize = downloadSize;
	}
}
