package com.brotherd.testapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.CalendarContract;
import android.text.TextUtils;
import android.util.Log;

import java.util.Calendar;
import java.util.TimeZone;

public class CalendarReminderUtils {

    private static final String TAG = "CalendarReminderUtils";

    private static Uri CALENDER_URL = CalendarContract.Calendars.CONTENT_URI;
    private static Uri CALENDER_EVENT_URL = CalendarContract.Events.CONTENT_URI;
    private static Uri CALENDER_REMINDER_URL = CalendarContract.Reminders.CONTENT_URI;

    /**
     * 检查是否已经添加了日历账户，如果没有添加先添加一个日历账户再查询
     * 获取账户成功返回账户id，否则返回-1
     */
    private static int checkAndAddCalendarAccount(Context context) {
        int oldId = checkCalendarAccount(context);
        if (oldId >= 0) {
            return oldId;
        } else {
            long addId = addCalendarAccount(context);
            if (addId >= 0) {
                return checkCalendarAccount(context);
            } else {
                return -1;
            }
        }
    }

    /**
     * 检查是否存在现有账户，存在则返回账户id，否则返回-1
     */
    private static int checkCalendarAccount(Context context) {
        Cursor userCursor = context.getContentResolver().query(CALENDER_URL,
                null, null, null, null);
        try {
            if (userCursor == null) { //查询返回空值
                return -1;
            }
            int count = userCursor.getCount();
            if (count > 0) { //存在现有账户，取第一个账户的id返回
                userCursor.moveToFirst();
                return userCursor.getInt(userCursor.getColumnIndex(CalendarContract.Calendars._ID));
            } else {
                return -1;
            }
        } finally {
            if (userCursor != null) {
                userCursor.close();
            }
        }
    }

