package com.cj.ticketsys.entities

enum class OrderStates(val value:Short) : CodeBaseEnum {
    Init(0),
    Paied(10),
    Issuing(15),
    Issued(18),
    ReadyRefund(20),
    Refunded(30),
    Used(40),
    Closed(99);

    override fun code(): Int {
        return this.value.toInt()
    }
}