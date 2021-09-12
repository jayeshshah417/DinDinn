package com.jayesh.dindinntest.utils

import android.util.Log
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

object DateTimeUtils {
    val TAG = DateTimeUtils::class.java.simpleName
    const val DEFAULT_FORMAT = "yyyy-MM-dd'T'HH:mm+00'Z'"
    const val DEFAULT_FORMAT_SECS = "yyyy-MM-dd'T'HH:mm:ss+00'Z'"
    const val DEFAULT_DATE_FORMAT = "yyyy-MM-dd"
    const val DEFAULT_TIME_FORMAT = "HH:mm:ss"
    const val DEFAULT_TIME_FORMAT_AM_PM = "HH:mm aa"

    /**
     * Return Current date string in "yyyy-MM-dd HH:mm:ss" format
     *
     * @return current date string (Default timezone)
     */
    val date: String
        get() = getDate(Date(), DEFAULT_FORMAT)

    /**
     * Returns current date string in given format
     *
     * @param format, date format
     * @return current date string (Default timezone)
     */
    fun getDate(format: String?): String {
        return getDate(Date(), format)
    }

    /**
     * Returns current date string in given format
     *
     * @param date,          date object
     * @param defaultFormat, date format
     * @return current date string (default timezone)
     */
    fun getDate(date: Date?, defaultFormat: String?): String {
        return createDate(date, defaultFormat, false)
    }

    /**
     * Returns UTC date string in "yyyy-MM-dd HH:mm:ss" format.
     *
     * @return string, UTC Date
     */
    val uTCDate: String
        get() = getUTCDate(Date(), DEFAULT_FORMAT)

    /**
     * Return UTC date in given format
     *
     * @param format, date format
     * @return UTC date string
     */
    fun getUTCDate(format: String?): String {
        return getUTCDate(Date(), format)
    }

    /**
     * Returns UTC Date string in given date format
     *
     * @param date,          Date object
     * @param defaultFormat, Date pattern format
     * @return UTC date string
     */
    fun getUTCDate(date: Date?, defaultFormat: String?): String {
        return createDate(date, defaultFormat, true)
    }
    /**
     * Convert UTC date to default timezone
     *
     * @param date       UTC date string
     * @param dateFormat default date format
     * @param toFormat   converting date format
     * @return string converted date string
     */
    /**
     * Convert UTC date to default timezone date
     *
     * @param date       date in string
     * @param dateFormat default date format
     * @return string converted date string
     */
    @JvmOverloads
    fun convertToDefault(
        date: String?,
        dateFormat: String?,
        toFormat: String? = dateFormat
    ): String {
        return createDate(createDateObject(date, dateFormat, false), toFormat, false)
    }
    /**
     * Convert default timezone date to UTC timezone
     *
     * @param date,      date in string
     * @param dateFormat default date format
     * @param toFormat   display format
     * @return string, returns string converted to UTC
     */
    /**
     * Convert to UTC date
     *
     * @param date       date in string
     * @param dateFormat default date format
     * @return string date string in UTC timezone
     */
    @JvmOverloads
    fun convertToUTC(date: String?, dateFormat: String?, toFormat: String? = dateFormat): String {
        return createDate(createDateObject(date, dateFormat, true), toFormat, true)
    }

    fun parseDate(date: String?, dateFormat: String?, toFormat: String?): String {
        return createDate(createDateObject(date, dateFormat, false), toFormat, true)
    }

    /**
     * Create Date instance from given date string.
     *
     * @param date               date in string
     * @param dateFormat,        original date format
     * @param hasDefaultTimezone if date is in default timezone than true, otherwise false
     * @return Date, returns Date object with given date
     */
    fun createDateObject(date: String?, dateFormat: String?, hasDefaultTimezone: Boolean?): Date? {
        var dateObj: Date? = null
        try {
            val simpleDateFormat = SimpleDateFormat(dateFormat)
            if (!hasDefaultTimezone!!) {
                simpleDateFormat.timeZone = TimeZone.getTimeZone("GMT")
            }
            dateObj = simpleDateFormat.parse(date)
        } catch (e: Exception) {
            Log.e(TAG, e.message!!)
        }
        return dateObj
    }

