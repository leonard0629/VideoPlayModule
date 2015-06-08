package com.jiuguo.app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.jiuguo.app.bean.SearchHistory;
import com.jiuguo.app.bean.Video;
import com.jiuguo.app.bean.VideoLoad;
import com.jiuguo.app.bean.VideoLoadPart;

import java.text.SimpleDateFormat;
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
            e.printStackTrace();
            Log.e(TAG, "catch exception when close database, cause: " + e.getMessage());
        }
    }

    /**
     * 查询缓存记录
     * @param checkId
     * @return
     */
    public VideoLoad queryVideoLoad(int checkId) {
        VideoLoad videoLoad = null;
        if (checkId != -1) {
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
                    videoLoad.setFileUrl(cursor.getString(cursor.getColumnIndex("fileurl")));
                    videoLoad.setDownLoadSize(cursor.getInt(cursor.getColumnIndex("downloadsize")));
                    videoLoad.setDownLoadPart(cursor.getInt(cursor.getColumnIndex("downloadpart")));
                    videoLoad.setTotalSize(cursor.getInt(cursor.getColumnIndex("totalsize")));
                    videoLoad.setCurrentSize(cursor.getInt(cursor.getColumnIndex("currentsize")));
                    videoLoad.setIsFinish((cursor.getInt(cursor.getColumnIndex("isfinish"))) > 0);
                    videoLoad.setIsStart((cursor.getInt(cursor.getColumnIndex("isstart"))) > 0);
                    videoLoad.setType((cursor.getInt(cursor.getColumnIndex("type"))));
                    videoLoad.setIsNew((cursor.getInt(cursor.getColumnIndex("isnew"))) > 0);
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
                + "fileurl,downloadsize,totalsize,isfinish,isstart,downloadpart,currentsize,"
                + "type,isnew) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        database.beginTransaction();
        try {
            database.execSQL(sql, new Object[]{videoLoad.getId(), videoLoad.getTitle(), videoLoad.getDescribe(),
                    videoLoad.getDuration(), videoLoad.getPostDate(), videoLoad.getPosterId(),
                    videoLoad.getPosterName(), videoLoad.getImage(), videoLoad.getPlayCount(),
                    videoLoad.getFavourCount(), videoLoad.getBookCount(), videoLoad.getCheckId(),
                    videoLoad.getFileUrl(), videoLoad.getDownLoadSize(), videoLoad.getTotalSize(),
                    videoLoad.isFinish(), videoLoad.isStart(), videoLoad.getDownLoadPart(),
                    videoLoad.getCurrentSize(), videoLoad.getType(), videoLoad.isNew()});
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
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
            values.put("downloadsize", videoload.getDownLoadSize());
            values.put("totalsize", videoload.getTotalSize());
            values.put("isfinish", videoload.isFinish());
            values.put("isstart", videoload.isStart());
            values.put("downloadpart", videoload.getDownLoadPart());
            values.put("currentsize", videoload.getCurrentSize());
            values.put("type", videoload.getType());
            values.put("isnew", videoload.isNew());
            database.update("video", values, "videoid=?", new String[]{videoload.getId() + ""});

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "catch exception when update video, cause: " + e.getMessage());
        }

    }

    /**
     * 删除缓存记录
     * @param videoLoad
     */
    public void deleteVideo(VideoLoad videoLoad) {
        try {
            database.delete("video", "videoid=?", new String[]{videoLoad.getId() + ""});
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "catch exception when delete video, cause: " + e.getMessage());
        }
    }

    /**
     * 批量删除缓存记录
     * @param videoLoads
     */
    public void deleteVideos(List<VideoLoad> videoLoads) {
        try {
            if (videoLoads != null && videoLoads.size() > 0) {
                for (VideoLoad videoLoad : videoLoads) {
                    deleteVideo(videoLoad);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "catch exception when delete videos, cause: " + e.getMessage());
        }
    }

    /**
     * 新增缓存块记录
     *
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
            e.printStackTrace();
            Log.e(TAG, "catch exception when add video load parts, cause: " + e.getMessage());
        } finally {
            database.endTransaction();
        }
    }

    /**
     * 删除缓存块记录
     *
     * @param videoId
     */
    public void deleteVideoLoadPart(Long videoId) {
        try {
            database.delete("loadvideopart", "videoid=?", new String[]{videoId + ""});
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "catch exception when delete video load part, cause: " + e.getMessage());
        }
    }

    /**
     * 删除缓存块记录
     *
     * @param videoLoads
     */
    public void deleteVideoLoadParts(List<VideoLoad> videoLoads) {
        try {
            if (videoLoads != null && videoLoads.size() > 0) {
                for (VideoLoad videoLoad : videoLoads) {
                    deleteVideoLoadPart(videoLoad.getId());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
     * 查询所有播放历史记录
     * @return
     */
    public List<Video> queryRecordVideos() {
        Cursor cursor = null;
        List<Video> videos = null;
        try{
            videos = new ArrayList<Video>();
            cursor = database.rawQuery("SELECT * FROM recordvideo", null);
            while (cursor.moveToNext()) {
                Video video = new Video();
                video.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                video.setDescribe(cursor.getString(cursor.getColumnIndex("describe")));
                video.setDuration(cursor.getInt(cursor.getColumnIndex("duration")));
                video.setPostDate(cursor.getString(cursor.getColumnIndex("postdate")));
                video.setPosterId(cursor.getInt(cursor.getColumnIndex("posterid")));
                video.setPosterName(cursor.getString(cursor.getColumnIndex("postername")));
                video.setImage(cursor.getString(cursor.getColumnIndex("imageUrl")));
                video.setPlayCount(cursor.getInt(cursor.getColumnIndex("playCount")));
                video.setFavourCount(cursor.getInt(cursor.getColumnIndex("favourcount")));
                video.setBookCount(cursor.getInt(cursor.getColumnIndex("bookcount")));
                video.setCheckId(cursor.getInt(cursor.getColumnIndex("checkid")));
                video.setPosterLogo(cursor.getString(cursor.getColumnIndex("posterlogo")));
                video.setRecordTime(cursor.getInt(cursor.getColumnIndex("recordtime")));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String lastTime = cursor.getString(cursor.getColumnIndex("lasttime"));
                if(lastTime != null && !"".equals(lastTime)) {
                    video.setLastTime(sdf.parse(lastTime));
                }
                video.setIsLive(cursor.getInt(cursor.getColumnIndex("islive")) == 1 ? true : false);
                videos.add(video);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "catch exception when query record videos, cause: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return videos;
    }

    /**
     * 查询播放历史记录
     * @param checkid
     * @return
     */
    public Video queryRecordVideo(long checkid) {
        Cursor cursor = null;
        try{
            cursor = database.rawQuery("SELECT * FROM recordvideo WHERE checkid=?", new String[] { "" + checkid});
            if (cursor.moveToNext()) {
                Video video = new Video();
                video.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                video.setDescribe(cursor.getString(cursor.getColumnIndex("describe")));
                video.setDuration(cursor.getInt(cursor.getColumnIndex("duration")));
                video.setPostDate(cursor.getString(cursor.getColumnIndex("postdate")));
                video.setPosterId(cursor.getInt(cursor.getColumnIndex("posterid")));
                video.setPosterName(cursor.getString(cursor.getColumnIndex("postername")));
                video.setImage(cursor.getString(cursor.getColumnIndex("imageUrl")));
                video.setPlayCount(cursor.getInt(cursor.getColumnIndex("playCount")));
                video.setFavourCount(cursor.getInt(cursor.getColumnIndex("favourcount")));
                video.setBookCount(cursor.getInt(cursor.getColumnIndex("bookcount")));
                video.setCheckId(cursor.getInt(cursor.getColumnIndex("checkid")));
                video.setPosterLogo(cursor.getString(cursor.getColumnIndex("posterlogo")));
                video.setRecordTime(cursor.getInt(cursor.getColumnIndex("recordtime")));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String lastTime = cursor.getString(cursor.getColumnIndex("lasttime"));
                if(lastTime != null && !"".equals(lastTime)) {
                    video.setLastTime(sdf.parse(lastTime));
                }
                video.setIsLive(cursor.getInt(cursor.getColumnIndex("islive")) == 1 ? true : false);
                return video;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "catch exception when query record video, cause: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * 新增播放历史记录
     * @param video
     */
    public void addRecordVideo(Video video) {
        database.beginTransaction();
        Cursor c = null;
        try {
            c = database.rawQuery("SELECT * FROM recordvideo WHERE checkid=?", new String[]{"" + video.getCheckId()});
            if (!c.moveToNext()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String lastTime = video.getLastTime() == null ? "" : sdf.format(video.getLastTime());
                database.execSQL("insert into recordvideo values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                        new Object[]{video.getId(), video.getTitle(), video.getDescribe(), video.getDuration(),
                                video.getPostDate(), video.getPosterId(), video.getPosterName(),
                                video.getImage(), video.getPlayCount(), video.getFavourCount(),
                                video.getBookCount(), video.getCheckId(), video.getPosterLogo(),
                                video.getRecordTime(), lastTime, video.isLive()});
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "catch exception when add record video, cause: " + e.getMessage());
        } finally {
            if (c != null) {
                c.close();
            }
            database.endTransaction();
        }
    }

    /**
     * 更新播放历史记录
     * @param video
     */
    public void updateRecordVideo(Video video) {
        try {
            ContentValues values = new ContentValues();
            values.put("title", video.getTitle());
            values.put("describe", video.getDescribe());
            values.put("duration", video.getDuration());
            values.put("postdate", video.getPostDate());
            values.put("posterid", video.getPosterId());
            values.put("postername", video.getPosterName());
            values.put("imageUrl", video.getImage());
            values.put("playCount", video.getPlayCount());
            values.put("favourcount", video.getFavourCount());
            values.put("bookcount", video.getBookCount());
            values.put("checkid", video.getCheckId());
            values.put("posterlogo", video.getPosterLogo());
            values.put("recordtime", video.getRecordTime());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String lastTime = video == null ? "" : sdf.format(video.getLastTime());
            values.put("lasttime", lastTime);
            values.put("islive", video.isLive());
            database.update("recordvideo", values, "checkid=?", new String[] { "" + video.getCheckId()});
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "catch exception when update record video, cause: " + e.getMessage());
        }
    }

    /**
     * 删除播放历史记录
     * @param video
     */
    public void deleteRecordVideo(Video video) {
        try {
            database.delete("recordvideo", "checkid=?", new String[] { "" + video.getCheckId()});
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "catch exception when delete record video, cause: " + e.getMessage());
        }
    }

    /**
     * 删除所有播放历史记录
     */
    public void deleteAllRecordVideo() {
        try {
            database.delete("recordvideo", null, null);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "catch exception when delete all record video, cause: " + e.getMessage());
        }
    }

	/**
	 * 新增搜索历史记录
	 * @param history
	 */
	public void addSearchHisitory(SearchHistory history) {
		deleteSearchHistory(history);

		String sql = "insert into searchhistory(historyid,content,date) values(?,?,?)";
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
