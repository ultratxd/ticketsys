package com.cj.ticketsys.controller.manage

import com.cj.ticketsys.controller.BaseController
import com.cj.ticketsys.controller.dto.RESULT_FAIL
import com.cj.ticketsys.controller.dto.RESULT_SUCCESS
import com.cj.ticketsys.controller.dto.ResultT
import com.cj.ticketsys.controller.manage.dto.MCategoryDto
import com.cj.ticketsys.controller.manage.dto.MTicketDto
import com.cj.ticketsys.controller.manage.dto.MTicketPriceDto
import com.cj.ticketsys.dao.*
import com.cj.ticketsys.entities.*
import com.cj.ticketsys.svc.DocTransformer
import com.cj.ticketsys.svc.SpotItemSvc
import com.cj.ticketsys.svc.TicketSvc
import com.google.common.base.Strings
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.primitives.Doubles
import com.google.common.primitives.Ints
import java.lang.Exception
import java.util.*
import javax.servlet.http.HttpServletRequest
import kotlin.collections.ArrayList

@RestController
@RequestMapping("/ota/v1/manage/ticket")
class ManageTicketController : BaseController() {

    @Autowired
    private lateinit var ticketDao: TicketDao

    @Autowired
    private lateinit var ticketSvc: TicketSvc

    @Autowired
    private lateinit var userDateDao: TicketUseDateDao

    @Autowired
    private lateinit var ticketPriceDao: TicketPriceDao

    @Autowired
    private lateinit var spotItemSvc: SpotItemSvc

    @Autowired
    private lateinit var ticketTransformer: DocTransformer<Ticket, MTicketDto>

    @Autowired
    private lateinit var priceTransformer: DocTransformer<TicketPrice, MTicketPriceDto>

    @GetMapping("{id}")
    fun getTicket(
        @PathVariable("id", required = false) id: Int
    ): ResultT<MTicketDto> {
        val ticket = ticketDao.get(id) ?: return ResultT(RESULT_FAIL, "订单不存在")
        val dto = ticketTransformer.transform(ticket)!!
        val relatedTickets = ticketDao.getRelatedTicket(ticket.id)
        dto.relatedTickets = ArrayList()
        for (rTid in relatedTickets) {
            val relTk = ticketDao.get(rTid)
            if (relTk != null) {
                dto.relatedTickets!!.add(ticketTransformer.transform(relTk)!!)
            }
        }
        return ResultT(RESULT_SUCCESS, "ok", dto)
    }

    @GetMapping("search")
    fun searchTickets(
        @RequestParam("sid", required = false) sid: Int?,
        @RequestParam("name", required = false) name: String?,
        @RequestParam("state", required = false) state: Int?,
        @RequestParam("cid", required = false) cid: Int?,
        @RequestParam("front_view", required = false) frontView: Boolean?,
        @RequestParam("page", required = false) page: Int? = 1,
        @RequestParam("size", required = false) size: Int? = 20
    ): ResultT<PagedList<MTicketDto>> {

        val query = TicketQuery()
        query.sid = sid
        if (!Strings.isNullOrEmpty(name)) {
            query.name = name
        }
        query.state = state
        query.cid = cid
        query.frontView = frontView
        if (size == null || size <= 0) {
            query.size = 20
        } else {
            query.size = size
        }
        var tmpPage = 0
        if (page == null || page <= 0) {
            tmpPage = 1
        } else {
            tmpPage = page
        }
        query.offset = (tmpPage - 1) * query.size
        val total = ticketDao.searchCountForAdmin(query)
        val list = ticketDao.searchForAdmin(query)

        val dtos = ArrayList<MTicketDto>()
        for (ticket in list) {
            val dto = ticketTransformer.transform(ticket)!!
            val relatedTickets = ticketDao.getRelatedTicket(ticket.id)
            dto.relatedTickets = ArrayList()
            for (rTid in relatedTickets) {
                val relTk = ticketDao.get(rTid)
                if (relTk != null) {
                    dto.relatedTickets!!.add(ticketTransformer.transform(relTk)!!)
                }
            }
            dtos.add(dto)
        }
        val pagedList = PagedList(tmpPage, query.size, total, dtos)

        return ResultT(RESULT_SUCCESS, "ok", pagedList)
    }

