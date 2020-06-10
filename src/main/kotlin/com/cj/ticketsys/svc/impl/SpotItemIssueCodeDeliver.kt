package com.cj.ticketsys.svc.impl

import com.cj.ticketsys.dao.PartnerDao
import com.cj.ticketsys.dao.TicketCategories
import com.cj.ticketsys.entities.ChannelTypes
import com.cj.ticketsys.entities.OrderStates
import com.cj.ticketsys.svc.IssueTicketDeliver
import com.cj.ticketsys.svc.SpotItemSvc
import com.cj.ticketsys.svc.Utils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service("Item_Issue_Code_Deliver")
class SpotItemIssueCodeDeliver : IssueTicketDeliver {

    @Autowired
    private lateinit var spotItemSvc: SpotItemSvc

    @Autowired
    private lateinit var partnerDao: PartnerDao

    override fun issue(orderNo: String) {
        val order = spotItemSvc.getOrder(orderNo) ?: return
        val partner = partnerDao.get(order.channelId) ?: return

        val code = makeCode(partner.channelType, order.createTime, TicketCategories.Retail.value)
        spotItemSvc.setOrderCode(order.orderId, code)
        spotItemSvc.setOrderState(order.orderId,OrderStates.Issued)
    }

    fun makeCode(channelType: ChannelTypes?, date: Date, cid: Int): String {
        val ydm = Utils.dateZoneFormat(date, "MMdd")//datefmt.format(date)
        val tag = String.format("%02d", channelType?.value ?: 99)
        val rnd = String.format("%05d", Random().nextInt(100000))
        return "B$tag$cid$ydm$rnd"
    }
}