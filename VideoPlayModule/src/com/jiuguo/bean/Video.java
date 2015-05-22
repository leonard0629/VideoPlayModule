package com.jiuguo.bean;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.Date;

public class Video extends BaseBean {
	private static final String TAG = "Video";

	private static final long serialVersionUID = -7661370442864795309L;

	public static final String ID = "id";
	public static final String TITLE = "title";
	public static final String DESCRIBE = "describe";
	public static final String DURATION = "duration";
	public static final String POSTDATE = "postdate";
	public static final String POSTERID = "posterid";
	public static final String POSTERLOGO = "posterlogo";
	public static final String POSTERNAME = "postername";
	public static final String IMAGEURL = "image";
	public static final String PLAYCOUNT = "pcount";
	public static final String FAVOURCOUNT = "fcount";
	public static final String BOOKCOUNT = "bcount";
	public static final String COMMENTCOUNT = "ccount";
	public static final String APPLECOUNT = "acount";
	public static final String CHECKID = "checked_id";
	public static final String ISBOOKED = "user_book";
	public static final String ISCOLLECTED = "user_collect";
	public static final String RECORDTIME = "recordtime";
	public static final Integer MAX_VALUE = 200000;
	public static final String TID = "tid";
	private Long id;
	private String title;
	private String describe;
	private int duration;
	private String postDate;
	private int posterId;
	private String posterName;
	private String posterlogo;
	private String imageUrl;
	private int playCount;
	private int favourCount;
	private int bookCount;
	private int commentCount;
	private int checkId;
	private boolean isBooked;
	private boolean isCollected;
	private String comment;
	private int appleCount;
	private int recordTime = 0;
	private int tid = 0;
	private Date lastTime;
	private boolean isLive = false;
	private int online = 0;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getPostDate() {
		return postDate;
	}

	public void setPostDate(String postDate) {
		this.postDate = postDate;
	}

	public int getPosterId() {
		return posterId;
	}

	public void setPosterId(int posterId) {
		this.posterId = posterId;
	}

	public String getPosterName() {
		return posterName;
	}

	public void setPosterName(String posterName) {
		this.posterName = posterName;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public int getPlayCount() {
		return playCount;
	}

	public void setPlayCount(int playCount) {
		this.playCount = playCount;
	}

	public int getFavourCount() {
		return favourCount;
	}

	public void setFavourCount(int favourCount) {
		this.favourCount = favourCount;
	}

	public int getBookCount() {
		return bookCount;
	}

	public void setBookCount(int bookCount) {
		this.bookCount = bookCount;
	}

	public int getCheckId() {
		return checkId;
	}

	public void setCheckId(int checkId) {
		this.checkId = checkId;
	}

	public String getPosterlogo() {
		return posterlogo;
	}

	public void setPosterlogo(String posterlogo) {
		this.posterlogo = posterlogo;
	}

	public boolean isBooked() {
		return isBooked;
	}

	public void setBooked(boolean isBooked) {
		this.isBooked = isBooked;
	}

	public boolean isCollected() {
		return isCollected;
	}

	public void setCollected(boolean isCollected) {
		this.isCollected = isCollected;
	}

	public boolean isLive() {
		return isLive;
	}

	public void setIsLive(boolean isLive) {
		this.isLive = isLive;
	}

	public Date getLastTime() {
		return lastTime;
	}

	public void setLastTime(Date lastTime) {
		this.lastTime = lastTime;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public int getTid() {
		return tid;
	}

	public void setTid(int tid) {
		this.tid = tid;
	}

	public int getAppleCount() {
		return appleCount;
	}

	public void setAppleCount(int i) {
		this.appleCount = i;
	}

	public int getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}

	public int getRecordTime() {
		return recordTime;
	}

	public void setRecordTime(int recordTime) {
		this.recordTime = recordTime;
	}

	public int getOnline() {
		return online;
	}

	public void setOnline(int online) {
		this.online = online;
	}
}
