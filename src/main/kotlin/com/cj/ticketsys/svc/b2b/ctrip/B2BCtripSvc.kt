package com.cj.ticketsys.svc.b2b.ctrip

import com.alibaba.druid.util.Utils.md5
import com.alibaba.fastjson.JSON
import com.cj.ticketsys.controller.b2b.ctrip.CtripCreateOrderBody
import com.cj.ticketsys.controller.b2b.ctrip.Encrypt
import com.cj.ticketsys.dao.B2bCtripDao
import com.cj.ticketsys.dao.OrderTicketCodeDao
import com.cj.ticketsys.dao.TicketPriceDao
import com.cj.ticketsys.entities.ChannelTypes
import com.cj.ticketsys.entities.TicketPrice
import com.cj.ticketsys.entities.b2b.ctrip.B2bContact
import com.cj.ticketsys.entities.b2b.ctrip.B2bCtripCoupon
import com.cj.ticketsys.entities.b2b.ctrip.B2bCtripOrder
import com.cj.ticketsys.entities.b2b.ctrip.*
import com.cj.ticketsys.svc.PriceBinder
import com.cj.ticketsys.svc.Utils
import com.google.common.base.Strings
import okhttp3.MediaType.Companion.toMediaType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar
import kotlin.collections.ArrayList
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.interceptor.TransactionAspectSupport


@Service
class B2BCtripSvc {
    @Autowired
    private lateinit var priceBinder: PriceBinder

    @Autowired
    private lateinit var priceDao: TicketPriceDao

    @Autowired
    private lateinit var b2bCtripDao: B2bCtripDao

    @Autowired
    private lateinit var orderTicketCodeDao: OrderTicketCodeDao

    @Value("\${b2b.ctrip.secret}")
    private lateinit var secret: String

    @Value("\${b2b.ctrip.accountId}")
    private lateinit var accountId: String

    @Value("\${b2b.ctrip.version}")
    private lateinit var version: String

    @Value("\${b2b.ctrip.notifyUrl}")
    private lateinit var notifyUrl: String

    @Value("\${b2b.ctrip.aesSecret}")
    private lateinit var aesSecret: String

    @Value("\${b2b.ctrip.aesIVSecret}")
    private lateinit var aesIVSecret: String


    val JsonMediaType  = "application/json; charset=utf-8".toMediaType()

    /**
     * 价格同步
     */
    fun pushPrices(price: TicketPrice): Boolean {
        if (price.channelType != ChannelTypes.Ctrip || price.b2bPLU == null) {
            return false
        }
        val tktId = price.tid
        val cPrices = ArrayList<CtripPrice>()
        for (i in 0..30) {
            val rightNow = Calendar.getInstance()
            rightNow.time = Date()
            rightNow.add(Calendar.DAY_OF_YEAR, i)//日期加1天
            val dt = rightNow.time
            val nPrices = priceBinder.getPrices(tktId, ChannelTypes.Ctrip, dt)
            if (nPrices.count() == 0) {
                continue
            }
            val p = nPrices.first()
            val cp = CtripPrice()
            val dateFmt = SimpleDateFormat("yyyy-MM-dd")
            cp.date = dateFmt.format(dt)
            cp.marketPrice = p.originalPrice ?: 0.0
            cp.salePrice = p.price
            cp.costPrice = p.price
            cPrices.add(cp)
            if (cPrices.count() == 0) {
                continue
            }
        }

        val ctripBody = CtripPriceBody()
        ctripBody.otaOptionId = price.b2bPLU!!
        ctripBody.sequenceId = makeSeqId()
        ctripBody.prices = cPrices

        val ctripHeader = CtripHeader()
        ctripHeader.accountId = accountId
        ctripHeader.requestTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
        ctripHeader.serviceName = "DatePriceModify"
        ctripHeader.version = version

        val req = CtripRequest<String>()
        req.header = ctripHeader
        req.body = Encrypt.encrypt(JSON.toJSONString(ctripBody),aesSecret,aesIVSecret)

        ctripHeader.sign = makeSign(accountId, ctripHeader.serviceName, req.body as String)

        val client = OkHttpClient()
        val reqBody = JSON.toJSONString(req).toRequestBody(JsonMediaType)
        val request = Request.Builder()
                .url(notifyUrl)
                .post(reqBody)
                .build();
        val resp = client.newCall(request).execute()
        val respBody = resp.body.toString()
        val respResult = JSON.parseObject(respBody,
            CtripResponseHeader::class.java)
        if(respResult.resultCode.equals("0000")) {
            return true
        }
        return false
    }

