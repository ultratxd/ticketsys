package com.cj.ticketsys.controller.b2b.ctrip

import com.alibaba.fastjson.JSON
import com.cj.ticketsys.cfg.SpringAppContext
import com.cj.ticketsys.controller.dto.RESULT_SUCCESS
import com.cj.ticketsys.dao.*
import com.cj.ticketsys.entities.*
import com.cj.ticketsys.entities.b2b.ctrip.B2bOtaCategory
import com.cj.ticketsys.svc.*
import com.cj.ticketsys.svc.b2b.B2BCtripSvc
import com.cj.ticketsys.svc.b2b.CtripRequest
import com.cj.ticketsys.svc.b2b.CtripResponseHeader
import com.cj.ticketsys.svc.b2b.CtripResponseMessage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.interceptor.TransactionAspectSupport
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.lang.Exception
import java.util.*
import javax.servlet.http.HttpServletRequest
import kotlin.collections.ArrayList

@RestController
@RequestMapping("/ota/v1/b2b/ctrip")
class CtripOrderController {

    @Autowired
    private lateinit var orderDao: OrderDao

    @Autowired
    private lateinit var subOrderDao: SubOrderDao

    private lateinit var partnerDao: PartnerDao

    @Autowired
    private lateinit var priceDao: TicketPriceDao

    @Autowired
    private lateinit var ctripSvc: B2BCtripSvc

    @Autowired
    private lateinit var b2bDao: B2bDao

    @Autowired
    private lateinit var b2bCtripDao: B2bCtripDao

    @Autowired
    private lateinit var tktBuy:TicketBuyer

    @Autowired
    private lateinit var orderSvc: OrderSvc

    @Autowired
    private lateinit var ticketSvc: TicketSvc

    @Autowired
    private lateinit var orderTicketCodeDao: OrderTicketCodeDao

    @Autowired
    private lateinit var inventoryManagement: InventoryManagement

    @Value("\${b2b.ctrip.momoSoptId}")
    private lateinit var soptId: String

    @Value("\${b2b.ctrip.aesSecret}")
    private lateinit var aesSecret: String

    @Value("\${b2b.ctrip.aesIVSecret}")
    private lateinit var aesIVSecret: String

    @Value("\${b2b.ctrip.accountId}")
    private lateinit var accountId: String

    @PostMapping("momo")
    @Transactional(rollbackFor = [Exception::class])
    fun ctripOrder(
            @RequestBody orderRequest: CtripRequest<String>,
            req: HttpServletRequest
    ): Any {
        val method = orderRequest.header!!.serviceName
        val reqTime = orderRequest.header!!.requestTime
        val reqBody = orderRequest.body!!
        val reqAccount = orderRequest.header!!.accountId
        val sign = orderRequest.header!!.sign

        var resp = CtripResponseMessage<Any>()
        resp.header = CtripResponseHeader()

        if(reqAccount != accountId) {
            resp.header!!.resultCode = "0003"
            resp.header!!.resultMessage = "供应商账户信息不正确"
            return resp
        }

        val checkSign = ctripSvc.makeSign(method,reqTime,reqBody)
        if(checkSign != sign) {
            resp.header!!.resultCode = "0002"
            resp.header!!.resultMessage = "签名错误"
            return resp
        }
        val body: String
        try {
            body = Encrypt.decrypt(reqBody, aesSecret, aesIVSecret)
        }catch (e:Exception) {
            e.printStackTrace()
            resp.header!!.resultCode = "0001"
            resp.header!!.resultMessage = "报文解析失败"
            return resp
        }
        when(method) {
            "VerifyOrder" -> {
                resp = verifyOrder(JSON.parseObject(body,CtripCreateOrderBody::class.java),req) as CtripResponseMessage<Any>
            }
            "CreateOrder" -> {
                resp =  newOrder(JSON.parseObject(body,CtripCreateOrderBody::class.java),req) as CtripResponseMessage<Any>
            }
            "CancelOrder" -> {
                resp =  cancelOrder(JSON.parseObject(body,CancelOrderBody::class.java),req) as CtripResponseMessage<Any>
            }
            "EditOrder" -> {
                resp =  modifyOrder(JSON.parseObject(body,CtripModifyOrderBody::class.java),req) as CtripResponseMessage<Any>
            }
            "RefundOrder" -> {
                resp =  refundOrder(JSON.parseObject(body,CtripRefundOrderBody::class.java),req) as CtripResponseMessage<Any>
            }
            "QueryOrder" -> {
                resp =  queryOrder(JSON.parseObject(body,CtripQueryOrderRequestBody::class.java),req) as CtripResponseMessage<Any>
            } else -> {
                resp.header!!.resultCode = "9999"
                resp.header!!.resultMessage = "未实现此接口"
                return resp
            }
        }
        if(resp.header!!.resultCode == "0000") {
            val respOk =  CtripResponseMessage<String>()
            respOk.header = resp.header
            resp.body = Encrypt.encrypt(JSON.toJSONString(resp.body),aesSecret,aesIVSecret)
            return resp
        }
        return resp
    }

