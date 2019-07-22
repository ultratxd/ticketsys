package com.cj.ticketsys.entities

enum class OrderStates(val value:Short) : CodeBaseEnum {
    Init(0),
    Paied(10),
    Issuing(15),
    Issued(18),
    ReadyRefund(20),
    Refunded(30),
    Used(40),
    Closed(99);

    override fun code(): Int {
        return this.value.toInt()
    }

    companion object {
        fun prase(value: Int): OrderStates {
            when (value) {
                0 -> return Init
                10 -> return Paied
                15 -> return Issuing
                18 -> return Issued
                20 -> return ReadyRefund
                30 -> return Refunded
                40 -> return Used
                99 -> return Closed
            }
            throw IllegalArgumentException("value不存在枚举")
        }
    }
}