package com.cj.ticketsys.svc

import java.lang.Exception
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

object Utils {
    fun intToDate(date: Int): Date {
        val d = LocalDate.parse(date.toString(), DateTimeFormatter.BASIC_ISO_DATE)
        val instant = d.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()
        return Date.from(instant)
    }

    fun zeroToNowSeconds(date:Date): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        var minutes = calendar.get(Calendar.MINUTE)
        var seconds = calendar.get(Calendar.SECOND)
        return hour * 3600 + minutes * 60 + seconds
    }

    fun dateToYYYYMMDDInt(date: Date): Int {
        val d = dateZoneFormat(date,"yyyyMMdd")
        return d.toInt()
    }

    fun dateZoneFormat(date: Date, fmt: String): String {
        val format = SimpleDateFormat(fmt)
        format.timeZone = TimeZone.getTimeZone("Asia/Shanghai")
        return format.format(date)
    }

    fun stringToDate(date:String?, fmt:String):Date? {
        if(date == null) {
            return null
        }
        return try {
            val format = SimpleDateFormat(fmt)
            format.parse(date);
        }catch(e:Exception) {
            null
        }
    }
}