    //@PostMapping("/verify_order")
    fun verifyOrder(
            data: CtripCreateOrderBody,
            req: HttpServletRequest
    ):CtripResponseMessage<CtripVerifyOrderResponseBody> {
        val items = data.items
        val resp = CtripResponseMessage<CtripVerifyOrderResponseBody>()
        val header = CtripResponseHeader()
        if (items == null) {
            resp.header!!.resultCode = "0001"
            resp.header!!.resultMessage = "报文解析失败"
            return resp
        }

        val bodyItems = ArrayList<CtripVerifyOrderResponseBodyItem>()
        header.resultCode = "1001"
        header.resultMessage = "产品PLU不存在/错误"
        for (item in items) {
            if (item?.PLU == null) {
                resp.header = header
                return resp
            }
            if (item.useStartDate != item.useEndDate) {
                header.resultCode = "1009"
                header.resultMessage = " 出行日期错误"
                resp.header = header
                return resp
            }
            val price = priceDao.getByPLU(item.PLU!!, ChannelTypes.Ctrip.code())
            if (price == null) {
                resp.header = header
                return resp
            }
            if(price.state != TicketStates.Enabled) {
                header.resultCode = "1002"
                header.resultMessage = " 产品已经下架"
                resp.header = header
                return resp
            }
            var passengerCheck = true
            if(item.passengers == null) {
                passengerCheck = false
            } else if(item.passengers!!.count { a->a!!.cardType == "0"} > 0) {
                passengerCheck = false
            }
            if(!passengerCheck) {
                header.resultCode = "1005"
                header.resultMessage = " 出行人证件信息不全"
                resp.header = header
                return resp
            }

            val inventorys = ArrayList<CtripVerifyOrderResponseBodyItemInventory>()
            inventorys.add(CtripVerifyOrderResponseBodyItemInventory(
                    useDate = item.useStartDate!!,
                    quantity = price.stocks
            ))
            val rItem = CtripVerifyOrderResponseBodyItem(
                    PLU = price.b2bPLU!!,
                    inventorys = inventorys
            )
            bodyItems.add(rItem)
        }
        header.resultCode = "0000"
        header.resultMessage = "操作成功"
        resp.header = header
        resp.body = CtripVerifyOrderResponseBody(items = bodyItems)
        return resp
    }