    /**
     * Returns date before given days
     *
     * @param days days to before
     * @return string date string before days
     */
    fun getDateBefore(days: Int): String {
        val today = Date()
        val cal: Calendar = GregorianCalendar()
        cal.time = today
        cal.add(Calendar.DAY_OF_MONTH, days * -1)
        val date = cal.time
        val gmtFormat = SimpleDateFormat()
        gmtFormat.applyPattern("yyyy-MM-dd 00:00:00")
        val gmtTime = TimeZone.getTimeZone("GMT")
        gmtFormat.timeZone = gmtTime
        return gmtFormat.format(date)
    }

    fun setDateTime(originalDate: Date?, hour: Int, minute: Int, second: Int): Date {
        val cal: Calendar = GregorianCalendar()
        cal.time = originalDate
        cal[Calendar.HOUR] = hour
        cal[Calendar.MINUTE] = minute
        cal[Calendar.SECOND] = second
        cal[Calendar.MILLISECOND] = 0
        return cal.time
    }

    fun getDateDayBeforeAfterUTC(utcDate: String?, days: Int): String {
        val dt = createDateObject(utcDate, DEFAULT_FORMAT, false)
        val cal: Calendar = GregorianCalendar()
        cal.time = dt
        cal.add(Calendar.DAY_OF_MONTH, days)
        return createDate(cal.time, DEFAULT_FORMAT, true)
    }

    fun getDateDayBefore(originalDate: Date?, days: Int): Date {
        val cal: Calendar = GregorianCalendar()
        cal.time = originalDate
        cal.add(Calendar.DAY_OF_MONTH, days * -1)
        return cal.time
    }

    fun getCurrentDateWithHour(addHour: Int): String {
        val cal = Calendar.getInstance()
        val hour = cal[Calendar.HOUR]
        cal[Calendar.HOUR] = hour + addHour
        val date = cal.time
        return createDate(date, DEFAULT_FORMAT, true)
    }

    fun getDateMinuteBefore(originalDate: Date?, minutes: Int): Date {
        val cal: Calendar = GregorianCalendar()
        cal.time = originalDate
        cal.add(Calendar.MINUTE, minutes * -1)
        return cal.time
    }

    private fun createDate(date: Date?, defaultFormat: String?, utc: Boolean): String {
        val gmtFormat = SimpleDateFormat()
        gmtFormat.applyPattern(defaultFormat)
        val gmtTime = if (utc) TimeZone.getTimeZone("GMT") else TimeZone.getDefault()
        gmtFormat.timeZone = gmtTime
        return gmtFormat.format(date)
    }

    fun floatToDuration(duration_in_float: String): String {
        var duration_in_float = duration_in_float
        duration_in_float = String.format("%2.2f", duration_in_float.toFloat())
        val parts = duration_in_float.split("\\.").toTypedArray()
        val minute = parts[0].toLong()
        val seconds = 60 * parts[1].toLong() / 100
        return String.format("%02d:%02d", minute, seconds)
    }

    fun durationToFloat(duration: String): String {
        val parts = duration.split("\\:").toTypedArray()
        if (parts.size == 2) {
            var minute = parts[0].toLong()
            var seconds = parts[1].toLong()
            if (seconds == 60L) {
                minute = minute + 1
                seconds = 0
            } else {
                seconds = 100 * seconds / 60
            }
            return String.format("%d.%d", minute, seconds)
        }
        return "false"
    }

    fun durationToFloat(milliseconds: Long): String {
        var minute = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        var seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                TimeUnit.MINUTES.toSeconds(minute)
        if (seconds == 60L) {
            minute = minute + 1
            seconds = 0
        } else {
            seconds = 100 * seconds / 60
        }
        return String.format("%d.%d", minute, seconds)
    }

    @ExperimentalTime
    fun getDateDiff(format: String, currDate: String, expiredAt: String): Long {
        val simpleDateFormat = SimpleDateFormat(format)
        return try {
            DurationUnit.MILLISECONDS.convert(
                simpleDateFormat.parse(expiredAt).time - simpleDateFormat.parse(currDate).time,
                DurationUnit.MILLISECONDS
            )
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }
    @ExperimentalTime
    fun getDateDiff(currDate: String, expiredAt: String): Long {
        val simpleDateFormat = SimpleDateFormat(DEFAULT_FORMAT)
        return try {
            DurationUnit.MILLISECONDS.convert(
                simpleDateFormat.parse(expiredAt).time - simpleDateFormat.parse(currDate).time,
                DurationUnit.MILLISECONDS
            )
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    fun getDateFromMilli(milliSeconds: Long, dateFormat: String?): String? {
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat)

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }
}
