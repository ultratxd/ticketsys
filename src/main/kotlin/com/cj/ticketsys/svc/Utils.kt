package com.cj.ticketsys.svc

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

    fun dateToYYYYMMDDInt(date: Date): Int {
        val d = dateZoneFormat(date,"yyyyMMdd")
        return d.toInt()
    }

    fun dateZoneFormat(date: Date, fmt: String): String {
        val format = SimpleDateFormat(fmt)
        format.timeZone = TimeZone.getTimeZone("Asia/Shanghai")
        return format.format(date)
    }
}