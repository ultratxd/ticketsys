package com.cj.ticketsys.svc

import com.cj.ticketsys.entities.*
import java.util.*

interface TicketSvc {

    fun getTicket(ticketId: Int, date: Date, channelType: ChannelTypes): Ticket?

    fun getTickets(scenicSid: Int, date: Date, channelType: ChannelTypes): Collection<Ticket>

    fun getTicketInMonth(
        ticketId: Int,
        year: Int,
        month: Int,
        channelType: ChannelTypes
    ): Map<Int, Collection<TicketPrice>>

    fun createTicket(
        tkt: Ticket,
        scenicSids: List<Int>,
        tags: List<String>,
        relTktIds: List<Int>,
        vararg args: Any
    ): Boolean

    fun updateTicket(
        tkt: Ticket,
        scenicSids: List<Int>,
        tags: List<String>,
        relTktIds: List<Int>,
        vararg args: Any
    ): Boolean


    /**
     * 添加价格
     */
    fun createPrice(price: TicketPrice, uDate: TicketUseDate): Boolean

    fun updatePrice(price: TicketPrice, uDate: TicketUseDate): Boolean
}