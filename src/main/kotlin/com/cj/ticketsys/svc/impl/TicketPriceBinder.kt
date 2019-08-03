package com.cj.ticketsys.svc.impl

import com.cj.ticketsys.dao.TicketDao
import com.cj.ticketsys.dao.TicketPriceDao
import com.cj.ticketsys.dao.TicketUseDateDao
import com.cj.ticketsys.entities.ChannelTypes
import com.cj.ticketsys.entities.DateTypes
import com.cj.ticketsys.entities.Ticket
import com.cj.ticketsys.entities.TicketPrice
import com.cj.ticketsys.svc.PriceBinder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@Service
class TicketPriceBinder : PriceBinder {

    @Autowired
    private lateinit var dataTypeRecognizer: DefaultDataTypeRecognizer

    @Autowired
    private lateinit var ticketUseDateDao: TicketUseDateDao

    @Autowired
    private lateinit var ticketPriceDao: TicketPriceDao

    @Autowired
    private lateinit var ticketDao: TicketDao

    override fun getPrices(ticketId: Int, channelType: ChannelTypes, date: Date): Collection<TicketPrice> {
        val ticket = ticketDao.get(ticketId)!!
        val prices = ticketPriceDao.gets(ticketId)
        if (prices.isEmpty()) {
            return Collections.emptyList()
        }
        val channelPrices = ArrayList<TicketPrice>()
        for (p in prices) {
            if (p.channelType == channelType) {
                channelPrices.add(p)
            }
        }
        val sdf = SimpleDateFormat("yyyyMMdd")
        val dateFmt = sdf.format(date)

        val dateType = dataTypeRecognizer.recognize(date)
        val usePrices = ArrayList<TicketPrice>()
        for (p in channelPrices) {
            val useDate = ticketUseDateDao.get(p.useDateId)
            if (useDate == null) {
                usePrices.add(p)
                continue
            }
            //特定不能使用日期
            val notBuyDates = useDate.notDates.split(",")
            if (notBuyDates.any { d -> d == dateFmt }) {
                continue
            }
            var added = false;
            when (dateType) {
                DateTypes.WorkDay -> {
                    if (useDate.workDay) {
                        usePrices.add(p)
                        added = true
                    }
                }
                DateTypes.WeekendDay -> {
                    if (useDate.weekendDay) {
                        usePrices.add(p)
                        added = true
                    }
                }
                DateTypes.LegalDay -> {
                    if (useDate.legalDay) {
                        usePrices.add(p)
                        added = true
                    }
                }
            }
            //特定使用日期
            if (!added) {
                val customDates = useDate.customDates.split(',')
                if (customDates.any { d -> d == dateFmt }) {
                    usePrices.add(p)
                }
            }
        }
        return usePrices
    }

    /**
     * 计算某日使用的票价
     */
    override fun bind(ticket: Ticket, channelType: ChannelTypes, date: Date): Collection<TicketPrice> {
        val prices = getPrices(ticket.id, channelType, date)
        if (prices.isEmpty()) {
            return Collections.emptyList()
        }
        ticket.prices.addAll(prices)
        return prices
    }
}