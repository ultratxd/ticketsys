package com.cj.ticketsys.svc.impl

import com.cj.ticketsys.dao.OrderTicketCodeDao
import com.cj.ticketsys.dao.OrderDao
import com.cj.ticketsys.dao.SubOrderDao
import com.cj.ticketsys.entities.OrderStates
import com.cj.ticketsys.entities.OrderTicketCode
import com.cj.ticketsys.entities.TicketCodeStates
import com.cj.ticketsys.svc.IssueTicketDeliver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

@Service("Def_Issue_Ticket_Deliver")
class DefaultIssueTicketDeliver : IssueTicketDeliver {

    @Autowired
    private lateinit var orderDao: OrderDao

    @Autowired
    private lateinit var subOrderDao: SubOrderDao

    @Autowired
    private lateinit var orderTicketCodeDao: OrderTicketCodeDao

    @Transactional(rollbackFor = [Exception::class])
    override fun issue(orderNo: String) {
        val order = orderDao.get(orderNo) ?: return
        val subOrders = subOrderDao.gets(orderNo)

        val tCode = OrderTicketCode()
        tCode.orderId = orderNo
        tCode.nums = subOrders.sumBy { a -> a.nums }
        tCode.state = TicketCodeStates.Unused
        tCode.createTime = Date()
        tCode.useDate = subOrders.first().useDate!!
        tCode.code = makeCode(subOrders.first().useDate!!)

        val c = orderTicketCodeDao.insert(tCode)
        if(c > 0) {
            order.state = OrderStates.Issued
            order.issueTicketTime = Date()
            orderDao.update(order)

            subOrders.map { s -> {
                s.state = OrderStates.Issued
                s.issueTicketTime = Date()
                subOrderDao.update(s)
            } }
        }
    }

    fun makeCode(date: Date): String {
        val datefmt = SimpleDateFormat("yyMMdd")
        val ydm = datefmt.format(date)
        val rnd = Random().nextInt(1000000)
        return "$ydm$rnd"
    }
}