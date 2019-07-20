package com.cj.ticketsys.svc

import com.cj.ticketsys.dao.OrderDao
import com.cj.ticketsys.dao.SubOrderDao
import com.cj.ticketsys.entities.OrderStates
import com.cj.ticketsys.svc.impl.PriceInventoryManagement
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.transaction.annotation.Transactional
import java.lang.Exception
import java.util.*

@Service
class OrderExpireScheduler {

    @Autowired
    private lateinit var orderDao: OrderDao

    @Autowired
    private lateinit var subOrderDao: SubOrderDao

    @Autowired
    private lateinit var inventoryManagement: InventoryManagement

    /**
     * 30秒运行一次
     */
    @Scheduled(fixedRate = (1000 * 30).toLong(), initialDelay = 1000)
    @Transactional(rollbackFor = [Exception::class])
    fun deleteExpireOrder() {
        val ts = System.currentTimeMillis()
        val d = Date(ts - 60 * 60 * 1000)
        val orders = orderDao.getExpiredOrders(d)
        for (order in orders) {
            val subOrders = subOrderDao.gets(order.orderId)
            for (subOrder in subOrders) {
                inventoryManagement.decrSolds(subOrder.ticketPid, subOrder.nums)
                subOrderDao.updateState(subOrder.id, OrderStates.Closed)
            }
            orderDao.updateState(order.orderId, OrderStates.Closed)
        }
    }
}