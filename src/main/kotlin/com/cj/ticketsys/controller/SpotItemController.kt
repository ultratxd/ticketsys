package com.cj.ticketsys.controller

import com.cj.ticketsys.cfg.SpringAppContext
import com.cj.ticketsys.controller.dto.*
import com.cj.ticketsys.controller.manage.dto.MSpotItemDto
import com.cj.ticketsys.controller.req.ItemBuyRequest
import com.cj.ticketsys.controller.req.ItemVerifyItemReq
import com.cj.ticketsys.dao.PartnerDao
import com.cj.ticketsys.entities.BuyTypes
import com.cj.ticketsys.entities.CardTypes
import com.cj.ticketsys.entities.OrderStates
import com.cj.ticketsys.entities.PagedList
import com.cj.ticketsys.entities.spotItem.*
import com.cj.ticketsys.svc.*
import com.cj.ticketsys.svc.impl.IssueTicketRunner
import com.google.common.base.Strings
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.util.*
import kotlin.collections.ArrayList

@RestController
@RequestMapping("/ota/v1/item")
class SpotItemController {

    @Autowired
    private lateinit var partnerDao: PartnerDao

    @Autowired
    private lateinit var spotItemSvc: SpotItemSvc

    @Autowired
    private lateinit var itemBuyer: ItemBuyer

    @Autowired
    private lateinit var itemOrderSvc: ItemOrderSvc

    @Autowired
    private lateinit var itemTransformer: DocTransformer<SpotItem, SpotItemDto>

    @Autowired
    private lateinit var itemPriceTransformer: DocTransformer<SpotItemPrice, SpotItemPriceDto>

    @Autowired
    private lateinit var orderTransformer: DocTransformer<SpotItemOrder,SpotItemOrderDto>

    @Autowired
    private lateinit var subOrderTransformer: DocTransformer<SpotItemSubOrder,SpotItemSubOrderDto>

    @Autowired
    private lateinit var spotItemVerifier: SpotItemVerifier

    @Autowired
    private lateinit var itemOrderDetailTransformer:DocTransformer<SpotItemOrderDetail,SpotItemOrderDetailDto>

    @GetMapping("/all/{sid}")
    fun getItems(
        @PathVariable("sid", required = true) sid: Int,
        @RequestParam("partner_id", required = false) partnerId: String?
    ): ResultT<List<SpotItemDto>> {
        if (sid <= 0 || Strings.isNullOrEmpty(partnerId)) {
            return ResultT(RESULT_FAIL, "参数错误")
        }
        val partner = partnerDao.get(partnerId!!) ?: return ResultT(RESULT_FAIL, "商户不存在")
        val items = spotItemSvc.querySpotItemsByChannel(sid,partner.channelType)
        val dtos = ArrayList<SpotItemDto>()
        for(item in items) {
            if(item.enabled) {
                val price = spotItemSvc.getSpotItemPrice(item.id,partner.channelType)
                if(price != null && price.state == ItemPriceStates.Enabled) {
                    val dto = itemTransformer.transform(item)!!
                    val pDto = itemPriceTransformer.transform(price)!!
                    dto.priceMS.add(pDto)
                    dtos.add(dto)
                }
            }
        }
        return ResultT(RESULT_SUCCESS,"ok", dtos)
    }

    @GetMapping("/{id}")
    fun getItem(
        @PathVariable("id", required = true) id: Int,
        @RequestParam("partner_id", required = false) partnerId: String?
    ): ResultT<SpotItemDto> {
        if (id <= 0 || Strings.isNullOrEmpty(partnerId)) {
            return ResultT(RESULT_FAIL, "参数错误")
        }
        val partner = partnerDao.get(partnerId!!) ?: return ResultT(RESULT_FAIL, "商户不存在")
        val item = spotItemSvc.getSpotItem(id) ?: return ResultT(RESULT_FAIL, "项目不存在")
        if(item.enabled) {
            val price = spotItemSvc.getSpotItemPrice(item.id,partner.channelType)
            if(price != null && price.state == ItemPriceStates.Enabled) {
                val dto = itemTransformer.transform(item)!!
                val pDto = itemPriceTransformer.transform(price)!!
                dto.priceMS.add(pDto)
                return ResultT(RESULT_SUCCESS,"ok", dto)
            }
        }
        return ResultT(RESULT_FAIL,"项目不存在")
    }

