package com.cj.ticketsys.controller

import com.cj.ticketsys.controller.dto.*
import com.cj.ticketsys.controller.req.BuyRequest
import com.cj.ticketsys.dao.PartnerDao
import com.cj.ticketsys.dao.RecommendDao
import com.cj.ticketsys.dao.TicketDao
import com.cj.ticketsys.dao.TicketPriceDao
import com.cj.ticketsys.entities.BuyTypes
import com.cj.ticketsys.entities.CardTypes
import com.cj.ticketsys.entities.Ticket
import com.cj.ticketsys.entities.TicketPrice
import com.cj.ticketsys.svc.*
import com.google.common.base.Strings
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import java.time.ZoneId
import java.util.Calendar


@RestController
@RequestMapping("/ota/v1/ticket")
class TicketController : BaseController() {

    @Autowired
    private lateinit var partnerDao: PartnerDao

    @Autowired
    private lateinit var ticketDao: TicketDao

    @Autowired
    private lateinit var ticketPriceDao: TicketPriceDao

    @Autowired
    private lateinit var recommendDao: RecommendDao

    @Autowired
    private lateinit var ticketDocTransformer: DocTransformer<Ticket, TicketDto>

    @Autowired
    private lateinit var ticketPriceDocTransformer: DocTransformer<TicketPrice, TicketPriceDto>

    @Autowired
    private lateinit var priceBinder: PriceBinder

    @Autowired
    private lateinit var ticketSvc: TicketSvc

    @Autowired
    private lateinit var ticketBuyer: TicketBuyer


    @GetMapping("/all/{sid}")
    fun getTickets(
        @PathVariable("sid", required = true) sid: Int,
        @RequestParam("date", required = false) date: Int?,
        @RequestParam("partner_id", required = false) partnerId: String?
    ): ResultT<List<TicketDto>> {
        if (sid <= 0 || Strings.isNullOrEmpty(partnerId)) {
            return ResultT(RESULT_FAIL, "参数错误")
        }
        if (date == null || date.toString().length != 8) {
            return ResultT(RESULT_FAIL, "日期格式错误")
        }
        val partner = partnerDao.get(partnerId!!) ?: return ResultT(RESULT_FAIL, "商户不存在")

        val tickets = ticketSvc.getTickets(sid, Utils.intToDate(date), partner.channelType)
        val dtos = ArrayList<TicketDto>()
        for (ticket in tickets) {
            val dto = ticketDocTransformer.transform(ticket)!!
            dtos.add(dto)
        }
        return ResultT(RESULT_SUCCESS, "ok", dtos)
    }

    @GetMapping("/{id}/prices/{year}/{month}")
    fun getTicketInMonth(
        @PathVariable("id", required = true) id: Int,
        @PathVariable("year", required = true) year: Int,
        @PathVariable("month", required = true) month: Int,
        @RequestParam("partner_id", required = false) partnerId: String?
    ): ResultT<List<DayTicketPriceDto>> {
        if (id <= 0 || Strings.isNullOrEmpty(partnerId)) {
            return ResultT(RESULT_FAIL, "参数错误")
        }
        if (month !in 0..13) {
            return ResultT(RESULT_FAIL, "月份格式错误")
        }
        val c = Calendar.getInstance()
        c.time = Date()
        if (year < c.get(Calendar.YEAR)) {
            return ResultT(RESULT_FAIL, "年份格式错误")
        }
        val partner = partnerDao.get(partnerId!!) ?: return ResultT(RESULT_FAIL, "商户不存在")
        val prices = ticketSvc.getTicketInMonth(id, year, month, partner.channelType)
        val dtos = ArrayList<DayTicketPriceDto>()
        for (kv in prices) {
            val dayPDto = DayTicketPriceDto(kv.key, kv.value.map { a -> ticketPriceDocTransformer.transform(a)!! })
            dtos.add(dayPDto)
        }
        return ResultT(RESULT_SUCCESS, "ok", dtos)
    }

