package com.cj.ticketsys.svc.impl

import com.cj.ticketsys.controller.dto.RESULT_FAIL
import com.cj.ticketsys.controller.dto.RESULT_SUCCESS
import com.cj.ticketsys.controller.dto.Result
import com.cj.ticketsys.controller.dto.ResultT
import com.cj.ticketsys.dao.SpotItemDao
import com.cj.ticketsys.entities.spotItem.SpotItemVerificiation
import com.cj.ticketsys.entities.spotItem.SpotOrderTypes
import com.cj.ticketsys.svc.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.interceptor.TransactionAspectSupport
import java.net.URLDecoder
import java.nio.charset.Charset
import java.util.*
import java.util.regex.Pattern
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import kotlin.collections.ArrayList

/**
 * 小项目核销功能类
 */
@Service
class SpotItemVerifierImpl : SpotItemVerifier {

    @Autowired
    private lateinit var spotItemDao: SpotItemDao

    @Autowired
    private lateinit var clientSvc: ClientSvc

    @Autowired
    private lateinit var acsCodeCipher: AcsCodeCipher

    override fun resolve(code: String): ResultT<List<SpotItemOrderDetail>> {
        val spotItemOrderDetails = ArrayList<SpotItemOrderDetail>()
        //购买小项目扫码核销流程
        if(code.startsWith("B")) {
            val spOrder = spotItemDao.getOrderByCode(code) ?: return ResultT(RESULT_FAIL,"兑换码不存在")
            val spSubOrders = spotItemDao.querySubOrderByOrderId(spOrder.orderId)
            for(ssOrder in spSubOrders) {
                val detail = SpotItemOrderDetail()
                val item = spotItemDao.getSpotItem(ssOrder.itemId) ?: continue
                detail.item = item
                detail.nums = ssOrder.nums
                detail.perNums = ssOrder.perNums
                val verification = spotItemDao.getVerificationOnItemOrder(ssOrder.orderId,ssOrder.id)
                if(verification == null) {
                    detail.surplus = ssOrder.nums
                }else {
                    detail.surplus = ssOrder.nums - verification.nums
                }
                spotItemOrderDetails.add(detail)
            }
            return ResultT(RESULT_SUCCESS,"ok",spotItemOrderDetails)
        }
        //票务订单赠送小项目核销流程
        //2019092699000124491-78-1
        val deCode = acsCodeCipher.decrypt(code) ?: return ResultT(RESULT_FAIL,"解码错误")
        if(Pattern.compile("^\\w+-\\d+-\\d+\$").matcher(deCode).matches()) {
            //val code = deCode.split("-")[0]
            val ccid = deCode.split("-")[1]
            val cSubOrder = clientSvc.queryClientSubOrder(ccid.toInt()) ?: return ResultT(RESULT_FAIL,"二维码不存在")
            val tktItems = spotItemDao.getTicketItems(cSubOrder.ticketId)
            for(tItem in tktItems) {
                val detail = SpotItemOrderDetail()
                detail.item = spotItemDao.getSpotItem(tItem.itemId)
                detail.nums = tItem.nums * cSubOrder.nums
                detail.perNums= tItem.perNums
                val verification = spotItemDao.getVerificationByTkt(cSubOrder.clientOrderNo,deCode)
                if(verification == null) {
                    detail.surplus = detail.nums
                }else {
                    detail.surplus = detail.nums - verification.nums
                }
                spotItemOrderDetails.add(detail)
            }
            return ResultT(RESULT_SUCCESS,"ok",spotItemOrderDetails)
        }

        return ResultT(RESULT_FAIL,"")
    }

