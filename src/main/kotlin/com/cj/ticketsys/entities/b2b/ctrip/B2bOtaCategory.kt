package com.cj.ticketsys.entities.b2b.ctrip

import com.cj.ticketsys.entities.CodeBaseEnum

enum class B2bOtaCategory(val value:Short) : CodeBaseEnum {
    Ctrip(1);

    override fun code(): Int {
        return this.value.toInt()
    }
}