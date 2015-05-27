package com.jiuguo.module.video;

import android.content.Intent;
import android.webkit.JavascriptInterface;
import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

/**
 * Created by leonard on 2015/5/20.
 */
public class VideoPlayModule extends UZModule {

    public VideoPlayModule(UZWebView webView) {
        super(webView);
    }

    @JavascriptInterface
    public void jsmethod_startActivity(UZModuleContext moduleContext) {
        Intent intent = new Intent(getContext(), VideoPlayActivity.class);

        String videoUrl = moduleContext.optString("videoUrl");
        intent.putExtra("videoUrl", videoUrl);

        startActivity(intent);
    }
}
