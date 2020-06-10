package com.cj.ticketsys.controller.manage

import com.alibaba.fastjson.JSON
import com.cj.ticketsys.controller.BaseController
import com.cj.ticketsys.controller.dto.RESULT_FAIL
import com.cj.ticketsys.controller.dto.RESULT_SUCCESS
import com.cj.ticketsys.controller.dto.ResultT
import com.cj.ticketsys.controller.manage.dto.MOrderDto
import com.cj.ticketsys.controller.manage.dto.MTicketCardDto
import com.cj.ticketsys.dao.CardTicketDao
import com.cj.ticketsys.dao.OrderDao
import com.cj.ticketsys.dao.OrderQuery
import com.cj.ticketsys.dao.SubOrderDao
import com.cj.ticketsys.entities.*
import com.cj.ticketsys.svc.DocTransformer
import com.cj.ticketsys.svc.TicketSnapshot
import com.google.common.base.Strings
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.SimpleFormatter
import kotlin.collections.ArrayList

@RestController
@RequestMapping("/ota/v1/manage/order")
class ManageOrderController : BaseController() {
    @Autowired
    private lateinit var orderDao: OrderDao

    @Autowired
    private lateinit var subOrderDao: SubOrderDao

    @Autowired
    private lateinit var cardTicketDao: CardTicketDao

    @Autowired
    private lateinit var orderTransformer: DocTransformer<Order,MOrderDto>

    @GetMapping("search")
    fun search(
        @RequestParam("order_no", required = false) orderNo: String?,
        @RequestParam("state", required = false) state: Short?,
        @RequestParam("pay_no", required = false) payNo: String?,
        @RequestParam("refund_no", required = false) refundNo: String?,
        @RequestParam("ch_id", required = false) chId: String?,
        @RequestParam("ch_uid", required = false) chUid: String?,
        @RequestParam("user_name", required = false) userName: String?,
        @RequestParam("user_card", required = false) userCard: String?,
        @RequestParam("user_mobile", required = false) userMobile: String?,
        @RequestParam("scenic_id", required = false) scenicId: Int?,
        @RequestParam("scenic_sid", required = false) scenicSid: Int?,
        @RequestParam("ticket_id", required = false) ticketId: Int?,
        @RequestParam("cid", required = false) cid: Int?,
        @RequestParam("buy_type", required = false) buyType: Short?,
        @RequestParam("page", required = false) page: Int? = 1,
        @RequestParam("size", required = false) size: Int? = 20
        ):ResultT<PagedList<MOrderDto>> {

        if(!Strings.isNullOrEmpty(orderNo)) {
            val order = orderDao.get(orderNo!!)
            if(order != null) {
                return ResultT(RESULT_SUCCESS,"OK",PagedList(1,1,1, listOf(orderTransformer.transform(order)!!)))
            }
            return ResultT(RESULT_FAIL,"订单不存在")
        }

        val query = OrderQuery()
        if(state != null) {
            if (!OrderStates.values().any { s -> s.value == state}) {
                return ResultT(RESULT_FAIL, "state状态不存在")
            }
            query.state = OrderStates.prase(state.toInt())
        }
        if(buyType != null) {
            if (!BuyTypes.values().any { s -> s.value == buyType}) {
                return ResultT(RESULT_FAIL, "buyType状态不存在")
            }
            query.buyType = BuyTypes.prase(buyType)
        }
        query.payNo = if(!Strings.isNullOrEmpty(payNo)) payNo else null
        query.refundNo = if(!Strings.isNullOrEmpty(refundNo)) refundNo else null
        query.chId = if(!Strings.isNullOrEmpty(chId)) chId else null
        query.chUid = if(!Strings.isNullOrEmpty(chUid)) chUid else null
        query.userName = if(!Strings.isNullOrEmpty(userName)) userName else null
        query.userCard = if(!Strings.isNullOrEmpty(userCard)) userCard else null
        query.userMobile = if(!Strings.isNullOrEmpty(userMobile)) userMobile else null
        query.scenicId = scenicId
        query.scenicSid = scenicSid
        query.ticketId = ticketId
        query.cid = cid
        if (size == null || size <= 0) {
            query.size = 20
        }else {
            query.size = size
        }
        var tmpPage = 0
        if (page == null || page <= 0) {
            tmpPage = 1
        } else {
            tmpPage = page
        }
        query.offset = (tmpPage - 1) * query.size
        val total = orderDao.searchForAdminCount(query)
        val list = orderDao.searchForAdmin(query)
        val dtos = ArrayList<MOrderDto>()
        for (order in list) {
            val dto = orderTransformer.transform(order)!!
            dtos.add(dto)
        }
        val pagedList = PagedList(tmpPage, query.size, total, dtos)

        return ResultT(RESULT_SUCCESS, "ok", pagedList)
    }

