package com.cj.ticketsys.controller.b2b.lvmama

import com.alibaba.fastjson.JSON
import com.cj.ticketsys.dao.OrderDao
import com.cj.ticketsys.dao.OrderTicketCodeDao
import com.cj.ticketsys.entities.Order
import com.cj.ticketsys.entities.OrderStates
import com.cj.ticketsys.svc.OrderSvc
import com.cj.ticketsys.svc.b2b.lvmm.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/ota/v1/b2b/lvmama")
class LvmamaController {
    @Autowired
    private lateinit var lvmmSvc: B2BLvmmSvc

    @Autowired
    private lateinit var orderDao: OrderDao

    @Autowired
    private lateinit var orderSvc: OrderSvc

    @Autowired
    private lateinit var orderTicketCodeDao: OrderTicketCodeDao

    @PostMapping("apply_code")
    @Transactional(rollbackFor = [Exception::class])
    fun applyCode(
        @RequestBody body: String
    ): LvResult {
        //将json转换成bean
        val orderParams: LvOrderParams? = try {
            JSON.parseObject(body, LvOrderParams::class.java)
        } catch (e: java.lang.Exception) {
            //如果json解析失败,说明参数格式错误
            return LvResultUtil.fail(
                LvResultCodeEnum.INVALID_PARAM_ERROR.status,
                LvResultCodeEnum.INVALID_PARAM_ERROR.message
            )
        }
        if (orderParams == null) {
            return LvResultUtil.fail(
                LvResultCodeEnum.SERVER_IS_ERROR.status,
                LvResultCodeEnum.SERVER_IS_ERROR.message
            )
        }
        //验证签名
        val boo: Boolean = LvSignUtil.verifySign(body)
        if (!boo) {
            return LvResultUtil.fail(
                LvResultCodeEnum.VERIFY_SIGN_FAIL.status,
                LvResultCodeEnum.VERIFY_SIGN_FAIL.message
            )
        }
        //根据驴妈妈平台订单id查询订单是否已存在
        var lvOrder = lvmmSvc.getOrderByOtaId(orderParams.serialNo)
        if (lvOrder == null) {
            //如果订单不存在，则保存订单数据
            //todo 本地业务逻辑：创建本地订单，判断库存是否充足，价格是否匹配等
            //设置本地订单号  todo 此时为idWorker.nextId()模拟生成本地订单号
            //将驴妈妈下单数据保存到数据库
            val lvResult = lvmmSvc.createOrder(orderParams)
            if(lvResult.status != "0") {
                return lvResult
            }
            lvOrder = lvmmSvc.getOrderByOtaId(orderParams.serialNo)
        }
        if(lvOrder == null) {
            return LvResultUtil.fail("4","找不到对应订单")
        }
        val tCode = orderTicketCodeDao.get(lvOrder.orderId)
            ?: return LvResultUtil.fail("3","创建码失败")

        //返回封装下单信息
        val lvApplyCodeResult = LvApplyCodeResult()
        //todo 设置本地订单id(此时为模拟生成)
        //todo 设置本地订单id(此时为模拟生成)
        lvApplyCodeResult.orderId = lvOrder.orderId
        //todo 设置取票、入园凭证码。多个时以逗号分隔，状态码不为0的时候为空
        //todo 设置取票、入园凭证码。多个时以逗号分隔，状态码不为0的时候为空
        lvApplyCodeResult.authCode = tCode.code
        //todo 设置二维码超链接。多个可以用逗号隔开
        //todo 设置二维码超链接。多个可以用逗号隔开
        lvApplyCodeResult.codeURL = ""

        //todo 如果下单需要审核，则返回此结果
        /*lvApplyCodeResult.setStatus(LvResultCodeEnum.AUDIT.getStatus());
        lvApplyCodeResult.setMsg(LvResultCodeEnum.AUDIT.getMessage());
        return LvResultUtil.success(lvApplyCodeResult);*/

        //todo 如果下单不需要审核，则返回此结果

        //todo 如果下单需要审核，则返回此结果
        /*lvApplyCodeResult.setStatus(LvResultCodeEnum.AUDIT.getStatus());
        lvApplyCodeResult.setMsg(LvResultCodeEnum.AUDIT.getMessage());
        return LvResultUtil.success(lvApplyCodeResult);*/

        //todo 如果下单不需要审核，则返回此结果
        lvApplyCodeResult.status = LvResultCodeEnum.SUCCESS.status
        lvApplyCodeResult.msg = ""
        return lvApplyCodeResult
    }

    /**
     * 废单接口
     * 业务描述：用户在线申请退款时驴妈妈调用的接口
     *
     * @param discardCode json
     * @return String
     */
    @PostMapping("discard_code")
    @Transactional(rollbackFor = [java.lang.Exception::class])
    fun discardCode(discardCode:String):LvResult
    {
        //获取请求参数中的本地订单id
        val orderId: String =  try {
            JSON.parseObject(discardCode).getString("extId")
        } catch (e: java.lang.Exception) {
            return LvResultUtil.fail(
                LvResultCodeEnum.INVALID_PARAM_ERROR.status,
                LvResultCodeEnum.INVALID_PARAM_ERROR.message
            )
        } ?: return LvResultUtil.fail(
            LvResultCodeEnum.VERIFY_SIGN_FAIL.status,
            LvResultCodeEnum.VERIFY_SIGN_FAIL.message
        )
        //验证签名
        val boo = LvSignUtil.verifySign(discardCode)
        if (!boo) {
            return LvResultUtil.fail(
                LvResultCodeEnum.VERIFY_SIGN_FAIL.status,
                LvResultCodeEnum.VERIFY_SIGN_FAIL.message
            )
        }

        //根据本地订单id查询订单是否存在
        val order: Order = orderDao.get(orderId)
            ?: //如果订单不存在，返回订单不存在
            return LvResultUtil.fail(
                LvResultCodeEnum.ORDER_NOT_EXIST.status,
                LvResultCodeEnum.ORDER_NOT_EXIST.message
            )
        if(order.state == OrderStates.Used) {
            return LvResultUtil.fail("6","订单已被使用")
        }
        val ok = orderSvc.refundOrder(orderId, Date(),"")
        if(ok) {
            return LvResultUtil.fail(LvResultCodeEnum.SUCCESS.status,"退单成功")
        }
        return LvResultUtil.fail(LvResultCodeEnum.SERVER_IS_ERROR.status,"系统异常")
    }


    @PostMapping("sms_resend")
    fun smsResend(smsResend: String): LvResult {
        //获取本地订单id
        try {
            val orderId = JSON.parseObject(smsResend).getString("extId")
        } catch (e: java.lang.Exception) {
            //如果json解析失败,说明参数格式错误
            return LvResultUtil.fail(
                LvResultCodeEnum.INVALID_PARAM_ERROR.status,
                LvResultCodeEnum.INVALID_PARAM_ERROR.message
            )
        }
        //验证签名
        val boo = LvSignUtil.verifySign(smsResend)
        if (!boo) {
            return LvResultUtil.fail(
                LvResultCodeEnum.VERIFY_SIGN_FAIL.status,
                LvResultCodeEnum.VERIFY_SIGN_FAIL.message
            )
        }
        //todo 业务逻辑:短信发送 此为模拟短信发送成功返回
        val isSend = true
        //短信发送
        return if (!isSend) {
            //如果短信发送失败
            LvResultUtil.fail(
                LvResultCodeEnum.SEND_MESSAGE_FAILED.status,
                LvResultCodeEnum.SEND_MESSAGE_FAILED.message
            )
        } else LvResultUtil.success()
    }
}