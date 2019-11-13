package com.cj.ticketsys.svc.impl

import com.cj.ticketsys.controller.clientapi.GateLogReqBody
import com.cj.ticketsys.controller.clientapi.OrderReqBody
import com.cj.ticketsys.controller.clientapi.SubOrderReqBody
import com.cj.ticketsys.controller.clientapi.dto.ClientOrderDto
import com.cj.ticketsys.controller.clientapi.utils.BeanHelper
import com.cj.ticketsys.controller.clientapi.vo.PageResult
import com.cj.ticketsys.controller.dto.RESULT_FAIL
import com.cj.ticketsys.controller.dto.RESULT_SUCCESS
import com.cj.ticketsys.controller.dto.Result
import com.cj.ticketsys.controller.dto.ResultT
import com.cj.ticketsys.dao.ClientDataDao
import com.cj.ticketsys.entities.ClientGateLog
import com.cj.ticketsys.entities.ClientOrder
import com.cj.ticketsys.entities.ClientSubOrder
import com.cj.ticketsys.svc.ClientSvc
import com.github.pagehelper.PageHelper
import com.github.pagehelper.PageInfo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.interceptor.TransactionAspectSupport

/**
 *  @author wangliwei
 *  @date 2019/11/12
 *
 *  ClientApi服务层实现
 */
@Service
class ClientSvcImpl : ClientSvc {

    @Autowired
    private lateinit var dataDao: ClientDataDao

    /**
     * 插入ClientGateLog
     */
    @Transactional(rollbackFor = [Exception::class])
    override fun insertClientGareLog(gLog: GateLogReqBody): Result {
        val log = createClientGateLog(gLog)
        log.scanDate = gLog.scanDate

        val c = dataDao.insertGateLog(log)
        if (c > 0) {
            return Result(RESULT_SUCCESS, "ok")
        }
        return Result(RESULT_FAIL, "fail")
    }


