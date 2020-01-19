package com.cj.ticketsys.entities

enum class OrderStates(val value:Short) : CodeBaseEnum {
    Init(0),
    Paied(10),
    Issuing(15),
    Issued(18),
    ReadyRefund(20),
    Refunded(30),
    RefundedPart(31),
    Used(40),
    Cancel(50),
    Closed(99),
    Unknown(100);

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
                31 -> return RefundedPart
                40 -> return Used
                50 -> return Cancel
                99 -> return Closed
                100 -> return Unknown
            }
            throw IllegalArgumentException("value不存在枚举")
        }
    }
}