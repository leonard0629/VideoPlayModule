package com.jiuguo.app.bean;

import java.util.Date;

public class Video extends BaseBean {
	private static final String TAG = "Video";

	private static final long serialVersionUID = -7661370442864795309L;

	private Long id;
	private String title;
	private String describe;
	private int duration;
	private String postDate;
	private int posterId;
	private String posterName;
	private String posterLogo;
	private String image;
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

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
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

	public String getPosterLogo() {
		return posterLogo;
	}

	public void setPosterLogo(String posterLogo) {
		this.posterLogo = posterLogo;
	}

	public int getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}

	public boolean isBooked() {
		return isBooked;
	}

	public void setIsBooked(boolean isBooked) {
		this.isBooked = isBooked;
	}

	public boolean isCollected() {
		return isCollected;
	}

	public void setIsCollected(boolean isCollected) {
		this.isCollected = isCollected;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public int getAppleCount() {
		return appleCount;
	}

	public void setAppleCount(int appleCount) {
		this.appleCount = appleCount;
	}

	public int getRecordTime() {
		return recordTime;
	}

	public void setRecordTime(int recordTime) {
		this.recordTime = recordTime;
	}

	public int getTid() {
		return tid;
	}

	public void setTid(int tid) {
		this.tid = tid;
	}

	public Date getLastTime() {
		return lastTime;
	}

	public void setLastTime(Date lastTime) {
		this.lastTime = lastTime;
	}

	public boolean isLive() {
		return isLive;
	}

	public void setIsLive(boolean isLive) {
		this.isLive = isLive;
	}
}