    @PostMapping("/buy")
    fun buy(
        @RequestBody buy: ItemBuyRequest
    ): ResultT<BuyItemResp> {
        if (Strings.isNullOrEmpty(buy.partnerId)
            || Strings.isNullOrEmpty(buy.channelUid)
        ) {
            return ResultT(RESULT_FAIL, "参数错误")
        }
        if (buy.buyItems.isEmpty()) {
            return ResultT(RESULT_FAIL, "未购买商品")
        }
        if (!BuyTypes.values().any { s -> s.value == buy.buyType }) {
            return ResultT(RESULT_FAIL, "购买类型不存在")
        }
        for (buyItem in buy.buyItems) {
            if (buyItem.date.toString().length != 8) {
                return ResultT(RESULT_FAIL, "日期格式错误")
            }
        }
        val bItem = BuyItemOrder()
        val partner = partnerDao.get(buy.partnerId) ?: return ResultT(RESULT_FAIL, "商户不存在")
        bItem.partner = partner
        if (!Strings.isNullOrEmpty(buy.buyerIp)) {
            bItem.buyerIp = buy.buyerIp
        }
        bItem.channelUid = buy.channelUid
        bItem.buyType = BuyTypes.prase(buy.buyType)
        for (buyItem in buy.buyItems) {
            if (buyItem.itemNums <= 0) {
                return ResultT(RESULT_FAIL, "购买数量错误")
            }
            if (buyItem.itemPriceId <= 0) {
                return ResultT(RESULT_FAIL, "票价未选择")
            }
//            if (Strings.isNullOrEmpty(buyItem.userCard)) {
//                return ResultT(RESULT_FAIL, "身份证未填写")
//            }
//            if (Strings.isNullOrEmpty(buyItem.userMobile)) {
//                return ResultT(RESULT_FAIL, "手机号未填写")
//            }
//            if (buyItem.cardType <= 0) {
//                return ResultT(RESULT_FAIL, "证件类型错误")
//            }
            val buyItemInfo = BuyItemInfo()
            buyItemInfo.itemPriceId = buyItem.itemPriceId
            buyItemInfo.itemNums = buyItem.itemNums
            buyItemInfo.date = buyItem.date
//            buyItemInfo.userName = buyItem.userName
//            buyItemInfo.cardType = CardTypes.prase(ticket.cardType)
//            buyItemInfo.userCard = buyItem.userCard
//            buyItemInfo.userMobile = buyItem.userMobile
            bItem.items.add(buyItemInfo)
        }

        val result = itemBuyer.buy(bItem)
        if (result.status == RESULT_SUCCESS) {
            val resp = BuyItemResp()
            resp.orderNo = result.order!!.orderId
            resp.expires = Date(result.order!!.createTime.time + 30 * 60 * 1000).time / 1000
            resp.itemCount = result.order!!.nums
            resp.totalMoney = result.order!!.totalPrice
            return ResultT(RESULT_SUCCESS, "ok", resp)
        }
        return ResultT(result.status, result.msg)
    }

    @PutMapping("/order/cancel/{orderNo}")
    fun cancel(
        @PathVariable("orderNo", required = true) orderNo: String
    ): Result {
        if (Strings.isNullOrEmpty(orderNo)) {
            return Result(RESULT_FAIL, "参数错误")
        }
        val ok = itemOrderSvc.cancelOrder(orderNo)
        if (ok) {
            return Result(RESULT_SUCCESS, "ok")
        }
        return Result(RESULT_FAIL, "fail")
    }

    @PutMapping("/order/pay/success")
    fun paySuccess(
        @RequestParam("order_no", required = true) orderNo: String?,
        @RequestParam("pay_time", required = true) payTime: Long?,
        @RequestParam("pay_no", required = true) payNo: String?,
        @RequestParam("partner_id", required = false) partnerId: String?
    ): ResultT<String> {
        if (Strings.isNullOrEmpty(orderNo)) {
            return ResultT(RESULT_FAIL, "参数错误:order_no")
        }
        if (payTime == null || payTime <= 0) {
            return ResultT(RESULT_FAIL, "参数错误:pay_time")
        }
        if (Strings.isNullOrEmpty(payNo)) {
            return ResultT(RESULT_FAIL, "参数错误:pay_no")
        }
        val order = spotItemSvc.getOrder(orderNo!!) ?: return ResultT(RESULT_FAIL, "订单不存在或已过期")
        if (order.state != OrderStates.Init) {
            return ResultT("paied", "订单已支付")
        }
        val ok = itemOrderSvc.completedPay(orderNo, Date(payTime * 1000), payNo!!)
        if (ok) {
            IssueTicketRunner.run(Runnable {
                val deliver = SpringAppContext.getBean(Consts.IssueItemCodeDeliveName) as IssueTicketDeliver
                deliver.issue(orderNo)
            })
            return ResultT(RESULT_SUCCESS, "ok", "")
        }
        return ResultT(RESULT_FAIL, "fail", "")
    }

