package com.cj.ticketsys.dao

import com.cj.ticketsys.entities.CodeBaseEnum

enum class TicketCategories(val value: Int) : CodeBaseEnum {
    Retail(1),
    Suit(2),
    Card(3);

    override fun code(): Int {
        return this.value.toInt()
    }

    companion object {
        fun prase(value: Int): TicketCategories {
            when (value) {
                1 -> return Retail
                2 -> return Suit
                3 -> return Card
            }
            throw IllegalArgumentException("value不存在枚举")
        }

        fun getName(category: TicketCategories): String {
            return when (category) {
                Retail -> "门票"
                Suit -> "套票"
                Card -> "年票"
            }
        }

        fun getName(value: Int): String {
            return when (value) {
                1 -> "门票"
                2 -> "套票"
                3 -> "年票"
                else -> "未知"
            }
        }
    }

}