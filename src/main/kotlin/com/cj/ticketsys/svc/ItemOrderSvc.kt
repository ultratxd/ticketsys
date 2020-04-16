package com.cj.ticketsys.svc

import com.cj.ticketsys.entities.spotItem.SpotItemOrder
import com.cj.ticketsys.entities.spotItem.SpotItemSubOrder

interface ItemOrderSvc {
    fun create(order: SpotItemOrder, subOrder: ArrayList<SpotItemSubOrder>): Boolean
}