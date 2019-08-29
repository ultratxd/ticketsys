package com.cj.ticketsys.entities

enum class PriceDiscountTypes(val value: Short) : CodeBaseEnum {
    Nothing(0),
    Date(1),
    IDCard(2);

    override fun code(): Int {
        return this.value.toInt()
    }

}