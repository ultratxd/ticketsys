package com.cj.ticketsys.entities

enum class TicketCodeStates(val value:Short) : CodeBaseEnum {
    Unused(0),
    Used(1),
    Expired(2),
    Invalid(4);

    override fun code(): Int {
        return this.value.toInt()
    }
}