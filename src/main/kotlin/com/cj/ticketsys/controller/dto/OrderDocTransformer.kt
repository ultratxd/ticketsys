package com.cj.ticketsys.controller.dto

import com.cj.ticketsys.dao.OrderTicketCodeDao
import com.cj.ticketsys.dao.SubOrderDao
import com.cj.ticketsys.entities.Order
import com.cj.ticketsys.entities.OrderStates
import com.cj.ticketsys.entities.SubOrder
import com.cj.ticketsys.svc.DocTransformer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.text.SimpleDateFormat

@Component
class OrderDocTransformer : DocTransformer<Order, OrderDto> {

    @Autowired
    private lateinit var subOrderDao: SubOrderDao

    @Autowired
    private lateinit var subOrderDocTransformer: DocTransformer<SubOrder, SubOrderDto>

    @Autowired
    private lateinit var orderTicketCodeDao: OrderTicketCodeDao

    override fun transform(data: Order): OrderDto? {
        val dto = OrderDto(data.orderId)
        dto.createTime = data.createTime
        dto.payTime = data.payTime
        dto.payNo = data.payNo
        dto.refundTime = data.refundTime
        dto.refundNo = data.refundNo
        dto.price = data.price
        dto.childs = data.childs
        dto.state = data.state.value

        val subs = subOrderDao.gets(data.orderId)
        val spotDtos = subs.map { a -> subOrderDocTransformer.transform(a)!! }
        dto.subOrders.addAll(spotDtos)

        bindOrderTicketCode(data, dto)

        if (data.state == OrderStates.Init) {
            val expireTime = data.createTime.time + 60 * 60 * 1000
            if (expireTime > System.currentTimeMillis()) {
                dto.expireSeconds = ((expireTime - System.currentTimeMillis()) / 1000).toInt()
            }
        }

        return dto
    }


    fun bindOrderTicketCode(data: Order, dto: OrderDto) {
        if (OrderStates.Issued.value <= data.state.value && OrderStates.Closed.value > data.state.value) {
            val tcode = orderTicketCodeDao.get(data.orderId)
            if (tcode != null) {
                dto.extra.put("ticket_code", tcode.code)
                dto.extra.put("code_use_date", SimpleDateFormat("yyyy-MM-dd").format(tcode.useDate))
                dto.extra.put("code_use_state", tcode.state.value)
            }
        }
    }
}