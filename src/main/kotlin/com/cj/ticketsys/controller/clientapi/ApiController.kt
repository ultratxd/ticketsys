package com.cj.ticketsys.controller.clientapi

import com.cj.ticketsys.controller.dto.RESULT_FAIL
import com.cj.ticketsys.controller.dto.RESULT_SUCCESS
import com.cj.ticketsys.controller.dto.Result
import com.cj.ticketsys.dao.ClientDataDao
import com.cj.ticketsys.entities.ClientGateLog
import com.cj.ticketsys.entities.ClientOrder
import com.cj.ticketsys.entities.ClientSubOrder
import com.cj.ticketsys.entities.SubOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.util.*
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
    ): Result  {
        val order = ClientOrder()
        order.clientId = orderBody.clientId;
        order.cloudId = orderBody.cloudId;
        order.clientOrderNo = orderBody.clientOrderNo;
        order.nums = orderBody.nums;
        order.orderType = orderBody.orderType;
        order.amount = orderBody.amount;
        order.perNums = orderBody.perNums;
        order.createTime = orderBody.createTime;
        order.state = orderBody.state;
        order.payType = orderBody.payType;
        order.realPay = orderBody.realPay;
        order.changePay = orderBody.changePay;
        order.shouldPay = orderBody.shouldPay;
        order.exCode = orderBody.exCode;
        order.remark = orderBody.remark;
        order.saleClientNo = orderBody.saleClientNo;
        order.ext1 = orderBody.ext1;
        order.ext2 = orderBody.ext2;
        order.ext3 = orderBody.ext3;

        val c = dataDao.insertOrder(order)
        if(c > 0) {
            return Result(RESULT_SUCCESS,"ok")
        }
        return Result(RESULT_FAIL,"fail")
    }

    @PostMapping("/sub_order")
    fun subOrder(
            @RequestBody subOrderBody: SubOrderReqBody,
            req: HttpServletRequest
    ): Result  {
        val subOrder = ClientSubOrder();
        subOrderBody.clientId = subOrder.clientId;
        subOrderBody.cloudId = subOrder.cloudId;
        subOrderBody.clientOrderNo = subOrder.clientOrderNo;
        subOrderBody.orderType = subOrder.orderType;
        subOrderBody.ticketId = subOrder.ticketId;
        subOrderBody.ticketName = subOrder.ticketName;
        subOrderBody.amount = subOrder.amount;
        subOrderBody.unitPrice = subOrder.unitPrice;
        subOrderBody.nums = subOrder.nums;
        subOrderBody.perNums = subOrder.perNums;
        subOrderBody.createTime = subOrder.createTime;
        subOrderBody.prints = subOrder.prints;
        subOrderBody.useDate = subOrder.useDate;
        subOrderBody.enterTime = subOrder.enterTime;

        val c = dataDao.insertSubOrder(subOrder);
        if(c > 0) {
            return Result(RESULT_SUCCESS,"ok")
        }
        return Result(RESULT_FAIL,"fail")
    }


}