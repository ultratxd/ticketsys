package com.cj.ticketsys.svc

interface AcsCodeCipher {
    fun decrypt(code:String):String?
    fun encrypt(code:String):String?
}