package com.brotherd.testapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_use_calendar.*
import pub.devrel.easypermissions.EasyPermissions

/**
 * Created by dumingwei on 2020/4/21
 *
 * Desc: 使用日历
 */
class UseCalendarActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {


    private val TAG: String? = "UseCalendarActivity"

    private val weekMillis: Long = 3600L * 24 * 1000 * 7
    val mPerms: ArrayList<String> = arrayListOf(Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR)

    companion object {

        fun launch(context: Context) {
            val intent = Intent(context, UseCalendarActivity::class.java)
            context.startActivity(intent)
        }
    }

    private val requestCode = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_use_calendar)
        btn_add.setOnClickListener {

            if (EasyPermissions.hasPermissions(this, mPerms.component1(), mPerms.component2())) {
                addCalendarEvent()

            } else {
                EasyPermissions.requestPermissions(this, "I need permission",
                        requestCode, Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR)
            }
        }

        btn_delete.setOnClickListener {

            if (EasyPermissions.hasPermissions(this, mPerms.component1(), mPerms.component2())) {
                deleteCalendarEvent()
            } else {
                EasyPermissions.requestPermissions(this, "I need permission",
                        requestCode, Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR)
            }
        }

        btn_update.setOnClickListener {

            if (EasyPermissions.hasPermissions(this, mPerms.component1(), mPerms.component2())) {
                updateCalendarEvent()
            } else {
                EasyPermissions.requestPermissions(this, "I need permission",
                        requestCode, Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR)
            }
        }

        btn_query.setOnClickListener {

            if (EasyPermissions.hasPermissions(this, mPerms.component1(), mPerms.component2())) {
                queryCalendarEvent()
            } else {
                EasyPermissions.requestPermissions(this, "I need permission",
                        requestCode, Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR)
            }
        }

        btn_add_by_intent.setOnClickListener {
            CalenderUtil.insertEventByIntent(this)
        }
        btn_update_by_intent.setOnClickListener {
            CalenderUtil.updateEventByIntent(this)
        }
    }

    private fun updateCalendarEvent() {
        val rows = CalendarReminderUtils.updateCalendarEvent(this, "日历事件", "新的日历事件",
                "新的日历描述")
        if (rows > 0) {
            Log.d(TAG, "updateCalendarEvent: rows =$rows")
            Toast.makeText(this, "更新成功", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "更新失败", Toast.LENGTH_SHORT).show()
        }

    }

    private fun queryCalendarEvent() {
        val exist = CalendarReminderUtils.existCalendarEvent(this, "日历事件")
        if (exist) {
            Toast.makeText(this, "存在", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "不存在", Toast.LENGTH_SHORT).show()
        }

    }

    private fun deleteCalendarEvent() {
        val rows = CalendarReminderUtils.deleteCalendarEvent(this, "日历事件")
        if (rows > 0) {
            Log.d(TAG, "deleteCalendarEvent: rows =$rows")
            Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "删除失败", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addCalendarEvent() {
        /*val success = CalendarReminderUtils.addCalendarEvent(this, "日历事件", "日历事件描述",
                System.currentTimeMillis() + weekMillis, 7)
        if (success) {
            Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show()

        } else {
            Toast.makeText(this, "添加失败", Toast.LENGTH_SHORT).show()
        }*/


        CalenderUtil.addCalendarEvent(this, "日历事件标题")

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (perms.containsAll(mPerms)) {
            //获得了所有权限
            Toast.makeText(this, "已经获取了所有权限", Toast.LENGTH_SHORT).show()
        }
    }


}
