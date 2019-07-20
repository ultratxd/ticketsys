package com.cj.ticketsys.entities

enum class TicketStates(val value:Short) : CodeBaseEnum {
    Enabled(1),
    Disabled(2),
    Stop(3);

    override fun code(): Int {
        return this.value.toInt()
    }
}