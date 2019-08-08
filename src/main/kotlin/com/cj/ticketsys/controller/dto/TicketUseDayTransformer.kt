package com.cj.ticketsys.controller.dto

import com.cj.ticketsys.entities.TicketUseDate
import com.cj.ticketsys.svc.DocTransformer
import com.google.common.base.Strings
import org.springframework.stereotype.Component

@Component
class TicketUseDayTransformer: DocTransformer<TicketUseDate,TicketUseDateDto> {
    override fun transform(data: TicketUseDate): TicketUseDateDto? {
        val dto = TicketUseDateDto()
        dto.id = data.id
        dto.name = data.name
        dto.remark = data.remark
        dto.workDay = data.workDay
        dto.weekendDay = data.weekendDay
        dto.legalDay = data.legalDay
        if(!Strings.isNullOrEmpty(data.customDates)) {
            dto.customDates = data.customDates.split(",").map { a->a.toInt() }
        }
        if(!Strings.isNullOrEmpty(data.notDates)) {
            dto.notDates = data.notDates.split(",").map { a->a.toInt() }
        }
        dto.validity = data.validity
        return dto
    }
}