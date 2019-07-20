package com.cj.ticketsys.entities

enum class CardTypes(val value:Short) : CodeBaseEnum {
    IDCard(1),
    HKAndMacao(2),
    Passport(3);

    override fun code(): Int {
        return this.value.toInt()
    }

    companion object {
        fun prase(value: Int): CardTypes {
            when (value) {
                1 -> return IDCard
                2 -> return HKAndMacao
                3 -> return Passport
            }
            throw IllegalArgumentException("value不存在枚举")
        }
    }
}