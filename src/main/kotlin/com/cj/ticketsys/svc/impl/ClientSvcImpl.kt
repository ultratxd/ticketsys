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
import com.cj.ticketsys.dao.*
import com.cj.ticketsys.entities.*
import com.cj.ticketsys.svc.ClientSvc
import com.github.pagehelper.PageHelper
import com.github.pagehelper.PageInfo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.interceptor.TransactionAspectSupport
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 *  @author wangliwei
 *  @date 2019/11/12
 *
 *  ClientApi服务层实现
 */
@Service
class ClientSvcImpl : ClientSvc {

    @Autowired
    private lateinit var clientDataDao: ClientDataDao

    @Autowired
    private lateinit var clientUserDao: ClientUserDao

    @Autowired
    private lateinit var scenicSpotDao: ScenicSpotDao

    @Autowired
    private lateinit var orderDao: OrderDao

    @Autowired
    private lateinit var subOrderDao: SubOrderDao

    /**
     * 插入ClientGateLog
     */
    @Transactional(rollbackFor = [Exception::class])
    override fun insertUpdateClientGareLog(gLog: GateLogReqBody): Result {
        val log = createClientGateLog(gLog)
        log.scanDate = gLog.scanDate

        val localLog = clientDataDao.queryGateLog(gLog.clientId)
        if(localLog == null) {
            val c = clientDataDao.insertGateLog(log)
            if (c > 0) {
                return Result(RESULT_SUCCESS, "ok")
            }
        }else {
            val c = clientDataDao.updateGateLog(log);
            if (c > 0) {
                return Result(RESULT_SUCCESS, "ok")
            }
        }
        //syncCloudOrderState(gLog)
        return Result(RESULT_FAIL, "fail")
    }

    private fun syncCloudOrderState(gLog: GateLogReqBody) {
        
    }

    /**
     * 插入ClientOrder
     */
    @Transactional(rollbackFor = [Exception::class])
    override fun insertUpdateClientOrder(orderBody: OrderReqBody): Result {
        val order = createClientOrder(orderBody)
        order.createTime = orderBody.createTime

        //插入主订单
        val localOrder = clientDataDao.queryOrder(order.clientId)
       if(localOrder != null) {
           val c = clientDataDao.updateOrder(order)
           if (c <= 0) {
               return Result(RESULT_FAIL, "fail")
           }
        } else {
            clientDataDao.insertOrder(order)
        }


        val subOrders = orderBody.subOrders
        //如果subOrders不为空，插入所有subOrders
        if (subOrders != null) {
            for (subOrder in subOrders) {
                val localSubOrder = clientDataDao.queryOrder(subOrder.clientId)
                if(localSubOrder != null) {
                    updateSubOrder(subOrder)
                } else {
                    insertSubOrder(subOrder)
                }
            }
        }

        //同步云端订单状态
        //syncCloudOrder(orderBody);

        return Result(RESULT_SUCCESS, "ok")
    }

    fun syncCloudOrder(orderBody: OrderReqBody) : Boolean {
        when(orderBody.orderType.toInt()) {
            //门市
            1,4 -> {
                val order = orderReqToOrder(orderBody, BuyTypes.Offline)
                val subOrders = orderReqToSubOrders(orderBody)
                val localOrder = orderDao.get(order.orderId)
                if(localOrder == null) {
                    orderDao.insert(order)
                } else {
                    localOrder.price = order.price
                    localOrder.childs = order.childs
                    orderDao.update(localOrder)
                }
                val localSubOrders = subOrderDao.gets(order.orderId)
                for(subOrder in subOrders) {
                    val lso = localSubOrders.firstOrNull { a->a.ticketPid == subOrder.ticketPid }
                    if(lso == null) {
                        subOrderDao.insert(subOrder)
                    } else {
                        lso.useDate = subOrder.useDate
                        lso.pernums = subOrder.pernums
                        lso.ticketPid = subOrder.ticketPid
                        lso.cid = subOrder.cid
                        lso.totalPrice = subOrder.totalPrice
                        lso.unitPrice = subOrder.unitPrice
                        subOrderDao.update(lso)
                    }
                }
                return true
            }
            //换票
            2 -> {
                return true
            }
            //电商换票
            3 -> {
                return true
            }
        }
        return false
    }

    fun orderReqToOrder(orderBody: OrderReqBody,buyType: BuyTypes) : Order {
        val order = Order()
        order.orderId = orderBody.clientOrderNo
        order.createTime = orderBody.createTime
        order.price = orderBody.amount
        order.childs = if(orderBody.subOrders == null) 0 else orderBody.subOrders!!.size
        order.state = clientOrderStateConvert(orderBody.state)
        order.buyType = buyType
        return order
    }

    fun orderReqToSubOrders(orderBody: OrderReqBody) : List<SubOrder> {
        if(orderBody.subOrders == null || orderBody.subOrders!!.isEmpty()) {
            return emptyList()
        }
        val sOrders = ArrayList<SubOrder>()
        for (subOrder in orderBody.subOrders!!) {
            val client = clientUserDao.getUserByNo(orderBody.saleClientNo) ?: continue
            val scenicSpot = scenicSpotDao.get(client.scenicSid) ?: continue

            val sOrder = SubOrder()
            sOrder.orderId = subOrder.clientOrderNo
            sOrder.createTime = subOrder.createTime
            sOrder.scenicId = scenicSpot.pid
            sOrder.scenicSid = scenicSpot.id
            sOrder.ticketId = 0
            sOrder.ticketPid = subOrder.ticketId
            sOrder.unitPrice = subOrder.unitPrice
            sOrder.totalPrice = subOrder.amount
            sOrder.nums = subOrder.nums
            sOrder.pernums = subOrder.perNums
            sOrder.state = clientOrderStateConvert(orderBody.state)
            sOrder.useDate = numberToDate(subOrder.useDate)
            sOrder.cid = TicketCategories.Retail.value
            sOrder.issueTicketTime = subOrder.createTime

            sOrders.add(sOrder)
        }
        return sOrders
    }

