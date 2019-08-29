package com.cj.ticketsys.entities

enum class BuyTypes (val value:Short) : CodeBaseEnum {
    Online(0),
    Offline(1),
    Activity(2),
    Seckill(3);

    override fun code(): Int {
        return this.value.toInt()
    }

    companion object {
        fun prase(value: Short): BuyTypes {
            when (value) {
                0.toShort() -> return Online
                1.toShort() -> return Offline
                2.toShort() -> return Activity
                3.toShort() -> return Seckill
            }
            throw IllegalArgumentException("value不存在枚举")
        }
    }
}