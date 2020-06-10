package com.cj.ticketsys.svc.impl

import com.cj.ticketsys.svc.AcsCodeCipher
import org.springframework.stereotype.Component
import java.net.URLDecoder

@Component
class AcsCodeCipherImpl : AcsCodeCipher {
    val acsKey = "h3HKH9d5mqP2g3jL"

    override fun decrypt(code: String): String? {
        return try {
            AES.decrypt(URLDecoder.decode(code), acsKey)
        }catch (e:Exception) {
            null
        }
    }

    override fun encrypt(code: String): String? {
        return try {
            AES.encrypt(URLDecoder.decode(code), acsKey)
        }catch (e:Exception) {
            null
        }
    }
}