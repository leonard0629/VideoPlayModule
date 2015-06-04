package com.jiuguo.module.db;

import com.jiuguo.app.db.DatabaseManager;
import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;

/**
 * 数据操作模块
 * Created by leonard on 2015/6/2.
 */
public class DataModule extends UZModule {

    private DatabaseManager dbManager;

    public DataModule(UZWebView webView) {
        super(webView);
    }

}
