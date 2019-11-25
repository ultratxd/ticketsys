package com.cj.ticketsys.svc.b2b

import com.alibaba.druid.util.Utils.md5
import com.alibaba.fastjson.JSON
import com.cj.ticketsys.entities.ChannelTypes
import com.cj.ticketsys.entities.Order
import com.cj.ticketsys.entities.TicketPrice
import com.cj.ticketsys.svc.PriceBinder
import okhttp3.MediaType
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
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody


@Service
class B2BCtripSvc {
    @Autowired
    private lateinit var priceBinder: PriceBinder

    @Value("\${b2b.ctrip.secret}")
    private lateinit var secret: String

    @Value("\${b2b.ctrip.accountId}")
    private lateinit var accountId: String

    @Value("\${b2b.ctrip.version}")
    private lateinit var version: String

    @Value("\${b2b.ctrip.notifyUrl}")
    private lateinit var notifyUrl: String

    val JsonMediaType  = "application/json; charset=utf-8".toMediaType()

    /**
     * 价格同步
     */
    fun pushPrices(price: TicketPrice) {
        if (price.channelType != ChannelTypes.Ctrip || price.b2bPLU == null) {
            return
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
        ctripHeader.sign = makeSign(accountId, ctripHeader.serviceName, JSON.toJSONString(ctripBody))

        val ctripReqBody = CtripRequest<CtripPriceBody>()
        ctripReqBody.header = ctripHeader
        ctripReqBody.body = ctripBody

        val client = OkHttpClient()
        val reqBody = JSON.toJSONString(ctripReqBody).toRequestBody(JsonMediaType)
        val request = Request.Builder()
                .url(notifyUrl)
                .post(reqBody)
                .build();
        val resp = client.newCall(request).execute()
        val respBody = resp.body.toString()
        val respResult = JSON.parseObject(respBody,CtripResponseHeader::class.java)
        if(respResult.resultCode.equals("0000")) {

        }
    }

    /**
     * 库存同步
     */
    fun pushInventory(price: TicketPrice) {
        if (price.channelType != ChannelTypes.Ctrip || price.b2bPLU == null) {
            return
        }
        val tktId = price.tid
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
        ctripHeader.sign = makeSign(accountId, ctripHeader.serviceName, JSON.toJSONString(ctripBody))

        val ctripReqBody = CtripRequest<CtripInventoryBody>()
        ctripReqBody.header = ctripHeader
        ctripReqBody.body = ctripBody

        val client = OkHttpClient()
        val reqBody = JSON.toJSONString(ctripReqBody).toRequestBody(JsonMediaType)
        val request = Request.Builder()
                .url(notifyUrl)
                .post(reqBody)
                .build();
        val resp = client.newCall(request).execute()
        val respBody = resp.body.toString()
        val respResult = JSON.parseObject(respBody,CtripResponseHeader::class.java)
        if(respResult.resultCode.equals("0000")) {

        }
    }

    fun verifyOrder(order: Order) {

    }

    private fun makeSign(serviceName: String, requestTime: String, body: String): String {
        //accountId+serviceName+requestTime+body+version+signkey
        return md5(accountId + serviceName + requestTime + body + version + secret).toLowerCase()
    }

    private fun makeSeqId(): String {
        val dateFmt = SimpleDateFormat("yyyyMMdd")
        val uuid = UUID.randomUUID().toString().replace("-", "")
        return dateFmt.format(Date()) + uuid
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

class CtripResponseMessage {
    var header: CtripResponseMessage? = null
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


//Inventory

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