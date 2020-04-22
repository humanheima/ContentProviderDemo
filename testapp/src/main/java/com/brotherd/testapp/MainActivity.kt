package com.brotherd.testapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
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
    }
}
