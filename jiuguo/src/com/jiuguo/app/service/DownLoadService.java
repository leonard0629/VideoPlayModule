package com.jiuguo.app.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import com.gju.app.utils.M3U8AsyncTask;
import com.jiuguo.app.bean.VideoLoad;
import com.jiuguo.app.core.AppContext;
import com.jiuguo.event.StopLoadAndPlayEvent;
import de.greenrobot.event.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownLoadService extends Service{
	private static final int POOL_SIZE = 3;

	private static final String TAG = "M3U8DownLoadService";

	private static List<DownloadTask> tasks = new ArrayList<DownloadTask>();
	
	private static ExecutorService svc = null;

	@Override
	public void onCreate() {
		super.onCreate();
		if(svc == null){
			svc = Executors.newFixedThreadPool(POOL_SIZE);
			Log.i(TAG,"download thread pool size :" + POOL_SIZE);
		}
		EventBus.getDefault().register(this);
	}
	
	@Override
	 public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent == null) {
			return super.onStartCommand(intent, flags, startId);
		}
		VideoLoad videoLoad = (VideoLoad)intent.getSerializableExtra("videoload");
		//if we do not find the same task, start a process
		if(!findTaskById(videoLoad.getId())){
			DownloadTask task = new DownloadTask(appContext, videoLoad,svc);
			tasks.add(task);
			task.execute((Integer)videoLoad.getType());
		} else {

        }
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public synchronized static boolean stopTask(long videoId){
		for(M3U8AsyncTask task : tasks){
			if(task.getVideoId() ==  videoId){
				task.setCanceled();
				tasks.remove(task);
				return true;
			}
		}
		return false;
	}
	
	public static boolean findTaskById(long videoId){
		for(M3U8AsyncTask task : tasks){
			if(task.getVideoId() ==  videoId ){
				if(!task.isCancelled()&&!task.getCanceled()){
					return true;
				}else{
					tasks.remove(task);
					return false;
				}
			}
		}
		return false;
	}
	
	@Override
	public void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}
	
	public void onEvent(StopLoadAndPlayEvent event) {
		stopAllTask();
	}

	private void stopAllTask() {
		for(M3U8AsyncTask task : tasks){
			task.setCanceled();
		}
		tasks.clear();
	}
}