    @GetMapping("/{id}")
    fun getTicket(
        @PathVariable("id", required = true) id: Int,
        @RequestParam("date", required = false) date: Int?,
        @RequestParam("partner_id", required = false) partnerId: String?
    ): ResultT<TicketDto> {
        if (id <= 0 || Strings.isNullOrEmpty(partnerId)) {
            return ResultT(RESULT_FAIL, "参数错误")
        }
        if (date == null || date.toString().length != 8) {
            return ResultT(RESULT_FAIL, "日期格式错误")
        }
        val ticket = ticketDao.get(id) ?: return ResultT(RESULT_FAIL, "对象不存在")
        val partner = partnerDao.get(partnerId!!) ?: return ResultT(RESULT_FAIL, "商户不存在")

        priceBinder.bind(ticket, partner.channelType, Utils.intToDate(date))

        val dto = ticketDocTransformer.transform(ticket)!!

        val relatedTickets = ticketDao.getRelatedTicket(id)
        dto.relatedTickets = ArrayList()
        for (rTid in relatedTickets) {
            val relTk = ticketDao.get(rTid)
            if (relTk != null) {
                if (!relTk.frontView) {
                    continue
                }
                priceBinder.bind(relTk, partner.channelType, Utils.intToDate(date))
                dto.relatedTickets!!.add(ticketDocTransformer.transform(relTk)!!)
            }
        }

        return ResultT(RESULT_SUCCESS, "ok", dto)
    }

    @GetMapping("/price/{id}")
    fun getTicketPrice(
        @PathVariable("id", required = true) id: Int
    ): ResultT<TicketPriceDto> {
        if (id <= 0) {
            return ResultT(RESULT_FAIL, "参数错误")
        }
        val price = ticketPriceDao.get(id) ?: return ResultT(RESULT_FAIL, "对象不存在")
        val dto = ticketPriceDocTransformer.transform(price)!!
        return ResultT(RESULT_SUCCESS, "ok", dto)
    }


    @GetMapping("/search")
    fun searchTickets(
        @RequestParam("cid", required = false) cid: Int?,
        @RequestParam("date", required = false) date: Int?,
        @RequestParam("scenic_sid", required = false) sid: Int,
        @RequestParam("partner_id", required = false) partnerId: String?
    ): ResultT<List<TicketDto>> {
        if ((cid != null && cid <= 0) || sid <= 0 || Strings.isNullOrEmpty(partnerId)) {
            return ResultT(RESULT_FAIL, "参数错误")
        }
        if (date == null || date.toString().length != 8) {
            return ResultT(RESULT_FAIL, "日期格式错误")
        }

        val partner = partnerDao.get(partnerId!!) ?: return ResultT(RESULT_FAIL, "商户不存在")
        val tickets = ticketDao.gets(sid, cid, partner.channelType,true)
        val dtos = ArrayList<TicketDto>()
        for (ticket in tickets) {
            priceBinder.bind(ticket, partner.channelType, Utils.intToDate(date))
            val dto = ticketDocTransformer.transform(ticket)!!
            dtos.add(dto)
        }
        return ResultT(RESULT_SUCCESS, "ok", dtos)
    }

    @GetMapping("/recommends")
    fun recommendTickets(
        @RequestParam("nums", required = false) nums: Int?,
        @RequestParam("date", required = false) date: Int?,
        @RequestParam("partner_id", required = false) partnerId: String?
    ): ResultT<List<TicketDto>> {
        if (nums == null || Strings.isNullOrEmpty(partnerId)) {
            return ResultT(RESULT_FAIL, "参数错误")
        }
        var buyDate = SimpleDateFormat("yyyyMMdd").format(Date()).toInt()
        if (date != null && date.toString().length == 8) {
            buyDate = date
        }
        val rems = recommendDao.gets(nums, 1)
        val tickets = ArrayList<Ticket>()
        for (r in rems) {
            val ticket = ticketDao.get(r.refId)
            if (ticket != null) {
                tickets.add(ticket)
            }
        }
        val partner = partnerDao.get(partnerId!!) ?: return ResultT(RESULT_FAIL, "商户不存在")
        val dtos = ArrayList<TicketDto>()
        for (ticket in tickets) {
            priceBinder.bind(ticket, partner.channelType, Utils.intToDate(buyDate))
            val dto = ticketDocTransformer.transform(ticket)!!
            dtos.add(dto)
        }
        return ResultT(RESULT_SUCCESS, "ok", dtos)
    }

