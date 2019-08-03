package com.cj.ticketsys.entities

enum class OrderTicketCodeProviders(val value:Short) : CodeBaseEnum {
    System(0),
    ShuCheng(1);

    override fun code(): Int {
        return this.value.toInt()
    }

    companion object {
        fun prase(value: Int): OrderTicketCodeProviders {
            when (value) {
                0 -> return System
                1 -> return ShuCheng
            }
            throw IllegalArgumentException("value不存在枚举")
        }
    }
}