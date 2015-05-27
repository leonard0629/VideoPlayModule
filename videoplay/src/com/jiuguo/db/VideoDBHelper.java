package com.jiuguo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class VideoDBHelper extends SQLiteOpenHelper {
    private static final String TAG = "VideoDBHelper";

    public static final String DATABASE_NAME = "videoLoad.db";
    public static final int DATABASE_VERSION = 30;

    public static final String sql_video = "create table if not exists video ("
            + "_id integer primary key autoincrement,"
            + "videoid integer not null unique,"
            + "title text,"
            + "describe text,"
            + "duration integer,"
            + "postdate text,"
            + "posterid integer,"
            + "postername text,"
            + "imageUrl text,"
            + "playCount integer,"
            + "favourcount integer,"
            + "bookcount integer,"
            + "checkid integer,"
            + "fileurl integer,"
            + "downloadsize integer,"
            + "totalsize integer,"
            + "isfinish numeric,"
            + "isstart numeric,"
            + "downloadpart integer,"
            + "type integer,"
            + "isnew numeric);";

    public static final String record_video = "create table if not exists recordvideo ("
            + "_id integer primary key autoincrement,"
            + "videoid integer,"
            + "title text,"
            + "describe text,"
            + "duration integer,"
            + "postdate text,"
            + "posterid integer,"
            + "postername text,"
            + "imageUrl text,"
            + "playCount integer,"
            + "favourcount integer,"
            + "bookcount integer,"
            + "checkid integer,"
            + "posterlogo text,"
            + "recordtime integer,"
            + "lasttime text,"
            + "islive numeric);";

    public static final String load_video_partitions ="create table if not exists loadvideopart ("
            + " _id integer primary key autoincrement,"
            + " videoid integer not null,"
            + "part integer,"
            + "state integer,"
            + "url text,"
            + "totalsize integer,"
            + "downloadsize integer);";

    public static final String search_history = "create table if not exists searchhistory ("
            + "_id integer primary key autoincrement,"
            + "historyid text not null unique,"
            + "content text,"
            + "date text);";

    public VideoDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sql_video);
        db.execSQL(record_video);
        db.execSQL(search_history);
        db.execSQL(load_video_partitions);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints 开启外键约束
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "database upgrade");
    }
}
