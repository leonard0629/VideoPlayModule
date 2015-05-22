/*
 * Copyright (C) 2006 The Android Open Source Project
 * Copyright (C) 2013 YIXIA.COM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.vov.vitamio.widget;

import android.R.anim;
import android.R.integer;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.utils.Log;
import io.vov.vitamio.utils.StringUtils;

/**
 * A view containing controls for a MediaPlayer. Typically contains the buttons
 * like "Play/Pause" and a progress slider. It takes care of synchronizing the
 * controls with the state of the MediaPlayer.
 * <p/>
 * The way to use this class is to a) instantiate it programatically or b)
 * create it in your xml layout.
 * <p/>
 * a) The MediaController will create a default set of controls and put them in
 * a window floating above your application. Specifically, the controls will
 * float above the view specified with setAnchorView(). By default, the window
 * will disappear if left idle for three seconds and reappear when the user
 * touches the anchor view. To customize the MediaController's style, layout and
 * controls you should extend MediaController and override the {#link
 * {@link #makeControllerView()} method.
 * <p/>
 * b) The MediaController is a FrameLayout, you can put it in your layout xml
 * and get it through {@link #findViewById(int)}.
 * <p/>
 * NOTES: In each way, if you want customize the MediaController, the SeekBar's
 * id must be mediacontroller_progress, the Play/Pause's must be
 * mediacontroller_pause, current time's must be mediacontroller_time_current,
 * total time's must be mediacontroller_time_total, file name's must be
 * mediacontroller_file_name. And your resources must have a pause_button
 * drawable and a play_button drawable.
 * <p/>
 * Functions like show() and hide() have no effect when MediaController is
 * created in an xml layout.
 */
public class MediaController extends FrameLayout {
  private static final String TAG = "MediaController";
  private static final int sDefaultTimeout = 3000;
  private static final int FADE_OUT = 1;
  private static final int SHOW_PROGRESS = 2;
  private MediaPlayerControl mPlayer;
  private Context mContext;
  private PopupWindow mWindow;
  private int mAnimStyle;
  private View mAnchor;
  private View mRoot;
  private SeekBar mProgress;
  private TextView mEndTime, mCurrentTime,mTime;
  private TextView mFileName;
  private OutlineTextView mInfoView;
  private String mTitle;
  private long mDuration;
  private boolean mShowing;
  private boolean mDragging;
  private boolean mInstantSeeking = false;
  private boolean mFromXml = false;
  private ImageButton mPauseButton;
  private AudioManager mAM;
  private int maxVolume;
  private int curVolume;
  private OnShownListener mShownListener;
  private OnHiddenListener mHiddenListener;
  private OnVolumeChangeListener mVolumeChangeListener;
  private OnLightChangeListener mLightChangeListener;
  private OnTouchUpListener mOnTouchUpListener;
  
  //custom area
  public static final int NONE_MODE = 0x0;
  public static final int VOLUME_MODE = 0x1;
  public static final int SPEED_MODE = 0x2;
  public static final int LIGHT_MODE = 0x3;
  
  private float moveOldX = 0;
  private float moveOldY = 0;
  private int screenWidth = 480;
  private int screenHeight = 800;
  private int slidingMode = NONE_MODE;
  
  private boolean isLocked = false;
  
  public boolean isLocked() {
	return isLocked;
  }

