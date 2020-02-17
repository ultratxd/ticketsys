package com.cj.ticketsys.controller.clientapi.transformer

import com.cj.ticketsys.controller.clientapi.dto.ClientOrderDto
import com.cj.ticketsys.controller.clientapi.dto.ClientSubOrderDto
import com.cj.ticketsys.controller.manage.dto.MOrderDto
import com.cj.ticketsys.controller.manage.dto.MSubOrderDto
import com.cj.ticketsys.dao.ClientDataDao
import com.cj.ticketsys.dao.OrderTicketCodeDao
import com.cj.ticketsys.dao.SubOrderDao
import com.cj.ticketsys.entities.*
import com.cj.ticketsys.svc.DocTransformer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


@Component
class ClientOrderTransformer : DocTransformer<ClientOrder, ClientOrderDto> {

    @Autowired
    private lateinit var clientDataDao: ClientDataDao

    @Autowired
    private lateinit var subOrderTransformer : DocTransformer<ClientSubOrder, ClientSubOrderDto>

    override fun transform(data: ClientOrder): ClientOrderDto? {
        val dto = ClientOrderDto()
        dto.id = data.id
        dto.clientId = data.clientId
        dto.cloudId = data.cloudId
        dto.clientOrderNo = data.clientOrderNo
        dto.nums = data.nums
        dto.orderType = data.orderType
        dto.amount = data.amount
        dto.perNums = data.perNums
        dto.createTime = data.createTime
        dto.state = data.state
        dto.payType = data.payType
        dto.realPay = data.realPay
        dto.changePay = data.changePay
        dto.shouldPay = data.shouldPay
        dto.exCode = data.exCode
        dto.remark = data.remark
        dto.saleClientNo = data.saleClientNo
        dto.ext1 = data.ext1
        dto.ext2 = data.ext2
        dto.ext3 = data.ext3
        dto.properties = data.properties

        val subOrders = clientDataDao.selectByPid(dto.clientId)
        val outSubOrders = ArrayList<ClientSubOrderDto>()
        for(subOrder in subOrders) {
            outSubOrders.add(subOrderTransformer.transform(subOrder)!!)
        }
        dto.childrens = outSubOrders

        return dto
    }
}