    @Transactional(rollbackFor = [java.lang.Exception::class])
    override fun verifying(code: String, itemNums: List<VerifyItemNums>, verifier:String): ResultT<List<SpotItemOrderDetail>> {
        /**
         * 单独购买小项目扫码核销
         */
        if(code.startsWith("B") && code.length < 15) {
            val verifyAfterDetail = ArrayList<SpotItemOrderDetail>()
            val spOrder = spotItemDao.getOrderByCode(code) ?: return ResultT(RESULT_FAIL,"兑换码不存在")
            val spSubOrders = spotItemDao.querySubOrderByOrderId(spOrder.orderId)
            for(ssOrder in spSubOrders) {
                val vin = itemNums.firstOrNull { a -> a.itemId == ssOrder.itemId } ?: continue
                val item = spotItemDao.getSpotItem(ssOrder.itemId) ?: continue
                val verification = spotItemDao.getVerificationOnItemOrder(ssOrder.orderId,ssOrder.id)
                var surplus = 0
                if(verification == null) {
                    surplus = ssOrder.nums
                } else {
                    surplus = ssOrder.nums - verification.nums
                }
                if(surplus <= 0) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()
                    return ResultT(RESULT_FAIL,item.name + "已无剩余票可用")
                }
                if(surplus - vin.nums < 0) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()
                    return ResultT(RESULT_FAIL,item.name + "剩余可用数量不足")
                }
                if(verification == null) {
                    val sivf = SpotItemVerificiation()
                    sivf.itemOrderId = ssOrder.orderId
                    sivf.itemSubId = ssOrder.id
                    sivf.orderType = SpotOrderTypes.BUY
                    sivf.createTime = Date()
                    sivf.nums = vin.nums
                    sivf.verifier = verifier
                    sivf.scenicSpotId = ssOrder.scenicSpotId
                    val ok = spotItemDao.insertVerification(sivf) > 0
                    if(!ok) {
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()
                        return ResultT(RESULT_FAIL,"核销失败")
                    }
                }else {
                    val ok = spotItemDao.incrVerificationNums(verification.id,vin.nums) > 0
                    if(!ok) {
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()
                        return ResultT(RESULT_FAIL,"核销失败")
                    }
                }
                val afterDetail = SpotItemOrderDetail()
                afterDetail.item = item
                afterDetail.nums = ssOrder.nums
                afterDetail.perNums = ssOrder.perNums
                afterDetail.surplus = surplus - vin.nums
                verifyAfterDetail.add(afterDetail)
            }
            return ResultT(RESULT_SUCCESS,"核销成功", verifyAfterDetail)
        }

        /**
         * 购票订单赠送小项目核销
         */
        val deCode = acsCodeCipher.decrypt(code) ?: return ResultT(RESULT_FAIL,"解码错误")
        if(Pattern.compile("^\\w+-\\d+-\\d+\$").matcher(deCode).matches()) {
            val ccid = deCode.split("-")[1]
            val cSubOrder = clientSvc.queryClientSubOrder(ccid.toInt()) ?: return ResultT(RESULT_FAIL,"二维码不存在")
            val tktItems = spotItemDao.getTicketItems(cSubOrder.ticketId)
            val verifyAfterDetail = ArrayList<SpotItemOrderDetail>()
            for(tItem in tktItems) {
                val vin = itemNums.firstOrNull { a -> a.itemId == tItem.itemId } ?: continue
                val item = spotItemDao.getSpotItem(tItem.itemId) ?: continue
                val verification = spotItemDao.getVerificationByTkt(cSubOrder.clientOrderNo,deCode)
                var surplus = 0
                if(verification == null) {
                    surplus = tItem.nums
                } else {
                    surplus = tItem.nums - verification.nums
                }
                if(surplus <= 0) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()
                    return ResultT(RESULT_FAIL,item.name + "已无剩余票可用")
                }
                if(surplus - vin.nums < 0) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()
                    return ResultT(RESULT_FAIL,item.name + "剩余可用数量不足")
                }
                if(verification == null) {
                    val sivf = SpotItemVerificiation()
                    sivf.clientOrderNo = cSubOrder.clientOrderNo
                    sivf.clientQrCode = deCode
                    sivf.orderType = SpotOrderTypes.TKT_GIFT
                    sivf.createTime = Date()
                    sivf.nums = vin.nums
                    sivf.verifier = verifier
                    sivf.scenicSpotId = item.scenicSpotId
                    val ok = spotItemDao.insertVerification(sivf) > 0
                    if(!ok) {
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()
                        return ResultT(RESULT_FAIL,"核销失败")
                    }
                }else {
                    val ok = spotItemDao.incrVerificationNums(verification.id,vin.nums) > 0
                    if(!ok) {
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()
                        return ResultT(RESULT_FAIL,"核销失败")
                    }
                }
                val afterDetail = SpotItemOrderDetail()
                afterDetail.item = item
                afterDetail.nums = tItem.nums
                afterDetail.perNums = tItem.perNums
                afterDetail.surplus = surplus - vin.nums
                verifyAfterDetail.add(afterDetail)
            }
            return ResultT(RESULT_SUCCESS,"核销成功", verifyAfterDetail)
        }
        return ResultT(RESULT_FAIL,"未匹配到核销码")
    }
}