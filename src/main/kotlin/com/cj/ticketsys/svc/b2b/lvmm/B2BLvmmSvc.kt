package com.cj.ticketsys.svc.b2b.lvmm

import com.cj.ticketsys.cfg.SpringAppContext
import com.cj.ticketsys.controller.dto.RESULT_SUCCESS
import com.cj.ticketsys.dao.B2bLvmmDao
import com.cj.ticketsys.dao.PartnerDao
import com.cj.ticketsys.dao.TicketPriceDao
import com.cj.ticketsys.entities.BuyTypes
import com.cj.ticketsys.entities.CardTypes
import com.cj.ticketsys.entities.ChannelTypes
import com.cj.ticketsys.entities.b2b.lvmama.LvmmContact
import com.cj.ticketsys.entities.b2b.lvmama.LvmmOrder
import com.cj.ticketsys.entities.b2b.lvmama.LvmmTraveller
import com.cj.ticketsys.svc.*
import org.apache.logging.log4j.util.Strings
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.interceptor.TransactionAspectSupport
import java.util.*
import kotlin.collections.ArrayList

@Service
class B2BLvmmSvc {

    @Autowired
    private lateinit var lvmmDao: B2bLvmmDao

    @Autowired
    private lateinit var partnerDao: PartnerDao

    @Autowired
    private lateinit var priceDao: TicketPriceDao

    @Autowired
    private lateinit var tktBuy: TicketBuyer

    @Autowired
    private lateinit var orderSvc: OrderSvc

    @Value("\${b2b.lvmm.momoSoptId}")
    private lateinit var soptId: String

    @Transactional(rollbackFor = [Exception::class])
    fun createOrder(reqOrder:LvOrderParams): LvResult {

        val buyTicket = BuyTicketOrder()
        buyTicket.partner = partnerDao.get("lumama")
        buyTicket.channelUid = reqOrder.uid ?: ""
        buyTicket.buyType = BuyTypes.B2B
        buyTicket.buyerIp = ""
        buyTicket.scenicSpotId = soptId.toInt()
        val buyTicketInfos = ArrayList<BuyTicketInfo>()

        val price = priceDao.getByPLU(reqOrder.supplierGoodsId, ChannelTypes.LuMaMa.code())
            ?: return LvResultUtil.fail("13","商品id不存在")
        val buyItem = BuyTicketInfo()
        buyItem.ticketPriceId = price.id
        buyItem.ticketNums = reqOrder.num.toInt()
        if(Strings.isNotEmpty(reqOrder.visitTime)) {
            return LvResultUtil.fail("1","具体错误类型日期")
        }
        buyItem.date = reqOrder.visitTime.substring(0,8).toInt()
        buyItem.userName = reqOrder.uid ?: ""
        buyItem.cardType = CardTypes.B2BProvider
        buyItem.userCard = ""
        buyItem.userMobile = reqOrder.contacts?.mobile ?: ""
        buyTicketInfos.add(buyItem)

        buyTicket.buyTickets.addAll(buyTicketInfos)
        val buyResult = tktBuy.buy(buyTicket)
        when(buyResult.status) {
            "BUY:1005" -> {
                return LvResultUtil.fail("13","商品id不存在")
            }
            "BUY:1006" -> {
                return LvResultUtil.fail("11",buyResult.msg)
            }
            "BUY:1007" -> {
                return LvResultUtil.fail("1",buyResult.msg)
            }
            "BUY:1008" -> {
                return LvResultUtil.fail("11",buyResult.msg)
            }
            else -> when {
                buyResult.status != RESULT_SUCCESS -> {
                    return LvResultUtil.fail("11",buyResult.msg)
                }
            }
        }

        val newOrder = LvmmOrder()
        newOrder.orderId = buyResult.order!!.orderId
        newOrder.otaId = reqOrder.serialNo
        newOrder.goodId = reqOrder.supplierGoodsId
        newOrder.nums = reqOrder.num.toInt()
        newOrder.settlePrice = reqOrder.settlePrice.toDouble()
        newOrder.ts = reqOrder.timestamp
        newOrder.visitTs = reqOrder.visitTime
        newOrder.uid = reqOrder.uid
        val ok = lvmmDao.insertOrder(newOrder) > 0
        if (ok) {
            if (reqOrder.contacts != null) {
                val contact = LvmmContact()
                contact.idNum = reqOrder.contacts.idNum
                contact.idType = reqOrder.contacts.idType
                contact.mobile = reqOrder.contacts.mobile
                contact.name = reqOrder.contacts.name
                contact.otaId = reqOrder.serialNo
                lvmmDao.insertContact(contact)
            }
            if (reqOrder.travellerList != null) {
                for (traveller in reqOrder.travellerList) {
                    val trave = LvmmTraveller()
                    trave.idNum = reqOrder.contacts.idNum
                    trave.idType = reqOrder.contacts.idType
                    trave.mobile = reqOrder.contacts.mobile
                    trave.name = reqOrder.contacts.name
                    trave.otaId = reqOrder.serialNo
                    lvmmDao.insertTraveller(trave)
                }
            }
        } else {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()
            return LvResultUtil.fail("8",buyResult.msg)
        }
        //生成凭证
        val payOk = orderSvc.completedPay(buyResult.order!!.orderId, Date(), reqOrder.serialNo ?: "lvmm")
        if(payOk) {
            val deliver = SpringAppContext.getBean(Consts.IssueTicketDeliveName) as IssueTicketDeliver
            deliver.issue(buyResult.order!!.orderId)
        }
        return LvResultUtil.fail("0",buyResult.msg)
    }

    fun getOrderByOtaId(otaId:String): LvmmOrder? {
        return lvmmDao.getOrder(otaId)
    }
}