    @GetMapping("prices/{ticketId}")
    fun getPrices(
        @PathVariable("ticketId", required = false) id: Int
    ): ResultT<List<MTicketPriceDto>> {
        if (id <= 0) {
            return ResultT(RESULT_FAIL, "参数错误")
        }
        val prices = ticketPriceDao.gets(id)
        val dtos = ArrayList<MTicketPriceDto>()
        for (price in prices) {
            val dto = priceTransformer.transform(price)!!
            dtos.add(dto)
        }
        return ResultT(RESULT_SUCCESS, "ok", dtos)
    }

    @GetMapping("categories")
    fun getTicketCategories(): ResultT<List<MCategoryDto>> {
        val dtos = ArrayList<MCategoryDto>()
        val categories = TicketCategories.values()
        for (category in categories) {
            dtos.add(MCategoryDto(category.value, TicketCategories.getName(category.value)))
        }
        return ResultT(RESULT_SUCCESS, "ok", dtos)
    }

    @PostMapping("")
    fun createTicket(
        @RequestParam("scenic_sids", required = false) scenicSids: String?,
        @RequestParam("cloud_id", required = false) cloudId: String?,
        @RequestParam("name", required = false) name: String?,
        @RequestParam("title", required = false) title: String?,
        @RequestParam("per_nums", required = false) perNums: Int?,
        @RequestParam("enter_remark", required = false) enterRemark: String?,
        @RequestParam("buy_remark", required = false) buyRemark: String?,
        @RequestParam("stocks", required = false) stocks: Int?,
        @RequestParam("front_view", required = false) frontView: Boolean?,
        @RequestParam("cid", required = false) cid: Int?,
        @RequestParam("icon_url", required = false) iconUrl: String?,
        @RequestParam("tags", required = false) tags: String?,
        @RequestParam("rel_tkt_ids", required = false) relTktIds: String?,
        @RequestParam("state", required = false) state: Int?,
        @RequestParam("item_ids", required = false) itemIds: String?,
        @RequestParam("open_start_ts", required = false) openStartTs: Int?,
        @RequestParam("open_end_ts", required = false) openEndTs: Int?
    ): com.cj.ticketsys.controller.dto.Result {
        if (Strings.isNullOrEmpty(scenicSids)) {
            return com.cj.ticketsys.controller.dto.Result(RESULT_FAIL, "参数错误:scenic_sids")
        }
//        if (Strings.isNullOrEmpty(cloudId)) {
//            return com.cj.ticketsys.controller.dto.Result(RESULT_FAIL, "参数错误:cloud_id")
//        }
        if (Strings.isNullOrEmpty(name)) {
            return com.cj.ticketsys.controller.dto.Result(RESULT_FAIL, "参数错误:name")
        }
        if (perNums == null || perNums <= 0) {
            return com.cj.ticketsys.controller.dto.Result(RESULT_FAIL, "参数错误:per_nums")
        }
        if (stocks == null || stocks <= 0) {
            return com.cj.ticketsys.controller.dto.Result(RESULT_FAIL, "参数错误:stocks")
        }
        if (cid == null || cid <= 0 || !TicketCategories.values().any { a -> a.value == cid }) {
            return com.cj.ticketsys.controller.dto.Result(RESULT_FAIL, "参数错误:cid")
        }
        if (state == null || state <= 0) {
            return com.cj.ticketsys.controller.dto.Result(RESULT_FAIL, "参数错误:state")
        }
        val tkt = Ticket()
        tkt.cloudId = cloudId ?: ""
        tkt.name = name!!
        tkt.perNums = perNums
        tkt.enterRemark = enterRemark ?: ""
        tkt.buyRemark = buyRemark ?: ""
        tkt.stocks = stocks
        tkt.frontView = frontView ?: false
        tkt.cid = cid
        tkt.iconUrl = iconUrl ?: ""
        tkt.title = title
        tkt.state = TicketStates.prase(state)

        if(openStartTs != null && openStartTs > 0) {
            tkt.openStartTime = Date(openStartTs * 1000L)
        }
        if(openEndTs != null && openEndTs > 0) {
            tkt.openEndTime = Date(openEndTs * 1000L)
        }

        var relIds: List<Int> = ArrayList()
        var sids: List<Int> = ArrayList()

        val addTags = tags?.split(",") ?: emptyList()
        if (!Strings.isNullOrEmpty(relTktIds)) {
            val tmpRids = relTktIds?.split(",")
            relIds = tmpRids!!.map { a -> a.toInt() }
        }
        if (!Strings.isNullOrEmpty(scenicSids)) {
            val tmpSids = scenicSids?.split(',')
            sids = tmpSids!!.map { a -> a.toInt() }
        }

        val ok = ticketSvc.createTicket(tkt, sids, addTags, relIds)
        if (ok) {
            addTktItems(tkt,itemIds)
            return com.cj.ticketsys.controller.dto.Result(RESULT_SUCCESS, "创建成功")
        }
        return com.cj.ticketsys.controller.dto.Result(RESULT_FAIL, "创建失败")
    }