  public void setLocked(boolean isLocked) {
	this.isLocked = isLocked;
  }

@SuppressLint("HandlerLeak")
  private Handler mHandler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      long pos;
      switch (msg.what) {
        case FADE_OUT:
          hide();
          break;
        case SHOW_PROGRESS:
          pos = setProgress();
          if (!mDragging && mShowing) {
            msg = obtainMessage(SHOW_PROGRESS);
            sendMessageDelayed(msg, 1000 - (pos % 1000));
            updatePausePlay();
          }
          break;
      }
    }
  };
  
  private OnClickListener mPauseListener = new OnClickListener() {
    public void onClick(View v) {
      doPauseResume();
      show(sDefaultTimeout);
    }
  };
  private OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {
    public void onStartTrackingTouch(SeekBar bar) {
    android.util.Log.v(TAG, "start track");
      mDragging = true;
      show(3600000);
      mHandler.removeMessages(SHOW_PROGRESS);
      if (mInstantSeeking)
        mAM.setStreamMute(AudioManager.STREAM_MUSIC, true);
      if (mInfoView != null) {
        mInfoView.setText("");
        mInfoView.setVisibility(View.VISIBLE);
      }
    }

    public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
    	//mProgress.setProgress(progress);
      if (!fromuser)
        return;
        
      android.util.Log.v(TAG, "tracking");
      long newposition = (mDuration * progress) / 1000;
      String time = StringUtils.generateTime(newposition);
      if (mInstantSeeking)
        mPlayer.seekTo(newposition);
      if (mInfoView != null)
        mInfoView.setText(time);
      if (mCurrentTime != null)
        mCurrentTime.setText(time+"/");
    }

    public void onStopTrackingTouch(SeekBar bar) {
    	android.util.Log.v(TAG, "start track");
      if (!mInstantSeeking)
        mPlayer.seekTo((mDuration * bar.getProgress()) / 1000);
      if (mInfoView != null) {
        mInfoView.setText("");
        mInfoView.setVisibility(View.GONE);
      }
      show(sDefaultTimeout);
      mHandler.removeMessages(SHOW_PROGRESS);
      mAM.setStreamMute(AudioManager.STREAM_MUSIC, false);
      mDragging = false;
      mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, 1000);
    }
  };


  public MediaController(Context context, AttributeSet attrs) {
    super(context, attrs);
    android.util.Log.v(TAG,"constructor:true");
    mRoot = this;
    mFromXml = true;
    initController(context);
  }

  public MediaController(Context context) {
    super(context);
    android.util.Log.v(TAG,"constructor:false");
    if (!mFromXml && initController(context))
      initFloatingWindow();
  }

  private boolean initController(Context context) {
    mContext = context;
    mAM = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
    maxVolume = mAM.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    curVolume = mAM.getStreamVolume(AudioManager.STREAM_MUSIC);
    return true;
  }

  @Override
  public void onFinishInflate() {
    if (mRoot != null)
      initControllerView(mRoot);
  }

  private void initFloatingWindow() {
    mWindow = new PopupWindow(mContext);
    mWindow.setFocusable(false);
    mWindow.setBackgroundDrawable(null);
    mWindow.setOutsideTouchable(true);
    mAnimStyle = android.R.style.Animation;
  }
  
  @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public void setWindowLayoutType() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			try {
				mAnchor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
				Method setWindowLayoutType = PopupWindow.class.getMethod("setWindowLayoutType", new Class[] { int.class });
				setWindowLayoutType.invoke(mWindow, WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG);
			} catch (Exception e) {
				Log.e("setWindowLayoutType", e);
			}
		}
	}

  /**
   * Set the view that acts as the anchor for the control view. This can for
   * example be a VideoView, or your Activity's main view.
   *
   * @param view The view to which to anchor the controller when it is visible.
   */
  public void setAnchorView(View view) {
    mAnchor = view;
    if (!mFromXml) {
      removeAllViews();
      mRoot = makeControllerView();
      mWindow.setContentView(mRoot);
      mWindow.setWidth(LayoutParams.MATCH_PARENT);
      mWindow.setHeight(LayoutParams.WRAP_CONTENT);
    }
    initControllerView(mRoot);
  }

  /**
   * Create the view that holds the widgets that control playback. Derived
   * classes can override this to create their own.
   *
   * @return The controller view.
   */
  protected View makeControllerView() {
    return ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(getResources().getIdentifier("mediacontroller", "layout", mContext.getPackageName()), this);
  }

  private void initControllerView(View v) {
    mPauseButton = (ImageButton) v.findViewById(getResources().getIdentifier("mediacontroller_play_pause", "id", mContext.getPackageName()));
    if (mPauseButton != null) {
      mPauseButton.requestFocus();
      mPauseButton.setOnClickListener(mPauseListener);
    }

    mProgress = (SeekBar) v.findViewById(getResources().getIdentifier("mediacontroller_seekbar", "id", mContext.getPackageName()));
    if (mProgress != null) {
      if (mProgress instanceof SeekBar) {
        SeekBar seeker = (SeekBar) mProgress;
        seeker.setOnSeekBarChangeListener(mSeekListener);
      }
      mProgress.setMax(1000);
      mProgress.setIndeterminate(false);
    }
    
    mEndTime = (TextView) v.findViewById(getResources().getIdentifier("mediacontroller_time_total", "id", mContext.getPackageName()));
    mCurrentTime = (TextView) v.findViewById(getResources().getIdentifier("mediacontroller_time_current", "id", mContext.getPackageName()));
    mFileName = (TextView) v.findViewById(getResources().getIdentifier("mediacontroller_file_name", "id", mContext.getPackageName()));
    mTime = (TextView) v.findViewById(getResources().getIdentifier("video_time", "id", mContext.getPackageName()));
    if (mFileName != null)
      mFileName.setText(mTitle);
  }

  public void setMediaPlayer(MediaPlayerControl player) {
    mPlayer = player;
    updatePausePlay();
  }

  public MediaPlayerControl getMediaPlayer(){
	  return mPlayer;
  }
  
  /**
   * Control the action when the seekbar dragged by user
   *
   * @param seekWhenDragging True the media will seek periodically
   */
  public void setInstantSeeking(boolean seekWhenDragging) {
    mInstantSeeking = seekWhenDragging;
  }

  public void show() {
    show(sDefaultTimeout);
  }

  /**
   * Set the content of the file_name TextView
   *
   * @param name
   */
  public void setFileName(String name) {
    mTitle = name;
    if (mFileName != null)
      mFileName.setText(mTitle);
  }

  /**
   * Set the View to hold some information when interact with the
   * MediaController
   *
   * @param v
   */
  public void setInfoView(OutlineTextView v) {
    mInfoView = v;
  }

  /**
   * <p>
   * Change the animation style resource for this controller.
   * </p>
   * <p/>
   * <p>
   * If the controller is showing, calling this method will take effect only the
   * next time the controller is shown.
   * </p>
   *
   * @param animationStyle animation style to use when the controller appears
   *                       and disappears. Set to -1 for the default animation, 0 for no animation, or
   *                       a resource identifier for an explicit animation.
   */
  public void setAnimationStyle(int animationStyle) {
    mAnimStyle = animationStyle;
  }

  /**
   * Show the controller on screen. It will go away automatically after
   * 'timeout' milliseconds of inactivity.
   *
   * @param timeout The timeout in milliseconds. Use 0 to show the controller
   *                until hide() is called.
   */
  public void show(int timeout) {
		long time = System.currentTimeMillis();
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		Date d1 = new Date(time);
		String t1 = format.format(d1);
		mTime.setText(t1);
    if (!mShowing && mAnchor != null && mAnchor.getWindowToken() != null) {
      if (mPauseButton != null)
        mPauseButton.requestFocus();

      if (mFromXml) {
        setVisibility(View.VISIBLE);
      } else {
        int[] location = new int[2];

        mAnchor.getLocationOnScreen(location);
        Rect anchorRect = new Rect(location[0], location[1], location[0] + mAnchor.getWidth(), location[1] + mAnchor.getHeight());

        mWindow.setAnimationStyle(mAnimStyle);
        setWindowLayoutType();
        mWindow.showAtLocation(mAnchor, Gravity.NO_GRAVITY, anchorRect.left, anchorRect.bottom);
      }
      mShowing = true;
      if (mShownListener != null)
        mShownListener.onShown();
    }
    updatePausePlay();
    mHandler.sendEmptyMessage(SHOW_PROGRESS);

    if (timeout > 0) {
      mHandler.removeMessages(FADE_OUT);
      mHandler.sendMessageDelayed(mHandler.obtainMessage(FADE_OUT), timeout);
    }else{
    	//shown until call hide()
    }
  }
  
  public boolean isShowing() {
    return mShowing;
  }

  public void hide() {
	android.util.Log.v(TAG, "hide");
    if (mAnchor == null)
      return;
    
    if (mShowing) {
      try {
        mHandler.removeMessages(SHOW_PROGRESS);
        if (mFromXml){
          setVisibility(View.GONE);
          android.util.Log.v(TAG, "hide view");
        }
        else
          mWindow.dismiss();
      } catch (IllegalArgumentException ex) {
        Log.d("MediaController already removed");
      }
      mShowing = false;
      if (mHiddenListener != null)
        mHiddenListener.onHidden();
    }
  }

  public void setOnShownListener(OnShownListener l) {
    mShownListener = l;
  }

  public void setOnHiddenListener(OnHiddenListener l) {
    mHiddenListener = l;
  }

  private long setProgress() {
    if (mPlayer == null || mDragging)
      return 0;

    long position = mPlayer.getCurrentPosition();
    long duration = mPlayer.getDuration();
    if (mProgress != null) {
      if (duration > 0) {
        long pos = 1000L * position / duration;
        mProgress.setProgress((int) pos);
      }
      int percent = mPlayer.getBufferPercentage();
      mProgress.setSecondaryProgress(percent * 10);
    }

    mDuration = duration;

    if (mEndTime != null)
      mEndTime.setText(StringUtils.generateTime(mDuration));
    if (mCurrentTime != null)
      mCurrentTime.setText(StringUtils.generateTime(position)+"/");

    return position;
  }
  

  @Override
  public boolean onTouchEvent(MotionEvent event) {
	if(isLocked) return false;
    show(sDefaultTimeout);
    /*switch (event.getAction()) {
	case MotionEvent.ACTION_DOWN:
		Log.e("cyj onTouch Event in mediaController action down");
		moveOldX = event.getX();
		moveOldY = event.getY();
		return true;
	case MotionEvent.ACTION_UP:
		Log.e("cyj onTouch Event in mediaController action up");
		onTouchUp();
		if(slidingMode!=NONE_MODE){
			slidingMode = NONE_MODE;
			return true;
		}else {
			if(isShowing()){
				hide();
			}
		}
		break;
	case MotionEvent.ACTION_MOVE:
		float deltaX = Math.abs((event.getX()-moveOldX));
		float deltaY = Math.abs((event.getY()-moveOldY));
		if(slidingMode==NONE_MODE){
			if(deltaX-deltaY>20){
				slidingMode = SPEED_MODE;
			}else if(event.getX()<screenWidth/2&&moveOldX<screenWidth/2&&deltaX - deltaY < -20){
				slidingMode = LIGHT_MODE;
			}else if(event.getX()>screenWidth/2&&moveOldX>screenWidth/2&&deltaX - deltaY < -20){
				slidingMode = VOLUME_MODE;
			}
		}else{
			if(slidingMode==SPEED_MODE){
				if((event.getX()-moveOldX)>20){
					//horizontal sliding
					speedHalf(true);
					moveOldX = event.getX();
					moveOldY = event.getY();
				}else if((event.getX()-moveOldX)<-20){
					//vertical sliding
					speedHalf(false);
					moveOldX = event.getX();
					moveOldY = event.getY();
				}
			}else if(slidingMode==VOLUME_MODE){
				//vertical sliding
				if((event.getY()-moveOldY)>5){
					addVolume((int) (-(event.getY()-moveOldY)/5));
					moveOldX = event.getX();
					moveOldY = event.getY();
				}else if((event.getY()-moveOldY)<-5){
					addVolume((int) (-(event.getY()-moveOldY)/5));
					moveOldX = event.getX();
					moveOldY = event.getY();
				}
			}else if(slidingMode==LIGHT_MODE){
				if((event.getY()-moveOldY)>5){
					addLight((int) (-(event.getY()-moveOldY)/5));
					moveOldX = event.getX();
					moveOldY = event.getY();
				}else if((event.getY()-moveOldY)<-5){
					addLight((int) (-(event.getY()-moveOldY)/5));
					moveOldX = event.getX();
					moveOldY = event.getY();
				}
			}
		}
		break;
	default:
		break;
	}*/
    return false;
  }

  @Override
  public boolean onTrackballEvent(MotionEvent ev) {
    show(sDefaultTimeout);
    return false;
  }

  @Override
  public boolean dispatchKeyEvent(KeyEvent event) {
    int keyCode = event.getKeyCode();
    if (event.getRepeatCount() == 0 && (keyCode == KeyEvent.KEYCODE_HEADSETHOOK || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE || keyCode == KeyEvent.KEYCODE_SPACE)) {
      doPauseResume();
      show(sDefaultTimeout);
      if (mPauseButton != null)
        mPauseButton.requestFocus();
      return true;
    } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP) {
      if (mPlayer.isPlaying()) {
        mPlayer.pause();
        updatePausePlay();
      }
      return true;
    } else if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU) {
      hide();
      return true;
    } else {
      show(sDefaultTimeout);
    }
    return super.dispatchKeyEvent(event);
  }

  private void updatePausePlay() {
    if (mRoot == null || mPauseButton == null||mPlayer==null)
      return;
    
    if (mPlayer.isPlaying())
      mPauseButton.setImageResource(getResources().getIdentifier("mediacontroller_pause", "drawable", mContext.getPackageName()));
    else
      mPauseButton.setImageResource(getResources().getIdentifier("mediacontroller_play", "drawable", mContext.getPackageName()));
  }

  void doPauseResume() {
    if (mPlayer.isPlaying())
      mPlayer.pause();
    else
      mPlayer.start();
    updatePausePlay();
  }

  @Override
  public void setEnabled(boolean enabled) {
    if (mPauseButton != null)
      mPauseButton.setEnabled(enabled);
    if (mProgress != null)
      mProgress.setEnabled(enabled);
    super.setEnabled(enabled);
  }
  
  public void setScreenWidth(int screenWidth){
	  this.screenWidth = screenWidth;
  }
  
  public void setScreenHeight(int screenHeight){
	  this.screenHeight = screenHeight;
  }
  
  public int getVolume(){
	  return this.curVolume;
  }
  
  public int getMaxVolume(){
	  return this.maxVolume;
  }
  
  /**
   * speed forward or backward for delta%
   */
  public void speed(int delta){
	  if(mPlayer==null)
		  return;
	  android.util.Log.v(TAG, "delta:"+delta);
	  show(3600000);
      mHandler.removeMessages(SHOW_PROGRESS);
      long newposition = mPlayer.getCurrentPosition()+delta*(mPlayer.getDuration()/2)/100;
      if(newposition>mPlayer.getDuration())
    	  newposition = mPlayer.getDuration()-60*1000;
      else if(newposition<0)
    	  newposition = 0;
      String time = StringUtils.generateTime(newposition);
      android.util.Log.v(TAG, "cur time:"+time);
      mPlayer.seekTo(newposition);
      if (mCurrentTime != null)
        mCurrentTime.setText(time+"/");
      show(sDefaultTimeout);
      mHandler.removeMessages(SHOW_PROGRESS);
      mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, 1000);
  }
  
  public void speedHalf(boolean isSpeedForward){
	  if(mPlayer==null)
		  return;
	  int delta = isSpeedForward?10:-10;
	  show(3600000);
      mHandler.removeMessages(SHOW_PROGRESS);
      long newposition = mPlayer.getCurrentPosition()+delta*1000;
      if(newposition>mPlayer.getDuration())
    	  newposition = mPlayer.getDuration()-60*1000;
      else if(newposition<0)
    	  newposition = 0;
      String time = StringUtils.generateTime(newposition);
      android.util.Log.v(TAG, "cur time:"+time);
      mPlayer.seekTo(newposition);
      if (mCurrentTime != null)
        mCurrentTime.setText(time+"/");
      show(sDefaultTimeout);
      mHandler.removeMessages(SHOW_PROGRESS);
      mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, 1000);
  }
  
  public void speedPlayer(){
	  if(mPlayer==null)
		  return;
	  long newposition = (mProgress.getProgress()/mProgress.getMax())*mDuration;
	  if(newposition>mPlayer.getDuration())
    	  newposition = mPlayer.getDuration()-60*1000;
//      else if(newposition<0)
    	  newposition = 0;
	  mPlayer.seekTo(newposition);
  }
  
  /**
   * 
   */
  public void setVolume(int percent,boolean isSeekBar){
	  if(curVolume<=0){
		  curVolume = mAM.getStreamVolume(AudioManager.STREAM_MUSIC);
          if (curVolume < 0)
        	  curVolume = 0;
	  }
	  
	  android.util.Log.v(TAG, "curVolum:"+curVolume+";maxVolume:"+maxVolume+";percent:"+percent);
	  int volume = 0;
	  if(isSeekBar)
		  volume = (int) (percent * (maxVolume/2)/100) + curVolume;
	  else {
		  volume = (int) (percent * maxVolume/100);
	  }
	  if(volume>maxVolume)
		  volume = maxVolume;
	  else if(volume<0)
		  volume = 0;
	  
	  mAM.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
	  curVolume = volume;
	  
	  if(isSeekBar&&mVolumeChangeListener!=null){
		  mVolumeChangeListener.volumeChanged(curVolume, maxVolume);
	  }
  }
  
  public void addVolume(int diff){
/*	  if(curVolume<=0){
		  curVolume = mAM.getStreamVolume(AudioManager.STREAM_MUSIC);
          if (curVolume < 0)
        	  curVolume = 0;
	  }
	  
	  int volume = curVolume + diff;
	  if(volume>maxVolume)
		  volume = maxVolume;
	  else if(volume<0)
		  volume = 0;
	  
	  mAM.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
	  curVolume = volume;*/
	  
	  if(mVolumeChangeListener!=null){
		  mVolumeChangeListener.volumeChanged(diff, maxVolume);
	  }
  }
  
  public void setOnVolumeChangedListener(OnVolumeChangeListener l){
	  mVolumeChangeListener = l;
  }
  
  /**
   * 
   */
