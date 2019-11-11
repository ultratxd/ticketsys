package com.cj.ticketsys.controller.clientapi

import com.cj.ticketsys.controller.dto.RESULT_FAIL
import com.cj.ticketsys.controller.dto.RESULT_SUCCESS
import com.cj.ticketsys.controller.dto.Result
import com.cj.ticketsys.dao.ClientDataDao
import com.cj.ticketsys.entities.ClientGateLog
import com.cj.ticketsys.entities.ClientOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/ota/v1/sync")
class ApiController {

    @Autowired
    private lateinit var dataDao:ClientDataDao

    @PostMapping("/gate_log")
    fun gateLog(
            @RequestBody gLog: GateLogReqBody,
            req: HttpServletRequest
    ): Result {
        val log = ClientGateLog()
        log.clientId = gLog.clientId
        log.clientOrderNo = gLog.clientOrderNo
        log.clientOrderSid = gLog.clientOrderSid
        log.code = gLog.code
        log.cType = gLog.cType
        log.scanDate = gLog.scanDate
        log.scanTime = gLog.scanTime
        log.inTime = gLog.inTime
        log.outTime = gLog.outTime
        log.perNums = gLog.perNums
        log.inPasses = gLog.inPasses
        log.outPasses = gLog.outPasses

        val c = dataDao.insertGateLog(log);
        if(c > 0) {
            return Result(RESULT_SUCCESS,"ok")
        }
        return Result(RESULT_FAIL,"fail")
    }

    @PostMapping("/order")
    fun order(
            @RequestBody orderBody: OrderReqBody,
            req: HttpServletRequest
    ) {
        val order = ClientOrder()

    }

    @PostMapping("/sub_order")
    fun subOrder(
            @RequestBody order: OrderReqBody,
            req: HttpServletRequest
    ) {

    }
}