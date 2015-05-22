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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import io.vov.vitamio.MediaFormat;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.MediaPlayer.OnErrorListener;
import io.vov.vitamio.MediaPlayer.OnInfoListener;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;
import io.vov.vitamio.MediaPlayer.OnSeekCompleteListener;
import io.vov.vitamio.MediaPlayer.OnTimedTextListener;
import io.vov.vitamio.MediaPlayer.OnVideoSizeChangedListener;
import io.vov.vitamio.MediaPlayer.TrackInfo;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.utils.Log;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Displays a video file. The VideoView class can load images from various
 * sources (such as resources or content providers), takes care of computing its
 * measurement from the video so that it can be used in any layout manager, and
 * provides various display options such as scaling and tinting.
 * <p/>
 * VideoView also provide many wrapper methods for
 * {@link io.vov.vitamio.MediaPlayer}, such as {@link #getVideoWidth()},
 * {@link #setTimedTextShown(boolean)}
 */
public class VideoView extends SurfaceView implements MediaController.MediaPlayerControl {
  public static final int VIDEO_LAYOUT_ORIGIN = 0;
  public static final int VIDEO_LAYOUT_SCALE = 1;
  public static final int VIDEO_LAYOUT_STRETCH = 2;
  public static final int VIDEO_LAYOUT_ZOOM = 3;
  private static final int STATE_ERROR = -1;
  private static final int STATE_IDLE = 0;
  private static final int STATE_PREPARING = 1;
  private static final int STATE_PREPARED = 2;
  private static final int STATE_PLAYING = 3;
  private static final int STATE_PAUSED = 4;
  private static final int STATE_PLAYBACK_COMPLETED = 5;
  private static final int STATE_SUSPEND = 6;
  private static final int STATE_RESUME = 7;
  private static final int STATE_SUSPEND_UNSUPPORTED = 8;
  
  private boolean isLocked = false;
  
  public boolean isLocked() {
	return isLocked;
  }

  public void setLocked(boolean isLocked) {
	this.isLocked = isLocked;
  }
  
  OnVideoSizeChangedListener mSizeChangedListener = new OnVideoSizeChangedListener() {
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
      Log.d("onVideoSizeChanged: (%dx%d)", width, height);
      mVideoWidth = mp.getVideoWidth();
      mVideoHeight = mp.getVideoHeight();
      mVideoAspectRatio = mp.getVideoAspectRatio();
      if (mVideoWidth != 0 && mVideoHeight != 0)
        setVideoLayout(mVideoLayout, mAspectRatio);
    }
  };
  OnPreparedListener mPreparedListener = new OnPreparedListener() {
    public void onPrepared(MediaPlayer mp) {
      Log.d("onPrepared");
      mCurrentState = STATE_PREPARED;
      // mTargetState = STATE_PLAYING;
      
      // Get the capabilities of the player for this stream
      //TODO mCanPause

      if (mOnPreparedListener != null)
        mOnPreparedListener.onPrepared(mMediaPlayer);
      if (mMediaController != null)
        mMediaController.setEnabled(true);
      mVideoWidth = mp.getVideoWidth();
      mVideoHeight = mp.getVideoHeight();
      mVideoAspectRatio = mp.getVideoAspectRatio();

      long seekToPosition = mSeekWhenPrepared;
      if (seekToPosition != 0)
        seekTo(seekToPosition);
      
      if (mVideoWidth != 0 && mVideoHeight != 0) {
        setVideoLayout(mVideoLayout, mAspectRatio);
        if (mSurfaceWidth == mVideoWidth && mSurfaceHeight == mVideoHeight) {
          if (mTargetState == STATE_PLAYING) {
            start();
            if (mMediaController != null){
              mMediaController.show();
              android.util.Log.v("videoplay", "OnPreparedListener meidia show");
            }
          } else if (!isPlaying() && (seekToPosition != 0 || getCurrentPosition() > 0)) {
            if (mMediaController != null)
              mMediaController.show(0);
          }
        }
      } else if (mTargetState == STATE_PLAYING) {
        start();
      }
    }
  };
  SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback() {
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
      mSurfaceWidth = w;
      mSurfaceHeight = h;
      boolean isValidState = (mTargetState == STATE_PLAYING);
      boolean hasValidSize = (mVideoWidth == w && mVideoHeight == h);
      if (mMediaPlayer != null && isValidState && hasValidSize) {
        if (mSeekWhenPrepared != 0)
          seekTo(mSeekWhenPrepared);
        start();
        if (mMediaController != null) {
          if (mMediaController.isShowing())
            mMediaController.hide();
          mMediaController.show();
        }
      }
      android.util.Log.v("videoplay", "sufacechange meidia show");
    }

    public void surfaceCreated(SurfaceHolder holder) {
      mSurfaceHolder = holder;
      if (mMediaPlayer != null && mCurrentState == STATE_SUSPEND && mTargetState == STATE_RESUME) {
        mMediaPlayer.setDisplay(mSurfaceHolder);
        resume();
      } else {
        openVideo();
      }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
      mSurfaceHolder = null;
      if (mMediaController != null) mMediaController.hide();
      release(true);
    }
  };
  private Uri mUri;
  private long mDuration;
  private int mCurrentState = STATE_IDLE;
  private int mTargetState = STATE_IDLE;
  private float mAspectRatio = 0;
  private int mVideoLayout = VIDEO_LAYOUT_STRETCH;
  private SurfaceHolder mSurfaceHolder = null;
  private MediaPlayer mMediaPlayer = null;
  private int mVideoWidth;
  private int mVideoHeight;
  private float mVideoAspectRatio;
  private int mVideoChroma = MediaPlayer.VIDEOCHROMA_RGBA;
  private boolean mHardwareDecoder = false;
  private int mSurfaceWidth;
  private int mSurfaceHeight;
  private MediaController mMediaController;
  private View mMediaBufferingIndicator;
  private OnCompletionListener mOnCompletionListener;
  private OnPreparedListener mOnPreparedListener;
  private OnErrorListener mOnErrorListener;
  private OnSeekCompleteListener mOnSeekCompleteListener;
  private OnTimedTextListener mOnTimedTextListener;
  private OnInfoListener mOnInfoListener;
  private OnBufferingUpdateListener mOnBufferingUpdateListener;
  private LifeCycleListener mLifeCycleListener;
  private int mCurrentBufferPercentage;
  private long mSeekWhenPrepared; // recording the seek position while preparing
  private Context mContext;
  private Map<String, String> mHeaders;
  private int mBufSize;
  
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				mMediaController.onTouchUp();
				if(!mMediaController.isShowing()){
					mMediaController.show();
				}else{
					mMediaController.hide();
				}
			}
		}
	};
  private Handler completeHandler = new Handler(){
	@Override
	public void handleMessage(Message msg){
		if(msg.what == 1){
			 mOnCompletionListener.onCompletion(mMediaPlayer);
		}
	}
  };
  
  private OnCompletionListener mCompletionListener = new OnCompletionListener() {
    public void onCompletion(MediaPlayer mp) {
      Log.d("onCompletion");
      mCurrentState = STATE_PLAYBACK_COMPLETED;
      mTargetState = STATE_PLAYBACK_COMPLETED;
      if (mMediaController != null)
        mMediaController.hide();
      if (mOnCompletionListener != null){
    	  long delta = mp.getDuration()-mp.getCurrentPosition();
    	  completeHandler.sendEmptyMessageDelayed(1, delta-500);
      }
    }
  };
  private OnErrorListener mErrorListener = new OnErrorListener() {
    public boolean onError(MediaPlayer mp, int framework_err, int impl_err) {
      Log.d("Error: %d, %d", framework_err, impl_err);
      mCurrentState = STATE_ERROR;
      mTargetState = STATE_ERROR;
      if (mMediaController != null)
        mMediaController.hide();

      if (mOnErrorListener != null) {
        if (mOnErrorListener.onError(mMediaPlayer, framework_err, impl_err))
          return true;
      }

      if (getWindowToken() != null) {
        int message = framework_err == MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK ? getResources().getIdentifier("VideoView_error_text_invalid_progressive_playback", "string", mContext.getPackageName()): getResources().getIdentifier("VideoView_error_text_unknown", "string", mContext.getPackageName());

        new AlertDialog.Builder(mContext).setTitle(getResources().getIdentifier("VideoView_error_title", "string", mContext.getPackageName())).setMessage(message).setPositiveButton(getResources().getIdentifier("VideoView_error_button", "string", mContext.getPackageName()), new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int whichButton) {
            if (mOnCompletionListener != null)
              mOnCompletionListener.onCompletion(mMediaPlayer);
          }
        }).setCancelable(false).show();
      }
      return true;
    }
  };
  private OnBufferingUpdateListener mBufferingUpdateListener = new OnBufferingUpdateListener() {
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
      mCurrentBufferPercentage = percent;
      if (mOnBufferingUpdateListener != null)
        mOnBufferingUpdateListener.onBufferingUpdate(mp, percent);
    }
  };
  private OnInfoListener mInfoListener = new OnInfoListener() {
    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
      Log.d("onInfo: (%d, %d)", what, extra);
      if (mOnInfoListener != null) {
        mOnInfoListener.onInfo(mp, what, extra);
      } else if (mMediaPlayer != null) {
        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
          mMediaPlayer.pause();
          if (mMediaBufferingIndicator != null)
            mMediaBufferingIndicator.setVisibility(View.VISIBLE);
        } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
          mMediaPlayer.start();
          if (mMediaBufferingIndicator != null)
            mMediaBufferingIndicator.setVisibility(View.GONE);
        }
      }
      return true;
    }
  };
  private OnSeekCompleteListener mSeekCompleteListener = new OnSeekCompleteListener() {
    @Override
    public void onSeekComplete(MediaPlayer mp) {
      Log.d("onSeekComplete");
      if (mOnSeekCompleteListener != null)
        mOnSeekCompleteListener.onSeekComplete(mp);
    }
  };
  private OnTimedTextListener mTimedTextListener = new OnTimedTextListener() {
    @Override
    public void onTimedTextUpdate(byte[] pixels, int width, int height) {
      Log.i("onSubtitleUpdate: bitmap subtitle, %dx%d", width, height);
      if (mOnTimedTextListener != null)
        mOnTimedTextListener.onTimedTextUpdate(pixels, width, height);
    }

    @Override
    public void onTimedText(String text) {
      Log.i("onSubtitleUpdate: %s", text);
      if (mOnTimedTextListener != null)
        mOnTimedTextListener.onTimedText(text);
    }
  };
  private String cacheDirectory = null;
  private String[] mUris = null;

  public void setCacheDirectory(String directory){
	  this.cacheDirectory = directory;
  }
  public VideoView(Context context) {
    super(context);
    initVideoView(context);
  }

  public VideoView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
    initVideoView(context);
  }

  public VideoView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    initVideoView(context);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
    int height = getDefaultSize(mVideoHeight, heightMeasureSpec);
    setMeasuredDimension(width, height);
  }

  /**
   * Set the display options
   *
   * @param layout      <ul>
   *                    <li>{@link #VIDEO_LAYOUT_ORIGIN}
   *                    <li>{@link #VIDEO_LAYOUT_SCALE}
   *                    <li>{@link #VIDEO_LAYOUT_STRETCH}
   *                    <li>{@link #VIDEO_LAYOUT_ZOOM}
   *                    </ul>
   * @param aspectRatio video aspect ratio, will audo detect if 0.
   */
  public void setVideoLayout(int layout, float aspectRatio) {
    LayoutParams lp = getLayoutParams();
    DisplayMetrics disp = mContext.getResources().getDisplayMetrics();
    int windowWidth = disp.widthPixels, windowHeight = disp.heightPixels;
    float windowRatio = windowWidth / (float) windowHeight;
    float videoRatio = aspectRatio <= 0.01f ? mVideoAspectRatio : aspectRatio;
    mSurfaceHeight = mVideoHeight;
    mSurfaceWidth = mVideoWidth;
    if (VIDEO_LAYOUT_ORIGIN == layout && mSurfaceWidth < windowWidth && mSurfaceHeight < windowHeight) {
      lp.width = (int) (mSurfaceHeight * videoRatio);
      lp.height = mSurfaceHeight;
    } else if (layout == VIDEO_LAYOUT_ZOOM) {
      lp.width = windowRatio > videoRatio ? windowWidth : (int) (videoRatio * windowHeight);
      lp.height = windowRatio < videoRatio ? windowHeight : (int) (windowWidth / videoRatio);
    } else {
      boolean full = layout == VIDEO_LAYOUT_STRETCH;
      lp.width = (full || windowRatio < videoRatio) ? windowWidth : (int) (videoRatio * windowHeight);
      lp.height = (full || windowRatio > videoRatio) ? windowHeight : (int) (windowWidth / videoRatio);
    }
    setLayoutParams(lp);
    getHolder().setFixedSize(mSurfaceWidth, mSurfaceHeight);
    Log.d("VIDEO: %dx%dx%f, Surface: %dx%d, LP: %dx%d, Window: %dx%dx%f", mVideoWidth, mVideoHeight, mVideoAspectRatio, mSurfaceWidth, mSurfaceHeight, lp.width, lp.height, windowWidth, windowHeight, windowRatio);
    mVideoLayout = layout;
    mAspectRatio = aspectRatio;
  }

  @SuppressWarnings("deprecation")
  private void initVideoView(Context ctx) {
    mContext = ctx;
    mVideoWidth = 0;
    mVideoHeight = 0;
    getHolder().setFormat(PixelFormat.RGBA_8888); // PixelFormat.RGB_565
    getHolder().addCallback(mSHCallback);
    // this value only use Hardware decoder before Android 2.3
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB && mHardwareDecoder) {
      getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
    setFocusable(true);
    setFocusableInTouchMode(true);
    requestFocus();
    mCurrentState = STATE_IDLE;
    mTargetState = STATE_IDLE;
    if (ctx instanceof Activity)
      ((Activity) ctx).setVolumeControlStream(AudioManager.STREAM_MUSIC);
  }

  public boolean isValid() {
    return (mSurfaceHolder != null && mSurfaceHolder.getSurface().isValid());
  }
 
  public void setVideoPath(String path) {
    setVideoURI(Uri.parse(path));
  }

  public void setVideoURI(Uri uri) {
    setVideoURI(uri, null);
  }
  
  public void setVideoURI(Uri uri, Map<String, String> headers) {
    mUri = uri;
    mHeaders = headers;
    mSeekWhenPrepared = 0;
    openVideo();
    requestLayout();
    invalidate();
  }
  
  public void setVideoURI(String[] uris,String cacheDirectory) {
	  mUris = uris;
	  this.cacheDirectory = cacheDirectory;
	  mSeekWhenPrepared = 0;
	  openVideo();
	  requestLayout();
	  invalidate();
  }
 
  public void stopPlayback() {
    if (mMediaPlayer != null) {
      mMediaPlayer.stop();
      mMediaPlayer.release();
      mMediaPlayer = null;
      mCurrentState = STATE_IDLE;
      mTargetState = STATE_IDLE;
    }
  }
  
  private void openVideo() {
    if ((mUri == null&&mUris==null) || mSurfaceHolder == null || !Vitamio.isInitialized(mContext))
      return;

    Intent i = new Intent("com.android.music.musicservicecommand");
    i.putExtra("command", "pause");
    mContext.sendBroadcast(i);

    release(false);
    try {
      mDuration = -1;
      mCurrentBufferPercentage = 0;
      mMediaPlayer = new MediaPlayer(mContext, mHardwareDecoder);
      mMediaPlayer.setOnPreparedListener(mPreparedListener);
      mMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
      mMediaPlayer.setOnCompletionListener(mCompletionListener);
      mMediaPlayer.setOnErrorListener(mErrorListener);
      mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
      mMediaPlayer.setOnInfoListener(mInfoListener);
      mMediaPlayer.setOnSeekCompleteListener(mSeekCompleteListener);
      mMediaPlayer.setOnTimedTextListener(mTimedTextListener);
      if(mUris==null){
    	  mMediaPlayer.setDataSource(mContext, mUri, mHeaders);
      }else{
    	  mMediaPlayer.setDataSegments(mUris, cacheDirectory);
      }
      mMediaPlayer.setDisplay(mSurfaceHolder);
      mMediaPlayer.setBufferSize(mBufSize);
//      mMediaPlayer.setUseCache(true);
      mMediaPlayer.setVideoChroma(mVideoChroma == MediaPlayer.VIDEOCHROMA_RGB565 ? MediaPlayer.VIDEOCHROMA_RGB565 : MediaPlayer.VIDEOCHROMA_RGBA);
      mMediaPlayer.setScreenOnWhilePlaying(true);
      if(playMode==MODE_LIVE||playMode==MODE_NET){
    	/*  mMediaPlayer.setUseCache(true);
    	  if(cacheDirectory!=null){
    		  mMediaPlayer.setCacheDirectory(cacheDirectory);
    	  }*/
      }
      mMediaPlayer.prepareAsync();
      mCurrentState = STATE_PREPARING;
      attachMediaController();
    } catch (IOException ex) {
      Log.e("Unable to open content: " + mUri, ex);
      mCurrentState = STATE_ERROR;
      mTargetState = STATE_ERROR;
      mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
      return;
    } catch (IllegalArgumentException ex) {
      Log.e("Unable to open content: " + mUri, ex);
      mCurrentState = STATE_ERROR;
      mTargetState = STATE_ERROR;
      mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
      return;
    }
  }
  
  public void setMediaController(MediaController controller) {
//    if (mMediaController != null)
//      mMediaController.hide();
    mMediaController = controller;
    attachMediaController();
  }
  
  public void setMediaBufferingIndicator(View mediaBufferingIndicator) {
    if (mMediaBufferingIndicator != null)
      mMediaBufferingIndicator.setVisibility(View.GONE);
    mMediaBufferingIndicator = mediaBufferingIndicator;
  }

  private void attachMediaController() {
    if (mMediaPlayer != null && mMediaController != null) {
      mMediaController.setMediaPlayer(this);
      View anchorView = this.getParent() instanceof View ? (View) this.getParent() : this;
      mMediaController.setAnchorView(anchorView);
      mMediaController.setEnabled(isInPlaybackState());

      if (mUri != null) {
        List<String> paths = mUri.getPathSegments();
        String name = paths == null || paths.isEmpty() ? "null" : paths.get(paths.size() - 1);
        mMediaController.setFileName(name);
      }
    }
  }

  public void setOnPreparedListener(OnPreparedListener l) {
    mOnPreparedListener = l;
  }

  public void setOnCompletionListener(OnCompletionListener l) {
    mOnCompletionListener = l;
  }

  public void setOnErrorListener(OnErrorListener l) {
    mOnErrorListener = l;
  }

  public void setOnBufferingUpdateListener(OnBufferingUpdateListener l) {
    mOnBufferingUpdateListener = l;
  }

  public void setOnSeekCompleteListener(OnSeekCompleteListener l) {
    mOnSeekCompleteListener = l;
  }

  public void setOnTimedTextListener(OnTimedTextListener l) {
    mOnTimedTextListener = l;
  }

  public void setOnInfoListener(OnInfoListener l) {
    mOnInfoListener = l;
  }

  public void setLifecycleListener(LifeCycleListener l){
	  mLifeCycleListener = l;
  }
  
  private void release(boolean cleartargetstate) {
    if (mMediaPlayer != null) {
      mMediaPlayer.reset();
      mMediaPlayer.release();
      mMediaPlayer = null;
      mCurrentState = STATE_IDLE;
      if (cleartargetstate)
        mTargetState = STATE_IDLE;
    }
  }

  public static final int NONE_MODE = 0x0;
  public static final int VOLUME_MODE = 0x1;
  public static final int SPEED_MODE = 0x2;
  public static final int LIGHT_MODE = 0x3;
  
  private float moveOldX;
  private float moveOldY;
  private int slidingMode = NONE_MODE;

  private long time = 0;
  @Override
  public boolean onTouchEvent(MotionEvent event) {
	  if(isLocked) return false;
//	  android.util.Log.e("cyj","event x = " + event.getX() + "acturalWidth = " + acturalWidth);
	  switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
//			Log.e("cyj onTouch Event in VideoView action down");
			moveOldX = event.getX();
			moveOldY = event.getY();
			return true;
		case MotionEvent.ACTION_UP:
//			Log.e("cyj onTouch Event in VideoView action up");
			long interval = System.currentTimeMillis()-time;
			if(interval<300){
				mHandler.removeMessages(1);
				mMediaController.doPauseResume();
                mMediaController.show();
				time = 0;
			}else{
				time = System.currentTimeMillis();
				if(slidingMode!=NONE_MODE){
					slidingMode = NONE_MODE;
					return true;
				}else {
					mHandler.sendEmptyMessageDelayed(1, 400);
				}
			}
			break;
		case MotionEvent.ACTION_MOVE:
			float deltaX = Math.abs((event.getX()-moveOldX));
			float deltaY = Math.abs((event.getY()-moveOldY));
			if(slidingMode==NONE_MODE){
				if(deltaX-deltaY>20){
					slidingMode = SPEED_MODE;
				}else if(event.getX()<acturalWidth/2&&moveOldX<acturalWidth/2&&deltaX - deltaY < -20){
					slidingMode = LIGHT_MODE;
				}else if(event.getX()>acturalWidth/2&&moveOldX>acturalWidth/2&&deltaX - deltaY < -20){
					slidingMode = VOLUME_MODE;
				}
			}else{
				if(slidingMode==SPEED_MODE){
					if((event.getX()-moveOldX)>20){
						//horizontal sliding
						mMediaController.speedHalf(true);
						moveOldX = event.getX();
						moveOldY = event.getY();
					}else if((event.getX()-moveOldX)<-20){
						//vertical sliding
						mMediaController.speedHalf(false);
						moveOldX = event.getX();
						moveOldY = event.getY();
					}
				}else if(slidingMode==VOLUME_MODE){
					//vertical sliding
					if((event.getY()-moveOldY)>5){
						mMediaController.addVolume((int) (-(event.getY()-moveOldY)/5));
						moveOldX = event.getX();
						moveOldY = event.getY();
					}else if((event.getY()-moveOldY)<-5){
						mMediaController.addVolume((int) (-(event.getY()-moveOldY)/5));
						moveOldX = event.getX();
						moveOldY = event.getY();
					}
				}else if(slidingMode==LIGHT_MODE){
					if((event.getY()-moveOldY)>5){
						mMediaController.addLight((int) (-(event.getY()-moveOldY)/5));
						moveOldX = event.getX();
						moveOldY = event.getY();
					}else if((event.getY()-moveOldY)<-5){
						mMediaController.addLight((int) (-(event.getY()-moveOldY)/5));
						moveOldX = event.getX();
						moveOldY = event.getY();
					}
				}
			}
			break;
		default:
			break;
		}
	    return false;
  }

  @Override
  public boolean onTrackballEvent(MotionEvent ev) {
	  android.util.Log.v("viewplay", "onTrack");
    if (isInPlaybackState() && mMediaController != null)
      toggleMediaControlsVisiblity();
    return false;
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
	  android.util.Log.v("viewplay", "onKeyDown");
    boolean isKeyCodeSupported = keyCode != KeyEvent.KEYCODE_BACK && keyCode != KeyEvent.KEYCODE_VOLUME_UP && keyCode != KeyEvent.KEYCODE_VOLUME_DOWN && keyCode != KeyEvent.KEYCODE_MENU && keyCode != KeyEvent.KEYCODE_CALL && keyCode != KeyEvent.KEYCODE_ENDCALL;
    if (isInPlaybackState() && isKeyCodeSupported && mMediaController != null) {
      if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE || keyCode == KeyEvent.KEYCODE_SPACE) {
        if (mMediaPlayer.isPlaying()) {
          pause();
          mMediaController.show();
        } else {
          start();
          mMediaController.hide();
        }
        return true;
      } else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY) {
        if (!mMediaPlayer.isPlaying()) {
            start();
            mMediaController.hide();
        }
        return true;
      } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP || keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE) {
        if (mMediaPlayer.isPlaying()) {
          pause();
          mMediaController.show();
        }
        return true;
      } else {
        toggleMediaControlsVisiblity();
      }
    }

    return super.onKeyDown(keyCode, event);
  }

  private void toggleMediaControlsVisiblity() {
    if (mMediaController.isShowing()) {
      mMediaController.hide();
    } else {
      mMediaController.show();
      android.util.Log.v("videoplay", "toggle meidia show");
    }
  }

  public boolean isMediaCtl = false;
  public void start() {
    if (isInPlaybackState()) {
      mMediaPlayer.start();
      mCurrentState = STATE_PLAYING;
    }
    mTargetState = STATE_PLAYING;
    
    mLifeCycleListener.onStart(this, mMediaPlayer, mDuration);
  }

  public void pause() {
    if (isInPlaybackState()) {
      if (mMediaPlayer.isPlaying()) {
        mMediaPlayer.pause();
        mCurrentState = STATE_PAUSED;
      }
    }
    mTargetState = STATE_PAUSED;
    mLifeCycleListener.onPause(this, mMediaPlayer, mDuration);
  }

  public void suspend() {
    if (isInPlaybackState()) {
      release(false);
      mCurrentState = STATE_SUSPEND_UNSUPPORTED;
      Log.d("Unable to suspend video. Release MediaPlayer.");
    }
  }

  public void resume() {
    if (mSurfaceHolder == null && mCurrentState == STATE_SUSPEND) {
      mTargetState = STATE_RESUME;
    } else if (mCurrentState == STATE_SUSPEND_UNSUPPORTED) {
      openVideo();
    }
  }

  public long getDuration() {
    if (isInPlaybackState()) {
      if (mDuration > 0)
        return mDuration;
      mDuration = mMediaPlayer.getDuration();
      return mDuration;
    }
    mDuration = -1;
    return mDuration;
  }
  public long localPosition = 0;
  public long getCurrentPosition() {
    if (isInPlaybackState()){
    	if(playMode==MODE_LOCAL){ localPosition = mMediaPlayer.getCurrentPosition();}
      return mMediaPlayer.getCurrentPosition();
    }
    return 0;
  }

  public void seekTo(long msec) {
//	Log.e("cyj seek To Time = " + msec);
	if(playMode == MODE_LIVE){
		return;
	}
//	Log.e("cyj isInPlaybackState() = " + isInPlaybackState());
    if (isInPlaybackState()) {
//      Log.e("cyj seek To Time 1 = " + msec);
      mMediaPlayer.seekTo(msec);
      mSeekWhenPrepared = 0;
    } else {
      mSeekWhenPrepared = msec;
    }
    mLifeCycleListener.onSeekTo(this, mMediaPlayer, mDuration);
  }

  public void seekToNext(){
	  if(playMode == MODE_LOCAL){
		  seekTo(localPosition+20*2000);
	  };
  }
  public boolean isPlaying() {
    return isInPlaybackState() && mMediaPlayer.isPlaying();
  }

  public int getBufferPercentage() {
    if (mMediaPlayer != null)
      return mCurrentBufferPercentage;
    return 0;
  }

  public void setVolume(float leftVolume, float rightVolume) {
    if (mMediaPlayer != null)
      mMediaPlayer.setVolume(leftVolume, rightVolume);
  }

  public int getVideoWidth() {
    return mVideoWidth;
  }

  public int getVideoHeight() {
    return mVideoHeight;
  }

  public float getVideoAspectRatio() {
    return mVideoAspectRatio;
  }
  
  /**
   * Must set before {@link #setVideoURI}
   * @param chroma
   */
  public void setVideoChroma(int chroma) {
    getHolder().setFormat(chroma == MediaPlayer.VIDEOCHROMA_RGB565 ? PixelFormat.RGB_565 : PixelFormat.RGBA_8888); // PixelFormat.RGB_565
    mVideoChroma = chroma;
  }
  
  public void setHardwareDecoder(boolean hardware) {
    mHardwareDecoder= hardware;
  }
  
  public void setVideoQuality(int quality) {
    if (mMediaPlayer != null)
      mMediaPlayer.setVideoQuality(quality);
  }
  
  public void setBufferSize(int bufSize) {
    mBufSize = bufSize;
  }

  public boolean isBuffering() {
    if (mMediaPlayer != null)
      return mMediaPlayer.isBuffering();
    return false;
  }

  public String getMetaEncoding() {
    if (mMediaPlayer != null)
      return mMediaPlayer.getMetaEncoding();
    return null;
  }

  public void setMetaEncoding(String encoding) {
    if (mMediaPlayer != null)
      mMediaPlayer.setMetaEncoding(encoding);
  }

  public SparseArray<MediaFormat> getAudioTrackMap(String encoding) {
    if (mMediaPlayer != null)
      return mMediaPlayer.findTrackFromTrackInfo(TrackInfo.MEDIA_TRACK_TYPE_AUDIO, mMediaPlayer.getTrackInfo(encoding));
    return null;
  }

  public int getAudioTrack() {
    if (mMediaPlayer != null)
      return mMediaPlayer.getAudioTrack();
    return -1;
  }

  public void setAudioTrack(int audioIndex) {
    if (mMediaPlayer != null)
      mMediaPlayer.selectTrack(audioIndex);
  }

  public void setTimedTextShown(boolean shown) {
    if (mMediaPlayer != null)
      mMediaPlayer.setTimedTextShown(shown);
  }

  public void setTimedTextEncoding(String encoding) {
    if (mMediaPlayer != null)
      mMediaPlayer.setTimedTextEncoding(encoding);
  }

  public int getTimedTextLocation() {
    if (mMediaPlayer != null)
      return mMediaPlayer.getTimedTextLocation();
    return -1;
  }

  public void addTimedTextSource(String subPath) {
    if (mMediaPlayer != null)
      mMediaPlayer.addTimedTextSource(subPath);
  }

  public String getTimedTextPath() {
    if (mMediaPlayer != null)
      return mMediaPlayer.getTimedTextPath();
    return null;
  }

  public void setSubTrack(int trackId) {
    if (mMediaPlayer != null)
      mMediaPlayer.selectTrack(trackId);
  }

  public int getTimedTextTrack() {
    if (mMediaPlayer != null)
      return mMediaPlayer.getTimedTextTrack();
    return -1;
  }

  public SparseArray<MediaFormat> getSubTrackMap(String encoding) {
    if (mMediaPlayer != null)
      return mMediaPlayer.findTrackFromTrackInfo(TrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT, mMediaPlayer.getTrackInfo(encoding));
    return null;
  }

  protected boolean isInPlaybackState() {
    return (mMediaPlayer != null && mCurrentState != STATE_ERROR && mCurrentState != STATE_IDLE && mCurrentState != STATE_PREPARING);
  }
  
  public MediaPlayer getMediaPlayer(){
	  return mMediaPlayer;
  }
  
  public interface LifeCycleListener{
	  void onStart(VideoView view, MediaPlayer mp, long duration);
	  void onPause(VideoView view, MediaPlayer mp, long duration);
	  void onSeekTo(VideoView view, MediaPlayer mp, long duration);
	  void onStop(VideoView view, MediaPlayer mp, long duration);
  }
  
  public static final int MODE_NET = 0;
  public static final int MODE_LOCAL = 1;
  public static final int MODE_LIVE = 2;
  private int playMode = MODE_NET;
  
  public void setMode(int playMode){
	  this.playMode = playMode;	  
  }
  
	public int getmVideoLayout() {
		return mVideoLayout;
	}

	public void setmVideoLayout(int mVideoLayout) {
		this.mVideoLayout = mVideoLayout;
	}
	
	private int acturalWidth = 1280;
	public void setActuralWidth(int width){
		acturalWidth = width;
	}
}