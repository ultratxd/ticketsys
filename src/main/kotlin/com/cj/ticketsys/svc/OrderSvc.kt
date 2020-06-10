package com.cj.ticketsys.svc

import com.cj.ticketsys.entities.Order
import com.cj.ticketsys.entities.OrderTicketCodeProviders
import com.cj.ticketsys.entities.SubOrder
import java.util.*
import kotlin.collections.ArrayList

interface OrderSvc {
    fun create(order: Order, subOrder: ArrayList<SubOrder>): Boolean

    fun completedPay(orderNo: String, payTime: Date, payNo: String): Boolean

    fun cancelOrder(orderNo: String): Boolean

    fun refundOrder(orderNo:String,refundTime:Date,refundNo:String):Boolean

    fun completedEnter(orderNo: String, enterCode: String, provider: OrderTicketCodeProviders): Boolean

    fun modifyUseDate(subOrderId:Int, newUseDate:Date): Boolean
}