package com.brotherd.contentproviderdemo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by dumingwei on 2017/5/18.
 */
public class MyDbHelper extends SQLiteOpenHelper {

    private static final String TAG = "MyDbHelper";

    public static final String CREATE_BOOK = "create table book (" +
            " id integer primary key autoincrement ," +
            "author text ," +
            "price real ," +
            "pages integer ," +
            "name text )";
    public static final String CREATE_CATEGORY = "create table category (" +
            " id integer primary key autoincrement ," +
            "category_name text ," +
            "category_code integer )";

    public MyDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BOOK);
        db.execSQL(CREATE_CATEGORY);
        Log.d(TAG, "SQLiteOpenHelper onCreate: ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
