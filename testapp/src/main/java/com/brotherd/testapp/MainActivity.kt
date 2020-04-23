package com.brotherd.testapp

import android.annotation.TargetApi
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by dumingwei on 2020/4/21
 *
 * Desc:
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnUseContentProvider.setOnClickListener {
            ContentProviderBaseActivity.launch(this)
        }
        btnUseCalendar.setOnClickListener {
            UseCalendarActivity.launch(this)
        }

        btnTestTargetApi.setOnClickListener {
            testTargetApi()
        }
    }


    @TargetApi(Build.VERSION_CODES.P)
    fun testTargetApi() {
        Toast.makeText(this, "版本：" + Build.VERSION.SDK_INT, Toast.LENGTH_SHORT).show()
    }
}