    //@PostMapping("/new_order")
    @Transactional(rollbackFor = [Exception::class])
    fun newOrder(
            data: CtripCreateOrderBody,
            req: HttpServletRequest
    ): CtripResponseMessage<CtripCreateOrderResponseBody> {

        val resp = CtripResponseMessage<CtripCreateOrderResponseBody>()
        resp.header = CtripResponseHeader()
        val respBody = CtripCreateOrderResponseBody()
        val bodyItems = ArrayList<CtripCreateOrderResponseBodyItem>()

        if(data.items == null) {
            resp.header!!.resultCode = "0001"
            resp.header!!.resultMessage = "报文解析失败"
            return resp
        }
        val order = b2bDao.getOrderByOtaId(data.otaOrderId,
            B2bOtaCategory.Ctrip.code())
        if(order != null) {
            resp.header!!.resultCode = "0000"
            resp.header!!.resultMessage = "订单已存在"

            respBody.otaOrderId = data.otaOrderId
            respBody.supplierOrderId = order.orderId
            respBody.supplierConfirmType = 1
            respBody.voucherSender = 2 //供应商发送凭证,非携程
            val tCode = orderTicketCodeDao.get(order.orderId) ?: throw Exception("生成提取码错误")
            val vouchers = ArrayList<CtripCreateOrderResponseBodyVoucher>()

            for(item in data.items!!) {
                val voucher = CtripCreateOrderResponseBodyVoucher()
                voucher.itemId = item?.itemId
                voucher.voucherType = 3
                voucher.voucherCode = tCode.code
                voucher.voucherData = ""
                vouchers.add(voucher)

                val price = priceDao.getByPLU(item!!.PLU!!, ChannelTypes.Ctrip.code())
                val inventorys = ArrayList<CtripCreateOrderResponseBodyInventory>()
                val inventory = CtripCreateOrderResponseBodyInventory()
                inventory.quantity = price!!.stocks
                inventory.useDate = item.useStartDate!!
                inventorys.add(inventory)
                val rItem = CtripCreateOrderResponseBodyItem()
                rItem.itemId = item.itemId
                rItem.inventorys = inventorys
                bodyItems.add(rItem)
            }
            respBody.vouchers = vouchers
            respBody.items = bodyItems
            resp.body = respBody
            return resp
        }

        val buyTicket = BuyTicketOrder()
        buyTicket.partner = partnerDao.get("ctrip")
        buyTicket.channelUid = data.items?.firstOrNull()?.openId ?: ""
        buyTicket.buyType = BuyTypes.B2B
        buyTicket.buyerIp = ""
        buyTicket.scenicSpotId = soptId.toInt()
        val buyTicketInfos = ArrayList<BuyTicketInfo>()
        for (item in data.items!!) {
            //检查出行人信息
            var passengerCheck = true
            if(item?.passengers == null) {
                passengerCheck = false
            } else if(item.passengers!!.count { a->a!!.cardType == "0"} > 0) {
                passengerCheck = false
            }
            if(!passengerCheck) {
                resp.header!!.resultCode = "1005"
                resp.header!!.resultMessage = " 出行人证件信息不全"
                return resp
            }

            val price = priceDao.getByPLU(item!!.PLU!!, ChannelTypes.Ctrip.code())
            if(price == null) {
                resp.header!!.resultCode = "1001"
                resp.header!!.resultMessage = "产品PLU不存在/错误"
                return resp
            }
            val buyItem = BuyTicketInfo()
            buyItem.ticketPriceId = price.id
            buyItem.ticketNums = item.quantity ?: 0
            if(item.useStartDate == null) {
                resp.header!!.resultCode = "1009"
                resp.header!!.resultMessage = "日期错误+具体错误类型日期"
                return resp
            }
            buyItem.date = item.useStartDate?.replace("-","")?.toInt() ?:0
            buyItem.userName = item.openId ?: ""
            buyItem.cardType = CardTypes.B2BProvider
            buyItem.userCard = ""
            buyItem.userMobile = ""
            buyTicketInfos.add(buyItem)

            val inventorys = ArrayList<CtripCreateOrderResponseBodyInventory>()
            val inventory = CtripCreateOrderResponseBodyInventory()
            inventory.quantity = price.stocks
            inventory.useDate = item.useStartDate!!
            inventorys.add(inventory)
            val rItem = CtripCreateOrderResponseBodyItem()
            rItem.itemId = item.itemId
            rItem.inventorys = inventorys
            bodyItems.add(rItem)
        }
        buyTicket.buyTickets.addAll(buyTicketInfos)
        val buyResult = tktBuy.buy(buyTicket)
        var respCode: String? = null
        var respMsg:String? = null
        when(buyResult.status) {
            "BUY:1005" -> {
                respCode = "1007"
                respMsg = "产品价格不存在"
            }
            "BUY:1006" -> {
                respCode = "1003"
                respMsg = "库存不足"
            }
            "BUY:1007" -> {
                respCode = "1009"
                respMsg = "日期错误+具体错误类型日期"
            }
            "BUY:1008" -> {
                respCode = "1004"
                respMsg = "被限购"
            }
            else -> when {
                buyResult.status != RESULT_SUCCESS -> {
                    respCode = "8"
                    respMsg = "其他错误"
                }
            }
        }
        if(respCode != null) {
            resp.header!!.resultCode = respCode
            resp.header!!.resultMessage = respMsg!!
            return resp
        }

        val orderId =  buyResult.order!!.orderId
        val b2bOrder = ctripSvc.createOrder(data, orderId)
        if(b2bOrder == null || b2bOrder.id <= 0) {
            resp.header!!.resultCode = "8"
            resp.header!!.resultMessage = "创建订单错误"
            return resp
        }

        //生成凭证
        val ok = orderSvc.completedPay(orderId, Date(), data.sequenceId ?: "ctrip")
        if(ok) {
            val deliver = SpringAppContext.getBean(Consts.IssueTicketDeliveName) as IssueTicketDeliver
            deliver.issue(orderId)
        }
        val tCode = orderTicketCodeDao.get(orderId) ?: throw Exception("生成提取码错误")

        respBody.otaOrderId = data.otaOrderId
        respBody.supplierOrderId = buyResult.order!!.orderId
        respBody.supplierConfirmType = 1
        respBody.voucherSender = 2 //供应商发送凭证,非携程
        val vouchers = ArrayList<CtripCreateOrderResponseBodyVoucher>()
        for(item in data.items!!) {
            val voucher = CtripCreateOrderResponseBodyVoucher()
            voucher.itemId = item?.itemId
            voucher.voucherType = 3
            voucher.voucherCode = tCode.code
            voucher.voucherData = ""
            vouchers.add(voucher)
        }
        respBody.vouchers = vouchers
        respBody.items = bodyItems
        resp.body = respBody
        resp.header!!.resultCode = "0000"
        resp.header!!.resultMessage = "操作成功"
        return resp
    }

