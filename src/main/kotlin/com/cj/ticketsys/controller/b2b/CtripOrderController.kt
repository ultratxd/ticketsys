package com.cj.ticketsys.controller.b2b

import com.cj.ticketsys.cfg.SpringAppContext
import com.cj.ticketsys.controller.dto.RESULT_SUCCESS
import com.cj.ticketsys.dao.*
import com.cj.ticketsys.entities.*
import com.cj.ticketsys.entities.b2b.B2bOtaCategory
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
import java.nio.channels.Channel
import java.util.*
import javax.servlet.http.HttpServletRequest
import kotlin.collections.ArrayList

@RestController
@RequestMapping("/ota/v1/b2b/ctrip/momo")
class CtripOrderController {

    @Autowired
    private lateinit var orderDao: OrderDao

    @Autowired
    private lateinit var subOrderDao: SubOrderDao

    @Autowired
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

    @PostMapping("/verify_order")
    fun verifyOrder(
            @RequestBody orderRequest: CtripRequest<CtripCreateOrderBody>,
            req: HttpServletRequest
    ):CtripResponseMessage<CtripVerifyOrderResponseBody>  {
        val items = orderRequest.body?.items
        val resp = CtripResponseMessage<CtripVerifyOrderResponseBody>()
        val header = CtripResponseHeader()
        if(orderRequest.body?.items == null) {
            resp.header!!.resultCode = "0001"
            resp.header!!.resultMessage = "报文解析失败"
            return resp
        }

        val bodyItems = ArrayList<CtripVerifyOrderResponseBodyItem>()
        if(items != null) {
            header.resultCode = "1001"
            header.resultMessage = "产品PLU不存在/错误"
            for (item in items) {
                if(item?.PLU == null) {
                    resp.header = header
                    return resp
                }
                if(item.useStartDate != item.useEndDate) {
                    header.resultCode = "1009"
                    header.resultMessage = " 出行日期错误"
                    resp.header = header
                    return resp
                }
                val price = priceDao.getByPLU(item.PLU!!, ChannelTypes.Ctrip.code())
                if(price == null) {
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
        }
        header.resultCode = "0000"
        header.resultMessage = "操作成功"
        resp.header = header
        resp.body = CtripVerifyOrderResponseBody(items = bodyItems)
        return resp
    }

    @PostMapping("/new_order")
    @Transactional(rollbackFor = [Exception::class])
    fun newOrder(
            @RequestBody orderRequest: CtripRequest<CtripCreateOrderBody>,
            req: HttpServletRequest
    ): CtripResponseMessage<CtripCreateOrderResponseBody> {

        val resp = CtripResponseMessage<CtripCreateOrderResponseBody>()
        resp.header = CtripResponseHeader()

        if(orderRequest.body?.items == null) {
            resp.header!!.resultCode = "0001"
            resp.header!!.resultMessage = "报文解析失败"
            return resp
        }
        val order = b2bDao.getOrderByOtaId(orderRequest.body!!.otaOrderId,B2bOtaCategory.Ctrip.code())
        if(order != null) {
            resp.header!!.resultCode = "0003"
            resp.header!!.resultMessage = "订单已存在"
            return resp
        }

        val buyTicket = BuyTicketOrder()
        buyTicket.partner = partnerDao.get("ctrip")
        buyTicket.channelUid = orderRequest.body!!.items?.firstOrNull()?.openId ?: ""
        buyTicket.buyType = BuyTypes.B2B
        buyTicket.buyerIp = ""
        buyTicket.scenicSpotId = soptId.toInt()
        val buyTicketInfos = ArrayList<BuyTicketInfo>()
        for (item in orderRequest.body!!.items!!) {
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
        val b2bOrder = ctripSvc.createOrder(orderRequest.body!!, orderId)
        if(b2bOrder == null || b2bOrder.id <= 0) {
            resp.header!!.resultCode = "8"
            resp.header!!.resultMessage = "创建订单错误"
            return resp
        }

        //生成凭证
        val ok = orderSvc.completedPay(orderId, Date(), orderRequest.body?.sequenceId ?: "ctrip")
        if(ok) {
            val deliver = SpringAppContext.getBean(Consts.IssueTicketDeliveName) as IssueTicketDeliver
            deliver.issue(orderId)
        }
        val tCode = orderTicketCodeDao.get(orderId) ?: throw Exception("生成提取码错误")

        val respBody = CtripCreateOrderResponseBody()
        respBody.otaOrderId = orderRequest.body!!.otaOrderId
        respBody.supplierOrderId = buyResult.order!!.orderId
        respBody.supplierConfirmType = 1
        respBody.voucherSender = 2 //供应商发送凭证,非携程
        val vouchers = ArrayList<CtripCreateOrderResponseBodyVoucher>()
        for(item in orderRequest.body!!.items!!) {
            val voucher = CtripCreateOrderResponseBodyVoucher()
            voucher.itemId = item?.itemId
            voucher.voucherType = 3
            voucher.voucherCode = tCode.code
            voucher.voucherData = ""
            vouchers.add(voucher)
        }
        respBody.vouchers = vouchers
        return resp
    }

    @PostMapping("/cancel_order")
    @Transactional(rollbackFor = [Exception::class])
    fun cancelOrder(
            @RequestBody orderRequest: CtripRequest<CancelOrderBody>,
            req: HttpServletRequest
    ): CtripResponseMessage<CtripCancelOrderResponseBody> {
        val resp = CtripResponseMessage<CtripCancelOrderResponseBody>()
        resp.header = CtripResponseHeader()
        val orderId = orderRequest.body?.supplierOrderId
        if(orderId == null) {
            resp.header!!.resultCode = "2001"
            resp.header!!.resultMessage = "该订单号不存在"
            return resp
        }
        val order = orderDao.get(orderId)
        if(order == null) {
            resp.header!!.resultCode = "2001"
            resp.header!!.resultMessage = "该订单号不存在"
            return resp
        }
        if(order.state == OrderStates.Cancel || order.state == OrderStates.Refunded || order.state == OrderStates.Used) {
            resp.header!!.resultCode = "2002"
            resp.header!!.resultMessage = "该订单已经使用或已退票"
            return resp
        }
        val childOrders = subOrderDao.gets(order.orderId)
        for (item in orderRequest.body!!.items!!) {
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

        val ok = orderSvc.cancelOrder(orderId)
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

    @PostMapping("/modify_order")
    @Transactional(rollbackFor = [Exception::class])
    fun modifyOrder(
            @RequestBody orderRequest: CtripRequest<CtripModifyOrderBody>,
            req: HttpServletRequest
    ): CtripResponseMessage<CtripModifyOrderResponseBody> {
        val resp = CtripResponseMessage<CtripModifyOrderResponseBody>()
        resp.header = CtripResponseHeader()
        if(orderRequest.body?.supplierOrderId == null) {
            resp.header!!.resultCode = "0001"
            resp.header!!.resultMessage = "报文解析失败"
            return resp
        }
        val tktCode = orderTicketCodeDao.get(orderRequest.body!!.supplierOrderId!!)
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

        val subOrders = subOrderDao.gets(orderRequest.body!!.supplierOrderId!!)
        for (item in orderRequest.body!!.items!!) {
            val cItem = b2bCtripDao.getItem(orderRequest.body!!.otaOrderId!!,item!!.itemId!!)
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
        }
        resp.header!!.resultCode = "0000"
        resp.header!!.resultMessage = "操作成功"
        return resp
    }

    @PostMapping("/refund_order")
    @Transactional(rollbackFor = [Exception::class])
    fun refundOrder(
            @RequestBody orderRequest: CtripRequest<CtripRefundOrderBody>,
            req: HttpServletRequest
    ): CtripResponseMessage<CtripRefundOrderResponseBody> {
        val resp = CtripResponseMessage<CtripRefundOrderResponseBody>()
        resp.header = CtripResponseHeader()
        val order = orderDao.get(orderRequest.body!!.supplierOrderId!!)
        val subOrders = subOrderDao.gets(orderRequest.body!!.supplierOrderId!!)
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
        for(item in orderRequest.body!!.items!!) {
            val cItem = b2bCtripDao.getItem(orderRequest.body!!.otaOrderId!!,item!!.itemId!!)
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
        val updatedSubOrders = subOrderDao.gets(orderRequest.body!!.supplierOrderId!!)
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
}