    /**
     * 库存同步
     */
    fun pushInventory(price: TicketPrice): Boolean {
        if (price.channelType != ChannelTypes.Ctrip || price.b2bPLU == null) {
            return false
        }
        val cInventorys = ArrayList<CtripInventory>()
        for (i in 0..30) {
            val rightNow = Calendar.getInstance()
            rightNow.time = Date()
            rightNow.add(Calendar.DAY_OF_YEAR, i)//日期加1天
            val dt = rightNow.time
            val cp = CtripInventory()
            val dateFmt = SimpleDateFormat("yyyy-MM-dd")
            cp.date = dateFmt.format(dt)
            cp.quantity = price.stocks
            cInventorys.add(cp)
            if (cInventorys.count() == 0) {
                continue
            }
        }
        val ctripBody = CtripInventoryBody()
        ctripBody.otaOptionId = price.b2bPLU!!
        ctripBody.sequenceId = makeSeqId()
        ctripBody.inventorys = cInventorys

        val ctripHeader = CtripHeader()
        ctripHeader.accountId = accountId
        ctripHeader.requestTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
        ctripHeader.serviceName = "DateInventoryModify"
        ctripHeader.version = version

        val req = CtripRequest<String>()
        req.header = ctripHeader
        req.body = Encrypt.encrypt(JSON.toJSONString(ctripBody),aesSecret,aesIVSecret)

        ctripHeader.sign = makeSign(accountId, ctripHeader.serviceName, req.body as String)

        val client = OkHttpClient()
        val reqBody = JSON.toJSONString(req).toRequestBody(JsonMediaType)
        val request = Request.Builder()
                .url(notifyUrl)
                .post(reqBody)
                .build();
        val resp = client.newCall(request).execute()
        val respBody = resp.body.toString()
        val respResult = JSON.parseObject(respBody,
            CtripResponseHeader::class.java)
        if(respResult.resultCode.equals("0000")) {
            return true
        }
        return false
    }

    /**
     * 核销通知
     */
    fun consumedNotice(orderId:String):Boolean {
        val orderItems = b2bCtripDao.getItemsByOrderId(orderId)
        val order = b2bCtripDao.getOrder(orderId) ?: return false

        val ctripBody = CtripConsumedNoticeBody()
        val dateFmt = SimpleDateFormat("yyyy-MM-dd")
        for(item in orderItems) {
            val cItem = CtripConsumedNoticeBodyItem()
            cItem.itemId = item.itemId
            if(item.useStartDate != null) {
                cItem.useStartDate = dateFmt.format(cItem.useStartDate)
            }
            if(item.useEndDate != null) {
                cItem.useEndDate = dateFmt.format(cItem.useEndDate)
            }
            cItem.quantity = item.quantity
            cItem.useQuantity = item.quantity
            cItem.remark = item.remark
            cItem.lostAmount = item.price
            cItem.lostAmountCurrency = item.priceCurrency
            ctripBody.items!!.add(cItem)
        }
        ctripBody.otaOrderId = order.otaId
        ctripBody.supplierOrderId = order.orderId
        ctripBody.sequenceId = makeSeqId()

        val header = CtripHeader()
        header.accountId = accountId
        header.requestTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
        header.serviceName = "OrderConsumedNotice"
        header.version = version

        val req = CtripRequest<String>()
        req.header = header
        req.body = Encrypt.encrypt(JSON.toJSONString(ctripBody),aesSecret,aesIVSecret)

        header.sign = makeSign(accountId, header.serviceName, req.body as String)

        val client = OkHttpClient()
        val reqBody = JSON.toJSONString(req).toRequestBody(JsonMediaType)
        val request = Request.Builder()
                .url(notifyUrl)
                .post(reqBody)
                .build();
        val resp = client.newCall(request).execute()
        val respBody = resp.body.toString()
        val respResult = JSON.parseObject(respBody,
            CtripResponseHeader::class.java)
        if(respResult.resultCode.equals("0000")) {
            return true
        }
        return false
    }

