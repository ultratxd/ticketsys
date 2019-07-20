package com.cj.ticketsys.svc


/**
 * 出票
 */
interface IssueTicketDeliver {
    fun issue(orderNo: String)
}