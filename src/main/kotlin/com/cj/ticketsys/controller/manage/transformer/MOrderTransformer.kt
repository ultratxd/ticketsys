package com.cj.ticketsys.controller.manage.transformer

import com.cj.ticketsys.controller.dto.SubOrderDto
import com.cj.ticketsys.controller.manage.dto.MOrderDto
import com.cj.ticketsys.controller.manage.dto.MSubOrderDto
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
class MOrderTransformer : DocTransformer<Order, MOrderDto> {

    @Autowired
    private lateinit var subOrderDao: SubOrderDao

    @Autowired
    private lateinit var subOrderDocTransformer: DocTransformer<SubOrder, MSubOrderDto>

    @Autowired
    private lateinit var orderTicketCodeDao: OrderTicketCodeDao

    override fun transform(data: Order): MOrderDto? {
        val dto = MOrderDto(data.orderId)
        dto.createTime = data.createTime
        dto.payTime = data.payTime
        dto.payNo = data.payNo
        dto.refundTime = data.refundTime
        dto.refundNo = data.refundNo
        dto.price = data.price
        dto.childs = data.childs
        dto.state = data.state.value
        dto.ip = data.ip
        dto.channelId = data.channelId
        dto.channelUid = data.channelUid
        dto.deleted = data.deleted

        val subs = subOrderDao.gets(data.orderId)
        val spotDtos = subs.map { a -> subOrderDocTransformer.transform(a)!! }
        dto.subOrders.addAll(spotDtos)

        bindOrderTicketCode(data, dto)

        return dto
    }

    fun bindOrderTicketCode(data: Order, dto: MOrderDto) {
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