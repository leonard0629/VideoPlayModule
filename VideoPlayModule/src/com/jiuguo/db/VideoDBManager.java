//package com.jiuguo.db;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import com.jiuguo.bean.VideoLoad;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.List;
//
//public class VideoDBManager {
//	private static final String TAG = "VideoManager";
//
//	private VideoDBHelper helper;
//	private SQLiteDatabase database;
//
//	public VideoDBManager(Context context) {
//		try {
//			helper = new VideoDBHelper(context);
//			database = helper.getWritableDatabase();
//		} catch (Exception e) {
//			e.printStackTrace();
//			try {
//				database = helper.getReadableDatabase();
//			} catch (Exception e2) {
//				// TODO: handle exception
//				e2.printStackTrace();
//			}
//
//		}
//	}
//
//	/**
//	 * add load video to database
//	 *
//	 * @param videoLoads
//	 */
//	public void addVideos(List<VideoLoad> videoLoads) throws SQLException {
//		database.beginTransaction();
//		try {
//			for (VideoLoad videoLoad : videoLoads) {
//                database.execSQL("insert into video values(null,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
//                        new Object[]{videoLoad.getId(), videoLoad.getTitle(), videoLoad.getDescribe(),
//                                videoLoad.getDuration(), videoLoad.getPostDate(), videoLoad.getPosterId(),
//                                videoLoad.getPosterName(), videoLoad.getImageUrl(), videoLoad.getPlayCount(),
//                                videoLoad.getFavourCount(), videoLoad.getBookCount(), videoLoad.getCheckId(),
//                                videoLoad.getFileUrl(), videoLoad.getDownLoadSize(), videoLoad.getTotalSize(),
//                                videoLoad.isFinish(), videoLoad.isStart(), videoLoad.getDownLoadPart(),
//                                videoLoad.getType(), videoLoad.isNew()});
//            }
//            database.setTransactionSuccessful();
//		} catch (Exception e) {
//			// TODO: handle exception
//			throw new SQLException(e,
//					"Error: Get Exception when insert video into database.");
//		} finally {
//			database.endTransaction();
//		}
//	}
//
//    /**
//     *
//     * @param videoload
//     * @throws SQLException
//     */
//	public void updateVideo(VideoLoad videoload) throws SQLException {
//		try {
//			ContentValues values = new ContentValues();
//			values.put("id", videoload.getId());
//			values.put("title", videoload.getTitle());
//			values.put("describe", videoload.getDescribe());
//			values.put("duration", videoload.getDuration());
//			values.put("postDate", videoload.getPostDate());
//			values.put("posterId", videoload.getPosterId());
//			values.put("posterName", videoload.getPosterName());
//			values.put("imageUrl", videoload.getImageUrl());
//			values.put("playCount", videoload.getPlayCount());
//			values.put("favourCount", videoload.getFavourCount());
//			values.put(VIDEO_BOOKCOUNT, videoload.getBookCount());
//			values.put(VIDEO_CHECKID, videoload.getCheckId());
////			values.put(VIDEO_POSTERLOGO, videoload.getPosterlogo());
//			values.put(VIDEO_FILEURL, videoload.getFileUrl());
//			values.put(VIDEO_DOWNLOADSIZE, videoload.getDownLoadSize());
//			values.put(VIDEO_DOWNLOADPART, videoload.getDownLoadPart());
//			values.put(VIDEO_TOTALSIZE, videoload.getTotalSize());
//			values.put(VIDEO_ISFINISH, videoload.isFinish());
//			values.put(VIDEO_ISSTART, videoload.isStart());
//			values.put(VIDEO_TYPE, videoload.getType());
//			values.put(VIDEO_ISNEW, videoload.isNew());
//			database.update(VIDEO_TABLE, values, "videoid=?",
//					new String[] { videoload.getId() + "", });
///*			List<M3U8> m3u8s = videoload.getM3u8s();
//			for (M3U8 m3u8 : m3u8s)
//				updateM3u8(m3u8);*/
//
//		} catch (Exception e) {
//			// TODO: handle exception
//			throw new SQLException(e,
//					"Error: Get Exception when update video database.");
//		}
//
//	}
//
//	/**
//	 *
//	 * @param videoLoad
//	 */
//	public void deleteVideo(VideoLoad videoLoad) throws SQLException {
//		try {
//			database.delete(VIDEO_TABLE, "videoid=?",
//					new String[] { videoLoad.getId() + "", });
//		} catch (Exception e) {
//			// e.printStackTrace();
//			throw new SQLException(e,
//					"Error: Get Exception when delete video database.");
//		}
//	}
//
//	/**
//	 *
//     * @param videoLoads
//     */
//	public void deleteVideos(List<VideoLoad> videoLoads) throws SQLException {
//		try {
//			if(videoLoads != null && videoLoads.size() > 0) {
//                for(VideoLoad videoLoad : videoLoads) {
//                    deleteVideo(videoLoad);
//                }
//            }
//		} catch (Exception e) {
//			// e.printStackTrace();
//			throw new SQLException(e,
//					"Error: Get Exception when delete video database.");
//		}
//	}
//
//	/**
//	 *
//	 * @return
//	 * @throws SQLException
//	 */
//	public List<VideoLoad> queryVideoLoadForList() throws SQLException {
//		return queryVideoLoadForList("select * from video");
//	}
//
//	public List<VideoLoad> queryVideoLoadForList(String query)
//			throws SQLException {
//		return queryVideoLoadForList(query, null);
//	}
//
//	/**
//	 *
//	 * @param query
//	 * @return
//	 * @throws SQLException
//	 */
//	public List<VideoLoad> queryVideoLoadForList(String query,
//			String[] selectionArgs) throws SQLException {
//		List<VideoLoad> videoLoads = null;
//		Cursor cursor = null;
//		try {
//			cursor = queryVideoLoadForCursor(query, selectionArgs);
//			videoLoads = new ArrayList<VideoLoad>();
//			if (cursor.moveToFirst()) {
//				do {
//					VideoLoad videoLoad = new VideoLoad();
//					videoLoad.setId(cursor.getLong(cursor
//							.getColumnIndex(VIDEO_ID)));
//					videoLoad.setTitle(cursor.getString(cursor
//							.getColumnIndex(VIDEO_TITLE)));
//					videoLoad.setDescribe(cursor.getString(cursor
//							.getColumnIndex(VIDEO_DESCRIBE)));
//					videoLoad.setDuration(cursor.getInt(cursor
//							.getColumnIndex(VIDEO_DURATION)));
//					videoLoad.setPostDate(cursor.getString(cursor
//							.getColumnIndex(VIDEO_POSTDATE)));
//					videoLoad.setPosterId(cursor.getInt(cursor
//							.getColumnIndex(VIDEO_POSTERID)));
//					videoLoad.setPosterName(cursor.getString(cursor
//							.getColumnIndex(VIDEO_POSTERNAME)));
//					videoLoad.setImageUrl(cursor.getString(cursor
//							.getColumnIndex(VIDEO_IMAGEURL)));
//					videoLoad.setPlayCount(cursor.getInt(cursor
//							.getColumnIndex(VIDEO_PLAYCOUNT)));
//					videoLoad.setFavourCount(cursor.getInt(cursor
//							.getColumnIndex(VIDEO_FAVOURCOUNT)));
//					videoLoad.setBookCount(cursor.getInt(cursor
//							.getColumnIndex(VIDEO_BOOKCOUNT)));
//					videoLoad.setCheckId(cursor.getInt(cursor
//							.getColumnIndex(VIDEO_CHECKID)));
//					videoLoad.setFileUrl(cursor.getString(cursor
//							.getColumnIndex(VIDEO_FILEURL)));
//					videoLoad.setDownLoadSize(cursor.getInt(cursor
//							.getColumnIndex(VIDEO_DOWNLOADSIZE)));
//					videoLoad.setDownLoadPart(cursor.getInt(cursor
//							.getColumnIndex(VIDEO_DOWNLOADPART)));
//					videoLoad.setTotalSize(cursor.getInt(cursor
//							.getColumnIndex(VIDEO_TOTALSIZE)));
//					/*videoLoad.setPosterlogo(cursor.getString(cursor
//							.getColumnIndex(VIDEO_POSTERLOGO)));*/
//					videoLoad.setFinish((cursor.getInt(cursor
//							.getColumnIndex(VIDEO_ISFINISH))) > 0);
//					videoLoad
//							.setStart((cursor.getInt(cursor
//									.getColumnIndex(VIDEO_ISSTART))) > 0);
//					videoLoad.setType((cursor.getInt(cursor.getColumnIndex(VIDEO_TYPE))));
//					videoLoad.setNew((cursor.getInt(cursor
//									.getColumnIndex(VIDEO_ISNEW))) > 0);
//					/*					String m3u8Query = "select * from m3u8 where "
//							+ M3U8_VIDEOID + "=" + videoLoad.getId();
//					videoLoad
//							.setM3u8s((ArrayList<M3U8>) queryM3u8ForList(m3u8Query));*/
//					videoLoads.add(videoLoad);
//				} while (cursor.moveToNext());
//			}
//		} catch (Exception e) {
//			// TODO: handle exception
//			throw new SQLException(e,
//					"Error: Get Exception when query video from database.");
//		} finally {
//			if (cursor != null) {
//				cursor.close();
//			}
//		}
//
//		return videoLoads;
//	}
//
//	/**
//	 *
//	 * @return
//	 * @throws SQLException
//	 */
//	public Cursor queryVideoLoadForCursor() throws SQLException {
//		return queryVideoLoadForCursor("select * from video");
//	}
//
//	public Cursor queryVideoLoadForCursor(String query) throws SQLException {
//		return queryVideoLoadForCursor(query, null);
//	}
//
//	/**
//	 *
//	 * @param query
//	 * @return
//	 * @throws SQLException
//	 */
//	public Cursor queryVideoLoadForCursor(String query, String[] selectionArgs)
//			throws SQLException {
//		Cursor cursor = null;
//		try {
//			cursor = database.rawQuery(query, selectionArgs);
//		} catch (Exception e) {
//			// TODO: handle exception
//			throw new SQLException(e,
//					"Error: Get Exception when query video from database.");
//		}
//		return cursor;
//	}
//
//	/**
//	 *
//	 * @param m3u8s
//	 * @throws SQLException
//	 */
//	public void addM3u8(List<M3U8> m3u8s) throws SQLException {
//		database.beginTransaction();
//		try {
//			for (M3U8 m3u8 : m3u8s) {
//				database.execSQL(
//						"insert into m3u8 values(null,?,?,?,?,?,?,?)",
//						new Object[] { m3u8.getId(), m3u8.getDuration(),
//								m3u8.getUrl(), m3u8.getName(),
//								m3u8.getDownLoadSize(), m3u8.getTotalSize(),
//								m3u8.getVideoId(), });
//			}
//			database.setTransactionSuccessful();
//		} catch (Exception e) {
//			// TODO: handle exception
//			throw new SQLException(e,
//					"Error: Get Exception when insert m3u8 into database.");
//		} finally {
//			database.endTransaction();
//		}
//	}
//
//	/**
//	 *
//	 * @param m3u8
//	 * @throws SQLException
//	 */
//	public void updateM3u8(M3U8 m3u8) throws SQLException {
//		try {
//			ContentValues values = new ContentValues();
//			values.put(M3U8_ID, m3u8.getId());
//			values.put(M3U8_DURATION, m3u8.getDuration());
//			values.put(M3U8_URL, m3u8.getUrl());
//			values.put(M3U8_NAME, m3u8.getName());
//			values.put(M3U8_DOWNLOADSIZE, m3u8.getDownLoadSize());
//			values.put(M3U8_TOTALSIZE, m3u8.getTotalSize());
//			values.put(M3U8_VIDEOID, m3u8.getVideoId());
//			database.update(M3U8_TABLE, values, "name=?",
//					new String[]{m3u8.getName(),});
//		} catch (Exception e) {
//			// TODO: handle exception
//			throw new SQLException(e,
//					"Error: Get Exception when update m3u8 database.");
//		}
//
//	}
//
//	/**
//	 *
//	 * @param m3u8
//	 * @throws SQLException
//	 */
//	public void deleteM3u8(M3U8 m3u8) throws SQLException {
//		try {
//			database.delete(M3U8_TABLE, "name=?",
//					new String[]{m3u8.getName(),});
//		} catch (Exception e) {
//			// e.printStackTrace();
//			throw new SQLException(e,
//					"Error: Get Exception when delete m3u8 database.");
//		}
//	}
//
//	/**
//	 *
//	 * @return
//	 * @throws SQLException
//	 */
//	public List<M3U8> queryM3u8ForList() throws SQLException {
//		return queryM3u8ForList("select * from m3u8");
//	}
//
//	public List<M3U8> queryM3u8ForList(String query) throws SQLException {
//		return queryM3u8ForList(query, null);
//	}
//
//	/**
//	 *
//	 * @param query
//	 * @return
//	 * @throws SQLException
//	 */
//	public List<M3U8> queryM3u8ForList(String query, String[] selectionArgs)
//			throws SQLException {
//		List<M3U8> m3u8s = null;
//		Cursor cursor = null;
//		try {
//			cursor = queryM3u8ForCursor(query, selectionArgs);
//			m3u8s = new ArrayList<M3U8>();
//			if (cursor.moveToFirst()) {
//				do {
//					M3U8 m3u8 = new M3U8();
//					m3u8.setId(cursor.getInt(cursor.getColumnIndex(M3U8_ID)));
//					m3u8.setDownLoadSize(cursor.getInt(cursor
//							.getColumnIndex(M3U8_DOWNLOADSIZE)));
//					m3u8.setDuration(cursor.getInt(cursor
//							.getColumnIndex(M3U8_DURATION)));
//					m3u8.setTotalSize(cursor.getInt(cursor
//							.getColumnIndex(M3U8_TOTALSIZE)));
//					m3u8.setName(cursor.getString(cursor
//							.getColumnIndex(M3U8_NAME)));
//					m3u8.setUrl(cursor.getString(cursor
//							.getColumnIndex(M3U8_URL)));
//					m3u8.setVideoId(cursor.getInt(cursor
//							.getColumnIndex(M3U8_VIDEOID)));
//					m3u8s.add(m3u8);
//				} while (cursor.moveToNext());
//			}
//		} catch (Exception e) {
//			// TODO: handle exception
//			throw new SQLException(e,
//					"Error: Get Exception when query m3u8 from database.");
//		} finally {
//			if (cursor != null) {
//				cursor.close();
//			}
//		}
//
//		return m3u8s;
//	}
//
//	/**
//	 *
//	 * @return
//	 * @throws SQLException
//	 */
//	public Cursor queryM3u8ForCursor() throws SQLException {
//		return queryM3u8ForCursor("select * from m3u8");
//	}
//
//	public Cursor queryM3u8ForCursor(String query) throws SQLException {
//		return queryM3u8ForCursor(query, null);
//	}
//
//	/**
//	 *
//	 * @param query
//	 * @return
//	 * @throws SQLException
//	 */
//	public Cursor queryM3u8ForCursor(String query, String[] selectionArgs)
//			throws SQLException {
//		Cursor cursor = null;
//		try {
//			cursor = database.rawQuery(query, selectionArgs);
//		} catch (Exception e) {
//			// TODO: handle exception
//			throw new SQLException(e,
//					"Error: Get Exception when query m3u8 from database.");
//		}
//		return cursor;
//	}
//
//	/**
//	 * @param video
//	 * @throws SQLException
//	 */
//	public void addRecordVideo(Video video) throws SQLException {
//		database.beginTransaction();
//		Cursor c = null;
//		try {
//			c = database.rawQuery("SELECT * FROM " + RECORD_TABLE + " WHERE checkid=? AND islive=?", new String[] { "" + video.getCheckId(), video.isLive() ? "1" : "0"});
//			if (!c.moveToNext()) {
//				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//				String lastTime = video.getLastTime() == null ? "" : sdf.format(video.getLastTime());
//				database.execSQL("insert into " + RECORD_TABLE + " values (null,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
//						new Object[] {video.getId(), video.getTitle(), video.getDescribe(), video.getDuration(),
//								video.getPostDate(), video.getPosterId(), video.getPosterName(),
//								video.getImageUrl(), video.getPlayCount(), video.getFavourCount(),
//								video.getBookCount(), video.getCheckId(), video.getPosterlogo(),
//								video.getRecordTime(), lastTime, video.isLive()});
//			}
//			database.setTransactionSuccessful();
//		} catch (Exception e) {
//			// TODO: handle exception
//			throw new SQLException(e,
//					"Error: Get Exception when insert video into database.");
//		} finally {
//			if(c!=null){
//				c.close();
//			}
//			database.endTransaction();
//		}
//	}
//
//	public List<Video> loadRecordVideos() throws SQLException {
//		Cursor cursor = null;
//		List<Video> videos = null;
//		try{
//			videos = new ArrayList<Video>();
//			cursor = database.rawQuery("SELECT * FROM " + RECORD_TABLE, null);
//			while (cursor.moveToNext()) {
//				Video video = new Video();
//				video.setTitle(cursor.getString(cursor
//						.getColumnIndex(VIDEO_TITLE)));
//				video.setDescribe(cursor.getString(cursor
//						.getColumnIndex(VIDEO_DESCRIBE)));
//				video.setDuration(cursor.getInt(cursor
//						.getColumnIndex(VIDEO_DURATION)));
//				video.setPostDate(cursor.getString(cursor
//						.getColumnIndex(VIDEO_POSTDATE)));
//				video.setPosterId(cursor.getInt(cursor
//						.getColumnIndex(VIDEO_POSTERID)));
//				video.setPosterName(cursor.getString(cursor
//						.getColumnIndex(VIDEO_POSTERNAME)));
//				video.setImageUrl(cursor.getString(cursor
//						.getColumnIndex(VIDEO_IMAGEURL)));
//				video.setPlayCount(cursor.getInt(cursor
//						.getColumnIndex(VIDEO_PLAYCOUNT)));
//				video.setFavourCount(cursor.getInt(cursor
//						.getColumnIndex(VIDEO_FAVOURCOUNT)));
//				video.setBookCount(cursor.getInt(cursor
//						.getColumnIndex(VIDEO_BOOKCOUNT)));
//				video.setCheckId(cursor.getInt(cursor
//						.getColumnIndex(VIDEO_CHECKID)));
//				video.setPosterlogo(cursor.getString(cursor
//						.getColumnIndex(VIDEO_POSTERLOGO)));
//				video.setRecordTime(cursor.getInt(cursor
//						.getColumnIndex(VIDEO_RECORDTIME)));
//				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//				String lastTime = cursor.getString(cursor.getColumnIndex("lasttime"));
//				if(lastTime != null && !"".equals(lastTime)) {
//					video.setLastTime(sdf.parse(lastTime));
//				}
//				video.setIsLive(cursor.getInt(cursor
//						.getColumnIndex("islive")) == 1 ? true : false);
//				videos.add(video);
//			}
//		} catch (Exception e) {
//			throw new SQLException(e,
//					"Error: Get Exception when getRecordVideo.");
//		} finally {
//			if (cursor != null) {
//				cursor.close();
//			}
//		}
//		return videos;
//	}
//
//	public Video getRecordVideo(long checkid, boolean isLive) throws SQLException{
//		Cursor cursor = null;
//		try{
//			cursor = database.rawQuery("SELECT * FROM " + RECORD_TABLE + " WHERE checkid=? AND islive=?", new String[] { "" + checkid, isLive ? "1" : "0"});
//			if (cursor.moveToNext()) {
//				Video video = new Video();
//				video.setTitle(cursor.getString(cursor
//						.getColumnIndex(VIDEO_TITLE)));
//				video.setDescribe(cursor.getString(cursor
//						.getColumnIndex(VIDEO_DESCRIBE)));
//				video.setDuration(cursor.getInt(cursor
//						.getColumnIndex(VIDEO_DURATION)));
//				video.setPostDate(cursor.getString(cursor
//						.getColumnIndex(VIDEO_POSTDATE)));
//				video.setPosterId(cursor.getInt(cursor
//						.getColumnIndex(VIDEO_POSTERID)));
//				video.setPosterName(cursor.getString(cursor
//						.getColumnIndex(VIDEO_POSTERNAME)));
//				video.setImageUrl(cursor.getString(cursor
//						.getColumnIndex(VIDEO_IMAGEURL)));
//				video.setPlayCount(cursor.getInt(cursor
//						.getColumnIndex(VIDEO_PLAYCOUNT)));
//				video.setFavourCount(cursor.getInt(cursor
//						.getColumnIndex(VIDEO_FAVOURCOUNT)));
//				video.setBookCount(cursor.getInt(cursor
//						.getColumnIndex(VIDEO_BOOKCOUNT)));
//				video.setCheckId(cursor.getInt(cursor
//						.getColumnIndex(VIDEO_CHECKID)));
//				video.setPosterlogo(cursor.getString(cursor
//						.getColumnIndex(VIDEO_POSTERLOGO)));
//				video.setRecordTime(cursor.getInt(cursor
//						.getColumnIndex(VIDEO_RECORDTIME)));
//				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//				String lastTime = cursor.getString(cursor.getColumnIndex("lasttime"));
//				if(lastTime != null && !"".equals(lastTime)) {
//					video.setLastTime(sdf.parse(lastTime));
//				}
//				video.setIsLive(isLive);
//				return video;
//			}
//		} catch (Exception e) {
//			throw new SQLException(e,
//					"Error: Get Exception when getRecordVideo.");
//		} finally {
//			if (cursor != null) {
//				cursor.close();
//			}
//		}
//		return null;
//	}
//
//	/**
//	 *
//	 * @param video
//	 * @throws SQLException
//	 */
//	public void updateRecordVideo(Video video) throws SQLException {
//		try {
//			ContentValues values = new ContentValues();
//			values.put(VIDEO_TITLE, video.getTitle());
//			values.put(VIDEO_DESCRIBE, video.getDescribe());
//			values.put(VIDEO_DURATION, video.getDuration());
//			values.put(VIDEO_POSTDATE, video.getPostDate());
//			values.put(VIDEO_POSTERID, video.getPosterId());
//			values.put(VIDEO_POSTERNAME, video.getPosterName());
//			values.put(VIDEO_IMAGEURL, video.getImageUrl());
//			values.put(VIDEO_PLAYCOUNT, video.getPlayCount());
//			values.put(VIDEO_FAVOURCOUNT, video.getFavourCount());
//			values.put(VIDEO_BOOKCOUNT, video.getBookCount());
//			values.put(VIDEO_CHECKID, video.getCheckId());
//			values.put(VIDEO_POSTERLOGO, video.getPosterlogo());
//			values.put(VIDEO_RECORDTIME, video.getRecordTime());
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			String lastTime = video == null ? "" : sdf.format(video.getLastTime());
//			values.put("lasttime", lastTime);
//			values.put("islive", video.isLive());
//			database.update(RECORD_TABLE, values, "checkid=? AND islive=?",
//					new String[] { "" + video.getCheckId(), video.isLive() ? "1" : "0"});
//		} catch (Exception e) {
//			// TODO: handle exception
//			throw new SQLException(e,
//					"Error: Get Exception when update video to database.");
//		}
//	}
//
//	/**
//	 *
//	 * @param video
//	 * @throws SQLException
//	 */
//	public void deleteRecordVideo(Video video) throws SQLException {
//		try {
//			database.delete(RECORD_TABLE, "checkid=? AND islive=?", new String[] { "" + video.getCheckId(), video.isLive() ? "1" : "0"});
//		} catch (Exception e) {
//			// e.printStackTrace();
//			throw new SQLException(e,
//					"Error: Get Exception when delete video from database.");
//		}
//	}
//
//	/**
//	 * delete all videos
//	 *
//	 * @throws SQLException
//	 */
//	public void deleteAllRecordVideo() throws SQLException {
//		try {
//			database.delete(RECORD_TABLE, null, null);
//		} catch (Exception e) {
//			// e.printStackTrace();
//			throw new SQLException(e,
//					"Error: Get Exception when delete video from database.");
//		}
//	}
//
//	/**
//	 *
//	 * @param query
//	 * @return
//	 * @throws SQLException
//	 */
//	public List<Video> queryVideoForList(String query) throws SQLException {
//		return queryVideoForList(query, null);
//	}
//
//	/**
//	 *
//	 * @param query
//	 * @return
//	 * @throws SQLException
//	 */
//	public List<Video> queryVideoForList(String query, String[] selectionArgs)
//			throws SQLException {
//		List<Video> videos = null;
//		Cursor cursor = null;
//		try {
//			cursor = queryVideoForCursor(query, selectionArgs);
//			videos = new ArrayList<Video>();
//			if (cursor.moveToFirst()) {
//				do {
//					Video video = new Video();
//					video.setId(cursor.getLong(cursor.getColumnIndex(VIDEO_ID)));
//					video.setTitle(cursor.getString(cursor
//							.getColumnIndex(VIDEO_TITLE)));
//					video.setDescribe(cursor.getString(cursor
//							.getColumnIndex(VIDEO_DESCRIBE)));
//					video.setDuration(cursor.getInt(cursor
//							.getColumnIndex(VIDEO_DURATION)));
//					video.setPostDate(cursor.getString(cursor
//							.getColumnIndex(VIDEO_POSTDATE)));
//					video.setPosterId(cursor.getInt(cursor
//							.getColumnIndex(VIDEO_POSTERID)));
//					video.setPosterName(cursor.getString(cursor
//							.getColumnIndex(VIDEO_POSTERNAME)));
//					video.setImageUrl(cursor.getString(cursor
//							.getColumnIndex(VIDEO_IMAGEURL)));
//					video.setPlayCount(cursor.getInt(cursor
//							.getColumnIndex(VIDEO_PLAYCOUNT)));
//					video.setFavourCount(cursor.getInt(cursor
//							.getColumnIndex(VIDEO_FAVOURCOUNT)));
//					video.setBookCount(cursor.getInt(cursor
//							.getColumnIndex(VIDEO_BOOKCOUNT)));
//					video.setCheckId(cursor.getInt(cursor
//							.getColumnIndex(VIDEO_CHECKID)));
//					video.setPosterlogo(cursor.getString(cursor
//							.getColumnIndex(VIDEO_POSTERLOGO)));
//					video.setRecordTime(cursor.getInt(cursor
//							.getColumnIndex(VIDEO_RECORDTIME)));
//					videos.add(video);
//				} while (cursor.moveToNext());
//			}
//		} catch (Exception e) {
//			// TODO: handle exception
//			throw new SQLException(e,
//					"Error: Get Exception when query video from database.");
//		} finally {
//			if (cursor != null) {
//				cursor.close();
//			}
//		}
//
//		return videos;
//	}
//
//	/**
//	 *
//	 * @param query
//	 * @return
//	 * @throws SQLException
//	 */
//	public Cursor queryVideoForCursor(String query) throws SQLException {
//		return queryVideoForCursor(query, null);
//	}
//
//	/**
//	 *
//	 * @param query
//	 * @return
//	 * @throws SQLException
//	 */
//	public Cursor queryVideoForCursor(String query, String[] selectionArgs)
//			throws SQLException {
//		Cursor cursor = null;
//		try {
//			cursor = database.rawQuery(query, selectionArgs);
//		} catch (Exception e) {
//			// TODO: handle exception
//			throw new SQLException(e,
//					"Error: Get Exception when query video from database.");
//		}
//		return cursor;
//	}
//
//	public void addSearchHisitory(SearchHistory history) throws SQLException {
//		deleteSearchHistory(history);
//		database.beginTransaction();
//		try {
//			database.execSQL("insert into searchhistory values(null,?,?,?)",
//					new String[] { history.getId() + "", history.getContent(),
//							history.getDate(), });
//			database.setTransactionSuccessful();
//		} catch (Exception e) {
//			// TODO: handle exception
//			throw new SQLException(e,
//					"Error: Get Exception when insert histories into database.");
//		} finally {
//			database.endTransaction();
//		}
//	}
//
//	public void deleteSearchHistory() throws SQLException {
//		try {
//			List<SearchHistory> searchHistories = querySearchHistoryForList();
//			for (SearchHistory searchHistory : searchHistories) {
//				deleteSearchHistory(searchHistory);
//			}
//		} catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//			throw new SQLException(e,
//					"Error: Get Exception when delete histories into database.");
//		}
//
//	}
//
//	// delete the same content search;
//	public void deleteSearchHistory(SearchHistory searchHistory)
//			throws SQLException {
//		try {
//			database.delete(SEARCHHISTORY_TABLE, "content=?",
//					new String[] { searchHistory.getContent(), });
//		} catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//			throw new SQLException(e,
//					"Error: Get Exception when delete histories into database.");
//		}
//	}
//
//
//
//	public List<SearchHistory> querySearchHistoryForList() throws SQLException {
//		List<SearchHistory> searchHistories = null;
//		Cursor cursor = null;
//		try {
//			cursor = querySearchHistoryForCursor();
//			// from back to first
//			if (cursor.moveToLast()) {
//				searchHistories = new ArrayList<SearchHistory>();
//				do {
//					SearchHistory searchHistory = new SearchHistory();
//					searchHistory.setId(cursor.getString(cursor
//							.getColumnIndex(SEARCHHISTORY_ID)));
//					searchHistory.setContent(cursor.getString(cursor
//							.getColumnIndex(SEARCHHISTORY_CONTENT)));
//					searchHistory.setDate(cursor.getString(cursor
//							.getColumnIndex(SEARCHHISTORY_DATE)));
//					searchHistories.add(searchHistory);
//				} while (cursor.moveToPrevious());
//			}
//		} catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//			throw new SQLException(e,
//					"Error:get Exception when query histories from database");
//		} finally {
//			if (cursor != null) {
//				cursor.close();
//			}
//		}
//		return searchHistories;
//	}
//
//	public Cursor querySearchHistoryForCursor() throws SQLException {
//		Cursor cursor = null;
//		try {
//			cursor = database.rawQuery("select * from " + SEARCHHISTORY_TABLE,
//					null);
//		} catch (Exception e) {
//			// TODO: handle exception
//			throw new SQLException(e,
//					"Error: Get Exception when query video from database.");
//		}
//		return cursor;
//	}
//
//	/**
//	 *
//	 * @throws SQLException
//	 */
//	public void close() throws SQLException {
//		try {
//			database.close();
//		} catch (Exception e) {
//			// TODO: handle exception
//			throw new SQLException(e, "Error:get Exception when colse database");
//		}
//	}
//
//	/**
//	 *
//	 * @author Lee
//	 *
//	 */
//	public static class SQLException extends Exception {
//		private static final long serialVersionUID = -4478947130132069588L;
//
//		private String mCauseMessage = "";
//
//		public SQLException() {
//			super();
//		}
//
//		public SQLException(String cause) {
//			super(cause);
//			this.mCauseMessage = cause;
//		}
//
//		public SQLException(Exception e) {
//			super(e);
//		}
//
//		public SQLException(Exception e, String cause) {
//			super(e);
//			this.mCauseMessage = cause;
//		}
//
//		public void setCauseMessage(String msg) {
//			this.mCauseMessage = msg;
//		}
//
//		public String getCauseMessage() {
//			return mCauseMessage;
//		}
//	}
//
//	// VideoLoadPart
//    public long queryVideoTotalDownloadSize(Long videoId) throws SQLException {
//        Cursor cursor =null;
//        try{
//            cursor = database.rawQuery("select sum(downloadsize) from loadvideopart where videoid =?", new String[]{""+videoId});
//            if (cursor != null && cursor.moveToFirst()) {
//                return cursor.getLong(0);
//            }else{
//                return 0;
//            }
//        }catch(Exception e){
//            throw new SQLException(e,
//                    "Error:get Exception when queryVideoTotalDownloadSize");
//
//        }finally{
//            if(cursor!=null){
//                cursor.close();
//            }
//        }
//    }
//
//	public List<VideoLoadPart> queryVideoLoadPartForList(Long videoId,Integer state) throws SQLException {
//		Cursor cursor = null;
//		try{
//		cursor = database.rawQuery("select * from loadvideopart where videoid =? and state=?", new String[]{""+videoId,""+state});
//		if (cursor!=null&&cursor.moveToFirst()) {
//			List<VideoLoadPart> videoLoadparts = new ArrayList<VideoLoadPart>();
//			do {
//				VideoLoadPart videoLoadPart = new VideoLoadPart();
//				videoLoadPart.setId(cursor.getInt(cursor.getColumnIndex("_id")));
//				videoLoadPart.setVideoId(cursor.getLong(cursor.getColumnIndex("videoid")));
//				videoLoadPart.setPart(cursor.getInt(cursor.getColumnIndex("part")));
//				videoLoadPart.setState(cursor.getInt(cursor.getColumnIndex("state")));
//				videoLoadPart.setUrl(cursor.getString(cursor.getColumnIndex("url")));
//                videoLoadPart.setTotalSize(cursor.getLong(cursor.getColumnIndex("totalsize")));
//                videoLoadPart.setDownloadSize(cursor.getLong(cursor.getColumnIndex("downloadsize")));
//				videoLoadparts.add(videoLoadPart);
//			} while (cursor.moveToNext());
//			return videoLoadparts;
//		}else{
//			return null;
//		}
//		}catch(Exception e){
//			throw new SQLException(e,
//					"Error:get Exception when queryVideoLoadPartForList");
//		}finally{
//			if(cursor!=null){
//				cursor.close();
//			}
//		}
//	}
//
//	public long queryVideoLoadPartForListCount(Long videoId,Integer state) throws SQLException{
//		Cursor cursor =null;
//		try{
//			cursor = database.rawQuery("select count(*) from loadvideopart where videoid =? and state=?", new String[]{""+videoId,""+state});
//			if (cursor!=null&&cursor.moveToFirst()) {
//				return cursor.getLong(0);
//			}else{
//				return 0;
//			}
//		}catch(Exception e){
//			throw new SQLException(e,
//					"Error:get Exception when queryVideoLoadPartForListCount");
//
//		}finally{
//			if(cursor!=null){
//				cursor.close();
//			}
//		}
//	}
//
//	public boolean initVideoLoadPart(List<VideoLoadPart> videoLoadParts) throws SQLException{
//		database.beginTransaction();
//		if(videoLoadParts==null||videoLoadParts.size()<=0)	return false;
//		try {
//			for (VideoLoadPart videoLoadPart : videoLoadParts ) {
//				database.execSQL(
//						"insert into loadvideopart(videoid,part,state,url) values(?,?,?,?)",
//						new Object[] { videoLoadPart.getVideoId(),videoLoadPart.getPart(), VideoLoadPart.STATE_UNLOADED,videoLoadPart.getUrl()});
//			}
//			database.setTransactionSuccessful();
//		} catch (Exception e) {
//			// TODO: handle exception
//			throw new SQLException(e,
//					"Error: Get Exception when initVideoLoadPart into database.");
//		} finally {
//			database.endTransaction();
//		}
//		return true;
//	}
//
//	public void deleteVideoLoadPart(Long videoId) throws SQLException {
//		try {
//			database.delete(LoadVideoPart_TABLE, "videoid=?",
//					new String[] {videoId + "", });
//		} catch (Exception e) {
//			// e.printStackTrace();
//			throw new SQLException(e,
//					"Error: Get Exception when delete video database.");
//		}
//	}
//
//	public void deleteVideoLoadParts(List<VideoLoad> videoLoads) throws SQLException {
//		try {
//            if(videoLoads != null && videoLoads.size() > 0) {
//                for(VideoLoad videoLoad : videoLoads) {
//                    deleteVideoLoadPart(videoLoad.getId());
//                }
//            }
//		} catch (Exception e) {
//			throw new SQLException(e,
//					"Error: Get Exception when delete video database.");
//		}
//	}
//
//	public boolean updateVideoLoadPart(VideoLoadPart videoLoadPart) throws SQLException {
//		ContentValues values = new ContentValues();
//		values.put("videoid", videoLoadPart.getVideoId());
//		values.put("part", videoLoadPart.getPart());
//		values.put("state", videoLoadPart.getState());
//		values.put("url", videoLoadPart.getUrl());
//        values.put("totalsize", videoLoadPart.getTotalSize());
//        values.put("downloadsize", videoLoadPart.getDownloadSize());
//		database.update(LoadVideoPart_TABLE, values, "videoid=? and part=?",
//				new String[] { videoLoadPart.getVideoId()+"", videoLoadPart.getPart()+""});
//
//
//		return true;
//	}
//}