    @PostMapping("/buy")
    fun buy(
        @RequestBody buy: BuyRequest
    ): ResultT<BuyTicketResp> {
        if (Strings.isNullOrEmpty(buy.partnerId)
            || Strings.isNullOrEmpty(buy.channelUid)
        ) {
            return ResultT(RESULT_FAIL, "参数错误")
        }
        if (buy.BuyTickets.isEmpty()) {
            return ResultT(RESULT_FAIL, "未购买商品")
        }
        if (!BuyTypes.values().any { s -> s.value == buy.buyType }) {
            return ResultT(RESULT_FAIL, "购买类型不存在")
        }
        for (buyTicket in buy.BuyTickets) {
            if (buyTicket.date.toString().length != 8) {
                return ResultT(RESULT_FAIL, "日期格式错误")
            }
        }

        val buyTicket = BuyTicketOrder()
        val partner = partnerDao.get(buy.partnerId) ?: return ResultT(RESULT_FAIL, "商户不存在")
        buyTicket.partner = partner
        if (!Strings.isNullOrEmpty(buy.buyerIp)) {
            buyTicket.buyerIp = buy.buyerIp
        }
        if (buy.scenicSid <= 0) {
            return ResultT(RESULT_FAIL, "景点参数错误")
        }
        buyTicket.scenicSpotId = buy.scenicSid
        buyTicket.channelUid = buy.channelUid
        buyTicket.buyType = BuyTypes.prase(buy.buyType)
        for (ticket in buy.BuyTickets) {
            if (ticket.ticketNums <= 0) {
                return ResultT(RESULT_FAIL, "购买数量错误")
            }
            if (ticket.ticketPriceId <= 0) {
                return ResultT(RESULT_FAIL, "票价未选择")
            }
            if (Strings.isNullOrEmpty(ticket.userCard)) {
                return ResultT(RESULT_FAIL, "身份证未填写")
            }
            if (Strings.isNullOrEmpty(ticket.userMobile)) {
                return ResultT(RESULT_FAIL, "手机号未填写")
            }
            if (ticket.cardType <= 0) {
                return ResultT(RESULT_FAIL, "证件类型错误")
            }
            val buyTicketInfo = BuyTicketInfo()
            buyTicketInfo.ticketPriceId = ticket.ticketPriceId
            buyTicketInfo.ticketNums = ticket.ticketNums
            buyTicketInfo.date = ticket.date
            buyTicketInfo.userName = ticket.userName
            buyTicketInfo.cardType = CardTypes.prase(ticket.cardType)
            buyTicketInfo.userCard = ticket.userCard
            buyTicketInfo.userMobile = ticket.userMobile
            buyTicket.buyTickets.add(buyTicketInfo)
        }

        val result = ticketBuyer.buy(buyTicket)
        if (result.status == RESULT_SUCCESS) {
            val resp = BuyTicketResp()
            resp.orderNo = result.order!!.orderId
            resp.expires = Date(result.order!!.createTime.time + 30 * 60 * 1000).time / 1000
            resp.ticketCount = result.order!!.childs
            resp.totalMoney = result.order!!.price
            return ResultT(RESULT_SUCCESS, "ok", resp)
        }
        return ResultT(result.status, result.msg)
    }
}