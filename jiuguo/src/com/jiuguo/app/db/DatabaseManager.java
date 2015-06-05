package com.jiuguo.app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.jiuguo.app.bean.SearchHistory;
import com.jiuguo.app.bean.VideoLoad;
import com.jiuguo.app.bean.VideoLoadPart;

import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
	private static final String TAG = "VideoManager";

	private DatabaseHelper helper;
	private SQLiteDatabase database;

	public DatabaseManager(Context context) {
		try {
			helper = new DatabaseHelper(context);
			database = helper.getWritableDatabase();
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "catch exception when get writable database, cause: " + e.getMessage());
			try {
				database = helper.getReadableDatabase();
			} catch (Exception e2) {
				e2.printStackTrace();
				Log.e(TAG, "catch exception when get readable database, cause: " + e.getMessage());
			}
		}
	}

	/**
	 * 关闭数据库连接
	 */
	public void close() {
		try {
			database.close();
		} catch (Exception e) {
			Log.e(TAG, "catch exception when close database, cause: " + e.getMessage());
		}
	}

	public VideoLoad queryVideoLoad(int checkId) {
        VideoLoad videoLoad = null;
        if(checkId != - 1) {
            Cursor cursor = null;
            try {
                cursor = database.rawQuery("select * from video where checkid=?", new String[]{checkId + ""});
                if (cursor.moveToNext()) {
                    videoLoad = new VideoLoad();
                    videoLoad.setId(cursor.getLong(cursor.getColumnIndex("videoid")));
                    videoLoad.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                    videoLoad.setDescribe(cursor.getString(cursor.getColumnIndex("describe")));
                    videoLoad.setDuration(cursor.getInt(cursor.getColumnIndex("duration")));
                    videoLoad.setPostDate(cursor.getString(cursor.getColumnIndex("postdate")));
                    videoLoad.setPosterId(cursor.getInt(cursor.getColumnIndex("posterid")));
                    videoLoad.setPosterName(cursor.getString(cursor.getColumnIndex("postername")));
                    videoLoad.setImage(cursor.getString(cursor.getColumnIndex("imageUrl")));
                    videoLoad.setPlayCount(cursor.getInt(cursor.getColumnIndex("playCount")));
                    videoLoad.setFavourCount(cursor.getInt(cursor.getColumnIndex("favourcount")));
                    videoLoad.setBookCount(cursor.getInt(cursor.getColumnIndex("bookcount")));
                    videoLoad.setCheckId(cursor.getInt(cursor.getColumnIndex("checkid")));
                    videoLoad.setFileUrl(cursor.getString(cursor
                            .getColumnIndex(VIDEO_FILEURL)));
                    videoLoad.setDownLoadSize(cursor.getInt(cursor
                            .getColumnIndex(VIDEO_DOWNLOADSIZE)));
                    videoLoad.setDownLoadPart(cursor.getInt(cursor
                            .getColumnIndex(VIDEO_DOWNLOADPART)));
                    videoLoad.setTotalSize(cursor.getInt(cursor
                            .getColumnIndex(VIDEO_TOTALSIZE)));
					/*videoLoad.setPosterlogo(cursor.getString(cursor
							.getColumnIndex(VIDEO_POSTERLOGO)));*/
                    videoLoad.setFinish((cursor.getInt(cursor
                            .getColumnIndex(VIDEO_ISFINISH))) > 0);
                    videoLoad
                            .setStart((cursor.getInt(cursor
                                    .getColumnIndex(VIDEO_ISSTART))) > 0);
                    videoLoad.setType((cursor.getInt(cursor.getColumnIndex(VIDEO_TYPE))));
                    videoLoad.setNew((cursor.getInt(cursor
                            .getColumnIndex(VIDEO_ISNEW))) > 0);
					/*					String m3u8Query = "select * from m3u8 where "
							+ M3U8_VIDEOID + "=" + videoLoad.getId();
					videoLoad
							.setM3u8s((ArrayList<M3U8>) queryM3u8ForList(m3u8Query));*/
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "catch exception when query all search history, cause: " + e.getMessage());
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return videoLoad;
	}

	/**
	 * 新增缓存记录
	 * @param videoLoad
	 */
	public void addVideoLoad(VideoLoad videoLoad) {
		String sql = "insert into video(videoid,title,describe,duration,postdate,"
				+ "posterid,postername,imageUrl,playCount,favourcount,bookcount,checkid,"
				+ "fileurl,downloadsize,totalsize,isfinish,isstart,downloadpart,totalpart,"
				+ "type,isnew) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		database.beginTransaction();
		try {
            database.execSQL(sql, new Object[]{videoLoad.getId(), videoLoad.getTitle(), videoLoad.getDescribe(),
					videoLoad.getDuration(), videoLoad.getPostDate(), videoLoad.getPosterId(),
					videoLoad.getPosterName(), videoLoad.getImage(), videoLoad.getPlayCount(),
					videoLoad.getFavourCount(), videoLoad.getBookCount(), videoLoad.getCheckId(),
					videoLoad.getFileUrl(), videoLoad.getDownloadSize(), videoLoad.getTotalSize(),
					videoLoad.isFinish(), videoLoad.isStart(), videoLoad.getDownloadPart(),
					videoLoad.getTotalPart(), videoLoad.getType(), videoLoad.isNew()});
			database.setTransactionSuccessful();
		} catch (Exception e) {
			Log.e(TAG, "catch exception when insert video, cause: " + e.getMessage());
		} finally {
			database.endTransaction();
		}
	}

    /**
     * 更新缓存记录
     * @param videoload
     */
	public void updateVideo(VideoLoad videoload) {
		try {
			ContentValues values = new ContentValues();
			values.put("videoid", videoload.getId());
			values.put("title", videoload.getTitle());
			values.put("describe", videoload.getDescribe());
			values.put("duration", videoload.getDuration());
			values.put("postdate", videoload.getPostDate());
			values.put("posterid", videoload.getPosterId());
			values.put("postername", videoload.getPosterName());
			values.put("imageUrl", videoload.getImage());
			values.put("playCount", videoload.getPlayCount());
			values.put("favourcount", videoload.getFavourCount());
			values.put("bookcount", videoload.getBookCount());
			values.put("checkid", videoload.getCheckId());
			values.put("fileurl", videoload.getFileUrl());
			values.put("downloadsize", videoload.getDownloadSize());
			values.put("totalsize", videoload.getTotalSize());
			values.put("isfinish", videoload.isFinish());
			values.put("isstart", videoload.isStart());
			values.put("downloadpart", videoload.getDownloadPart());
			values.put("totalpart", videoload.getTotalPart());
			values.put("type", videoload.getType());
			values.put("isnew", videoload.isNew());
			database.update("video", values, "videoid=?", new String[] { videoload.getId() + ""});

		} catch (Exception e) {
			Log.e(TAG, "catch exception when update video, cause: " + e.getMessage());
		}

	}

	/**
	 * 删除缓存记录
	 * @param videoLoad
	 */
	public void deleteVideo(VideoLoad videoLoad) {
		try {
			database.delete("video", "videoid=?", new String[] { videoLoad.getId() + ""});
		} catch (Exception e) {
			Log.e(TAG, "catch exception when delete video, cause: " + e.getMessage());
		}
	}

	/**
	 * 批量删除缓存记录
     * @param videoLoads
     */
	public void deleteVideos(List<VideoLoad> videoLoads) {
		try {
			if(videoLoads != null && videoLoads.size() > 0) {
                for(VideoLoad videoLoad : videoLoads) {
                    deleteVideo(videoLoad);
                }
            }
		} catch (Exception e) {
			Log.e(TAG, "catch exception when delete videos, cause: " + e.getMessage());
		}
	}

	/**
	 * 新增缓存块记录
	 * @param videoLoadParts
	 * @return
	 */
	public void addVideoLoadParts(List<VideoLoadPart> videoLoadParts) {
		String sql = "insert into loadvideopart(videoid,part,state,url) values(?,?,?,?)";

		database.beginTransaction();
		try {
			if (videoLoadParts != null && videoLoadParts.size() > 0) {
				for (VideoLoadPart videoLoadPart : videoLoadParts) {
					database.execSQL(sql, new Object[]{videoLoadPart.getVideoId(), videoLoadPart.getPart(), VideoLoadPart.STATE_FINISHED, videoLoadPart.getUrl()});
				}
				database.setTransactionSuccessful();
			}
		} catch (Exception e) {
			Log.e(TAG, "catch exception when add video load parts, cause: " + e.getMessage());
		} finally {
			database.endTransaction();
		}
	}

	/**
	 * 删除缓存块记录
	 * @param videoId
	 */
	public void deleteVideoLoadPart(Long videoId) {
		try {
			database.delete("loadvideopart", "videoid=?", new String[] {videoId + ""});
		} catch (Exception e) {
			Log.e(TAG, "catch exception when delete video load part, cause: " + e.getMessage());
		}
	}

	/**
	 * 删除缓存块记录
	 * @param videoLoads
	 */
	public void deleteVideoLoadParts(List<VideoLoad> videoLoads) {
		try {
			if(videoLoads != null && videoLoads.size() > 0) {
				for(VideoLoad videoLoad : videoLoads) {
					deleteVideoLoadPart(videoLoad.getId());
				}
			}
		} catch (Exception e) {
			Log.e(TAG, "catch exception when delete video load parts, cause: " + e.getMessage());
		}
	}

	/**
	 * 更新缓存块记录
	 * @param videoLoadPart
	 */
	public void updateVideoLoadPart(VideoLoadPart videoLoadPart) {
		try {
			ContentValues values = new ContentValues();
			values.put("videoid", videoLoadPart.getVideoId());
			values.put("part", videoLoadPart.getPart());
			values.put("state", videoLoadPart.getState());
			values.put("url", videoLoadPart.getUrl());
			values.put("totalsize", videoLoadPart.getTotalSize());
			values.put("downloadsize", videoLoadPart.getDownloadSize());
			database.update("loadvideopart", values, "videoid=? and part=?", new String[]{videoLoadPart.getVideoId() + "", videoLoadPart.getPart() + ""});
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "catch exception when update video load part, cause: " + e.getMessage());
		}
	}

	/**
	 * 新增搜索历史记录
	 * @param history
	 */
	public void addSearchHisitory(SearchHistory history) {
		deleteSearchHistory(history);

		String sql = "insert into searchhistory(historyid,content,date) values(null,?,?,?)";
		database.beginTransaction();
		try {
			database.execSQL(sql, new String[] { history.getId() + "", history.getContent(), history.getDate()});
			database.setTransactionSuccessful();
		} catch (Exception e) {
			Log.e(TAG, "catch exception when add search history, cause: " + e.getMessage());
		} finally {
			database.endTransaction();
		}
	}

	/**
	 * 删除所有搜索历史记录
	 */
	public void deleteAllSearchHistory() {
		try {
			database.delete("searchhistory", null, null);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "catch exception when delete all search history, cause: " + e.getMessage());
		}

	}

	/**
	 * 删除搜索历史记录
	 * @param searchHistory
	 */
	public void deleteSearchHistory(SearchHistory searchHistory) {
		try {
			database.delete("searchhistory", "content=?", new String[] { searchHistory.getContent()});
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "catch exception when delete search history, cause: " + e.getMessage());
		}
	}

	/**
	 * 查询所有搜索历史记录
	 * @return
	 */
	public List<SearchHistory> queryAllSearchHistory() {
		List<SearchHistory> searchHistories = new ArrayList<SearchHistory>();
		Cursor cursor = null;
		try {
			cursor = database.rawQuery("select * from searchhistory order by date desc", null);
			while (cursor.moveToNext()) {
				SearchHistory searchHistory = new SearchHistory();
				searchHistory.setId(cursor.getString(cursor.getColumnIndex("historyid")));
				searchHistory.setContent(cursor.getString(cursor.getColumnIndex("content")));
				searchHistory.setDate(cursor.getString(cursor.getColumnIndex("date")));
				searchHistories.add(searchHistory);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "catch exception when query all search history, cause: " + e.getMessage());
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return searchHistories;
	}
}
