package com.jiuguo.module.video;

import android.content.Intent;
import android.webkit.JavascriptInterface;
import com.jiuguo.app.bean.Video;
import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;
import org.json.JSONObject;

/**
 * 视频播放模块
 * Created by leonard on 2015/5/20.
 */
public class VideoPlayModule extends UZModule {

    public VideoPlayModule(UZWebView webView) {
        super(webView);
    }

    @JavascriptInterface
    public void jsmethod_startActivity(UZModuleContext moduleContext) {
        Intent intent = new Intent(getContext(), VideoPlayActivity.class);

        int mode = moduleContext.optInt("mode");
        JSONObject jsonObject = moduleContext.optJSONObject("video");
        Video video = com.alibaba.fastjson.JSONObject.parseObject(jsonObject.toString(), Video.class);
        intent.putExtra("mode", mode);
        intent.putExtra("video", video);

        startActivity(intent);
    }
}
