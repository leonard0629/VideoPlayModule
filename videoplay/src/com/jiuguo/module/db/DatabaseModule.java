package com.jiuguo.module.db;

import com.jiuguo.app.db.DatabaseManager;
import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;

/**
 * 数据库操作模块
 * Created by leonard on 2015/6/2.
 */
public class DatabaseModule extends UZModule {

    private DatabaseManager dbManager;

    public DatabaseModule(UZWebView webView) {
        super(webView);
    }

}
