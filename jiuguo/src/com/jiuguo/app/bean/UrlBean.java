package com.jiuguo.app.bean;

public class UrlBean extends BaseBean {

	private static final long serialVersionUID = 3295541508998375010L;

	private String type;
	private String showName;
	private String url = null;
	private int size;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getShowName() {
		return showName;
	}
	public void setShowName(String showName) {
		this.showName = showName;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
}
