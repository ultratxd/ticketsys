package com.cj.ticketsys.entities.spotItem

import com.cj.ticketsys.entities.CodeBaseEnum

enum class SpotOrderTypes(val value:Short) : CodeBaseEnum {
    BUY(1),
    TKT_GIFT(2);

    override fun code(): Int {
        return this.value.toInt()
    }

    companion object {
        fun prase(value: Int): SpotOrderTypes {
            when (value) {
                1 -> return SpotOrderTypes.BUY
                2 -> return SpotOrderTypes.TKT_GIFT
            }
            throw IllegalArgumentException("value不存在枚举")
        }
    }
}