    fun addTktItems(tkt:Ticket,itemIds: String?) {
        val iIds = itemIds?.split(",")
        if(iIds == null || iIds.isEmpty()) {
            return
        }
        for(itemId in iIds) {
            val item = spotItemSvc.getSpotItem(itemId.toInt()) ?: continue
            spotItemSvc.addTicketItem(tkt.id,item.id,item.personalNums)
        }
    }

    @PutMapping("{id}")
    fun updateTicket(
        @PathVariable("id", required = false) id: Int,
        @RequestParam("scenic_sids", required = false) scenicSids: String?,
        @RequestParam("cloud_id", required = false) cloudId: String?,
        @RequestParam("name", required = false) name: String?,
        @RequestParam("title", required = false) title: String?,
        @RequestParam("per_nums", required = false) perNums: Int?,
        @RequestParam("enter_remark", required = false) enterRemark: String?,
        @RequestParam("buy_remark", required = false) buyRemark: String?,
        @RequestParam("stocks", required = false) stocks: Int?,
        @RequestParam("front_view", required = false) frontView: Boolean?,
        @RequestParam("cid", required = false) cid: Int?,
        @RequestParam("icon_url", required = false) iconUrl: String?,
        @RequestParam("tags", required = false) tags: String?,
        @RequestParam("rel_tkt_ids", required = false) relTktIds: String?,
        @RequestParam("state", required = false) state: Int?,
        @RequestParam("item_ids", required = false) itemIds: String?,
        @RequestParam("open_start_ts", required = false) openStartTs: Int?,
        @RequestParam("open_end_ts", required = false) openEndTs: Int?
    ): com.cj.ticketsys.controller.dto.Result {
        val tkt = ticketDao.get(id) ?: return com.cj.ticketsys.controller.dto.Result(RESULT_FAIL, "票不存在")
        if (Strings.isNullOrEmpty(scenicSids)) {
            return com.cj.ticketsys.controller.dto.Result(RESULT_FAIL, "参数错误:scenic_sids")
        }
//        if (Strings.isNullOrEmpty(cloudId)) {
//            return com.cj.ticketsys.controller.dto.Result(RESULT_FAIL, "参数错误:cloud_id")
//        }
        if (Strings.isNullOrEmpty(name)) {
            return com.cj.ticketsys.controller.dto.Result(RESULT_FAIL, "参数错误:name")
        }
        if (perNums == null || perNums <= 0) {
            return com.cj.ticketsys.controller.dto.Result(RESULT_FAIL, "参数错误:per_nums")
        }
        if (stocks == null || stocks < 0) {
            return com.cj.ticketsys.controller.dto.Result(RESULT_FAIL, "参数错误:stocks")
        }
        if (cid == null || cid <= 0 || !TicketCategories.values().any { a -> a.value == cid }) {
            return com.cj.ticketsys.controller.dto.Result(RESULT_FAIL, "参数错误:cid")
        }
        if (state == null || state <= 0) {
            return com.cj.ticketsys.controller.dto.Result(RESULT_FAIL, "参数错误:state")
        }

        tkt.cloudId = cloudId ?: ""
        tkt.name = name!!
        tkt.perNums = perNums
        tkt.enterRemark = enterRemark ?: ""
        tkt.buyRemark = buyRemark ?: ""
        tkt.stocks = stocks
        tkt.frontView = frontView ?: false
        tkt.cid = cid
        tkt.iconUrl = iconUrl
        tkt.title = title
        tkt.state = TicketStates.prase(state)
        if(openStartTs != null && openStartTs > 0) {
            tkt.openStartTime = Date(openStartTs * 1000L)
        } else {
            tkt.openStartTime = null
        }
        if(openEndTs != null && openEndTs > 0) {
            tkt.openEndTime = Date(openEndTs * 1000L)
        } else {
            tkt.openEndTime = null
        }

        var relIds: List<Int> = ArrayList()
        var sids: List<Int> = ArrayList()

        val addTags = tags?.split(",") ?: emptyList()
        if (!Strings.isNullOrEmpty(relTktIds)) {
            val tmpRids = relTktIds?.split(",")
            relIds = tmpRids!!.map { a -> a.toInt() }
        }
        if (!Strings.isNullOrEmpty(scenicSids)) {
            val tmpSids = scenicSids?.split(',')
            sids = tmpSids!!.map { a -> a.toInt() }
        }
        val ok = ticketSvc.updateTicket(tkt, sids, addTags, relIds)
        if (ok) {
            updateTktItems(tkt,itemIds)
            return com.cj.ticketsys.controller.dto.Result(RESULT_SUCCESS, "更新成功")
        }
        return com.cj.ticketsys.controller.dto.Result(RESULT_FAIL, "更新失败")
    }

