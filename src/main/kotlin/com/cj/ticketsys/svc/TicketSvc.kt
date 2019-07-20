package com.cj.ticketsys.svc

import com.cj.ticketsys.entities.ChannelTypes
import com.cj.ticketsys.entities.Ticket
import com.cj.ticketsys.entities.TicketPrice
import java.util.*

interface TicketSvc {

    fun getTicket(ticketId: Int, scenicSid: Int, date: Date, channelType: ChannelTypes): Ticket?

    fun getTickets(scenicSid: Int, date: Date, channelType: ChannelTypes):Collection<Ticket>

    fun getTicketInMonth(ticketId: Int,year:Int, month: Int, channelType: ChannelTypes): Map<Int,Collection<TicketPrice>>
}