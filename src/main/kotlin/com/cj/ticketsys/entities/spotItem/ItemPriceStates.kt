package com.cj.ticketsys.entities.spotItem

import com.cj.ticketsys.entities.CodeBaseEnum

enum class ItemPriceStates(val value:Short) : CodeBaseEnum {
    Enabled(1),
    Disabled(2),
    Stop(3);

    override fun code(): Int {
        return this.value.toInt()
    }

    companion object {
        fun prase(value: Int): ItemPriceStates {
            when (value) {
                1 -> return Enabled
                2 -> return Disabled
                3 -> return Stop
            }
            throw IllegalArgumentException("value不存在枚举")
        }
    }
}