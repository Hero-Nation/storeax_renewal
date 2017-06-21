package net.heronattion.solowin.util;


import android.content.Context;
import android.util.Log;

import net.heronattion.solowin.R;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class DateTime implements Serializable, Cloneable, Comparable<DateTime> {

    /**
     *
     */
    private static final long serialVersionUID = 8887243500047069925L;

    public static final int SUNDAY = 1;
    public static final int MONDAY = 2;
    public static final int TUESDAY = 3;
    public static final int WEDNESDAY = 4;
    public static final int THURSDAY = 5;
    public static final int FRIDAY = 6;
    public static final int SATURDAY = 7;

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public final Calendar mCalendar = Calendar.getInstance();
    private final static SimpleDateFormat simpleYearDateFormat = new SimpleDateFormat("yy-MM-dd");
    private final static SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final static SimpleDateFormat sDateDotFormat = new SimpleDateFormat("yyyy.MM.dd");
    private final static SimpleDateFormat sMonthDayFormat = new SimpleDateFormat("MM/dd");
    private final static SimpleDateFormat sMonthDayFormatWithTime = new SimpleDateFormat("M월 dd일 a h:mm");
    private final static SimpleDateFormat sDayOfWeekFormat = new SimpleDateFormat("E요일");
    private final static SimpleDateFormat sTimeFormat = new SimpleDateFormat("HH:mm");
    private final static SimpleDateFormat sDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private final static SimpleDateFormat sDateTimeAFormat = new SimpleDateFormat("yyyy-MM-dd a hh:mm");
    private final static SimpleDateFormat sDisplayDateTimeFormat = new SimpleDateFormat("yyyy년 M월 d일 (E)");
    private final static SimpleDateFormat sDateTimeSecFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final static SimpleDateFormat sqliteDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

    private final static SimpleDateFormat sTimeFormatWithA = new SimpleDateFormat("a h:mm");
    private boolean mDateOnly = false;

    public DateTime()
    {
        mCalendar.set(Calendar.HOUR_OF_DAY, 0);
        mCalendar.set(Calendar.MINUTE, 0);
        mCalendar.set(Calendar.SECOND, 0);
        mCalendar.set(Calendar.MILLISECOND, 0);
    }


    public DateTime(Calendar calendar)
    {
        this();
        mCalendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
    }

    public DateTime(DateTime date)
    {
        this();
        mCalendar.set(date.getYear(), date.getMonth()-1, date.getDay(), date.getHour(), date.getMinute());
    }

    public DateTime(Date date)
    {
        this();
        mCalendar.setTime(date);
    }

    public DateTime(int year, int month, int day)
    {
        this();
        mCalendar.set(year, month, day);
        mDateOnly = true;
    }

    public DateTime(int year, int month, int day, int hour, int minute)
    {
        this();
        mCalendar.set(year, month, day, hour, minute);
        mDateOnly = false;
    }

    public boolean isDateOnly() {
        return mDateOnly;
    }

    public void setDateOnly(boolean dateOnly) {
        this.mDateOnly = dateOnly;
    }

    public void addYear(int year) {
        mCalendar.add(Calendar.YEAR, year);
    }

    public void addMonth(int month) {
        mCalendar.add(Calendar.MONTH, month);
    }

    public void addDay(int day) {
        mCalendar.add(Calendar.DAY_OF_MONTH, day);
    }

    public void addHour(int hour) {
        mCalendar.add(Calendar.HOUR_OF_DAY, hour);
    }

    public void addMinute(int minute) {
        mCalendar.add(Calendar.MINUTE, minute);
    }

    public int get(int field) {
        int value = mCalendar.get(field);
        if (field == Calendar.MONTH)
            value++;
        return value;
    }
    public void set(int field, int value) {
        mCalendar.set(field, value);
    }
    public void add(int field, int value) {
        mCalendar.add(field, value);
    }
    public int getYear() {
        return mCalendar.get(Calendar.YEAR);
    }

    public int getMonth() {
        return mCalendar.get(Calendar.MONTH) + 1;
    }

    public int getDay() {
        return mCalendar.get(Calendar.DAY_OF_MONTH);
    }

    public int getDayOfWeek() {
        return mCalendar.get(Calendar.DAY_OF_WEEK);
    }

    public int getHour() {
        return mCalendar.get(Calendar.HOUR_OF_DAY);
    }

    public int getMinute() {
        return mCalendar.get(Calendar.MINUTE);
    }

    public long getTimeInMillis() {
        return mCalendar.getTimeInMillis();
    }

    private long getTotalDays() {
        return (((mCalendar.getTimeInMillis() / 1000) / 60) / 60) / 24;
    }

    private long getTotalMinutes() {
        return (mCalendar.getTimeInMillis() / 1000) / 60;
    }

    public void setYear(int year) {
        mCalendar.set(Calendar.YEAR, year);
    }

    public void setMonth(int month) {
        mCalendar.set(Calendar.MONTH, month - 1);
    }

    public void setDayOfMonth(int day) {
        mCalendar.set(Calendar.DAY_OF_MONTH, day);
    }

    public void setHour(int hour) {
        mCalendar.set(Calendar.HOUR_OF_DAY, hour);
    }

    public void setMinute(int minute) {
        mCalendar.set(Calendar.MINUTE, minute);
    }
    public void setSeconds(int second) {
        mCalendar.set(Calendar.SECOND, second);
    }

    public boolean equalDay(DateTime other) {
        return this.getYear() == other.getYear() &&
                this.getMonth() == other.getMonth() &&
                this.getDay() == other.getDay();
    }

    public static DateTime parse(String format) {
        if (format == null)
            return null;
        DateTime result = null;
        try {
            Date date = sDateTimeFormat.parse(format);

            result = new DateTime(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }
    public static DateTime parseSqlite(String format) {
        if (format == null)
            return null;
        DateTime result = null;
        try {
            Date date = sqliteDateTimeFormat.parse(format);
            result = new DateTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static DateTime parseDate(String format) {
        if (format == null)
            return null;
        DateTime result = null;
        try {
            Date date = sDateFormat.parse(format);
            result = new DateTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Date toDate() {
        return mCalendar.getTime();
    }

    public String toDateString() {
        return sDateFormat.format(toDate());
    }


    public String toSimpleYearDateString() {
        return sDateDotFormat.format(toDate());
    }

    public String toMonthDayString() {
        return sMonthDayFormat.format(toDate());
    }

    public String toDayOfWeekString() {
        return sDayOfWeekFormat.format(toDate());
    }

    public String toTimeString() {
        return sTimeFormat.format(toDate());
    }

    public String toDateTimeString() {
        return sDateTimeFormat.format(toDate());
    }

    public String toDateTimeAString() {
        return sDateTimeAFormat.format(toDate());
    }

    public String toDisplayDateTimeString() {
        return sDisplayDateTimeFormat.format(toDate());
    }

    public String toDateTimeSecString() {
        return sDateTimeSecFormat.format(toDate());
    }

    @Override
    public String toString() {
        if (mDateOnly)
            return toDateString();
        else
            return toDateTimeString();
    }

    public String toAgoStringFromNow(Context context) {

        Calendar nowCal = Calendar.getInstance();
        long nowCalTimeInMillis = nowCal.getTimeInMillis();

        // TODO: localize
        final long diff = nowCalTimeInMillis - mCalendar.getTimeInMillis();
        if (diff < MINUTE_MILLIS) {
            return context.getResources().getString(R.string.just_now);
        } else if (diff < 2 * MINUTE_MILLIS) {
            return context.getResources().getString(R.string.a_minute_ago);
        } else if (diff < 50 * MINUTE_MILLIS) {
            return String.format(context.getResources().getString(R.string.minutes_ago_format), diff / MINUTE_MILLIS);
        } else if (diff < 90 * MINUTE_MILLIS) {
            return context.getResources().getString(R.string.a_hour_ago);
        } else if (diff < 24 * HOUR_MILLIS) {
            return String.format(context.getResources().getString(R.string.hours_ago_format), diff / HOUR_MILLIS);
        } else {

            nowCal.set(Calendar.HOUR_OF_DAY, 0);
            nowCal.set(Calendar.MINUTE, 0);
            nowCal.set(Calendar.SECOND, 0);
            nowCal.set(Calendar.MILLISECOND, 0);

            Calendar tempCal = Calendar.getInstance();

            tempCal.set(Calendar.YEAR, mCalendar.get(Calendar.YEAR));
            tempCal.set(Calendar.MONTH, mCalendar.get(Calendar.MONTH));
            tempCal.set(Calendar.DAY_OF_MONTH, mCalendar.get(Calendar.DAY_OF_MONTH));

            tempCal.set(Calendar.HOUR_OF_DAY, 0);
            tempCal.set(Calendar.MINUTE, 0);
            tempCal.set(Calendar.SECOND, 0);
            tempCal.set(Calendar.MILLISECOND, 0);

            long startTime = nowCal.getTimeInMillis();
            long endTime = tempCal.getTimeInMillis();
            long diffTime = startTime - endTime;
            long diffDays = diffTime / (1000 * 60 * 60 * 24);

            Log.d("diffDay", diffDays+ "일");
            if (diffDays == 1) {
                return String.format(context.getResources().getString(R.string.a_day_ago_and_time_format), sTimeFormatWithA.format(toDate()));

            }
            else {

                return sMonthDayFormatWithTime.format(toDate());
            }

        }
    }

    public String toDueDateStringFromNow(Context context) {

        long now = Calendar.getInstance().getTimeInMillis();

        // TODO: localize
        final long diff = mCalendar.getTimeInMillis() - now;

        if (diff <= 0) {
            return context.getResources().getString(R.string.due_date_over);
        }
        else {
            if (diff < 50 * MINUTE_MILLIS) {
                return context.getResources().getString(R.string.due_date_minutes);
            } else if (diff < 90 * MINUTE_MILLIS) {
                return context.getResources().getString(R.string.due_date_a_hour_ago);
            } else if (diff < 24 * HOUR_MILLIS) {
                return String.format(context.getResources().getString(R.string.due_date_hours_ago_format), diff / HOUR_MILLIS);
            } else if (diff < 48 * HOUR_MILLIS) {
                return context.getResources().getString(R.string.due_date_a_day_ago);
            }
            else {
                return String.format(context.getResources().getString(R.string.due_date_days_ago_format), diff / DAY_MILLIS);

            }
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long total = getTotalMinutes();
        result = prime * result + (int) (total ^ (total >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DateTime other = (DateTime) obj;
        return getTotalMinutes() == other.getTotalMinutes();
    }

    @Override
    public int compareTo(DateTime another) {
        long cmp = getTotalMinutes() - another.getTotalMinutes();
        if (cmp < 0)
            return -1;
        else if (cmp > 0)
            return 1;
        else
            return 0;
    }


}