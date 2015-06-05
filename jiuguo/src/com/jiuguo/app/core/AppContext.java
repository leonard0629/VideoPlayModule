package com.jiuguo.app.core;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import com.jiuguo.app.utils.ImageUtils;
import com.uzmap.pkg.uzcore.UZResourcesIDFinder;
import de.greenrobot.event.EventBus;

import java.io.File;
import java.io.IOException;

/**
 * Created by leonard on 2015/6/3.
 */
public class AppContext extends Application {

    private final static String TAG = "AppContext";

    private final static String CFG_SETTING = "setting.cfg";
    private static Toast toast = null;

    @Override
    public void onCreate() {
        super.onCreate();

        initAppFolder();

        EventBus.getDefault().register(this);
    }

    public int getLoginUserId() {
        return -1;
    }

    public String getLoginUserToken() {
        return "";
    }

    /**
     * 设置默认视频清晰度
     * @param resolution
     * @return
     */
    public void setResolution(int resolution) {
        SharedPreferences sp = getSharedPreferences(CFG_SETTING, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("resolution", resolution);
        editor.commit();
    }

    /**
     * 读取默认视频清晰度
     * @param defaultResolution
     * @return
     */
    public int getResolution(int defaultResolution) {
        SharedPreferences sp = getSharedPreferences(CFG_SETTING, Context.MODE_PRIVATE);
        return sp.getInt("resolution", defaultResolution);
    }

    /**
     * 设置SharedPreferences
     * @param key
     * @param value
     * @return
     */
    public void setPrefs(String key, String value) {
        SharedPreferences sp = getSharedPreferences(CFG_SETTING, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * 删除SharedPreferences
     * @param key
     * @return
     */
    public void removePrefs(String key) {
        SharedPreferences sp = getSharedPreferences(CFG_SETTING, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.commit();
    }

    /**
     * 缓存视频路径
     * @return
     */
    public static String getVideoSavePath() {
        return Environment.getExternalStorageDirectory() + File.separator + "jiuguo"
                + File.separator + "videos" + File.separator;
    }

    /**
     * 图片保存路径
     * @return
     */
    public static String getImageSharePath() {
        return Environment.getExternalStorageDirectory() + File.separator + "jiuguo"
                + File.separator + "shares" + File.separator;
    }

    /**
     * 临时缓存保存路径
     * @returns
     */
    public static String getCacheSavePath() {
        return Environment.getExternalStorageDirectory() + File.separator + "jiuguo"
                + File.separator + "cache" + File.separator;
    }

    /**
     * 提示
     * @param msg
     * @param duration
     */
    public void toast(String msg, int duration) {
        if(toast == null) {
            toast = Toast.makeText(this, msg, duration);
        } else {
            toast.setText(msg);
            toast.setDuration(duration);
        }
        toast.show();
    }

    /**
     * 提示
     * @param msgId
     * @param duration
     */
    public void toast(int msgId, int duration) {
        if(toast == null) {
            toast = Toast.makeText(this, msgId, duration);
        } else {
            toast.setText(msgId);
            toast.setDuration(duration);
        }
        toast.show();
    }

    /**
     * 初始化app文件夹
     */
    private void initAppFolder() {
        File file = new File(getImageSharePath());
        if (!file.exists()) {
            file.mkdirs();
        }

        file = new File(getVideoSavePath());
        if (!file.exists()) {
            file.mkdirs();
        }

        file = new File(getVideoSavePath());
        if (!file.exists()) {
            file.mkdirs();
        }

        int logoId = UZResourcesIDFinder.getResDrawableID("ic_launcher");
        if(logoId > 0) {
            Bitmap bitmap = ImageUtils.drawableToBitmap(getResources().getDrawable(logoId));
            try {
                ImageUtils.saveImageToSD(this, getImageSharePath() + "share.jpg", bitmap, 100);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "名为：ic_launcher的drawable不存在!请检查代码");
        }
    }
}
