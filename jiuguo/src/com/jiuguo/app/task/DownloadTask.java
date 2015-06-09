package com.jiuguo.app.task;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import com.jiuguo.app.bean.M3U8;
import com.jiuguo.app.bean.NewVideoUrl;
import com.jiuguo.app.bean.VideoLoad;
import com.jiuguo.app.bean.VideoLoadPart;
import com.jiuguo.app.core.AppClientV2;
import com.jiuguo.app.core.AppContext;
import com.jiuguo.app.db.VideoDBManager;
import com.jiuguo.app.receiver.M3U8DownloadReceiver;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class DownloadTask extends AsyncTask<Integer, Integer, Boolean> {
	private final static String TAG = "M3U8AsyncTask";
	public static final String M3U8_DIVIDER_1 = "#EXT-X-DISCONTINUITY";
	public static final String M3U8_DIVIDER_2 = "#EXT-X-ENDLIST";

    private final static int RETRY_TIMES = 10;

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	private ExecutorService svc = null;
	private String videoSavePath;
	private VideoDBManager dbManager;
	public DownloadTask(Context context, VideoLoad videoLoad, ExecutorService svc) {
		super();
		this.videoLoad = videoLoad;
		this.svc = svc;
		this.appContext = appContext;
		dbManager = appContext.getVideoDBManager();
		videoSavePath = appContext.getSaveVideoPath() + videoLoad.getId();
		File file = new File(videoSavePath);
		if (!file.exists()) {
			file.mkdirs();
		}
		Log.i(TAG, "videoSavePath = " + videoSavePath);
	}

	@Override
	protected void onCancelled() {
		isCanceled = true;
		super.onCancelled();
	}

	@Override
	protected void onPostExecute(Boolean result) {
		if(!result){
			sendBroadcast(M3U8DownloadReceiver.ERROR,
					videoLoad.getId(), 0);
		}
		super.onPostExecute(result);
	}

	private List<VideoLoadPart> m3u8s = new ArrayList<VideoLoadPart>();
	private VideoLoad videoLoad = null;

	@Override
	protected Boolean doInBackground(Integer... params) {
		Integer type = params[0];
		String format = "";
        String suffix = "";
        switch(type) {
			case VideoLoad.FLV:
				format = "";
                suffix = ".flv";
				break;
			case VideoLoad.MP4:
				format = "high";
                suffix = ".mp4";
				break;
			case VideoLoad.HD2:
                format = "super";
                suffix = ".hd2";
                break;
			case VideoLoad.HD3:
				format = "super";
                suffix = ".hd3";
				break;
			default:
				break;
		}
		try {
			NewVideoUrl newVideoUrl = null;

			sendBroadcast(M3U8DownloadReceiver.START, videoLoad.getId(), -1);
			m3u8s = dbManager.queryVideoLoadPartForList(videoLoad.getId(),
					VideoLoadPart.STATE_UNLOADED);
            Log.e(TAG, "videoId in download task: " + String.valueOf(videoLoad.getId()));
            newVideoUrl = AppClientV2.getNewYoukuUrl(appContext, videoLoad.getCheckId(), format);
            if(m3u8s == null) {
                Log.e(TAG, "VideoLoadPart list from db is null");
                m3u8s = new ArrayList<VideoLoadPart>();
                for(int i = 0 ;i < newVideoUrl.getPlaylist().length;i++){
                    VideoLoadPart videoLoadPart = new VideoLoadPart();
                    videoLoadPart.setUrl(newVideoUrl.getPlaylist()[i]);
                    videoLoadPart.setVideoId(videoLoad.getId());
                    videoLoadPart.setState(VideoLoadPart.STATE_UNLOADED);
                    videoLoadPart.setPart(i);
                    videoLoadPart.setSuffix(suffix);
                    m3u8s.add(videoLoadPart);
                }
                videoLoad.setDownLoadSize(newVideoUrl.getPlaylist().length);
				dbManager.updateVideo(videoLoad);
				dbManager.initVideoLoadPart(m3u8s);
            } else {
                Log.e(TAG, "VideoLoadPart list from db is not null");
                for(VideoLoadPart videoLoadPart : m3u8s){
                    videoLoadPart.setUrl(newVideoUrl.getPlaylist()[videoLoadPart.getPart()]);
                    videoLoadPart.setSuffix(suffix);
                }
            }
            if (m3u8s != null && m3u8s.size() == 0){
				publishProgress();
			}else{
				for (final VideoLoadPart videoLoadPart : m3u8s) {
					svc.execute(new Runnable() {

						@Override
						public void run() {
							int tryCount = RETRY_TIMES;
							while (tryCount > 0 && !isCanceled) {
								try {
									downLoad(videoLoadPart);
									tryCount = -1;
								} catch (Exception e) {
									e.printStackTrace();
									tryCount--;
//									Log.e("cyj","retry part:" + videoLoadPart.getPart());
									try {
										Thread.sleep(100);
									} catch (InterruptedException e1) {
										e1.printStackTrace();
									}
								}
							}
							if (tryCount == 0) {
								if (!isCanceled) {
									sendBroadcast(M3U8DownloadReceiver.ERROR,
											videoLoad.getId(), videoLoadPart.getPart());
								}
							}
						}

					});

				}

				svc.execute(new Runnable() {
					@Override
					public void run() {
						try{
							Thread.sleep(1000);
						}catch(Exception e){
							e.printStackTrace();
						}
						publishProgress();
					}
				});
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		Log.i(TAG, "doInBackground finish ");
		return true;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		try {
			int counter = (int) dbManager.queryVideoLoadPartForListCount(
					videoLoad.getId(), VideoLoadPart.STATE_LOADED);
			if (counter == videoLoad.getDownLoadSize()) {
//				Log.i(TAG, "counter :" + counter + "||size :" + videoLoad.getDownLoadSize());
				videoLoad.setDownLoadPart(counter);
				videoLoad.setStart(true);
				videoLoad.setFinish(true);
				dbManager.updateVideo(videoLoad);
				sendBroadcast(M3U8DownloadReceiver.COMPLETE, videoLoad.getId(), 0);
			} else {
				videoLoad.setDownLoadPart(counter);
				dbManager.updateVideo(videoLoad);
				sendBroadcast(M3U8DownloadReceiver.PART_COMPLETE, videoLoad.getId(), 0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private boolean isCanceled = false;

	public void setCanceled() {
		isCanceled = true;
		sendBroadcast(M3U8DownloadReceiver.STOP, videoLoad.getId(), -1);
	}

	public boolean getCanceled() {
		return isCanceled;
	}

	private void downLoad(VideoLoadPart videoLoadPart) {
		boolean isFinished = false;
		if(!isCanceled){
            InputStream in = null;
            RandomAccessFile out = null;
            try {
                long downloadSize = videoLoadPart.getDownloadSize();
                String fileName = String.valueOf(videoLoadPart.getPart()) + videoLoadPart.getSuffix();
                File file = new File(videoSavePath, fileName);
                if(!file.exists()){
                    downloadSize = 0;
                }
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(videoLoadPart.getUrl());
                httpGet.setHeader("Range", downloadSize + "-");

                HttpResponse response = httpClient.execute(httpGet);
                HttpEntity entity = response.getEntity();
                in = entity.getContent();

                long size = entity.getContentLength();
                videoLoadPart.setTotalSize(size);
                if(downloadSize >= size){
                    videoLoadPart.setState(VideoLoadPart.STATE_LOADED);
                    dbManager.updateVideoLoadPart(videoLoadPart);
                    publishProgress();
                    return;
                } else {
                    byte[] buf = new byte[8*1024];
                    int ch = -1;

                    if (size > 0 && in != null) {
                        out = new RandomAccessFile(file,"rw");
                        out.seek(downloadSize);
                        while ((ch = in.read(buf)) != -1) {
                            out.write(buf, 0, ch);
                            downloadSize += ch;
                            if(isCanceled){
                                videoLoadPart.setDownloadSize(downloadSize);
                                dbManager.updateVideoLoadPart(videoLoadPart);
                                break;
                            }
                        }
                        if(!isCanceled){
                            videoLoadPart.setState(VideoLoadPart.STATE_LOADED);
                            videoLoadPart.setDownloadSize(downloadSize);
                            dbManager.updateVideoLoadPart(videoLoadPart);
                            isFinished = true;
                            publishProgress();
                        }
                    } else {
                        throw new Exception();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }else{
			Log.i(TAG,"i is canceled");
		}
		if(!isFinished){
			Log.e("cyj","videoLoadPart not finished : " + videoLoadPart.getPart());
		}
	}

	public long getVideoId() {
		return videoLoad.getId();
	}

	public void sendBroadcast(int loadstate, long videoId, int progress) {
		Intent intent = new Intent();
		intent.setAction(M3U8DownloadReceiver.LOAD_STATE_BROADCAST);
		if (loadstate == M3U8DownloadReceiver.STOP) {
			intent.putExtra("loadstate", loadstate);
			intent.putExtra("videoId", videoId);
			intent.putExtra("progress", progress);
			intent.putExtra("title", videoLoad.getTitle());
			appContext.sendBroadcast(intent);
		} else if (!isCanceled) {
			intent.putExtra("loadstate", loadstate);
			intent.putExtra("videoId", videoId);
			intent.putExtra("progress", progress);
			intent.putExtra("title", videoLoad.getTitle());
			appContext.sendBroadcast(intent);
		}
	}
}
