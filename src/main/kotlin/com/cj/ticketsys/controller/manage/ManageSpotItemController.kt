package com.cj.ticketsys.controller.manage

import com.cj.ticketsys.controller.BaseController
import com.cj.ticketsys.controller.dto.*
import com.cj.ticketsys.controller.manage.dto.SpotItemDto
import com.cj.ticketsys.controller.manage.dto.SpotItemOrderDto
import com.cj.ticketsys.controller.manage.dto.SpotItemPriceDto
import com.cj.ticketsys.controller.manage.dto.TicketOfItemDto
import com.cj.ticketsys.dao.ScenicSpotDao
import com.cj.ticketsys.entities.ChannelTypes
import com.cj.ticketsys.entities.PagedList
import com.cj.ticketsys.entities.spotItem.*
import com.cj.ticketsys.svc.DocTransformer
import com.cj.ticketsys.svc.SpotItemSvc
import com.google.common.base.Strings
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.util.*
import kotlin.collections.ArrayList

@CrossOrigin(origins = ["*"],maxAge = 3600,allowCredentials = "true")
@RestController
@RequestMapping("/ota/v1/manage/spot")
class ManageSpotItemController :BaseController() {

    @Autowired
    private lateinit var spotItemSvc: SpotItemSvc

    @Autowired
    private lateinit var spotDao: ScenicSpotDao

    @Autowired
    private lateinit var spotItemTransformer: DocTransformer<SpotItem, SpotItemDto>

    @Autowired
    private lateinit var spotItemPriceTransformer: DocTransformer<SpotItemPrice, SpotItemPriceDto>

    @Autowired
    private lateinit var tktOfItemTransformer : DocTransformer<TicketOfItem, TicketOfItemDto>

    @Autowired
    private lateinit var orderTransformer: DocTransformer<SpotItemOrder, SpotItemOrderDto>

    @GetMapping("/item/items")
    fun getItems(
        @RequestParam("scenic_sid", required = false) scenicSid: Int?
    ): ResultT<List<SpotItemDto>> {
        if(scenicSid == null || scenicSid <= 0) {
            return ResultT(RESULT_FAIL,"参数错误")
        }
        val items = spotItemSvc.querySpotItems(scenicSid)
        val dtos = ArrayList<SpotItemDto>()
        for(item in items) {
            dtos.add(spotItemTransformer.transform(item)!!)
        }
        return  ResultT(RESULT_SUCCESS,"ok",dtos)
    }

    @GetMapping("/item/{id}")
    fun getItem(
        @PathVariable("id", required = false) id: Int
    ): ResultT<SpotItemDto> {
        if(id <= 0) {
            return ResultT(RESULT_FAIL,"参数错误")
        }
        val item = spotItemSvc.getSpotItem(id) ?: return ResultT(RESULT_FAIL,"对象不存在")
        val dto = spotItemTransformer.transform(item)
        return ResultT(RESULT_SUCCESS,"ok",dto)
    }

    @PostMapping("/item")
    fun addItem(
        @RequestParam("scenic_sid", required = false) scenicSid: Int?,
        @RequestParam("name", required = false) name: String?,
        @RequestParam("desc1", required = false) desc1: String?,
        @RequestParam("desc2", required = false) desc2: String?,
        @RequestParam("per_nums", required = false) perNums: Int?,
        @RequestParam("price", required = false) price: Double?
    ): Result {
        if(scenicSid == null || scenicSid <= 0) {
            return Result(RESULT_FAIL,"scenic_sid:参数错误")
        }
        if(Strings.isNullOrEmpty(name)) {
            return Result(RESULT_FAIL,"name:参数错误")
        }
        if(perNums == null || perNums <= 0) {
            return Result(RESULT_FAIL,"per_nums:参数错误")
        }
        val spot = spotDao.get(scenicSid) ?: return Result(RESULT_FAIL,"景点不存在")

        val item = SpotItem()
        item.createTime = Date()
        item.scenicId = spot.pid
        item.scenicSpotId = scenicSid
        item.name = name!!
        item.desc1 = if(Strings.isNullOrEmpty(desc1)) "" else desc1!!
        item.desc2 = if(Strings.isNullOrEmpty(desc2)) "" else desc2!!
        item.personalNums = perNums
        item.price = if(price == null || price <= 0) 0.0 else price
        val ok = spotItemSvc.addSpotItem(item)
        if(ok) {
            return Result(RESULT_SUCCESS,"添加成功")
        }
        return Result(RESULT_FAIL,"添加失败")
    }

