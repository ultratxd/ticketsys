package com.cj.ticketsys.svc.impl

import com.cj.ticketsys.dao.OrderDao
import com.cj.ticketsys.dao.OrderTicketCodeDao
import com.cj.ticketsys.dao.SubOrderDao
import com.cj.ticketsys.dao.TicketDao
import com.cj.ticketsys.entities.OrderStates
import com.cj.ticketsys.entities.OrderTicketCode
import com.cj.ticketsys.entities.OrderTicketCodeProviders
import com.cj.ticketsys.entities.TicketCodeStates
import com.cj.ticketsys.svc.IssueTicketDeliver
import com.cj.ticketsys.svc.entrance.CreateOrderParameter
import com.cj.ticketsys.svc.entrance.EntranceConsts
import com.cj.ticketsys.svc.entrance.SCApi
import com.cj.ticketsys.svc.entrance.VisitPerson
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@Service("Shuchen_Issue_Ticket_Deliver")
class ShuchenIssueTicketDeliver : IssueTicketDeliver {

    @Autowired
    private lateinit var orderDao: OrderDao

    @Autowired
    private lateinit var subOrderDao: SubOrderDao

    @Autowired
    private lateinit var ticketDao: TicketDao

    @Autowired
    private lateinit var orderTicketCodeDao: OrderTicketCodeDao

    @Transactional(rollbackFor = [Exception::class])
    override fun issue(orderNo: String) {
        val order = orderDao.get(orderNo) ?: return

        orderDao.updateState(orderNo, OrderStates.Issuing)
        subOrderDao.updateStateByOrderNo(orderNo, OrderStates.Issuing)

        val subOrders = subOrderDao.gets(orderNo)

        val tCode = OrderTicketCode()
        tCode.orderId = orderNo
        tCode.nums = subOrders.sumBy { a -> a.nums }
        tCode.state = TicketCodeStates.Unused
        tCode.createTime = Date()
        tCode.provider = OrderTicketCodeProviders.ShuCheng

        val firstSub = subOrders.first()
        tCode.useDate = firstSub.useDate!!

        val ticket = ticketDao.get(firstSub.ticketId)

        val scapi = SCApi(EntranceConsts.SC_USER_ID, EntranceConsts.SC_USER_KEY)
        val p = CreateOrderParameter()
        //待确认
        //...
        p.orderSerialId = order.orderId
        p.productNo = ticket!!.cloudId
        p.payType = 1
        p.tickets = subOrders.sumBy { s -> s.pernums }
        p.price = (order.price * 100).toLong()
        p.bookName = firstSub.uName
        p.bookMobile = firstSub.uMobile
        p.idCard = firstSub.uCard
        p.travelDate = SimpleDateFormat("yyyy-MM-dd").format(firstSub.useDate!!)
        p.visitPerson = listOf(VisitPerson(firstSub.uName, firstSub.uMobile, firstSub.uCard))


        val result = scapi.createOrder(p)
        tCode.code = result.data!!.partnerCode

        val c = orderTicketCodeDao.insert(tCode)
        if (c > 0) {
            order.state = OrderStates.Issued
            order.issueTicketTime = Date()
            orderDao.update(order)

            subOrders.map { s ->
                {
                    s.state = OrderStates.Issued
                    s.issueTicketTime = Date()
                    subOrderDao.update(s)
                }
            }
        }
    }
}