    /**
     * 插入ClientOrder
     */
    @Transactional(rollbackFor = [Exception::class])
    override fun insertClientOrder(orderBody: OrderReqBody): Result {
        val order = createClientOrder(orderBody)
        order.createTime = orderBody.createTime

        //插入主订单
        val c = dataDao.insertOrder(order)
        if (c <= 0) {
            return Result(RESULT_FAIL, "fail")
        }

        val subOrders = orderBody.subOrders
        //如果subOrders不为空，插入所有subOrders
        if (!subOrders.isNullOrEmpty()) {
            for (subOrder in subOrders) {
                //校验子订单的pid是否等于父订单的cid
                if (subOrder.clientParentId != order.clientId) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()
                    return Result(RESULT_FAIL, "fail")
                }
                val d = insertSubOrder(subOrder)
                if (d <= 0) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()
                    return Result(RESULT_FAIL, "fail")
                }
            }
        }

        return Result(RESULT_SUCCESS, "ok")
    }


    /**
     * 插入ClientSubOrder
     */
    @Transactional(rollbackFor = [Exception::class])
    override fun insertClientSubOrder(subOrderBody: SubOrderReqBody): Result {
        val c = insertSubOrder(subOrderBody)
        if (c > 0) {
            return Result(RESULT_SUCCESS, "ok")
        }
        return Result(RESULT_FAIL, "fail")
    }


    /**
     * 更新ClientGateLog
     */
    @Transactional(rollbackFor = [Exception::class])
    override fun updateClientGateLog(gLog: GateLogReqBody): Result {
        val log = createClientGateLog(gLog)

        val c = dataDao.updateGateLog(log)
        if (c > 0) {
            return Result(RESULT_SUCCESS, "ok")
        }
        return Result(RESULT_FAIL, "fail")
    }


    /**
     * 更新ClientOrder
     */
    @Transactional(rollbackFor = [Exception::class])
    override fun updateClientOrder(orderBody: OrderReqBody): Result {
        val order = createClientOrder(orderBody)

        val c = dataDao.updateOrder(order)
        if (c <= 0) {
            return Result(RESULT_FAIL, "fail")
        }

        val subOrders = orderBody.subOrders
        if (!subOrders.isNullOrEmpty()) {
            //如果subOrders不为空，插入所有subOrders
            for (subOrder in subOrders) {
                //校验子订单的pid是否等于父订单的cid
                if (subOrder.clientParentId != order.clientId) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()
                    return Result(RESULT_FAIL, "fail")
                }
                val d = updateSubOrder(subOrder)
                if (d <= 0) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()
                    return Result(RESULT_FAIL, "fail")
                }
            }
        }
        return Result(RESULT_SUCCESS, "ok")
    }


    /**
     * 更新ClientSubOrder
     */
    @Transactional(rollbackFor = [Exception::class])
    override fun updateClientSubOrder(subOrderBody: SubOrderReqBody): Result {
        //如果子订单要修改的pid不为空，校验是否存在对应的父订单cid
        if (subOrderBody.clientParentId != 0) {
            dataDao.selectByCid(subOrderBody.clientParentId) ?: return Result(RESULT_FAIL, "对应父订单不存在")
        }
        val c = updateSubOrder(subOrderBody)
        if (c > 0) {
            return Result(RESULT_SUCCESS, "ok")
        }
        return Result(RESULT_FAIL, "fail")
    }


    /**
     * 分页查询ClientGateLogs
     */
    override fun getClientGateLogs(page_num: Int, page_size: Int): ResultT<PageResult<ClientGateLog>> {
        //开启分页
        PageHelper.startPage<ClientGateLog>(page_num, page_size)
        //查询所有gateLogs数据
        val clientGateLogs = dataDao.selectGateLogList()
        //封装数据到分页助手中
        val pageInfo = PageInfo<ClientGateLog>(clientGateLogs)
        val pageResult = PageResult<ClientGateLog>(pageInfo.total, pageInfo.pages, clientGateLogs)
        //返回分页数据
        return ResultT(RESULT_SUCCESS, "ok", pageResult)
    }


    /**
     * 分页查询ClientOrders
     */
    override fun getClientOrders(page_num: Int, page_size: Int): ResultT<PageResult<ClientOrderDto>> {
        //开启分页
        PageHelper.startPage<ClientOrder>(page_num, page_size)
        //查询所有gateLogs数据
        val clientOrders = dataDao.selectClientOrderList()
        //拷贝数据到ClientOrdersDto中
        val clientOrdersDtos = BeanHelper.copyWithCollection(clientOrders, ClientOrderDto::class.java) ?: return ResultT(RESULT_FAIL, "【数据转换】订单数据转换出错")
        //将子订单的数据封装到dto中
        for (order in clientOrdersDtos) {
            //根据pId查询子订单，并将其封装到dto中
            val id = order.clientId
            val subOrders = dataDao.selectByPid(id)
            order.childrens = subOrders
        }
        //封装数据到分页助手中
        val pageInfo = PageInfo<ClientOrderDto>(clientOrdersDtos)
        val pageResult = PageResult<ClientOrderDto>(pageInfo.total, pageInfo.pages, clientOrdersDtos)
        //返回分页数据
        return ResultT(RESULT_SUCCESS, "ok", pageResult)
    }


    /**
     * 分页查询ClientSubOrders
     */
    override fun getClientSubOrders(page_num: Int, page_size: Int): ResultT<PageResult<ClientSubOrder>> {
        //开启分页
        PageHelper.startPage<ClientSubOrder>(page_num, page_size)
        //查询所有gateLogs数据
        val clientSubOrders = dataDao.selectSubOrderList()
        //封装数据到分页助手中
        val pageInfo = PageInfo<ClientSubOrder>(clientSubOrders)
        val pageResult = PageResult<ClientSubOrder>(pageInfo.total, pageInfo.pages, clientSubOrders)
        //返回分页数据
        return ResultT(RESULT_SUCCESS, "ok", pageResult)
    }


    private fun insertSubOrder(subOrderBody: SubOrderReqBody): Long {
        val subOrder = createClientSubOrder(subOrderBody)
        subOrder.createTime = subOrderBody.createTime

        return dataDao.insertSubOrder(subOrder)
    }

    private fun updateSubOrder(subOrderBody: SubOrderReqBody): Long {

        val subOrder = createClientSubOrder(subOrderBody)

        return dataDao.updateSubOrder(subOrder)
    }

    private fun createClientGateLog(gLog: GateLogReqBody): ClientGateLog {
        val log = ClientGateLog()
        log.clientId = gLog.clientId
        log.clientOrderNo = gLog.clientOrderNo
        log.clientOrderSid = gLog.clientOrderSid
        log.code = gLog.code
        log.cType = gLog.cType
        log.scanTime = gLog.scanTime
        log.inTime = gLog.inTime
        log.outTime = gLog.outTime
        log.perNums = gLog.perNums
        log.inPasses = gLog.inPasses
        log.outPasses = gLog.outPasses
        log.properties = gLog.properties
        return log
    }

    private fun createClientOrder(orderBody: OrderReqBody): ClientOrder {
        val order = ClientOrder()
        order.clientId = orderBody.clientId
        order.cloudId = orderBody.cloudId
        order.clientOrderNo = orderBody.clientOrderNo
        order.nums = orderBody.nums
        order.orderType = orderBody.orderType
        order.amount = orderBody.amount
        order.perNums = orderBody.perNums
        order.state = orderBody.state
        order.payType = orderBody.payType
        order.realPay = orderBody.realPay
        order.changePay = orderBody.changePay
        order.shouldPay = orderBody.shouldPay
        order.exCode = orderBody.exCode
        order.remark = orderBody.remark
        order.saleClientNo = orderBody.saleClientNo
        order.ext1 = orderBody.ext1
        order.ext2 = orderBody.ext2
        order.ext3 = orderBody.ext3
        order.properties = orderBody.properties
        return order
    }

    private fun createClientSubOrder(subOrderBody: SubOrderReqBody): ClientSubOrder {
        val subOrder = ClientSubOrder()
        subOrder.clientId = subOrderBody.clientId
        subOrder.cloudId = subOrderBody.cloudId
        subOrder.clientOrderNo = subOrderBody.clientOrderNo
        subOrder.orderType = subOrderBody.orderType
        subOrder.ticketId = subOrderBody.ticketId
        subOrder.ticketName = subOrderBody.ticketName
        subOrder.amount = subOrderBody.amount
        subOrder.unitPrice = subOrderBody.unitPrice
        subOrder.nums = subOrderBody.nums
        subOrder.perNums = subOrderBody.perNums
        subOrder.prints = subOrderBody.prints
        subOrder.useDate = subOrderBody.useDate
        subOrder.enterTime = subOrderBody.enterTime
        subOrder.clientParentId = subOrderBody.clientParentId
        subOrder.properties = subOrderBody.properties
        return subOrder
    }

}