    @PutMapping("/item/{id}")
    fun updateItem(
        @PathVariable("id", required = false) id: Int,
        @RequestParam("name", required = false) name: String?,
        @RequestParam("desc1", required = false) desc1: String?,
        @RequestParam("desc2", required = false) desc2: String?,
        @RequestParam("per_nums", required = false) perNums: Int?,
        @RequestParam("price", required = false) price: Double?
    ):Result {
        if(id <= 0) {
            return Result(RESULT_FAIL,"id:参数错误")
        }
        if(Strings.isNullOrEmpty(name)) {
            return Result(RESULT_FAIL,"name:参数错误")
        }
        if(perNums == null || perNums <= 0) {
            return Result(RESULT_FAIL,"per_nums:参数错误")
        }
        val item = spotItemSvc.getSpotItem(id) ?: return Result(RESULT_FAIL,"对象不存在")
        item.name = name!!
        item.desc1 = if(Strings.isNullOrEmpty(desc1)) "" else desc1!!
        item.desc2 = if(Strings.isNullOrEmpty(desc2)) "" else desc2!!
        item.personalNums = perNums
        item.price = if(price == null || price <= 0) 0.0 else price
        val ok = spotItemSvc.updateSpotItem(item)
        if(ok) {
            return Result(RESULT_SUCCESS,"更新成功")
        }
        return Result(RESULT_FAIL,"更新失败")
    }

    @GetMapping("/item/prices/{itemId}")
    fun getItmPrices(
        @PathVariable("itemId", required = false) itemId: Int
    ):ResultT<List<SpotItemPriceDto>> {
        if(itemId <= 0) {
            return ResultT(RESULT_FAIL,"itemId:参数错误")
        }
        val itemPrices = spotItemSvc.querySpotItemPrices(itemId)
        val itemPricesDtos = ArrayList<SpotItemPriceDto>()
        for(price in itemPrices) {
            val dto = spotItemPriceTransformer.transform(price)!!
            itemPricesDtos.add(dto)
        }
        return ResultT(RESULT_SUCCESS,"成功",itemPricesDtos)
    }

    @GetMapping("/item/price/{id}")
    fun getItemPrice(
        @PathVariable("id", required = false) id: Int
    ):ResultT<SpotItemPriceDto> {
        if(id <= 0) {
            return ResultT(RESULT_FAIL,"id:参数错误")
        }
        val price = spotItemSvc.getSpotItemPrice(id) ?: return ResultT(RESULT_FAIL,"对象不存在")
        val dto = spotItemPriceTransformer.transform(price)!!
        return ResultT(RESULT_SUCCESS,"ok",dto)
    }

    @PostMapping("/item/price")
    fun addItemPrice(
        @RequestParam("item_id", required = false) itemId: Int?,
        @RequestParam("name", required = false) name: String?,
        @RequestParam("desc", required = false) desc: String?,
        @RequestParam("price", required = false) price: Double?,
        @RequestParam("unit", required = false) unit: String?,
        @RequestParam("channel_type", required = false) channelType: Int?,
        @RequestParam("state", required = false) state: Int?,
        @RequestParam("stocks", required = false) stocks: Int?
    ):Result {
        if(itemId == null || itemId <= 0) {
            return Result(RESULT_FAIL,"item_id:参数错误")
        }
        if(Strings.isNullOrEmpty(name)) {
            return Result(RESULT_FAIL,"name:参数错误")
        }
        if(Strings.isNullOrEmpty(unit)) {
            return Result(RESULT_FAIL,"unit:参数错误")
        }
        if(channelType == null || channelType <= 0) {
            return Result(RESULT_FAIL,"channel_type:参数错误")
        }
        if(state == null || state <= 0) {
            return Result(RESULT_FAIL,"state:参数错误")
        }
        val itemPrice = spotItemSvc.getSpotItemPrice(itemId,channelType)
        if(itemPrice != null) {
            return Result(RESULT_FAIL,"相同渠道已存在")
        }
        val newPrice = SpotItemPrice()
        newPrice.itemId = itemId
        newPrice.name = name!!
        newPrice.desc = if(Strings.isNullOrEmpty(desc)) "" else desc!!
        newPrice.price = price ?: 0.0
        newPrice.unit = unit!!
        newPrice.channelType = ChannelTypes.prase(channelType)
        newPrice.state = ItemPriceStates.prase(state)
        newPrice.stocks = stocks ?: 0
        newPrice.createTime = Date()
        val ok = spotItemSvc.addSpotItemPrice(newPrice)
        if(ok) {
            return Result(RESULT_SUCCESS,"创建成功")
        }
        return Result(RESULT_FAIL,"创建失败")
    }