    fun updateTktItems(tkt:Ticket,itemIds: String?) {
        spotItemSvc.removeAllTicketItems(tkt.id)
        val iIds = itemIds?.split(",")
        if(iIds == null || iIds.isEmpty()) {
            return
        }
        for(itemId in iIds) {
            val item = spotItemSvc.getSpotItem(itemId.toInt()) ?: continue
            spotItemSvc.addTicketItem(tkt.id,item.id,item.personalNums)
        }
    }

    @GetMapping("price/{id}")
    fun getPrice(
        @PathVariable("id", required = false) id: Int
    ): ResultT<MTicketPriceDto> {
        if (id <= 0) {
            return ResultT(RESULT_FAIL, "参数错误")
        }
        val price = ticketPriceDao.get(id) ?: return ResultT(RESULT_FAIL, "票价不存在")
        return ResultT(RESULT_SUCCESS, "ok", priceTransformer.transform(price))
    }


    @PostMapping("price")
    fun createPrice(
        @RequestParam("tid", required = false) tid: Int?,
        @RequestParam("ud_date", required = false) udDate: String?,
        @RequestParam("ch_type", required = false) chType: Int?,
        @RequestParam("name", required = false) name: String?,
        @RequestParam("price", required = false) price: Double?,
        @RequestParam("stocks", required = false) stocks: Int?,
        @RequestParam("stock_limit_type", required = false) stockLimitType: Int?,
        @RequestParam("state", required = false) state: Int?,
        @RequestParam("front_view", required = false) frontView: Boolean?,
        @RequestParam("refund_type", required = false) refundType: Int?,
        @RequestParam("title", required = false) title: String?,
        @RequestParam("remark", required = false) remark: String?,
        @RequestParam("description", required = false) description: String?,
        @RequestParam("original_price", required = false) originalPrice: Double?,
        @RequestParam("notice_remark", required = false) noticeRemark: String?,
        @RequestParam("custom_prices", required = false) customPrices: String?,
        @RequestParam("idcard_prices", required = false) idCardPrices: String?,
        @RequestParam("buy_limit", required = false) buyLimit: Int?,
        @RequestParam("buy_time", required = false) buyTime: Int?,
        @RequestParam("b2b_plu", required = false) b2bPLU: String?,
        req: HttpServletRequest
    ): com.cj.ticketsys.controller.dto.Result {
        if (tid == null || tid <= 0) {
            return com.cj.ticketsys.controller.dto.Result(RESULT_FAIL, "参数错误:tid")
        }
        val ticket = ticketDao.get(tid) ?: return com.cj.ticketsys.controller.dto.Result(RESULT_FAIL, "票不存在")
        if (Strings.isNullOrEmpty(udDate)) {
            return com.cj.ticketsys.controller.dto.Result(RESULT_FAIL, "参数错误:udDate不能为空")
        }
        if (chType == null || chType <= 0) {
            return com.cj.ticketsys.controller.dto.Result(RESULT_FAIL, "参数错误:ch_type")
        }
        if (Strings.isNullOrEmpty(name)) {
            return com.cj.ticketsys.controller.dto.Result(RESULT_FAIL, "参数错误:name")
        }
        if (price == null || price < 0) {
            return com.cj.ticketsys.controller.dto.Result(RESULT_FAIL, "参数错误:price")
        }
        if (originalPrice == null || originalPrice < 0) {
            return com.cj.ticketsys.controller.dto.Result(RESULT_FAIL, "参数错误:original_price")
        }
        if (stocks == null || stocks < 0) {
            return com.cj.ticketsys.controller.dto.Result(RESULT_FAIL, "参数错误:stocks")
        }
        if (state == null || state <= 0) {
            return com.cj.ticketsys.controller.dto.Result(RESULT_FAIL, "参数错误:state")
        }

        var prices = ticketPriceDao.gets(tid)
        if (prices.any { a -> a.channelType.value == chType.toShort() }) {
            return com.cj.ticketsys.controller.dto.Result(RESULT_FAIL, "渠道已存在价格")
        }

        val tp = TicketPrice()
        tp.tid = tid
        tp.name = name!!
        tp.channelType = ChannelTypes.prase(chType)
        tp.price = price
        tp.originalPrice = originalPrice
        tp.stocks = stocks
        tp.stockLimitType = TicketStockLimitTypes.All
        tp.state = TicketStates.prase(state)
        tp.frontView = frontView ?: false
        tp.refundType = RefundTypes.NoAllow
        tp.title = title ?: ""
        tp.remark = remark ?: ""
        tp.description = description ?: ""
        tp.noticeRemark = noticeRemark ?: ""
        tp.buyLimit = buyLimit ?: 0
        tp.buyTime = buyTime ?: 0
        tp.b2bPLU = b2bPLU

        if (!Strings.isNullOrEmpty(customPrices)) {
            val checkOK = checkCustomPricesProperty(customPrices!!)
            if (!checkOK) {
                return com.cj.ticketsys.controller.dto.Result(RESULT_FAIL, "custom_prices格式错误")
            }
            tp.customPrices = customPrices
        }
        if (!Strings.isNullOrEmpty(idCardPrices)) {
            val checkOK = checkIDCardPricesProperty(idCardPrices!!)
            if (!checkOK) {
                return com.cj.ticketsys.controller.dto.Result(RESULT_FAIL, "idcard_prices格式错误")
            }
            tp.idCardPrices = idCardPrices
        }

        val useDate: TicketUseDate
        try {
            val objectMapper = ObjectMapper()
            val udr = objectMapper.readValue(udDate, UseDateRequest::class.java)
            useDate = TicketUseDate()
            useDate.name = udr.name ?: ""
            useDate.remark = udr.remark ?: ""
            useDate.workDay = udr.workDay
            useDate.weekendDay = udr.weekendDay
            useDate.legalDay = udr.legalDay
            useDate.workPrice = udr.workPrice
            useDate.weekendPrice = udr.weekendPrice
            useDate.legalPrice = udr.legalPrice
            useDate.customDates = if (udr.customDates != null) udr.customDates!!.joinToString(",") else ""
            useDate.notDates = if (udr.notDates != null) udr.notDates!!.joinToString(",") else ""
            useDate.enterTime =  if (udr.enterTime != null) udr.enterTime else null
        } catch (e: Exception) {
            e.printStackTrace()
            return com.cj.ticketsys.controller.dto.Result(RESULT_FAIL, "解析ud_date数据错误")
        }
        val ok = ticketSvc.createPrice(tp, useDate)
        if (ok) {
            return com.cj.ticketsys.controller.dto.Result(RESULT_SUCCESS, "添加成功")
        }
        return com.cj.ticketsys.controller.dto.Result(RESULT_FAIL, "添加失败")
    }

