package com.cj.ticketsys.svc.impl

import com.cj.ticketsys.dao.OrderDao
import com.cj.ticketsys.dao.OrderTicketCodeDao
import com.cj.ticketsys.dao.SubOrderDao
import com.cj.ticketsys.entities.*
import com.cj.ticketsys.svc.InventoryManagement
import com.cj.ticketsys.svc.OrderSvc
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.interceptor.TransactionAspectSupport
import java.util.*
import kotlin.collections.ArrayList

@Service
class OrderSvcImpl : OrderSvc {

    @Autowired
    private lateinit var orderDao: OrderDao

    @Autowired
    private lateinit var subOrderDao: SubOrderDao

    @Autowired
    private lateinit var orderTicketCodeDao: OrderTicketCodeDao

    @Autowired
    private lateinit var inventoryManagement: InventoryManagement

    @Transactional(rollbackFor = [Exception::class])
    override fun create(order: Order, subOrder: ArrayList<SubOrder>): Boolean {
        val c = orderDao.insert(order)
        if (c > 0) {
            for (sub in subOrder) {
                val sc = subOrderDao.insert(sub)
                if (sc <= 0) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()
                    return false
                }
                inventoryManagement.incrSolds(sub.ticketPid, sub.nums)
            }
        } else {
            return false
        }
        return true
    }

    @Transactional(rollbackFor = [Exception::class])
    override fun completedPay(orderNo: String, payTime: Date, payNo: String): Boolean {
        val order = orderDao.get(orderNo) ?: return false
        order.state = OrderStates.Paied
        order.payTime = payTime
        order.payNo = payNo
        val c = orderDao.update(order)
        if (c > 0) {
            subOrderDao.batchUpadte(orderNo, OrderStates.Paied, payTime, payNo, null, null)
        }
        return true
    }

    @Transactional(rollbackFor = [Exception::class])
    override fun cancelOrder(orderNo: String): Boolean {
        val c = orderDao.updateState(orderNo, OrderStates.Cancel)
        if (c > 0) {
            val subOrders = subOrderDao.gets(orderNo)
            for (subOrder in subOrders) {
                inventoryManagement.decrSolds(subOrder.ticketPid, subOrder.nums)
                subOrderDao.updateState(subOrder.id, OrderStates.Cancel)
            }
        }
        return c > 0
    }

    @Transactional(rollbackFor = [Exception::class])
    override fun completdEnter(orderNo: String, enterCode: String, provider: OrderTicketCodeProviders): Boolean {
        val code = orderTicketCodeDao.getByCode(enterCode, provider) ?: return false
        if (code.orderId != orderNo) return false
        orderTicketCodeDao.update(orderNo, Date(), TicketCodeStates.Used)
        orderDao.updateState(orderNo, OrderStates.Used)
        subOrderDao.updateStateByOrderNo(orderNo, OrderStates.Used)
        return true
    }
}