    //@PostMapping("/cancel_order")
    @Transactional(rollbackFor = [Exception::class])
    fun cancelOrder(
            data: CancelOrderBody,
            req: HttpServletRequest
    ): CtripResponseMessage<CtripCancelOrderResponseBody> {
        val resp = CtripResponseMessage<CtripCancelOrderResponseBody>()
        resp.header = CtripResponseHeader()
        val b2bOrder = b2bDao.getOrderByOtaId(data.otaOrderId!!,
            B2bOtaCategory.Ctrip.code())
        if(b2bOrder == null) {
            resp.header!!.resultCode = "2001"
            resp.header!!.resultMessage = "该订单号不存在"
            return resp
        }
        val order = orderDao.get(data.supplierOrderId!!)
        if(order == null) {
            resp.header!!.resultCode = "2001"
            resp.header!!.resultMessage = "该订单号不存在"
            return resp
        }
        if(order.state == OrderStates.Cancel || order.state == OrderStates.Refunded || order.state == OrderStates.Used) {
            resp.header!!.resultCode = "0000"
            resp.header!!.resultMessage = "该订单已经使用或已退票"
            resp.body = CtripCancelOrderResponseBody()
            resp.body!!.supplierConfirmType = 1
            return resp
        }
        val childOrders = subOrderDao.gets(order.orderId)
        for (item in data.items!!) {
            val price = priceDao.getByPLU(item!!.PLU!!, ChannelTypes.Ctrip.code())
            if(price == null) {
                resp.header!!.resultCode = "2004"
                resp.header!!.resultMessage = "取消数量不正确"
                return resp
            }
            for(subOrder in childOrders) {
                if(subOrder.ticketPid == price.id
                        && item.quantity!! != subOrder.nums) {
                    resp.header!!.resultCode = "2004"
                    resp.header!!.resultMessage = "取消数量不正确"
                    return resp
                }
            }
        }

        val ok = orderSvc.cancelOrder(data.supplierOrderId!!)
        if(ok) {
            resp.header!!.resultCode = "0000"
            resp.header!!.resultMessage = "取消成功"
            resp.body = CtripCancelOrderResponseBody()
            resp.body!!.supplierConfirmType = 1
            return resp
        }
        resp.header!!.resultCode = "2003"
        resp.header!!.resultMessage = "该订单已过期，不可退"
        return resp
    }

