package com.cj.ticketsys.controller.manage

import com.cj.ticketsys.controller.BaseController
import com.cj.ticketsys.controller.dto.RESULT_FAIL
import com.cj.ticketsys.controller.dto.RESULT_SUCCESS
import com.cj.ticketsys.controller.dto.ResultT
import com.cj.ticketsys.controller.manage.dto.MOrderDto
import com.cj.ticketsys.dao.OrderDao
import com.cj.ticketsys.dao.OrderQuery
import com.cj.ticketsys.entities.Order
import com.cj.ticketsys.entities.OrderStates
import com.cj.ticketsys.entities.PagedList
import com.cj.ticketsys.svc.DocTransformer
import com.google.common.base.Strings
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/ota/v1/manage/order")
class ManageOrderController : BaseController() {
    @Autowired
    private lateinit var orderDao: OrderDao

    @Autowired
    private lateinit var orderTransformer: DocTransformer<Order,MOrderDto>

    @GetMapping("search")
    fun search(
        @RequestParam("order_no", required = false) orderNo: String?,
        @RequestParam("state", required = false) state: Short?,
        @RequestParam("pay_no", required = false) payNo: String?,
        @RequestParam("refund_no", required = false) refundNo: String?,
        @RequestParam("ch_id", required = false) chId: String?,
        @RequestParam("ch_uid", required = false) chUid: String?,
        @RequestParam("user_name", required = false) userName: String?,
        @RequestParam("user_card", required = false) userCard: String?,
        @RequestParam("user_mobile", required = false) userMobile: String?,
        @RequestParam("scenic_id", required = false) scenicId: Int?,
        @RequestParam("scenic_sid", required = false) scenicSid: Int?,
        @RequestParam("ticket_id", required = false) ticketId: Int?,
        @RequestParam("cid", required = false) cid: Int?,
        @RequestParam("page", required = false) page: Int? = 1,
        @RequestParam("size", required = false) size: Int? = 20
        ):ResultT<PagedList<MOrderDto>> {

        if(!Strings.isNullOrEmpty(orderNo)) {
            val order = orderDao.get(orderNo!!)
            if(order != null) {
                return ResultT(RESULT_SUCCESS,"OK",PagedList(1,1,1, listOf(orderTransformer.transform(order)!!)))
            }
            return ResultT(RESULT_FAIL,"订单不存在")
        }

        val query = OrderQuery()
        if(state != null) {
            if (!OrderStates.values().any { s -> s.value == state}) {
                return ResultT(RESULT_FAIL, "state状态不存在")
            }
            query.state = OrderStates.prase(state.toInt())
        }
        query.payNo = payNo
        query.refundNo = refundNo
        query.chId = chId
        query.chUid = chUid
        query.userName = userName
        query.userCard = userCard
        query.userMobile = userMobile
        query.scenicId = scenicId
        query.scenicSid = scenicSid
        query.ticketId = ticketId
        query.cid = cid
        if (size == null || size <= 0) {
            query.size = 20
        }
        var tmpPage = 0
        if (page == null || page <= 0) {
            tmpPage = 1
        } else {
            tmpPage = page
        }
        query.offset = (tmpPage - 1) * query.size
        val total = orderDao.searchForAdminCount(query)
        val list = orderDao.searchForAdmin(query)
        val dtos = ArrayList<MOrderDto>()
        for (order in list) {
            val dto = orderTransformer.transform(order)!!
            dtos.add(dto)
        }
        val pagedList = PagedList(tmpPage, query.size, total, dtos)

        return ResultT(RESULT_SUCCESS, "ok", pagedList)
    }

}