package com.cj.ticketsys.controller.clientapi

import com.cj.ticketsys.controller.clientapi.dto.ClientOrderDto
import com.cj.ticketsys.controller.clientapi.dto.KValues
import com.cj.ticketsys.controller.clientapi.vo.PageResult
import com.cj.ticketsys.controller.dto.RESULT_FAIL
import com.cj.ticketsys.controller.dto.RESULT_SUCCESS
import com.cj.ticketsys.controller.dto.Result
import com.cj.ticketsys.controller.dto.ResultT
import com.cj.ticketsys.controller.manage.dto.MRecommendDto
import com.cj.ticketsys.dao.ClientDataDao
import com.cj.ticketsys.entities.*
import com.cj.ticketsys.svc.ClientSvc
import com.cj.ticketsys.svc.DocTransformer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import javax.servlet.http.HttpServletRequest
import kotlin.collections.ArrayList

@RestController
@RequestMapping("/ota/v1/manage/sync")
class ApiController {

    @Autowired
    private lateinit var clientSvc: ClientSvc

    @Autowired
    private lateinit var clientDataDao: ClientDataDao

    @Autowired
    private lateinit var orderTransformer: DocTransformer<ClientOrder, ClientOrderDto>

    @GetMapping("/statistic/gate")
    fun statisticGatePasses(startDate:String?,endDate:String?,spotId:Int?,type:Int?):ResultT<List<KValues<String, Int>>> {
        if(startDate == null || endDate == null || spotId == null || type == null) {
            return ResultT(RESULT_FAIL, "参数错误")
        }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        var sDate:Date
        var eDate:Date
        try{
            sDate = dateFormat.parse(startDate)
            eDate = dateFormat.parse(endDate)
        }catch(e:Exception){
            return ResultT(com.cj.ticketsys.controller.dto.RESULT_FAIL, "日期格式错误")
        }
        val result = ArrayList<KValues<String, Int>>()
         when(type) {
             1 -> { //月

                 val data = clientDataDao.statisticGateLogOfMonths(sDate,eDate, spotId)
                 for (skv in data) {
                     result.add(KValues("${skv.year}-${skv.month}", skv.value.toInt()))
                 }
             }
             2 -> { //天
                 val data = clientDataDao.statisticGateLogOfDays(sDate, eDate, spotId)
                 for (skv in data) {
                     result.add(KValues("${skv.year}-${skv.month}-${skv.day}", skv.value.toInt()))
                 }
             }
             3 -> {
                 val data = clientDataDao.statisticGateLogOfThisDay(sDate, spotId)
                 result.add(KValues(dateFormat.format(sDate), data.toInt()))
             }
             else -> {
                 return ResultT(RESULT_FAIL, "参数错误")
             }
         }
        return ResultT(RESULT_SUCCESS, "OK",result)
    }

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
            @RequestParam order_type: Int?,
            req: HttpServletRequest
    ) : ResultT<PagedList<ClientOrderDto>> {
        var page = 1
        var size = 20
        var orderType = 0
        if(page_num != null && page_num > 0) {
            page =  page_num
        }
        if(page_size != null && page_size > 0) {
            size = page_size
        }

        val pList = clientSvc.getClientOrders(page,size,order_type)
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