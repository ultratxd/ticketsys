package com.cj.ticketsys.svc.entrance

import com.alibaba.fastjson.JSON
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.util.DigestUtils
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.security.Key
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESKeySpec
import kotlin.collections.ArrayList

class SCApi(private var userId: String, private var userKey: String) {

    val log: Logger = LoggerFactory.getLogger(SCApi::class.java)

    /**
     *   下单接口(游客在合作方下单后，通过该接口将订单信息推送到开放平台中)
     */
    fun createOrder(p: CreateOrderParameter): EntranceResult<CreateOrderResult> {
        val json = JSON.toJSONString(p)
        val requestBody = desEncrypt(json, userKey)
        val rHead = RequestHead()
        rHead.user_id = userId
        rHead.method = "CreateOrder"
        rHead.timestamp = System.currentTimeMillis() / 1000
        rHead.version = "v1.0"
        rHead.sign = encrypt(rHead.method, rHead.timestamp, rHead.version, requestBody)

        val rBody = RequestBody()
        rBody.requestHead = rHead
        rBody.requestBody = requestBody
        val bodyStr = JSON.toJSONString(rBody)
        val body = Base64.getEncoder().encodeToString(bodyStr.toByteArray())
        try {
            val resp = post(body, "http://fx.minticket.cn/api/open")
            val respUnBase64 = String(Base64.getDecoder().decode(resp))
            val respBody = JSON.parseObject(respUnBase64, ResponseBody::class.java)
            if (respBody.responseHead?.res_code == "1000") {
                val deStr = desDecrypt(respBody.responseBody, userKey)
                val cor = JSON.parseObject(deStr, CreateOrderResult::class.java)
                return EntranceResult(true, respBody.responseHead!!.res_msg, cor)
            }

            return EntranceResult(false, respBody.responseHead!!.res_msg)
        } catch (e: Exception) {
            log.error("scapi createorder fail", e)
            return EntranceResult(false, "fail")
        }
    }

    fun retCodeNotice(body: String): RetCodeNoticeBody? {
        val str = String(Base64.getDecoder().decode(body))
        val req = JSON.parseObject(str, RequestBody::class.java)
        val sign =
            encrypt(req.requestHead!!.method, req.requestHead!!.timestamp, req.requestHead!!.version, req.requestBody)
        if (sign == req.requestHead!!.sign) {
            val deStr = desDecrypt(req.requestBody, userKey)
            return JSON.parseObject(deStr, RetCodeNoticeBody::class.java)
        }
        return null
    }

    private fun post(message: String, postUrl: String): String? {
        val url = URL(postUrl)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.connectTimeout = 300000
        connection.connectTimeout = 300000
        connection.doOutput = true
        connection.doInput = true

        val postData: ByteArray = message.toByteArray(StandardCharsets.UTF_8)
        connection.setRequestProperty("charset", "utf-8")
        connection.setRequestProperty("Content-lenght", postData.size.toString())
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

        try {
            connection.outputStream.write(postData)
            connection.outputStream.flush()
            connection.outputStream.close()
        } catch (exception: Exception) {
            throw Exception("Exception while post the notification  $exception.message")
        }

        try {
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val output: String = reader.readLine()
            return output
        } catch (exception: Exception) {
            throw Exception("Exception while push the notification  $exception.message")
        }
    }

    private fun encrypt(
        method: String,
        timestamp: Long,
        version: String,
        reqBody: String
    ): String {
        //md5(user_id+method+timestamp+version+requestBody（des后的数据）+user_key)
        val str = "$userId$method$timestamp$version$reqBody$userKey"
        return DigestUtils.md5DigestAsHex(str.toByteArray(Charset.forName("utf-8")))
    }

    //des加密
    fun desEncrypt(input: String, password: String): String {
        val cipher = Cipher.getInstance("DES")
        val kf = SecretKeyFactory.getInstance("DES")
        val keySpe = DESKeySpec(password.toByteArray())
        val key: Key = kf.generateSecret(keySpe)
        //加密模式
        cipher.init(Cipher.ENCRYPT_MODE, key)
        //3.加密/解密
        val encrypt = cipher.doFinal(input.toByteArray())
        return Base64.getEncoder().encodeToString(encrypt)
    }

    //des解密
    fun desDecrypt(input: String, password: String): String {
        val cipher = Cipher.getInstance("DES")
        val kf = SecretKeyFactory.getInstance("DES")
        val keySpe = DESKeySpec(password.toByteArray())
        val key: Key = kf.generateSecret(keySpe)
        //解密模式
        cipher.init(Cipher.DECRYPT_MODE, key)
        //3.加密/解密
        //val encrypt = cipher.doFinal(input.toByteArray())
        //base64解码
        val encrypt = cipher.doFinal(Base64.getDecoder().decode(input))
        return String(encrypt)
    }
}

class RequestBody {
    var requestHead: RequestHead? = null
    var requestBody: String = ""
}

class RequestHead {
    var user_id = ""
    var method = ""
    var timestamp = 0L
    var version = ""
    var sign = ""
}

class ResponseBody {
    var responseHead: ResponseHead? = null
    var responseBody: String = ""
}

class ResponseHead {
    var res_code = ""
    var res_msg = ""
    var timestamp = 0L
}

class RetCodeNoticeBody {
    var orderSerialId = ""
    var partnerOrderId = ""
    var partnerCode = ""
    var partnerQRCodeAddress = ""
}

data class EntranceResult<T>(val ok: Boolean, val msg: String, val data: T? = null)

class CreateOrderResult {
    var partnerOrderId: String = ""
    var partnerCode: String = ""
    var partnerQRCodeAddress: String = ""
    var partnerAreaInfo: String = ""
}

class CreateOrderParameter {
    var orderSerialId: String = ""
    var productNo: String = ""
    var payType: Short = 0
    var tickets: Int = 0
    var price: Long = 0
    var contractPrice: Long = 0
    var isEvent: Int = 0
    var eventId: String = ""
    var bookName: String = ""
    var bookMobile: String = ""
    var idCard: String = ""
    var travelDate: String = ""
    var visitPerson:List<VisitPerson> = ArrayList()
}

data class VisitPerson(val name:String = "",val mobile:String = "",val idCard: String = "")