    /**
     * 添加日历账户，账户创建成功则返回账户id，否则返回-1
     */
    private static long addCalendarAccount(Context context) {
        TimeZone timeZone = TimeZone.getDefault();
        ContentValues value = new ContentValues();
        String CALENDARS_NAME = "boohee";
        value.put(CalendarContract.Calendars.NAME, CALENDARS_NAME);
        String CALENDARS_ACCOUNT_NAME = "BOOHEE@boohee.com";
        value.put(CalendarContract.Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME);
        String CALENDARS_ACCOUNT_TYPE = "com.android.boohee";
        value.put(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE);
        String CALENDARS_DISPLAY_NAME = "BOOHEE账户";
        value.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CALENDARS_DISPLAY_NAME);
        value.put(CalendarContract.Calendars.VISIBLE, 1);
        value.put(CalendarContract.Calendars.CALENDAR_COLOR, Color.BLUE);
        value.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
        value.put(CalendarContract.Calendars.SYNC_EVENTS, 1);
        value.put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, timeZone.getID());
        value.put(CalendarContract.Calendars.OWNER_ACCOUNT, CALENDARS_ACCOUNT_NAME);
        value.put(CalendarContract.Calendars.CAN_ORGANIZER_RESPOND, 0);

        Uri calendarUri = CALENDER_URL;
        calendarUri = calendarUri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE)
                .build();

        Uri result = context.getContentResolver().insert(calendarUri, value);
        long id = result == null ? -1 : ContentUris.parseId(result);
        return id;
    }

    /**
     * 添加日历事件
     */
    public static boolean addCalendarEvent(Context context, String title, String description,
                                           long reminderTime, int previousDate) {
        if (context == null) {
            return false;
        }
        int calId = checkAndAddCalendarAccount(context); //获取日历账户的id
        if (calId < 0) { //获取账户id失败直接返回，添加日历事件失败
            Log.d(TAG, "addCalendarEvent: 不存在日历账户");
            return false;
        }

        //添加日历事件
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(reminderTime);//设置开始时间
        long start = mCalendar.getTime().getTime();
        mCalendar.setTimeInMillis(start + 10 * 60 * 1000);//设置终止时间，开始时间加10分钟
        long end = mCalendar.getTime().getTime();
        ContentValues event = new ContentValues();
        event.put("title", title);
        event.put("description", description);
        event.put("calendar_id", calId); //插入账户的id
        event.put(CalendarContract.Events.DTSTART, start);
        event.put(CalendarContract.Events.DTEND, end);
        event.put(CalendarContract.Events.HAS_ALARM, 1);//设置有闹钟提醒
        event.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Shanghai");//这个是时区，必须有
        event.put(CalendarContract.Events.RRULE, "FREQ=DAILY;COUNT=28");
        Uri newEvent = context.getContentResolver().insert(CALENDER_EVENT_URL, event); //添加事件
        if (newEvent == null) { //添加日历事件失败直接返回
            Log.d(TAG, "addCalendarEvent: 添加日历事件失败");
            return false;
        }

        //事件提醒的设定
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Reminders.EVENT_ID, ContentUris.parseId(newEvent));
        values.put(CalendarContract.Reminders.MINUTES, previousDate * 24 * 60);// 提前previousDate天有提醒
        values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        Uri uri = context.getContentResolver().insert(CALENDER_REMINDER_URL, values);
        //添加事件提醒失败直接返回
        return uri != null;
    }

    /**
     * 查询日历事件
     */
    public static boolean existCalendarEvent(Context context, String title) {
        if (context == null || TextUtils.isEmpty(title)) {
            return false;
        }
        String[] projection = new String[]{
                CalendarContract.Instances.TITLE
        };

        // 匹配条件
        String selection = "(" + CalendarContract.Events.TITLE + " = ?)";
        String[] selectionArgs = new String[]{String.valueOf(title)};


        Cursor eventCursor = context.getContentResolver().query(CALENDER_EVENT_URL,
                projection, selection, selectionArgs, null);
        try {
            if (eventCursor == null) { //查询返回空值
                return false;
            }
            if (eventCursor.getCount() > 0) {
                Log.d(TAG, "existCalendarEvent: " + eventCursor.getCount());
                //遍历所有事件，找到title跟需要查询的title一样的项
                for (eventCursor.moveToFirst(); !eventCursor.isAfterLast(); eventCursor.moveToNext()) {
                    String eventTitle = eventCursor.getString(eventCursor.getColumnIndex("title"));
                    if (title.equals(eventTitle)) {
                        return true;
                    }
                }
            }
            return false;
        } finally {
            if (eventCursor != null) {
                eventCursor.close();
            }
        }
    }

    /**
     * 删除日历事件
     */
    public static int deleteCalendarEvent(Context context, String title) {
        if (context == null) {
            return -1;
        }
        Cursor eventCursor = context.getContentResolver().query(CALENDER_EVENT_URL,
                null, null, null, null);
        try {
            if (eventCursor == null) { //查询返回空值
                return -1;
            }
            if (eventCursor.getCount() > 0) {
                //遍历所有事件，找到title跟需要查询的title一样的项
                for (eventCursor.moveToFirst(); !eventCursor.isAfterLast(); eventCursor.moveToNext()) {
                    String eventTitle = eventCursor.getString(eventCursor.getColumnIndex("title"));
                    if (!TextUtils.isEmpty(title) && title.equals(eventTitle)) {
                        int id = eventCursor.getInt(eventCursor.getColumnIndex(CalendarContract.Calendars._ID));//取得id
                        Uri deleteUri = ContentUris.withAppendedId(CALENDER_EVENT_URL, id);
                        int affectRows = context.getContentResolver().delete(deleteUri, null, null);
                        return affectRows;
                    }
                }
            }
            return -1;
        } finally {
            if (eventCursor != null) {
                eventCursor.close();
            }
        }
    }

    /**
     * 更新日历事件
     */
    public static int updateCalendarEvent(Context context, String oldTitle,
                                          String newTitle, String description) {
        if (context == null) {
            return -1;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put("title", newTitle);
        contentValues.put("description", description);

        // 匹配条件
        String selection = "(" + CalendarContract.Events.TITLE + " = ?)";
        String[] selectionArgs = new String[]{String.valueOf(oldTitle)};

        int rows = context.getContentResolver().update(CALENDER_EVENT_URL, contentValues, selection, selectionArgs);
        return rows;
    }

}