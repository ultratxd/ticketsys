package com.cj.ticketsys.entities

enum class ChannelTypes(val value:Short) : CodeBaseEnum {
    Rack(1),
    ECommerce(2),
    Travel(3),
    Terminal(4);

    override fun code(): Int {
        return this.value.toInt()
    }
}