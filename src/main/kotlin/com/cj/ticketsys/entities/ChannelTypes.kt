package com.cj.ticketsys.entities

enum class ChannelTypes(val value:Short) : CodeBaseEnum {
    Rack(1),
    ECommerce(2),
    Travel(3),
    Terminal(4);

    override fun code(): Int {
        return this.value.toInt()
    }

    companion object {
        fun prase(value: Int): ChannelTypes {
            when (value) {
                1 -> return Rack
                2 -> return ECommerce
                3 -> return Travel
                4 -> return Terminal
            }
            throw IllegalArgumentException("value不存在枚举")
        }
    }
}