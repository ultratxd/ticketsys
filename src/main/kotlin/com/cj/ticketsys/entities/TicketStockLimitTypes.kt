package com.cj.ticketsys.entities

enum class TicketStockLimitTypes(val value:Short) : CodeBaseEnum{
    All(1),
    Day(2),
    Month(3);

    override fun code(): Int {
        return this.value.toInt()
    }
}