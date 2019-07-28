package com.cj.ticketsys.controller.manage

import com.cj.ticketsys.controller.BaseController
import com.cj.ticketsys.controller.dto.RESULT_FAIL
import com.cj.ticketsys.controller.dto.RESULT_SUCCESS
import com.cj.ticketsys.controller.dto.ResultT
import com.cj.ticketsys.controller.dto.TicketPriceDto
import com.cj.ticketsys.controller.manage.dto.MTicketDto
import com.cj.ticketsys.controller.manage.dto.MTicketPriceDto
import com.cj.ticketsys.dao.TicketDao
import com.cj.ticketsys.dao.TicketPriceDao
import com.cj.ticketsys.dao.TicketQuery
import com.cj.ticketsys.entities.PagedList
import com.cj.ticketsys.entities.Ticket
import com.cj.ticketsys.entities.TicketPrice
import com.cj.ticketsys.svc.DocTransformer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/ota/v1/manage/ticket")
class ManageTicketController : BaseController() {

    @Autowired
    private lateinit var ticketDao: TicketDao

    @Autowired
    private lateinit var ticketPriceDao: TicketPriceDao

    @Autowired
    private lateinit var ticketTransformer: DocTransformer<Ticket, MTicketDto>

    @Autowired
    private lateinit var priceTransformer: DocTransformer<TicketPrice, MTicketPriceDto>

    @GetMapping("{id}")
    fun getTicket(
        @PathVariable("id", required = false) id: Int
    ): ResultT<MTicketDto> {
        val ticket = ticketDao.get(id) ?: return ResultT(RESULT_FAIL, "订单不存在")
        val dto = ticketTransformer.transform(ticket)!!
        val relatedTickets = ticketDao.getRelatedTicket(ticket.id)
        dto.relatedTickets = ArrayList()
        for (rTid in relatedTickets) {
            val relTk = ticketDao.get(rTid)
            if (relTk != null) {
                dto.relatedTickets!!.add(ticketTransformer.transform(relTk)!!)
            }
        }
        return ResultT(RESULT_SUCCESS, "ok", dto)
    }

    @GetMapping("search")
    fun searchTickets(
        @RequestParam("sid", required = false) sid: Int?,
        @RequestParam("name", required = false) name: String?,
        @RequestParam("state", required = false) state: Int?,
        @RequestParam("cid", required = false) cid: Int?,
        @RequestParam("front_view", required = false) frontView: Boolean?,
        @RequestParam("page", required = false) page: Int? = 1,
        @RequestParam("size", required = false) size: Int? = 20
    ): ResultT<PagedList<MTicketDto>> {

        val query = TicketQuery()
        query.sid = sid
        query.name = name
        query.state = state
        query.cid = cid
        query.frontView = frontView
        if (size == null || size <= 0) {
            query.size = 20
        }
        var tmpPage = 0
        if (page == null || page <= 0) {
            tmpPage = 1
        } else {
            tmpPage = page
        }
        query.offset = (tmpPage - 1) * query.size
        val total = ticketDao.searchCountForAdmin(query)
        val list = ticketDao.searchForAdmin(query)

        val dtos = ArrayList<MTicketDto>()
        for (ticket in list) {
            val dto = ticketTransformer.transform(ticket)!!
            val relatedTickets = ticketDao.getRelatedTicket(ticket.id)
            dto.relatedTickets = ArrayList()
            for (rTid in relatedTickets) {
                val relTk = ticketDao.get(rTid)
                if (relTk != null) {
                    dto.relatedTickets!!.add(ticketTransformer.transform(relTk)!!)
                }
            }
            dtos.add(dto)
        }
        val pagedList = PagedList(tmpPage, query.size, total, dtos)

        return ResultT(RESULT_SUCCESS, "ok", pagedList)
    }

    @GetMapping("prices/{ticketId}")
    fun getPrices(
        @PathVariable("ticketId", required = false) id: Int
    ) : ResultT<List<MTicketPriceDto>> {
        if (id <= 0) {
            return ResultT(RESULT_FAIL, "参数错误")
        }
        val prices = ticketPriceDao.gets(id)
        val dtos = ArrayList<MTicketPriceDto>()
        for (price in prices) {
            val dto = priceTransformer.transform(price)!!
            dtos.add(dto)
        }
        return ResultT(RESULT_SUCCESS, "ok", dtos)
    }
}