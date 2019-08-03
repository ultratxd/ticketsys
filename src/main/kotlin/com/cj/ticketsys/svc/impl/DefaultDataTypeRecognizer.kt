package com.cj.ticketsys.svc.impl

import com.cj.ticketsys.dao.HolidayDao
import com.cj.ticketsys.entities.DateTypes
import com.cj.ticketsys.svc.DateTypeRecognizer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar


@Service
class DefaultDataTypeRecognizer : DateTypeRecognizer {

    @Autowired
    private lateinit var holidayDao: HolidayDao

    override fun recognize(date: Date): DateTypes {
        val dataFmt = SimpleDateFormat("yyyyMMdd")
        val holidays = holidayDao.gets(dataFmt.format(Date()).toInt())
        val existed = holidays.any { h -> h.d == dataFmt.format(date).toInt() }
        if (existed) {
            return DateTypes.LegalDay
        }
        val cal = Calendar.getInstance()
        cal.time = date
        var w = cal.get(Calendar.DAY_OF_WEEK) - 1
        if (w < 0) {
            w = 0
        }
        if (w == 0 || w == 6) {
            return DateTypes.WeekendDay
        }
        return DateTypes.WorkDay
    }
}