    //@PostMapping("/modify_order")
    @Transactional(rollbackFor = [Exception::class])
    fun modifyOrder(
            data: CtripModifyOrderBody,
            req: HttpServletRequest
    ): CtripResponseMessage<CtripModifyOrderResponseBody> {
        val resp = CtripResponseMessage<CtripModifyOrderResponseBody>()
        resp.header = CtripResponseHeader()
        if(data.supplierOrderId == null) {
            resp.header!!.resultCode = "0001"
            resp.header!!.resultMessage = "报文解析失败"
            return resp
        }
        val tktCode = orderTicketCodeDao.get(data.supplierOrderId!!)
        if(tktCode == null) {
            resp.header!!.resultCode = "6001"
            resp.header!!.resultMessage = "该订单号不存在"
            return resp
        }
        if(tktCode.state == TicketCodeStates.Used) {
            resp.header!!.resultCode = "6100"
            resp.header!!.resultMessage = "订单已换票,不可修改"
            return resp
        }

        val subOrders = subOrderDao.gets(data.supplierOrderId!!)
        for (item in data.items!!) {
            val cItem = b2bCtripDao.getItem(data.otaOrderId!!,item!!.itemId!!)
            if(cItem == null) {
                resp.header!!.resultCode = "6001"
                resp.header!!.resultMessage = "该订单号不存在"
                return resp
            }
            val price = priceDao.get(cItem.ticketPriceId)
            if(price == null) {
                resp.header!!.resultCode = "6003"
                resp.header!!.resultMessage = "此价格已撤销,不允许修改"
                return resp
            }
            val sOrder = subOrders.firstOrNull { a -> a.ticketPid == cItem.ticketPriceId }
            if(sOrder == null) {
                resp.header!!.resultCode = "6001"
                resp.header!!.resultMessage = "该订单号不存在"
                return resp
            }
            val targetUseStartDate = item.targetUseStartDate!!.replace("-","").toInt()
            val ts = ticketSvc.getTicket(
                    price.tid,
                    Utils.intToDate(targetUseStartDate),
                   ChannelTypes.Ctrip
            )
            if(ts?.prices?.size == 0) {
                resp.header!!.resultCode = "6101"
                resp.header!!.resultMessage = "不能修改到指定日期"
                return resp
            }
            val surplus = inventoryManagement.surplus(price.id)
            if (surplus <= 0) {
                resp.header!!.resultCode = "6102"
                resp.header!!.resultMessage = "此日期票已售罄"
                return resp
            }
            val ok = orderSvc.modifyUseDate(sOrder.id, Utils.intToDate(targetUseStartDate))
            if(!ok) {
                resp.header!!.resultCode = "6103"
                resp.header!!.resultMessage = "修改失败"
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()
                return resp
            }
            b2bCtripDao.updateItemDate(cItem.id, Utils.stringToDate(item.targetUseStartDate!!,"yyyy-MM-dd"),Utils.stringToDate(item.targetUseEndDate!!,"yyyy-MM-dd"))
        }
        resp.header!!.resultCode = "0000"
        resp.header!!.resultMessage = "操作成功"
        return resp
    }

