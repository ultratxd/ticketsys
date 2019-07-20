package com.cj.ticketsys.controller.dto

import com.cj.ticketsys.entities.TicketUseDate
import com.cj.ticketsys.svc.DocTransformer
import org.springframework.stereotype.Component

@Component
class TicketUseDayTransformer: DocTransformer<TicketUseDate,TicketUseDateDto> {
    override fun transform(data: TicketUseDate): TicketUseDateDto? {
        val dto = TicketUseDateDto()
        dto.name = data.name
        dto.remark = data.remark
        dto.workDay = data.workDay
        dto.weekendDay = data.weekendDay
        dto.legalDay = data.legalDay
        dto.customDates = data.customDates
        dto.validity = data.validity
        return dto
    }
}