    /**
     * 出行通知
     */
    fun travelNotice(orderId:String):Boolean {
        val orderItems = b2bCtripDao.getItemsByOrderId(orderId)
        val order = b2bCtripDao.getOrder(orderId) ?: return false
        val ctripBody = CtripTravelNoticeBody()

        val tCode = orderTicketCodeDao.get(orderId) ?: return false
        for (item in orderItems) {
            ctripBody.vouchers.add(
                CtripTravelNoticeVoucher(
                    itemId = item.itemId,
                    voucherCode = tCode.code,
                    voucherData = "",
                    voucherType = 3
                )
            )
            ctripBody.items.add(
                CtripTravelNoticeItem(
                    itemId = item.itemId,
                    remark = item.remark
                )
            )
        }
        ctripBody.otaOrderId = order.otaId
        ctripBody.supplierOrderId = orderId
        ctripBody.sequenceId = makeSeqId()

        val header = CtripHeader()
        header.accountId = accountId
        header.requestTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
        header.serviceName = "OrderTravelNotice"
        header.version = version

        val req = CtripRequest<String>()
        req.header = header
        req.body = Encrypt.encrypt(JSON.toJSONString(ctripBody),aesSecret,aesIVSecret)

        header.sign = makeSign(accountId, header.serviceName, req.body as String)

        val client = OkHttpClient()
        val reqBody = JSON.toJSONString(req).toRequestBody(JsonMediaType)
        val request = Request.Builder()
                .url(notifyUrl)
                .post(reqBody)
                .build();
        val resp = client.newCall(request).execute()
        val respBody = resp.body.toString()
        val respResult = JSON.parseObject(respBody,
            CtripResponseHeader::class.java)
        if(respResult.resultCode.equals("0000")) {
            return true
        }
        return false
    }

    fun makeSign(serviceName: String, requestTime: String, body: String): String {
        //accountId+serviceName+requestTime+body+version+signkey
        return md5(accountId + serviceName + requestTime + body + version + secret).toLowerCase()
    }

    private fun makeSeqId(): String {
        val dateFmt = SimpleDateFormat("yyyyMMdd")
        val uuid = UUID.randomUUID().toString().replace("-", "")
        return dateFmt.format(Date()) + uuid
    }