    //@PostMapping("/refund_order")
    @Transactional(rollbackFor = [Exception::class])
    fun refundOrder(
            data: CtripRefundOrderBody,
            req: HttpServletRequest
    ): CtripResponseMessage<CtripRefundOrderResponseBody> {
        val resp = CtripResponseMessage<CtripRefundOrderResponseBody>()
        resp.header = CtripResponseHeader()
        val order = orderDao.get(data.supplierOrderId!!)
        val subOrders = subOrderDao.gets(data.supplierOrderId!!)
        if(order == null) {
            resp.header!!.resultCode = "3001"
            resp.header!!.resultMessage = "该订单号不存在"
            return resp
        }
        if(subOrders.isEmpty()) {
            resp.header!!.resultCode = "3001"
            resp.header!!.resultMessage = "该订单号不存在"
            return resp
        }
        for(item in data.items!!) {
            val cItem = b2bCtripDao.getItem(data.otaOrderId!!,item!!.itemId!!)
            if(cItem == null) {
                resp.header!!.resultCode = "3001"
                resp.header!!.resultMessage = "该订单号不存在"
                return resp
            }
            val subOrder = subOrders.firstOrNull { a->a.ticketPid == cItem.ticketPriceId }
            if(subOrder == null) {
                resp.header!!.resultCode = "3001"
                resp.header!!.resultMessage = "该订单号不存在"
                return resp
            }
            subOrderDao.updateRefund(cItem.id,OrderStates.Refunded,Date(),null)
        }
        val updatedSubOrders = subOrderDao.gets(data.supplierOrderId!!)
        val allRefunds = updatedSubOrders.count { a->a.state == OrderStates.Refunded }

        if(allRefunds == order.childs) {
            orderDao.updateState(order.orderId,OrderStates.Refunded)
        } else  {
            orderDao.updateState(order.orderId,OrderStates.RefundedPart)
        }
        resp.header!!.resultCode = "0000"
        resp.header!!.resultMessage = "退款成功"
        return resp
    }

    fun queryOrder(
            data: CtripQueryOrderRequestBody,
            req: HttpServletRequest
    ): CtripResponseMessage<CtripQueryOrderResponseBody> {
        val resp = CtripResponseMessage<CtripQueryOrderResponseBody>()
        resp.header = CtripResponseHeader()
        val order = orderDao.get(data.supplierOrderId!!)
        if(order == null) {
            resp.header!!.resultCode = "4001"
            resp.header!!.resultMessage = "该订单号不存在"
            return resp
        }
        val cOrder = b2bDao.getOrderByOtaId(data.otaOrderId!!,
            B2bOtaCategory.Ctrip.code())
        if(cOrder == null) {
            resp.header!!.resultCode = "4001"
            resp.header!!.resultMessage = "该订单号不存在"
            return resp
        }
        val cItems = b2bCtripDao.getItemsByOrderId(data.supplierOrderId!!)
        val items = ArrayList<CtripQueryOrderResponseBodyItem>()
        val tCode = orderTicketCodeDao.get(order.orderId)
        for(cItem in cItems) {
            val item = CtripQueryOrderResponseBodyItem()
            item.itemId = cItem.itemId
            item.useStartDate = Utils.dateZoneFormat(cItem.useStartDate!!,"yyyy-MM-dd")
            item.useEndDate = Utils.dateZoneFormat(cItem.useEndDate!!,"yyyy-MM-dd")
            item.orderStatus = orderStatusConvert(order.state)
            item.quantity = cItem.quantity
            item.useQuantity = if(tCode!!.state == TicketCodeStates.Unused || tCode.state == TicketCodeStates.Invalid) 0 else 1
            item.cancelQuantity = if(tCode.state == TicketCodeStates.Invalid) 1 else 0
            items.add(item)
        }
        resp.body = CtripQueryOrderResponseBody()
        resp.body!!.items = items
        resp.body!!.otaOrderId = cOrder.otaId
        resp.body!!.supplierOrderId = cOrder.orderId

        resp.header!!.resultCode = "0000"
        resp.header!!.resultMessage = "操作成功"
        return resp
    }

    fun orderStatusConvert(state:OrderStates):Int {
        return when(state) {
            OrderStates.Init -> 2
            OrderStates.Issued -> 2
            OrderStates.Used -> 8
            OrderStates.Closed -> 10
            OrderStates.Cancel -> 5
            OrderStates.Refunded -> 10
            OrderStates.RefundedPart -> 10
            OrderStates.Paied -> 2
            else -> 10
        }
    }
}