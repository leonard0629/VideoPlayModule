package com.jiuguo.app.bean;

public class SearchHistory extends BaseBean {
	private static final long serialVersionUID = -4956330268287763363L;

	private String id;
	private String content;
	private String date;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "SearchHistory [id=" + id + ", content=" + content + ", date="
				+ date + "]";
	}
}
