package com.cj.ticketsys.svc.impl

import com.cj.ticketsys.dao.TicketDao
import com.cj.ticketsys.dao.TicketPriceDao
import com.cj.ticketsys.dao.TicketUseDateDao
import com.cj.ticketsys.entities.*
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
        //val ticket = ticketDao.get(ticketId)!!
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
                break
            }
            //特定不能使用日期
            val notBuyDates = useDate.notDates.split(",")
            if (notBuyDates.any { d -> d == dateFmt }) {
                break
            }
            var added = false
            //特定使用日期
            val customDates = useDate.customDates.split(',')
            if (customDates.any() && customDates.first().isNotEmpty()) {
                if (customDates.any { d -> d == dateFmt }) {
                    usePrices.add(p)
                }
                break
            }

            //特定日期折扣
            val cusDatePrices = p.getTicketCustomDataPrices()
            if (cusDatePrices.any { a -> a.date == dateFmt.toInt() }) {
                p.price = cusDatePrices.first { a -> a.date == dateFmt.toInt() }.price
                p.discountType = PriceDiscountTypes.Date
                usePrices.add(p)
                break
            }

            when (dateType) {
                DateTypes.WorkDay -> {
                    if (useDate.workDay) {
                        if (useDate.workPrice != null) {
                            p.price = useDate.workPrice!!
                        }
                        usePrices.add(p)
                        added = true
                    }
                }
                DateTypes.WeekendDay -> {
                    if (useDate.weekendDay) {
                        if (useDate.weekendPrice != null) {
                            p.price = useDate.weekendPrice!!
                        }
                        usePrices.add(p)
                        added = true
                    }
                }
                DateTypes.LegalDay -> {
                    if (useDate.legalDay) {
                        if (useDate.legalPrice != null) {
                            p.price = useDate.legalPrice!!
                        }
                        usePrices.add(p)
                        added = true
                    }
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