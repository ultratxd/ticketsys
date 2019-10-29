package com.cj.ticketsys.svc

import com.alibaba.druid.util.Utils.md5
import com.cj.ticketsys.dao.TicketPriceDao
import com.cj.ticketsys.entities.ChannelTypes
import com.cj.ticketsys.svc.impl.TicketPriceBinder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class CtripScheduler {
    @Autowired
    private lateinit var tktPriceDao:TicketPriceDao

    @Autowired
    private lateinit var priceBuilder: TicketPriceBinder

    @Value("\${b2b.ctrip.secret}")
    private lateinit var secret:String

    @Value("\${b2b.ctrip.accountId}")
    private lateinit var accountId:String

    @Value("\${b2b.ctrip.version}")
    private lateinit var version:String

    @Value("\${b2b.ctrip.notifyUrl}")
    private lateinit var notifyUrl:String

    //@Scheduled(fixedRate = (1000 * 60 * 3).toLong(), initialDelay = 1000)
    fun pushPrices() {
        val prices = tktPriceDao.getsByChannelAndEnabled(ChannelTypes.Ctrip.code())
        for (price in prices) {
            if(price.ctripId == null) {
                continue
            }
            val tktId = price.tid

        }
    }

    fun pushStock() {

    }

    private fun makeSign(serviceName:String, requestTime: String, body:String): String {
        //accountId+serviceName+requestTime+body+version+signkey
        return md5(accountId + serviceName + requestTime + body + version + secret).toLowerCase()
    }

}

class CtripHeader {
    var accountId:String = ""
    var serviceName:String = ""
    var requestTime:String =""
    var version:String = ""
    var sign:String = ""
}

class CtripResponseHeader {
    var resultCode:String = ""
    var resultMessage:String = ""
}

class CtripResponseMessage {
    var header:CtripResponseMessage? = null
}

class CtripPriceBody {
    var sequenceId:String = ""
    var otaOptionId:String = ""
    var supplierOptionId:String = ""
    var prices:List<CtripPrice> = emptyList()
}

class CtripPrice {
    var date:String = ""
    var marketPrice:Double = 0.0
    var salePrice:Double = 0.0
    var costPrice:Double = 0.0
}

class CtripPriceRequest {
    var header:CtripHeader? = null
    var body:CtripPriceBody? = null
}