    private fun clientOrderStateConvert(state:Short):OrderStates {
        when(state.toInt()) {
            1 -> return OrderStates.Init
            2 -> return OrderStates.Paied
            3 -> return OrderStates.Used
            else -> return OrderStates.Unknown
        }
    }

    private fun numberToDate(date:Int) : Date {
        val format = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
        val year = date.toString().substring(0,4)
        val month = date.toString().substring(4,6)
        val day = date.toString().substring(6)
        return format.parse("${year}-${month}-${day} 00:00:00")
    }

    /**
     * 更新ClientGateLog
     */
    @Transactional(rollbackFor = [Exception::class])
    override fun updateClientGateLog(gLog: GateLogReqBody): Result {
        val log = createClientGateLog(gLog)

        val c = clientDataDao.updateGateLog(log)
        if (c > 0) {
            return Result(RESULT_SUCCESS, "ok")
        }
        return Result(RESULT_FAIL, "fail")
    }

//    override fun updateClientOrder(orderBody: OrderReqBody): Result {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    override fun updateClientSubOrder(subOrderBody: SubOrderReqBody): Result {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }


    /**
     * 分页查询ClientGateLogs
     */
    override fun getClientGateLogs(page_num: Int, page_size: Int): ResultT<PageResult<ClientGateLog>> {
        //开启分页
        PageHelper.startPage<ClientGateLog>(page_num, page_size)
        //查询所有gateLogs数据
        val clientGateLogs = clientDataDao.selectGateLogList()
        //封装数据到分页助手中
        val pageInfo = PageInfo<ClientGateLog>(clientGateLogs)
        val pageResult = PageResult<ClientGateLog>(pageInfo.total, pageInfo.pages, clientGateLogs)
        //返回分页数据
        return ResultT(RESULT_SUCCESS, "ok", pageResult)
    }


    /**
     * 分页查询ClientOrders
     */
    override fun getClientOrders(page_num: Int, page_size: Int): ResultT<PagedList<ClientOrderDto>> {
//        //开启分页
//        PageHelper.startPage<ClientOrder>(page_num, page_size)
//        //查询所有gateLogs数据
//        val clientOrders = clientDataDao.selectClientOrderList()
//        //拷贝数据到ClientOrdersDto中
//        val clientOrdersDtos = BeanHelper.copyWithCollection(clientOrders, ClientOrderDto::class.java) ?: return ResultT(RESULT_FAIL, "【数据转换】订单数据转换出错")
//        //将子订单的数据封装到dto中
//        for (order in clientOrdersDtos) {
//            //根据pId查询子订单，并将其封装到dto中
//            val id = order.clientId
//            val subOrders = clientDataDao.selectByPid(id)
//            order.childrens = subOrders
//        }
//        //封装数据到分页助手中
//        val pageInfo = PageInfo<ClientOrderDto>(clientOrdersDtos)
//        val pageResult = PageResult<ClientOrderDto>(pageInfo.total, pageInfo.pages, clientOrdersDtos)
//        //返回分页数据
//        return ResultT(RESULT_SUCCESS, "ok", pageResult)

        val offset = (page_num - 1) * page_size
        val total = clientDataDao.selectClientOrderCount()
        val list = clientDataDao.selectClientOrderList(offset,page_size)
        val clientOrdersDtos = BeanHelper.copyWithCollection(list, ClientOrderDto::class.java) ?: return ResultT(RESULT_FAIL, "【数据转换】订单数据转换出错")
        val pList = PagedList(page_num,page_size,total,clientOrdersDtos)
        return ResultT(RESULT_SUCCESS,"ok",pList)
    }


    /**
     * 分页查询ClientSubOrders
     */
    override fun getClientSubOrders(page_num: Int, page_size: Int): ResultT<PageResult<ClientSubOrder>> {
        //开启分页
        PageHelper.startPage<ClientSubOrder>(page_num, page_size)
        //查询所有gateLogs数据
        val clientSubOrders = clientDataDao.selectSubOrderList()
        //封装数据到分页助手中
        val pageInfo = PageInfo<ClientSubOrder>(clientSubOrders)
        val pageResult = PageResult<ClientSubOrder>(pageInfo.total, pageInfo.pages, clientSubOrders)
        //返回分页数据
        return ResultT(RESULT_SUCCESS, "ok", pageResult)
    }


    private fun insertSubOrder(subOrderBody: SubOrderReqBody): Long {
        val subOrder = createClientSubOrder(subOrderBody)
        subOrder.createTime = subOrderBody.createTime

        return clientDataDao.insertSubOrder(subOrder)
    }

    private fun updateSubOrder(subOrderBody: SubOrderReqBody): Long {

        val subOrder = createClientSubOrder(subOrderBody)

        return clientDataDao.updateSubOrder(subOrder)
    }

    private fun createClientGateLog(gLog: GateLogReqBody): ClientGateLog {
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