/*  public void setLight(int percent,boolean isSeekBar){
	  Settings.System.putInt(mContext.getContentResolver(),
              Settings.System.SCREEN_BRIGHTNESS_MODE,
              Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
	  android.provider.Settings.System.putInt(mContext.getContentResolver(),android.provider.Settings.System.SCREEN_BRIGHTNESS,percent*255/100);
  }*/
  
  public void addLight(int diff){
/*	  int oldValue = android.provider.Settings.System.getInt(mContext.getContentResolver(),android.provider.Settings.System.SCREEN_BRIGHTNESS,-1);
	  int newValue = oldValue+diff*30;
	  if(newValue<=0){
		  newValue=0;
	  }else if(newValue>=255){
		  newValue=255;
	  }
//	  Log.i("cyj","newValue = " + newValue);
	  Settings.System.putInt(mContext.getContentResolver(),
              Settings.System.SCREEN_BRIGHTNESS_MODE,
              Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
	  android.provider.Settings.System.putInt(mContext.getContentResolver(),android.provider.Settings.System.SCREEN_BRIGHTNESS,newValue);
	  if(mLightChangeListener!=null)*/
		  mLightChangeListener.ligthChanged(diff,255);
  }
  public void setOnLightChangedListener(OnLightChangeListener l){
	  mLightChangeListener = l;
  }
  
  
  public interface OnShownListener {
    public void onShown();
  }

  public interface OnHiddenListener {
    public void onHidden();
  }

  public interface MediaPlayerControl {
    void start();

    void pause();

    long getDuration();

    long getCurrentPosition();

    void seekTo(long pos);

    boolean isPlaying();

    int getBufferPercentage();
  }

  public interface OnVolumeChangeListener{
	  void volumeChanged(int curVolume, int maxVolume);
  }
  
  public interface OnLightChangeListener{
	  void ligthChanged(int curVolume, int maxVolume);
  }

	public void onTouchUp() {
		// TODO Auto-generated method stub
		if(mOnTouchUpListener!=null){
			mOnTouchUpListener.onTouchUp();
		}
	}
	public interface OnTouchUpListener{
		void onTouchUp();
	}
	
	 public void setOnTouchUpListener(OnTouchUpListener l){
		 mOnTouchUpListener = l;
	  }
}
