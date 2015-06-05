package com.jiuguo.app.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import com.uzmap.pkg.uzcore.UZResourcesIDFinder;

import java.io.File;
import java.io.IOException;

/**
 * Created by leonard on 2015/6/3.
 */
public class CommonUtils {

    private final static String TAG = "CommonUtils";

    private final static String CFG_SETTING = "setting.cfg";
    private static Toast toast = null;

    public static int getLoginUserId(Context context) {
        return -1;
    }

    public static String getLoginUserToken(Context context) {
        return "";
    }

    /**
     * 初始化app文件夹
     */
    public static void initAppFolder(Context context) {
        File file = new File(CommonUtils.getImageSharePath());
        if (!file.exists()) {
            file.mkdirs();
        }

        file = new File(CommonUtils.getVideoSavePath());
        if (!file.exists()) {
            file.mkdirs();
        }

        file = new File(CommonUtils.getVideoSavePath());
        if (!file.exists()) {
            file.mkdirs();
        }

        int logoId = UZResourcesIDFinder.getResDrawableID("ic_launcher");
        if(logoId > 0) {
            Bitmap bitmap = ImageUtils.drawableToBitmap(context.getResources().getDrawable(logoId));
            try {
                ImageUtils.saveImageToSD(context, CommonUtils.getImageSharePath() + "share.jpg", bitmap, 100);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "名为：ic_launcher的drawable不存在!请检查代码");
        }
    }

    /**
     * 设置默认视频清晰度
     * @param resolution
     * @return
     */
    public static void setResolution(Context context, int resolution) {
        SharedPreferences sp = context.getSharedPreferences(CFG_SETTING, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("resolution", resolution);
        editor.commit();
    }

    /**
     * 读取默认视频清晰度
     * @param defaultResolution
     * @return
     */
    public static int getResolution(Context context, int defaultResolution) {
        SharedPreferences sp = context.getSharedPreferences(CFG_SETTING, Context.MODE_PRIVATE);
        return sp.getInt("resolution", defaultResolution);
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
    public static void toast(Context context, String msg, int duration) {
        if(toast == null) {
            toast = Toast.makeText(context, msg, duration);
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
    public static void toast(Context context, int msgId, int duration) {
        if(toast == null) {
            toast = Toast.makeText(context, msgId, duration);
        } else {
            toast.setText(msgId);
            toast.setDuration(duration);
        }
        toast.show();
    }

}