    fun checkCustomPricesProperty(txt: String): Boolean {
        val dps = txt.split(";")
        for (dp in dps) {
            val dds = dp.split(":")
            if (dds.size != 2) {
                return false
            }
            if (Doubles.tryParse(dds[1]) == null) {
                return false
            }
            val dates = dds[0].split(",")
            for (date in dates) {
                if (Ints.tryParse(date) == null) {
                    return false
                }
            }
        }
        return true
    }

    fun checkIDCardPricesProperty(txt: String): Boolean {
        val dps = txt.split(";")
        for (dp in dps) {
            val dds = dp.split(":")
            if (dds.size != 3) {
                return false
            }
            if (Doubles.tryParse(dds[1]) == null) {
                return false
            }
            val ids = dds[0].split(",")
            for (id in ids) {
                if (Strings.isNullOrEmpty(id)) {
                    return false
                }
            }
        }
        return true
    }

    @PutMapping("price/{id}")
    fun updatePrice(
        @PathVariable("id", required = false) id: Int?,
        @RequestParam("ud_date", required = false) udDate: String?,
        @RequestParam("ch_type", required = false) chType: Int?,
        @RequestParam("name", required = false) name: String?,
        @RequestParam("price", required = false) price: Double?,
        @RequestParam("stocks", required = false) stocks: Int?,
        @RequestParam("stock_limit_type", required = false) stockLimitType: Int?,
        @RequestParam("state", required = false) state: Int?,
        @RequestParam("front_view", required = false) frontView: Boolean?,
        @RequestParam("refund_type", required = false) refundType: Int?,
        @RequestParam("title", required = false) title: String?,
        @RequestParam("remark", required = false) remark: String?,
        @RequestParam("description", required = false) description: String?,
        @RequestParam("original_price", required = false) originalPrice: Double?,
        @RequestParam("notice_remark", required = false) noticeRemark: String?,
        @RequestParam("custom_prices", required = false) customPrices: String?,
        @RequestParam("idcard_prices", required = false) idCardPrices: String?,
        @RequestParam("buy_limit", required = false) buyLimit: Int?,
        @RequestParam("buy_time", required = false) buyTime: Int?,
        @RequestParam("b2b_plu", required = false) b2bPLU: String?
    ): com.cj.ticketsys.controller.dto.Result {
        if (id == null || id <= 0) {
            return com.cj.ticketsys.controller.dto.Result(RESULT_FAIL, "参数错误:id")
        }
        val tp = ticketPriceDao.get(id) ?: return com.cj.ticketsys.controller.dto.Result(RESULT_FAIL, "票价不存在")
        if (Strings.isNullOrEmpty(udDate)) {
            return com.cj.ticketsys.controller.dto.Result(RESULT_FAIL, "参数错误:udDate不能为空")
        }
        if (chType == null || chType <= 0) {
            return com.cj.ticketsys.controller.dto.Result(RESULT_FAIL, "参数错误:ch_type")
        }
        if (Strings.isNullOrEmpty(name)) {
            return com.cj.ticketsys.controller.dto.Result(RESULT_FAIL, "参数错误:name")
        }
        if (price == null || price < 0) {
            return com.cj.ticketsys.controller.dto.Result(RESULT_FAIL, "参数错误:price")
        }
        if (originalPrice == null || originalPrice < 0) {
            return com.cj.ticketsys.controller.dto.Result(RESULT_FAIL, "参数错误:original_price")
        }
        if (stocks == null || stocks < 0) {
            return com.cj.ticketsys.controller.dto.Result(RESULT_FAIL, "参数错误:stocks")
        }
        if (stocks != 0 && stocks < tp.solds) {
            return com.cj.ticketsys.controller.dto.Result(RESULT_FAIL, "参数错误:库存不能小于已销售数量")
        }
        if (state == null || state <= 0) {
            return com.cj.ticketsys.controller.dto.Result(RESULT_FAIL, "参数错误:state")
        }
        tp.name = name!!
        tp.channelType = ChannelTypes.prase(chType)
        tp.price = price
        tp.originalPrice = originalPrice
        tp.stocks = stocks
        tp.stockLimitType = TicketStockLimitTypes.All
        tp.state = TicketStates.prase(state)
        tp.frontView = frontView ?: false
        tp.refundType = RefundTypes.NoAllow
        tp.title = title ?: ""
        tp.remark = remark ?: ""
        tp.description = description ?: ""
        tp.noticeRemark = noticeRemark ?: ""
        tp.buyLimit = buyLimit ?: 0
        tp.buyTime = buyTime ?: 0
        tp.b2bPLU = b2bPLU

        if (!Strings.isNullOrEmpty(customPrices)) {
            val checkOK = checkCustomPricesProperty(customPrices!!)
            if (!checkOK) {
                return com.cj.ticketsys.controller.dto.Result(RESULT_FAIL, "custom_prices格式错误")
            }
            tp.customPrices = customPrices
        } else {
            tp.customPrices = ""
        }

        if (!Strings.isNullOrEmpty(idCardPrices)) {
            val checkOK = checkIDCardPricesProperty(idCardPrices!!)
            if (!checkOK) {
                return com.cj.ticketsys.controller.dto.Result(RESULT_FAIL, "idcard_prices格式错误")
            }
            tp.idCardPrices = idCardPrices
        } else {
            tp.idCardPrices = ""
        }

        val useDate: TicketUseDate
        try {
            val objectMapper = ObjectMapper()
            val udr = objectMapper.readValue(udDate, UseDateRequest::class.java)
            useDate = TicketUseDate()
            useDate.id = tp.useDateId
            useDate.name = udr.name ?: ""
            useDate.remark = udr.remark ?: ""
            useDate.workDay = udr.workDay
            useDate.weekendDay = udr.weekendDay
            useDate.legalDay = udr.legalDay
            useDate.workPrice = udr.workPrice
            useDate.weekendPrice = udr.weekendPrice
            useDate.legalPrice = udr.legalPrice
            useDate.customDates = if (udr.customDates != null) udr.customDates!!.joinToString(",") else ""
            useDate.notDates = if (udr.notDates != null) udr.notDates!!.joinToString(",") else ""
            useDate.enterTime =  if (udr.enterTime != null) udr.enterTime else null
        } catch (e: Exception) {
            e.printStackTrace()
            return com.cj.ticketsys.controller.dto.Result(RESULT_FAIL, "解析ud_date数据错误")
        }
        val ok = ticketSvc.updatePrice(tp, useDate)
        if (ok) {
            return com.cj.ticketsys.controller.dto.Result(RESULT_SUCCESS, "更新成功")
        }
        return com.cj.ticketsys.controller.dto.Result(RESULT_FAIL, "更新失败")
    }
}