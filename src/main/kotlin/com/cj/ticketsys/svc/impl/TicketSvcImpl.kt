package com.cj.ticketsys.svc.impl

import com.cj.ticketsys.dao.TicketDao
import com.cj.ticketsys.entities.ChannelTypes
import com.cj.ticketsys.entities.Ticket
import com.cj.ticketsys.entities.TicketPrice
import com.cj.ticketsys.svc.PriceBinder
import com.cj.ticketsys.svc.TicketSvc
import com.cj.ticketsys.svc.Utils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.Calendar
import java.text.SimpleDateFormat
import io.micrometer.core.instrument.util.TimeUtils
import kotlin.collections.ArrayList
import kotlin.math.max


@Service
class TicketSvcImpl : TicketSvc {

    @Autowired
    private lateinit var ticketDao: TicketDao

    @Autowired
    private lateinit var priceBinder: PriceBinder


    override fun getTicket(ticketId: Int, scenicSid: Int, date: Date, channelType: ChannelTypes): Ticket? {

        val ticket = ticketDao.get(ticketId) ?: return null
        priceBinder.bind(ticket, channelType, date)
        return ticket
    }

    override fun getTickets(scenicSid: Int, date: Date, channelType: ChannelTypes): Collection<Ticket> {
        val tickets = ticketDao.gets(scenicSid)
        for (ticket in tickets) {
            priceBinder.bind(ticket, channelType, date)
        }
        return tickets
    }

    override fun getTicketInMonth(
        ticketId: Int,
        year: Int,
        month: Int,
        channelType: ChannelTypes
    ): Map<Int, Collection<TicketPrice>> {
        val ticket = ticketDao.get(ticketId) ?: return Collections.emptyMap()
        val days = getDaysOnMonth(year, month)
        val dayPrices = TreeMap<Int, Collection<TicketPrice>>()
        for (d in days) {
            dayPrices.put(d, priceBinder.getPrices(ticketId, channelType, Utils.intToDate(d)))
        }
        return dayPrices
    }

    private fun getDaysOnMonth(year: Int, month: Int): List<Int> {
        val maxDay = getMaxDayByYearMonth(year, month)
        val days = ArrayList<Int>()
        for (i in 1..maxDay) {
            val str = "$year" + String.format("%02d", month) + String.format("%02d", i)
            days.add(str.toInt())
        }
        return days
    }

    fun getMaxDayByYearMonth(year: Int, month: Int): Int {
        val a = Calendar.getInstance()
        a.set(Calendar.YEAR, year)
        a.set(Calendar.MONTH, month - 1)
        a.set(Calendar.DATE, 1)
        a.roll(Calendar.DATE, -1)
        return a.get(Calendar.DATE)
    }
}