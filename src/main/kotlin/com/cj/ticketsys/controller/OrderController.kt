package com.cj.ticketsys.controller

import com.cj.ticketsys.cfg.SpringAppContext
import com.cj.ticketsys.controller.dto.*
import com.cj.ticketsys.controller.req.BuyRequest
import com.cj.ticketsys.dao.OrderDao
import com.cj.ticketsys.dao.SubOrderDao
import com.cj.ticketsys.entities.Order
import com.cj.ticketsys.entities.OrderStates
import com.cj.ticketsys.entities.PagedList
import com.cj.ticketsys.entities.SubOrder
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
        val offset = (p - 1) * size
        val list = orderDao.getsByUid(uid, partnerId, offset, size)
        val dtos = ArrayList<OrderDto>()
        for (order in list) {
            val dto = orderTransformer.transform(order)!!
            dtos.add(dto)
        }
        val pList = PagedList(p, size, total, dtos)
        return ResultT(RESULT_SUCCESS, "ok", pList)
    }

    @PutMapping("/pay/success")
    fun paySuccess(
        @RequestParam("order_no", required = true) orderNo: String?,
        @RequestParam("pay_time", required = true) payTime: Long?,
        @RequestParam("pay_no", required = true) payNo: String?,
        @RequestParam("partner_id", required = false) partnerId: String?
    ):ResultT<String> {
        if (Strings.isNullOrEmpty(orderNo)) {
            return ResultT(RESULT_FAIL, "参数错误:order_no")
        }
        if(payTime == null || payTime <= 0 ) {
            return ResultT(RESULT_FAIL, "参数错误:pay_time")
        }
        if(Strings.isNullOrEmpty(payNo)) {
            return ResultT(RESULT_FAIL, "参数错误:pay_no")
        }
        val order = orderDao.get(orderNo!!) ?: return ResultT(RESULT_FAIL, "订单不存在或已过期")
        if(order.state != OrderStates.Init)  {
            return ResultT("paied","订单已支付")
        }
        val ok = orderSvc.completedPay(orderNo, Date(payTime * 1000),payNo!!)
        if(ok) {
            IssueTicketRunner.run(Runnable {
                val deliver = SpringAppContext.getBean(IssueTicketDeliver::class.java)
                deliver.issue(orderNo)
            })

            return ResultT(RESULT_SUCCESS, "ok","")
        }
        return ResultT(RESULT_FAIL, "fail","")
    }
}