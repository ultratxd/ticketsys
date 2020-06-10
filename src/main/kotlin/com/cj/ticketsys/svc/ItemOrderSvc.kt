package com.cj.ticketsys.svc

import com.cj.ticketsys.entities.spotItem.SpotItemOrder
import com.cj.ticketsys.entities.spotItem.SpotItemSubOrder
import java.util.*
import kotlin.collections.ArrayList

interface ItemOrderSvc {
    fun create(order: SpotItemOrder, subOrder: ArrayList<SpotItemSubOrder>): Boolean

    fun cancelOrder(orderNo: String): Boolean

    fun completedPay(orderNo: String, payTime: Date, payNo: String): Boolean
}