package com.cj.ticketsys.controller

import com.cj.ticketsys.cfg.SpringAppContext
import com.cj.ticketsys.controller.dto.*
import com.cj.ticketsys.controller.req.BuyRequest
import com.cj.ticketsys.dao.OrderDao
import com.cj.ticketsys.dao.OrderTicketCodeDao
import com.cj.ticketsys.dao.SubOrderDao
import com.cj.ticketsys.entities.*
import com.cj.ticketsys.svc.Consts
import com.cj.ticketsys.svc.DocTransformer
import com.cj.ticketsys.svc.IssueTicketDeliver
import com.cj.ticketsys.svc.OrderSvc
import com.cj.ticketsys.svc.impl.IssueTicketRunner
import com.google.common.base.Strings
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.util.*
import kotlin.collections.ArrayList

@RestController
@RequestMapping("/ota/v1/order")
class OrderController : BaseController() {

    @Autowired
    private lateinit var orderDao: OrderDao

    @Autowired
    private lateinit var subOrderDao: SubOrderDao

    @Autowired
    private lateinit var orderSvc: OrderSvc

    @Autowired
    private lateinit var orderTicketCodeDao: OrderTicketCodeDao

    @Autowired
    private lateinit var orderTransformer: DocTransformer<Order, OrderDto>

    @Autowired
    private lateinit var subOrderTransformer: DocTransformer<SubOrder, SubOrderDto>

    @GetMapping("/{orderNo}")
    fun get(
        @PathVariable("orderNo", required = true) orderNo: String
    ): ResultT<OrderDto> {
        if (Strings.isNullOrEmpty(orderNo)) {
            return ResultT(RESULT_FAIL, "参数错误")
        }
        val order = orderDao.get(orderNo) ?: return ResultT(RESULT_FAIL, "订单不存在")
        val dto = orderTransformer.transform(order)!!
        return ResultT(RESULT_SUCCESS, "ok", dto)
    }

    @DeleteMapping("/{orderNo}")
    fun del(
        @PathVariable("orderNo", required = true) orderNo: String
    ): Result {
        if (Strings.isNullOrEmpty(orderNo)) {
            return Result(RESULT_FAIL, "参数错误")
        }
        val order = orderDao.get(orderNo) ?: return Result(RESULT_FAIL, "订单不存在")
        if (order.deleted) {
            return Result(RESULT_FAIL, "订单已删除")
        }
        val c = orderDao.updateDel(orderNo, true)
        if (c > 0) {
            return Result(RESULT_SUCCESS, "ok")
        }
        return Result(RESULT_SUCCESS, "ok")
    }


    @GetMapping("/sub/{id}")
    fun get(
        @PathVariable("id", required = true) id: Int
    ): ResultT<SubOrderDto> {
        if (id <= 0) {
            return ResultT(RESULT_FAIL, "参数错误")
        }
        val order = subOrderDao.get(id) ?: return ResultT(RESULT_FAIL, "订单不存在")
        val dto = subOrderTransformer.transform(order)!!
        return ResultT(RESULT_SUCCESS, "ok", dto)
    }

    @GetMapping("/my/{uid}")
    fun getMy(
        @PathVariable("uid", required = true) uid: String,
        @RequestParam("state", required = false) state: Short?,
        @RequestParam("partner_id", required = false) partnerId: String?,
        @RequestParam("page", required = false) page: Int?
    ): ResultT<PagedList<OrderDto>> {
        if (Strings.isNullOrEmpty(uid) || Strings.isNullOrEmpty(partnerId)) {
            return ResultT(RESULT_FAIL, "参数错误")
        }
        val total = orderDao.getsByUidCount(uid, partnerId!!)
        var p = 1
        val size = 20
        if (page != null && page > 0) {
            p = page
        }
        var oState: OrderStates? = null
        if (state != null) {
            if (!OrderStates.values().any { s -> s.value == state }) {
                return ResultT(RESULT_FAIL, "state状态不存在")
            }
            oState = OrderStates.prase(state.toInt())
        }

        val offset = (p - 1) * size
        val list = orderDao.getsByUid(uid, partnerId, oState, offset, size)
        val dtos = ArrayList<OrderDto>()
        for (order in list) {
            val dto = orderTransformer.transform(order)!!
            dtos.add(dto)
        }
        val pList = PagedList(p, size, total, dtos)
        return ResultT(RESULT_SUCCESS, "ok", pList)
    }

    @PutMapping("/cancel/{orderNo}")
    fun cancel(
        @PathVariable("orderNo", required = true) orderNo: String
    ): Result {
        if (Strings.isNullOrEmpty(orderNo)) {
            return Result(RESULT_FAIL, "参数错误")
        }
        val ok = orderSvc.cancelOrder(orderNo)
        if (ok) {
            return Result(RESULT_SUCCESS, "ok")
        }
        return Result(RESULT_FAIL, "fail")
    }

    @PutMapping("/pay/success")
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
        val order = orderDao.get(orderNo!!) ?: return ResultT(RESULT_FAIL, "订单不存在或已过期")
        if (order.state != OrderStates.Init) {
            return ResultT("paied", "订单已支付")
        }
        val ok = orderSvc.completedPay(orderNo, Date(payTime * 1000), payNo!!)
        if (ok) {
            IssueTicketRunner.run(Runnable {
                val deliver = SpringAppContext.getBean(Consts.IssueTicketDeliveName) as IssueTicketDeliver
                deliver.issue(orderNo)
            })

            return ResultT(RESULT_SUCCESS, "ok", "")
        }
        return ResultT(RESULT_FAIL, "fail", "")
    }

    @GetMapping("/code/state/{code}")
    fun codeState(
        @PathVariable("code", required = true) code: String?,
        @RequestParam("provider", required = true) provider: Int?
    ): ResultT<Short> {
        if (Strings.isNullOrEmpty(code)) {
            return ResultT(RESULT_FAIL, "参数错误")
        }
        if(provider == null) {
            return ResultT(RESULT_FAIL, "参数错误")
        }
        val tCode = orderTicketCodeDao.getByCode(code!!,OrderTicketCodeProviders.prase(provider)) ?: return ResultT(RESULT_FAIL, "码不存在")
        return ResultT(RESULT_SUCCESS, "ok", tCode.state.value)
    }
}