    /**
     * 创建保存订单
     */
    @Transactional(rollbackFor = [Exception::class])
    fun createOrder(order: CtripCreateOrderBody, orderId:String): B2bCtripOrder? {
        val b2bOrder = B2bCtripOrder()
        b2bOrder.orderId = orderId
        b2bOrder.confirmType = order.confirmType
        b2bOrder.items = order.items?.count() ?: 0
        b2bOrder.quantity = order.items?.sumBy { a->a!!.quantity ?: 0 } ?: 0
        b2bOrder.otaId = order.otaOrderId
        b2bOrder.createTime = Date()
        var c = b2bCtripDao.saveOrder(b2bOrder)
        if(c <= 0) {
            return null
        }
        if(order.contacts != null) {
            for (contact in order.contacts!!) {
                val b2bContact = B2bContact()
                b2bContact.otaId = order.otaOrderId
                b2bContact.email = contact?.email
                b2bContact.intlCode = contact?.intlCode
                b2bContact.mobile = contact?.mobile
                b2bContact.name = contact?.name
                b2bContact.optionalIntlCode = contact?.optionalIntlCode
                b2bContact.optionalMobile = contact?.optionalMobile
                c = b2bCtripDao.saveContact(b2bContact)
                if(c <= 0) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()
                    return null
                }
            }
        }
        if(order.coupons != null) {
            for(coupon in order.coupons!!) {
                val b2bCoupon = B2bCtripCoupon()
                b2bCoupon.otaId = order.otaOrderId
                b2bCoupon.name = coupon?.name
                b2bCoupon.type = coupon?.type
                b2bCoupon.amount = coupon?.amount?.toDouble()
                b2bCoupon.amountCurrency = coupon?.amountCurrency
                b2bCoupon.code = coupon?.code
                c = b2bCtripDao.saveCoupon(b2bCoupon)
                if(c <= 0) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()
                    return null
                }
            }
        }
        if(order.items != null) {
            for(item in order.items!!) {
                val tktPrice = priceDao.getByPLU(item?.PLU!!,ChannelTypes.Ctrip.code())
                if(tktPrice == null) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()
                    return null
                }
                val b2bItem = B2bCtripItem()
                b2bItem.orderId = orderId
                b2bItem.ticketPriceId = tktPrice.id
                b2bItem.PLU = item.PLU
                b2bItem.otaId = order.otaOrderId
                b2bItem.cost = item.cost?.toDouble()
                b2bItem.costCurrency = item.costCurrency
                b2bItem.distributionChannel = item.distributionChannel
                b2bItem.itemId = item.itemId
                b2bItem.lastConfirmTime = Utils.stringToDate(item.lastConfirmTime,"yyyy-MM-dd HH:mm:ss")
                b2bItem.openId = item.openId
                b2bItem.price = item.price?.toDouble()
                b2bItem.priceCurrency = item.priceCurrency
                b2bItem.quantity = item.quantity
                b2bItem.remark = item.remark
                b2bItem.suggestedPrice = if(Strings.isNullOrEmpty(item.suggestedPrice)) null else item.suggestedPrice!!.toDouble()
                b2bItem.suggestedPriceCurrency = item.suggestedPriceCurrency
                b2bItem.useEndDate = Utils.stringToDate(item.useEndDate,"yyyy-MM-dd")
                b2bItem.useStartDate = Utils.stringToDate(item.useStartDate,"yyyy-MM-dd")
                b2bItem.depositType = item.deposit?.type
                b2bItem.depositAmount = item.deposit?.amount?.toDouble()
                b2bItem.depositAmountCurrency = item.deposit?.amountCurrency
                c = b2bCtripDao.saveItem(b2bItem)
                if(c <= 0) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()
                    return null
                }
                for (adj in item.adjunctions!!) {
                    val adjunction = B2bCtripAdjunction()
                    adjunction.otaId = order.otaOrderId
                    adjunction.itemId = item.itemId!!
                    adjunction.name = adj?.name
                    adjunction.nameCode = adj?.nameCode
                    adjunction.content = adj?.content
                    adjunction.contentCode = adj?.contentCode
                    c = b2bCtripDao.saveAdjunction(adjunction)
                    if(c <= 0) {
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()
                        return null
                    }
                }
                for (passenger in item.passengers!!) {
                    val b2bPassenger = B2bCtripPassenger()
                    b2bPassenger.otaId = order.otaOrderId
                    b2bPassenger.itemId = item.itemId!!
                    b2bPassenger.ageType = passenger?.ageType
                    b2bPassenger.birthDate = Utils.stringToDate(passenger?.birthDate,"yyyy-MM-dd")
                    b2bPassenger.birthPlace = passenger?.birthPlace
                    b2bPassenger.cardIssueCountry = passenger?.cardIssueCountry
                    b2bPassenger.cardIssueDate = Utils.stringToDate(passenger?.cardIssueDate,"yyyy-MM-dd")
                    b2bPassenger.cardIssuePlace = passenger?.cardIssuePlace
                    b2bPassenger.cardNo = passenger?.cardNo
                    b2bPassenger.cardType = passenger?.cardType
                    b2bPassenger.cardValidDate = Utils.stringToDate(passenger?.cardValidDate,"yyyy-MM-dd")
                    b2bPassenger.firstName = passenger?.firstName
                    b2bPassenger.gender = passenger?.gender
                    b2bPassenger.height = passenger?.height?.toDouble()
                    b2bPassenger.intlCode = passenger?.intlCode
                    b2bPassenger.lastName = passenger?.lastName
                    b2bPassenger.mobile = passenger?.mobile
                    b2bPassenger.myopiaDegreeL = passenger?.myopiaDegreeL?.toDouble()
                    b2bPassenger.myopiaDegreeR = passenger?.myopiaDegreeR?.toDouble()
                    b2bPassenger.name = passenger?.name
                    b2bPassenger.nationalityCode = passenger?.nationalityCode
                    b2bPassenger.nationalityName = passenger?.nationalityName
                    b2bPassenger.shoeSize = passenger?.shoeSize?.toDouble()
                    b2bPassenger.weight = passenger?.weight?.toDouble()
                    c = b2bCtripDao.savePassenger(b2bPassenger)
                    if(c <= 0) {
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()
                        return null
                    }
                }
            }
        }
        return b2bOrder
    }
}

