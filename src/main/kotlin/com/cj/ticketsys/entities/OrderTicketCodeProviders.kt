package com.cj.ticketsys.entities

enum class OrderTicketCodeProviders(val value:Short) : CodeBaseEnum {
    System(0),
    ShuCheng(1);

    override fun code(): Int {
        return this.value.toInt()
    }
}