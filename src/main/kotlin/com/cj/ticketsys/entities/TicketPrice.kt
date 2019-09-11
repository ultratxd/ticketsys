package com.cj.ticketsys.entities

import com.google.common.base.Strings
import java.math.BigDecimal
import java.util.*
import kotlin.collections.ArrayList

class TicketPrice : PropertyEntity() {
    var id: Int = 0
    var tid: Int = 0
    var useDateId: Int = 0
    var channelType: ChannelTypes = ChannelTypes.Rack
    var name: String = ""
    var price: Double = 0.0
    var createTime: Date = Date()
    var stocks: Int = 0
    var stockLimitType: TicketStockLimitTypes = TicketStockLimitTypes.All
    var solds: Int = 0
    var state: TicketStates = TicketStates.Enabled
    var frontView: Boolean = false
    var refundType: RefundTypes = RefundTypes.NoAllow

    var originalPrice: Double?
        get() {
            val v = this.getProperty("original_price") as BigDecimal?
            return v?.toDouble()
        }
        set(value) = this.setProperty("original_price", value)

    var title: String?
        get() = this.getProperty("title") as String?
        set(value) = this.setProperty("title", value)
    var remark: String?
        get() = this.getProperty("remark") as String?
        set(value) = this.setProperty("remark", value)
    var description: String?
        get() = this.getProperty("description") as String?
        set(value) = this.setProperty("description", value)
    var noticeRemark: String?
        get() = this.getProperty("notice_remark") as String?
        set(value) = this.setProperty("notice_remark", value)
    var customPrices: String?
        get() = this.getProperty("custom_prices") as String?
        set(value) = this.setProperty("custom_prices", value)
    var idCardPrices: String?
        get() = this.getProperty("id_card_prices") as String?
        set(value) = this.setProperty("id_card_prices", value)

    fun getTicketCustomDataPrices(): List<TicketDatePrice> {
        val cps = customPrices
        if (Strings.isNullOrEmpty(cps)) {
            return emptyList()
        }
        val prices = ArrayList<TicketDatePrice>()
        val dps = cps!!.split(";")
        for (dp in dps) {
            val dds = dp.split(":")
            val dates = dds[0].split(",")
            for (date in dates) {
                prices.add(TicketDatePrice(date.toInt(), dds[1].toDouble()))
            }
        }
        return prices
    }

    fun getTicketIDCardPrices(): List<TicketIDCardPrice> {
        val cps = idCardPrices
        if (Strings.isNullOrEmpty(cps)) {
            return emptyList()
        }
        val prices = ArrayList<TicketIDCardPrice>()
        val dps = cps!!.split(";")
        for (dp in dps) {
            val dds = dp.split(":")
            val cards = dds[0].split(",")
            val dates = ArrayList<Int>()
            if (dds.size == 3) {
                val sDates = dds[2].split(",")
                for (d in sDates) {
                    if (!Strings.isNullOrEmpty(d.trim()) && d.trim().toIntOrNull() != null) {
                        dates.add(d.trim().toInt())
                    }
                }
            }
            for (card in cards) {
                prices.add(TicketIDCardPrice(card, dds[1].toDouble(), dates))
            }
        }
        return prices
    }

    var discountType: PriceDiscountTypes = PriceDiscountTypes.Nothing
}

data class TicketDatePrice(val date: Int, val price: Double)

data class TicketIDCardPrice(val idCardPrefix: String, val price: Double, val limitDates: List<Int>)