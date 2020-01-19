package com.cj.ticketsys.controller.clientapi

import com.cj.ticketsys.controller.clientapi.dto.ClientOrderDto
import com.cj.ticketsys.controller.clientapi.vo.PageResult
import com.cj.ticketsys.controller.dto.RESULT_FAIL
import com.cj.ticketsys.controller.dto.RESULT_SUCCESS
import com.cj.ticketsys.controller.dto.Result
import com.cj.ticketsys.controller.dto.ResultT
import com.cj.ticketsys.controller.manage.dto.MRecommendDto
import com.cj.ticketsys.entities.*
import com.cj.ticketsys.svc.ClientSvc
import com.cj.ticketsys.svc.DocTransformer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/ota/v1/manage/sync")
class ApiController {

    @Autowired
    private lateinit var clientSvc: ClientSvc

    @Autowired
    private lateinit var orderTransformer: DocTransformer<ClientOrder, ClientOrderDto>

    /**
     * 插入ClientGateLog
     */
    @PostMapping("/gate_log")
    fun gateLog(
            @RequestBody gLog: GateLogReqBody,
            req: HttpServletRequest
    ): Result {
        return clientSvc.insertUpdateClientGareLog(gLog)
    }

    /**
     * 插入ClientOrder，如果subOrders不为null，则插入subOrders
     */
    @PostMapping("/order")
    fun order(
            @RequestBody orderBody: OrderReqBody,
            req: HttpServletRequest
    ): Result {
        return clientSvc.insertUpdateClientOrder(orderBody)
    }

    /**
     * 插入ClientSubOrder
     */
//    @PostMapping("/sub_order")
//    fun subOrder(
//            @RequestBody subOrderBody: SubOrderReqBody,
//            req: HttpServletRequest
//    ): Result {
//        return clientSvc.insertClientSubOrder(subOrderBody)
//
//    }


//    /**
//     * 修改ClientGateLog
//     */
//    @PostMapping("/updateGateLogs")
//    fun updateGateLogs(
//            @RequestBody gLog: GateLogReqBody,
//            req: HttpServletRequest
//    ): Result {
//        return clientSvc.updateClientGateLog(gLog)
//    }
//
//    /**
//     * 修改ClientOrder,如果subOrders不为null，则修改subOrders
//     */
//    @PostMapping("/updateOrders")
//    fun updateOrders(
//            @RequestBody orderBody: OrderReqBody,
//            req: HttpServletRequest
//    ): Result {
//        return clientSvc.updateClientOrder(orderBody)
//    }


    /**
     * 修改ClientSubOrder数据
     */
//    @PostMapping("/updateSubOrders")
//    fun updateSubOrders(
//            @RequestBody subOrderBody: SubOrderReqBody,
//            req: HttpServletRequest
//    ): Result {
//        return clientSvc.updateClientSubOrder(subOrderBody)
//    }



    /**
     * 获取ClientGateLogs分页数据
     */
    @GetMapping("/gateLogsList")
    fun getGateLogs(
            @RequestParam page_num: Int,
            @RequestParam page_size: Int,
            req: HttpServletRequest
    ): ResultT<PageResult<ClientGateLog>> {
        if (!checkPageParams(page_num, page_size)) {
            return ResultT(RESULT_FAIL, "参数错误")
        }
        return clientSvc.getClientGateLogs(page_num, page_size)
    }


    /**
     * 获取ClientOrders分页数据
     */
    @GetMapping("/ordersList")
    fun getOrders(
            @RequestParam page_num: Int?,
            @RequestParam page_size: Int?,
            req: HttpServletRequest
    ) : ResultT<PagedList<ClientOrderDto>> {
//        if (!checkPageParams(page_num, page_size)) {
//            return ResultT(RESULT_FAIL, "参数错误")
//        }
        var page = 1
        var size = 20
        if(page_num != null && page_num > 0) {
            page =  page_num
        }
        if(page_size != null && page_size > 0) {
            size = page_size
        }
        val pList = clientSvc.getClientOrders(page,size)
        val outs = ArrayList<ClientOrderDto>()
        for(order in pList.list) {
            outs.add(orderTransformer.transform(order)!!)
        }
        val outPList = PagedList(pList.page,pList.size,pList.total,outs)
        return ResultT(RESULT_SUCCESS,"",outPList)
    }

    /**
     * 获取ClientSubOrders分页数据
     */
    @GetMapping("/subOrdersList")
    fun getSubOrders(
            @RequestParam page_num: Int,
            @RequestParam page_size: Int,
            req: HttpServletRequest
    ) : ResultT<PageResult<ClientSubOrder>> {
        if (!checkPageParams(page_num, page_size)) {
            return ResultT(RESULT_FAIL, "参数错误")
        }
        return clientSvc.getClientSubOrders(page_num,page_size)

    }

    /**
     * 校验分页参数
     */
    private fun checkPageParams(
            page_num: Int,
            page_size: Int
    ): Boolean {
        return page_num > 0 && page_size > 0
    }



}