    @GetMapping("/order/my/{uid}")
    fun getMyOrders(
        @PathVariable("uid", required = true) uid: String,
        @RequestParam("state", required = false) state: Short?,
        @RequestParam("partner_id", required = false) partnerId: String?,
        @RequestParam("page", required = false) page: Int?,
        @RequestParam("size", required = false) size: Int?
    ):ResultT<PagedList<SpotItemOrderDto>> {
        if (Strings.isNullOrEmpty(uid) || Strings.isNullOrEmpty(partnerId)) {
            return ResultT(RESULT_FAIL, "参数错误")
        }
        var oState: OrderStates? = null
        if (state != null) {
            if (!OrderStates.values().any { s -> s.value == state }) {
                return ResultT(RESULT_FAIL, "state状态不存在")
            }
            oState = OrderStates.prase(state.toInt())
        }
        val query = SpotItemOrderQuery()
        if(oState != null) {
            query.state = oState
        }
        var oPage = 1
        var oSize = 20
        if(page != null && page > 0) {
            oPage = page
        }
        if(size != null && size > 0) {
            oSize = size
        }
        val partner = partnerDao.get(partnerId!!) ?: return ResultT(RESULT_FAIL, "商户不存在")
        query.channelId = partner.id
        query.channelUid = uid
        val orders = spotItemSvc.queryOrders(query,oPage,oSize)
        val list = ArrayList<SpotItemOrderDto>()
        for(order in orders.list) {
            val dto = orderTransformer.transform(order)!!
            val subOrders = spotItemSvc.querySubOrders(order.orderId)
            for (subOrder in subOrders) {
                dto.subOrders.add(subOrderTransformer.transform(subOrder)!!)
            }
            list.add(dto)
        }
        val pList = PagedList(orders.page,orders.size,orders.total,list)
        return ResultT(RESULT_SUCCESS,"ok",pList)
    }

    @GetMapping("/order/{orderNo}")
    fun getMyOrders(
        @PathVariable("orderNo", required = true) orderNo: String
    ):ResultT<SpotItemOrderDto> {
        if (Strings.isNullOrEmpty(orderNo)) {
            return ResultT(RESULT_FAIL, "参数错误")
        }
        val order = spotItemSvc.getOrder(orderNo) ?: return ResultT(RESULT_FAIL, "订单不存在")
        val dto = orderTransformer.transform(order)!!
        val subOrders = spotItemSvc.querySubOrders(order.orderId)
        for (subOrder in subOrders) {
            dto.subOrders.add(subOrderTransformer.transform(subOrder)!!)
        }
        return ResultT(RESULT_SUCCESS, "OK",dto)
    }

    /**
     * 兑换码包含小项目列表
     */
    @GetMapping("/order/ex/detail/{code}")
    fun codeItems(
        @PathVariable("code", required = true) code: String
    ):ResultT<List<SpotItemOrderDetailDto>> {
        if(Strings.isNullOrEmpty(code)) {
            return ResultT(RESULT_FAIL,"code不能为空")
        }
        val result = spotItemVerifier.resolve(code)
        if(result.code == RESULT_FAIL) {
            return ResultT(RESULT_FAIL,result.msg)
        }
        val dtos = ArrayList<SpotItemOrderDetailDto>()
        for(item in result.data!!) {
            dtos.add(itemOrderDetailTransformer.transform(item)!!)
        }
        return ResultT(RESULT_SUCCESS,"ok",dtos)
    }

    /**
     * 小项目核销
     */
    @PostMapping("/order/ex/verify")
    fun verify(
        @RequestBody itemNums: ItemVerifyItemReq
    ):ResultT<List<SpotItemOrderDetailDto>> {
        if(Strings.isNullOrEmpty(itemNums.code)
            || Strings.isNullOrEmpty(itemNums.uid)) {
            return ResultT(RESULT_FAIL, "参数错误")
        }
        if(itemNums.verifyItems == null || !itemNums.verifyItems!!.any()) {
            return ResultT(RESULT_FAIL, "参数错误")
        }
        val verifyItemNums = ArrayList<VerifyItemNums>()
        for(vi in itemNums.verifyItems!!) {
            if(vi.itemId == null || vi.nums == null) {
                return ResultT(RESULT_FAIL, "参数错误")
            }
            verifyItemNums.add(VerifyItemNums(vi.itemId!!,vi.nums!!))
        }
        val result = spotItemVerifier.verifying(itemNums.code!!,verifyItemNums,itemNums.uid!!)
        if(result.code == RESULT_FAIL) {
            return ResultT(RESULT_FAIL,result.msg)
        }
        val dtos = ArrayList<SpotItemOrderDetailDto>()
        for(item in result.data!!) {
            dtos.add(itemOrderDetailTransformer.transform(item)!!)
        }
        return ResultT(RESULT_SUCCESS,"ok",dtos)
    }
}