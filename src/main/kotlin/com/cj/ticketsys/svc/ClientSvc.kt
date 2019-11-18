package com.cj.ticketsys.svc

import com.cj.ticketsys.controller.clientapi.GateLogReqBody
import com.cj.ticketsys.controller.clientapi.OrderReqBody
import com.cj.ticketsys.controller.clientapi.SubOrderReqBody
import com.cj.ticketsys.controller.clientapi.dto.ClientOrderDto
import com.cj.ticketsys.controller.clientapi.vo.PageResult
import com.cj.ticketsys.controller.dto.Result
import com.cj.ticketsys.controller.dto.ResultT
import com.cj.ticketsys.entities.ClientGateLog
import com.cj.ticketsys.entities.ClientSubOrder

/**
 *  @author wangliwei
 *  @date 2019/11/12
 *
 */
interface ClientSvc {
    fun insertClientGareLog(gLog: GateLogReqBody): Result
    fun insertClientOrder(orderBody: OrderReqBody): Result
    fun insertClientSubOrder(subOrderBody: SubOrderReqBody): Result
    fun updateClientGateLog(gLog: GateLogReqBody): Result
    fun updateClientOrder(orderBody: OrderReqBody): Result
    fun updateClientSubOrder(subOrderBody: SubOrderReqBody): Result
    fun getClientGateLogs(page_num: Int, page_size: Int): ResultT<PageResult<ClientGateLog>>
    fun getClientOrders(page_num: Int, page_size: Int): ResultT<PageResult<ClientOrderDto>>
    fun getClientSubOrders(page_num: Int, page_size: Int): ResultT<PageResult<ClientSubOrder>>
}