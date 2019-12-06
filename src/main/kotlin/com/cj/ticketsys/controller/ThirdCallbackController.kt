package com.cj.ticketsys.controller

import com.cj.ticketsys.entities.OrderTicketCodeProviders
import com.cj.ticketsys.svc.OrderSvc
import com.cj.ticketsys.svc.entrance.EntranceConsts
import com.cj.ticketsys.svc.entrance.SCApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/ota/third")
class ThirdCallbackController : BaseController() {

    @Autowired
    private lateinit var orderSvc: OrderSvc

    @PostMapping("notice/sc/ret_code")
    fun SCRetCodeNotice(
        request: HttpServletRequest
    ): NoticeResult {
        val body = BufferedReader(InputStreamReader(request.inputStream)).readLine()
        val scapi = SCApi(EntranceConsts.SC_USER_ID, EntranceConsts.SC_USER_KEY)
        val result = scapi.retCodeNotice(body)

        val ret = NoticeResult()
        if (result == null) {
            ret.responseHead = NoticeResultHead("5004", "")
            return ret
        }
        val ok = orderSvc.completedEnter(result.partnerOrderId, result.partnerCode, OrderTicketCodeProviders.ShuCheng)
        if (ok) {
            ret.responseHead = NoticeResultHead("1000", "成功更新订单状态")
            return ret
        }
        ret.responseHead = NoticeResultHead("5004", "未成功")
        return ret
    }

    class NoticeResult {
        var responseHead: NoticeResultHead? = null
        var responseBody: String = ""
    }

    class NoticeResultHead(val res_code: String = "", val res_msg: String = "") {
        var timestamp = System.currentTimeMillis() / 1000
    }

}