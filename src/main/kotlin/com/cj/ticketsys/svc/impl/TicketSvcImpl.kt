package com.cj.ticketsys.svc.impl

import com.cj.ticketsys.dao.ScenicSpotDao
import com.cj.ticketsys.dao.TicketDao
import com.cj.ticketsys.dao.TicketPriceDao
import com.cj.ticketsys.dao.TicketUseDateDao
import com.cj.ticketsys.entities.*
import com.cj.ticketsys.svc.PriceBinder
import com.cj.ticketsys.svc.TicketSvc
import com.cj.ticketsys.svc.Utils
import com.google.common.base.Strings
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.Calendar
import java.text.SimpleDateFormat
import io.micrometer.core.instrument.util.TimeUtils
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.interceptor.TransactionAspectSupport
import java.lang.Exception
import kotlin.collections.ArrayList
import kotlin.math.max


@Service
class TicketSvcImpl : TicketSvc {

    @Autowired
    private lateinit var ticketDao: TicketDao

    @Autowired
    private lateinit var scenicSpotDao: ScenicSpotDao

    @Autowired
    private lateinit var ticketPriceDao: TicketPriceDao

    @Autowired
    private lateinit var ticketUseDateDao: TicketUseDateDao

    @Autowired
    private lateinit var priceBinder: PriceBinder


    override fun getTicket(ticketId: Int, date: Date, channelType: ChannelTypes): Ticket? {

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

    @Transactional(rollbackFor = [Exception::class])
    override fun createTicket(
        tkt: Ticket,
        scenicSids: List<Int>,
        tags: List<String>,
        relTktIds: List<Int>,
        vararg args: Any
    ): Boolean {
        val addTags = ArrayList<Int>()
        for (tag in tags) {
            if(Strings.isNullOrEmpty(tag)) {
                continue
            }
            var eTag = ticketDao.getTagByName(tag, 1)
            if (eTag == null) {
                eTag = Tag()
                eTag.name = tag
                eTag.type = 1
                ticketDao.insertTag(eTag)
                if (eTag.id > 0) {
                    addTags.add(eTag.id)
                }
            } else {
                addTags.add(eTag.id)
            }
        }
        val c = ticketDao.insert(tkt)
        if (c == 0L) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()
            return false
        }
        if (addTags.any()) {
            for (tagId in addTags) {
                ticketDao.insertTicketTag(tkt.id, tagId)
            }
        }
        if (relTktIds.any()) {
            for (rid in relTktIds) {
                if (ticketDao.insertRelatedTicket(tkt.id, rid) == 0L) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()
                    return false
                }
            }
        }
        if (scenicSids.any()) {
            for (sid in scenicSids) {
                if (ticketDao.insertScenicTicket(sid, tkt.id) == 0L) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()
                    return false
                }
                scenicSpotDao.updateTicketCount(sid, 1)
            }
        }
        return true
    }

    @Transactional(rollbackFor = [Exception::class])
    override fun updateTicket(
        tkt: Ticket,
        scenicSids: List<Int>,
        tags: List<String>,
        relTktIds: List<Int>,
        vararg args: Any
    ): Boolean {
        val addTags = ArrayList<Int>()
        for (tag in tags) {
            if(Strings.isNullOrEmpty(tag)) {
                continue
            }
            var eTag = ticketDao.getTagByName(tag, 1)
            if (eTag == null) {
                eTag = Tag()
                eTag.name = tag
                eTag.type = 1
                ticketDao.insertTag(eTag)
                if (eTag.id > 0) {
                    addTags.add(eTag.id)
                }
            }else {
                addTags.add(eTag.id)
            }
        }
        val c = ticketDao.update(tkt)
        if (c == 0L) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()
            return false
        }
        ticketDao.delTicketTags(tkt.id)
        if (addTags.any()) {
            for (tagId in addTags) {
                ticketDao.insertTicketTag(tkt.id, tagId)
            }
        }
        ticketDao.delRelatedTickets(tkt.id)
        if (relTktIds.any()) {
            for (rid in relTktIds) {
                if (ticketDao.insertRelatedTicket(tkt.id, rid) == 0L) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()
                    return false
                }
            }
        }
        ticketDao.delScenicTickets(tkt.id)
        if (scenicSids.any()) {
            for (sid in scenicSids) {
                if (ticketDao.insertScenicTicket(sid, tkt.id) == 0L) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()
                    return false
                }
            }
        }
        return true
    }

    @Transactional(rollbackFor = [Exception::class])
    override fun createPrice(price: TicketPrice, uDate: TicketUseDate): Boolean {
        val c = ticketUseDateDao.insert(uDate)
        if (c > 0) {
            price.useDateId = uDate.id
            val tc = ticketPriceDao.insert(price)
            if (tc > 0) {
                return true
            }
        }
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()
        return false
    }

    @Transactional(rollbackFor = [Exception::class])
    override fun updatePrice(price: TicketPrice, uDate: TicketUseDate): Boolean {
        val c = ticketUseDateDao.update(uDate)
        if (c > 0) {
            price.useDateId = uDate.id
            val tc = ticketPriceDao.update(price)
            if (tc > 0) {
                return true
            }
        }
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()
        return false
    }
}