package com.jiuguo.module.download;

import android.webkit.JavascriptInterface;
import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

/**
 * 视频缓存模块
 * Created by leonard on 2015/5/20.
 */
public class DownloadModule extends UZModule {

    public DownloadModule(UZWebView webView) {
        super(webView);
    }

    @JavascriptInterface
    public void jsmethod_startActivity(UZModuleContext moduleContext) {
    }
}