    @GetMapping("card/by_code/{code}")
    fun getCardsByCode(
        @PathVariable("code",required = true) code:String
    ):ResultT<MTicketCardDto> {
        if(Strings.isNullOrEmpty(code)) {
            return ResultT(RESULT_FAIL,"激活码不存在")
        }
        val cardTkt = cardTicketDao.getByCode(code) ?: return ResultT(RESULT_FAIL,"激活码不存在")
        val dto = MTicketCardDto()
        dto.orderNo = cardTkt.orderId
        dto.buyTime = cardTkt.buyTime
        dto.cardNo = cardTkt.cardNo
        dto.entityCardNo = cardTkt.entityCardNo
        val subOrder = subOrderDao.get(cardTkt.orderSubId)
        if(subOrder != null) {
            try {
                val tktSnapshot = JSON.parseObject(subOrder.snapshot, TicketSnapshot::class.java)
                dto.ticketName = tktSnapshot.name
            }catch (ee:Exception) {}
        }
        dto.activatedTime = cardTkt.activatedTime
        if(cardTkt.bindId != null) {
            val bindInfo = cardTicketDao.getBindInfoById(cardTkt.bindId!!)
            if(bindInfo != null) {
                dto.activatedMobile = bindInfo.mobile
                dto.activatedFullname = bindInfo.fullName
                dto.activatedIdCard = bindInfo.idCard
            }
        }
        dto.code = cardTkt.code
        dto.lastActiveTime = cardTkt.lastActivateTime
        if(dto.activatedTime != null) {
            dto.isActivated = true
        } else {
            /**
             * 未激活则生成预定义卡号
             */
            while(true){
                dto.cardNo = String.format("100%05d", (cardTicketDao.getMaxCardNo() + 1))
                if(cardTicketDao.getByCardNo(dto.cardNo!!) == null) {
                    break
                }
            }
        }
        dto.dayIn = cardTkt.dayIn

        return ResultT(RESULT_SUCCESS,"ok",dto)
    }

    @PutMapping("card/active/{code}")
    fun activeCard(
        @PathVariable("code",required = true) code:String,
        @RequestParam("to_uid",required = true) toUid:String,
        @RequestParam("card_no",required = true) cardNo:String,
        @RequestParam("full_name",required = true) fullName:String,
        @RequestParam("id_card",required = true) idCard:String,
        @RequestParam("mobile",required = true) mobile:String,
        @RequestParam("expired_time",required = true) expiredTime:String,
        @RequestParam("day_in",required = true) dayIn:Int,
        @RequestParam("entity_card_no",required = false) entityCardNo:String?,
        @RequestParam("avatar",required = false) avatar:String?
    ):com.cj.ticketsys.controller.dto.Result {
        if(Strings.isNullOrEmpty(code)) {
            return com.cj.ticketsys.controller.dto.Result(RESULT_FAIL,"激活码不存在")
        }
        if(Strings.isNullOrEmpty(cardNo) || Strings.isNullOrEmpty(fullName)
            || Strings.isNullOrEmpty(idCard) || Strings.isNullOrEmpty(mobile)
            || Strings.isNullOrEmpty(toUid)) {
            return com.cj.ticketsys.controller.dto.Result(RESULT_FAIL,"参数错误")
        }
        val dateFmt = SimpleDateFormat("yyyy-MM-dd")
        var expiredDate: Date? = null
        try{
            expiredDate = dateFmt.parse(expiredTime)
        }catch (ee:Exception) {
            return com.cj.ticketsys.controller.dto.Result(RESULT_FAIL,"过期参数错误")
        }
        val cardTkt = cardTicketDao.getByCode(code) ?: return com.cj.ticketsys.controller.dto.Result(RESULT_FAIL,"激活码不存在")
        if(cardTkt.activatedTime != null) {
            return com.cj.ticketsys.controller.dto.Result(RESULT_FAIL,"激活码已被激活")
        }
        val bindInfo = CardTicketBindInfo()
        bindInfo.fullName = fullName
        bindInfo.idCard = idCard
        bindInfo.mobile = mobile
        bindInfo.avatar = avatar
        val ok = cardTicketDao.insertBindInfo(bindInfo) > 0
        if(!ok) {
            return com.cj.ticketsys.controller.dto.Result(RESULT_FAIL,"激活失败,请联系管理员")
        }
        cardTkt.activatedTime = Date()
        cardTkt.bindId = bindInfo.id
        cardTkt.cardNo = cardNo
        cardTkt.dayIn = dayIn
        cardTkt.expireTime = expiredDate
        cardTkt.entityCardNo = entityCardNo
        cardTkt.toUid = toUid
        val succ = cardTicketDao.update(cardTkt) > 0
        if(succ) {
            return com.cj.ticketsys.controller.dto.Result(RESULT_SUCCESS, "激活成功")
        }
        return com.cj.ticketsys.controller.dto.Result(RESULT_FAIL, "激活失败")
    }
}