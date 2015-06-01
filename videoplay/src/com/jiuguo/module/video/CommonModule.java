package com.jiuguo.module.video;

import android.webkit.JavascriptInterface;
import com.jiuguo.app.utils.CommonUtils;
import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

/**
 * 通用操作模块
 * Created by leonard on 2015/6/1.
 */
public class CommonModule extends UZModule {

    public CommonModule(UZWebView webView) {
        super(webView);
    }

    /**
     * <strong>函数</strong><br><br>
     * 该函数映射至Javascript中moduleDemo对象的getPrefs函数<br><br>
     * <strong>JS Example：</strong><br>
     * moduleDemo.getPrefs(argument);
     *
     * @param moduleContext (Required)
     */
    @JavascriptInterface
    public String jsmethod_getPrefs(UZModuleContext moduleContext) {
        String key = moduleContext.optString("key");
        String defaultValue = moduleContext.optString("defaultValue");

        return CommonUtils.getPrefs(moduleContext.getContext(), key, defaultValue);
    }

    /**
     * <strong>函数</strong><br><br>
     * 该函数映射至Javascript中moduleDemo对象的setPrefs函数<br><br>
     * <strong>JS Example：</strong><br>
     * moduleDemo.setPrefs(argument);
     *
     * @param moduleContext (Required)
     */
    @JavascriptInterface
    public void jsmethod_setPrefs(UZModuleContext moduleContext) {
        String key = moduleContext.optString("key");
        String value = moduleContext.optString("value");

        CommonUtils.setPrefs(moduleContext.getContext(), key, value);
    }

    /**
     * <strong>函数</strong><br><br>
     * 该函数映射至Javascript中moduleDemo对象的removePrefs函数<br><br>
     * <strong>JS Example：</strong><br>
     * moduleDemo.removePrefs(argument);
     *
     * @param moduleContext (Required)
     */
    @JavascriptInterface
    public void jsmethod_removePrefs(UZModuleContext moduleContext) {
        String key = moduleContext.optString("key");

        CommonUtils.removePrefs(moduleContext.getContext(), key);
    }

}
