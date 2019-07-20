package com.cj.ticketsys.controller.dto

import com.cj.ticketsys.entities.ScenicSpot
import com.cj.ticketsys.svc.DocTransformer
import org.springframework.stereotype.Component

@Component
class ScenicSpotDocTransformer: DocTransformer<ScenicSpot,ScenicSpotDto> {
    override fun transform(data: ScenicSpot): ScenicSpotDto? {
        val dto = ScenicSpotDto(data.id)
        dto.name = data.name
        dto.pid = data.pid
        dto.ticketCount = data.tickets
        return dto
    }
}