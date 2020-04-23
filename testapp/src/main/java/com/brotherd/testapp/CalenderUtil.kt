package com.brotherd.testapp

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.CalendarContract
import android.util.Log
import java.util.*

/**
 * Created by dumingwei on 2020/4/23.
 *
 * Desc:
 */
class CalenderUtil {

    companion object {

        private val TAG: String? = "CalenderUtil"

        private val EVENT_PROJECTION: Array<String> = arrayOf(
                CalendarContract.Calendars._ID,                     // 0
                CalendarContract.Calendars.ACCOUNT_NAME,            // 1
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,   // 2
                CalendarContract.Calendars.OWNER_ACCOUNT            // 3
        )

        // The indices for the projection array above.
        private val PROJECTION_ID_INDEX: Int = 0
        private val PROJECTION_ACCOUNT_NAME_INDEX: Int = 1
        private val PROJECTION_DISPLAY_NAME_INDEX: Int = 2
        private val PROJECTION_OWNER_ACCOUNT_INDEX: Int = 3

        @JvmStatic
        fun queryCalendar(context: Context) {
            // Run query
            val uri: Uri = CalendarContract.Calendars.CONTENT_URI
            val selection: String = "((${CalendarContract.Calendars.ACCOUNT_NAME} = ?) AND (" +
                    "${CalendarContract.Calendars.ACCOUNT_TYPE} = ?) AND (" +
                    "${CalendarContract.Calendars.OWNER_ACCOUNT} = ?))"
            val selectionArgs: Array<String> = arrayOf("hera@example.com", "com.example", "hera@example.com")
            val cur: Cursor? = context.contentResolver.query(uri, EVENT_PROJECTION, selection, selectionArgs, null)
            cur?.let {
                while (cur.moveToNext()) {
                    val calID: Long = cur.getLong(PROJECTION_ID_INDEX)
                    val displayName: String = cur.getString(PROJECTION_DISPLAY_NAME_INDEX)
                    val accountName: String = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX)
                    val ownerName: String = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX)
                    Log.d(TAG, "query: $calID,$displayName,$accountName,$ownerName")
                }
            }

            cur?.close()
        }

        @JvmStatic
        fun addCalendarEvent(context: Context, title: String) {
            val calID: Long = 3
            val startMillis: Long = Calendar.getInstance().run {
                set(2020, 4, 25, 7, 30)
                timeInMillis
            }
            val endMillis: Long = Calendar.getInstance().run {
                set(2020, 4, 25, 8, 45)
                timeInMillis
            }

            val TIME_ZONE = TimeZone.getDefault().id
            Log.d(TAG, "addCalendarEvent: TIME_ZONE = $TIME_ZONE")
            val values = ContentValues().apply {
                put(CalendarContract.Events.DTSTART, startMillis)
                put(CalendarContract.Events.DTEND, endMillis)
                put(CalendarContract.Events.TITLE, title)
                put(CalendarContract.Events.DESCRIPTION, "Group workout")
                put(CalendarContract.Events.CALENDAR_ID, calID)
                put(CalendarContract.Events.EVENT_TIMEZONE, TIME_ZONE)
                put(CalendarContract.Events.RRULE, "FREQ=DAILY;COUNT=2")
            }

            val uri: Uri? = context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)

            val eventID: Long = uri?.lastPathSegment?.toLong() ?: return

            Log.d(TAG, "addCalendarEvent: eventID = $eventID")
            val remindersValues = ContentValues().apply {
                put(CalendarContract.Reminders.MINUTES, 15)
                put(CalendarContract.Reminders.EVENT_ID, eventID)
                put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT)
            }
            val remindersUri: Uri? = context.contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, remindersValues)
            Log.d(TAG, "addCalendarEvent: remindersUri = $remindersUri")

        }

        @JvmStatic
        fun insertEventByIntent(context: Context) {
            val startMillis: Long = Calendar.getInstance().run {
                set(2012, 0, 19, 7, 30)
                timeInMillis
            }
            val endMillis: Long = Calendar.getInstance().run {
                set(2012, 0, 19, 8, 30)
                timeInMillis
            }
            val intent = Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
                    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis)
                    .putExtra(CalendarContract.Events.TITLE, "Yoga")
                    .putExtra(CalendarContract.Events.DESCRIPTION, "Group class")
                    .putExtra(CalendarContract.Events.EVENT_LOCATION, "The gym")
                    .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
                    .putExtra(Intent.EXTRA_EMAIL, "rowan@example.com,trevor@example.com")
            context.startActivity(intent)
        }

        /**
         * 从哪里得到插入的eventID呢？
         */
        @JvmStatic
        fun updateEventByIntent(context: Context) {
            val eventID: Long = 208
            //val uri: Uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID)
            val uri: Uri = CalendarContract.Events.CONTENT_URI
            val intent = Intent(Intent.ACTION_EDIT)
                    .setData(uri)
                    .putExtra(CalendarContract.Events.TITLE, "My New Title")
            context.startActivity(intent)
        }

    }

}