package com.cj.ticketsys.entities

enum class RefundTypes(val value:Short) : CodeBaseEnum{
    NoAllow(0),
    Allow(1);

    override fun code(): Int {
        return this.value.toInt()
    }
}