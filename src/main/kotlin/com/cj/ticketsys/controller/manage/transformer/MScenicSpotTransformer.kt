package com.cj.ticketsys.controller.manage.transformer

import com.cj.ticketsys.controller.manage.dto.MScenicSpotDto
import com.cj.ticketsys.entities.ScenicSpot
import com.cj.ticketsys.svc.DocTransformer
import org.springframework.stereotype.Component

@Component
class MScenicSpotTransformer: DocTransformer<ScenicSpot, MScenicSpotDto> {
    override fun transform(data: ScenicSpot): MScenicSpotDto? {
        val dto = MScenicSpotDto(data.id)
        dto.name = data.name
        dto.pid = data.pid
        dto.ticketCount = data.tickets
        dto.createTime = data.createTime
        dto.properties = data.map
        return dto
    }
}