class CtripHeader {
    var accountId: String = ""
    var serviceName: String = ""
    var requestTime: String = ""
    var version: String = ""
    var sign: String = ""
}

class CtripResponseHeader {
    var resultCode: String = ""
    var resultMessage: String = ""
}

class CtripResponseMessage<T> {
    var header: CtripResponseHeader? = null
    var body: T? = null
}

class CtripPriceBody {
    var sequenceId: String = ""
    var otaOptionId: String = ""
    var supplierOptionId: String = ""
    var prices: List<CtripPrice> = emptyList()
}

class CtripPrice {
    var date: String = ""
    var marketPrice: Double = 0.0
    var salePrice: Double = 0.0
    var costPrice: Double = 0.0
}

class CtripRequest<T> {
    var header: CtripHeader? = null
    var body: T? = null
}



class CtripInventory {
    var date: String = ""
    var quantity:Int = 0
}

class CtripInventoryBody {
    var sequenceId: String = ""
    var otaOptionId: String = ""
    var supplierOptionId: String = ""
    var inventorys: List<CtripInventory> = emptyList()
}


/**
 * 核销订单
 */
data class CtripConsumedNoticeBody(
    var items: MutableList<CtripConsumedNoticeBodyItem?>? = ArrayList(),
    var otaOrderId: String? = "",
    var sequenceId: String? = "",
    var supplierOrderId: String? = ""
)

data class CtripConsumedNoticeBodyItem(
    var discount: CtripConsumedNoticeBodyItemDiscount? = CtripConsumedNoticeBodyItemDiscount(),
    var itemId: String? = "",
    var lostAmount: Double? = 0.0,
    var lostAmountCurrency: String? = "",
    var quantity: Int? = 0,
    var remark: String? = "",
    var useEndDate: String? = "",
    var useQuantity: Int? = 0,
    var useStartDate: String? = ""
)

data class CtripConsumedNoticeBodyItemDiscount(
    var policyList: List<CtripConsumedNoticeBodyItemPolicy?>? = listOf()
)

data class CtripConsumedNoticeBodyItemPolicy(
    var date: String? = "",
    var quantity: Int? = 0
)

/**
 * 订单通知
 */
data class CtripTravelNoticeBody(
    var items: MutableList<CtripTravelNoticeItem?> = ArrayList(),
    var vouchers: MutableList<CtripTravelNoticeVoucher> = ArrayList(),
    var otaOrderId: String? = "",
    var sequenceId: String? = "",
    var supplierOrderId: String? = ""
)

data class CtripTravelNoticeVoucher(
    var itemId: String? = "",
    var voucherCode: String? = "",
    var voucherData: String? = "",
    var voucherType: Int? = 0
)

data class CtripTravelNoticeItem(
    var itemId: String? = "",
    var remark: String? = ""
)