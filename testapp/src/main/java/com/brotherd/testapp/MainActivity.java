package com.brotherd.testapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private String newsId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void add(View view) {
        Uri uri = Uri.parse("content://com.brotherd.contentproviderdemo.provider/book");
        ContentValues value = new ContentValues();
        value.put("name", "white ship");
        value.put("author", "don't know");
        value.put("pages", 130);
        value.put("price", 22.85);
        Uri newUri = getContentResolver().insert(uri, value);
        newsId = newUri.getPathSegments().get(1);
        Log.d(TAG, "onBtnAddClicked: newsId=" + newsId);
    }

    public void query(View view) {
        Uri uri = Uri.parse("content://com.brotherd.contentproviderdemo.provider/book");
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                int pages = cursor.getInt(cursor.getColumnIndex("pages"));
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                Log.d(TAG, "onBtnQueryClicked: name=" + name + ",pages=" + pages + ",id=" + id);
            }
            cursor.close();
        }
    }

    public void delete(View view) {
        Uri uri = Uri.parse("content://com.brotherd.contentproviderdemo.provider/book/" + newsId);
        getContentResolver().delete(uri, null, null);
        query(null);
    }

    public void update(View view) {
        Uri uri = Uri.parse("content://com.brotherd.contentproviderdemo.provider/book/");
        ContentValues value = new ContentValues();
        value.put("name", "android");
        int rows = getContentResolver().update(uri, value, null, null);
        Log.d(TAG, "onBtnUpdateClicked: rows=" + rows);
        query(null);
    }
}