    @PutMapping("/item/price/{id}")
    fun updateItemPrice(
        @PathVariable("id", required = false) id: Int,
        @RequestParam("name", required = false) name: String?,
        @RequestParam("desc", required = false) desc: String?,
        @RequestParam("price", required = false) price: Double?,
        @RequestParam("unit", required = false) unit: String?,
        @RequestParam("state", required = false) state: Int?,
        @RequestParam("stocks", required = false) stocks: Int?
    ):Result {
        if(id <= 0) {
            return Result(RESULT_FAIL,"id:参数错误")
        }
        if(Strings.isNullOrEmpty(name)) {
            return Result(RESULT_FAIL,"name:参数错误")
        }
        if(Strings.isNullOrEmpty(unit)) {
            return Result(RESULT_FAIL,"unit:参数错误")
        }
        if(state == null || state <= 0) {
            return Result(RESULT_FAIL,"state:参数错误")
        }
        val tp = spotItemSvc.getSpotItemPrice(id) ?: return Result(RESULT_FAIL,"对象不存在")
        tp.name = name!!
        tp.desc = if(Strings.isNullOrEmpty(desc)) "" else desc!!
        tp.unit = unit!!
        tp.price = price ?: 0.0
        tp.state = ItemPriceStates.prase(state)
        tp.stocks = stocks ?: 0
        val ok = spotItemSvc.updateSpotItemPrice(tp)
        if(ok) {
            return Result(RESULT_SUCCESS,"更新成功")
        }
        return Result(RESULT_FAIL,"更新失败")
    }

    @GetMapping("/item/tickets/{itemPriceId}")
    fun getTicketOfItems(
        @PathVariable("itemPriceId", required = false) itemPriceId: Int
    ): ResultT<List<TicketOfItemDto>> {
        if(itemPriceId <= 0) {
            return ResultT(RESULT_FAIL,"itemId:参数错误")
        }
        val tItems = spotItemSvc.queryItemOfTickets(itemPriceId)
        val dtos = ArrayList<TicketOfItemDto>()
        for(it in tItems) {
            val dto = tktOfItemTransformer.transform(it)!!
            dtos.add(dto)
        }
        return ResultT(RESULT_SUCCESS,"OK",dtos)
    }

    @GetMapping("/item/verifications")
    fun queryItemVerifications(
        @RequestParam("id", required = false) scenicSids: String?
    ) {

    }

    @GetMapping("/item/orders")
    fun querySpotItemOrders(
        @RequestParam("order_id", required = false) orderId: String?,
        @RequestParam("pay_no", required = false) payNo: String?,
        @RequestParam("scenic_sid", required = false) scenicSid: Int?,
        @RequestParam("state", required = false) state: Int?,
        @RequestParam("code", required = false) code: String?,
        @RequestParam("start_time", required = false) startTime: String?,
        @RequestParam("end_time", required = false) endTime: String?,
        @RequestParam("page", required = false) page: Int?,
        @RequestParam("size", required = false) size: Int?
    ):ResultT<PagedList<SpotItemOrderDto>> {
        if(!Strings.isNullOrEmpty(orderId)) {
            val order = spotItemSvc.getOrder(orderId!!) ?: return ResultT(RESULT_FAIL,"订单不存在")
            val pList = PagedList(1,1,1, listOf(orderTransformer.transform(order)!!))
            return ResultT(RESULT_SUCCESS,"ok",pList)
        }
        if(!Strings.isNullOrEmpty(payNo)) {
            val order = spotItemSvc.getOrderByPayNo(orderId!!) ?: return ResultT(RESULT_FAIL,"订单不存在")
            val pList = PagedList(1,1,1, listOf(orderTransformer.transform(order)!!))
            return ResultT(RESULT_SUCCESS,"ok",pList)
        }
        if(!Strings.isNullOrEmpty(code)) {
            val order = spotItemSvc.getOrderByCode(code!!) ?: return ResultT(RESULT_FAIL,"订单不存在")
            val pList = PagedList(1,1,1, listOf(orderTransformer.transform(order)!!))
            return ResultT(RESULT_SUCCESS,"ok",pList)
        }
        var p = 1
        var s = 20
        if(page != null && page > 0) {
            p = page
        }
        if(size != null && size > 0) {
            s = size
        }
        val query = SpotItemOrderQuery()
        query.scenicSid = scenicSid
        query.state = state
        query.startTime = startTime
        val orders = spotItemSvc.queryOrders(query,p,s)
        var pList = PagedList(p,s,orders.total,orders.list.map { a->orderTransformer.transform(a)!! })
        return ResultT(RESULT_SUCCESS,"OK",pList)
    }

    @GetMapping("/item/order/{orderId}")
    fun getSpotItemOrder(
        @PathVariable("orderId", required = false) orderId: String?
    ):ResultT<SpotItemOrderDto> {
        if(!Strings.isNullOrEmpty(orderId)) {
            val order = spotItemSvc.getOrder(orderId!!) ?: return ResultT(RESULT_FAIL,"订单不存在")
            return ResultT(RESULT_SUCCESS,"ok",orderTransformer.transform(order)!!)
        }
        return ResultT(RESULT_FAIL,"orderId:参数错误")
    }
}