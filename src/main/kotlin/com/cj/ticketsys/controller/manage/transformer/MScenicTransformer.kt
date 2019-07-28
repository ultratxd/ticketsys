package com.cj.ticketsys.controller.manage.transformer

import com.cj.ticketsys.controller.manage.dto.MScenicDto
import com.cj.ticketsys.entities.Scenic
import com.cj.ticketsys.svc.DocTransformer
import org.springframework.stereotype.Component

@Component
class MScenicTransformer : DocTransformer<Scenic, MScenicDto> {
    override fun transform(data: Scenic): MScenicDto? {
        val dto = MScenicDto(data.id)
        dto.name = data.name
        dto.address = data.address
        dto.spotCount = data.spots
        dto.createTime = data.createTime
        dto.properties = data.map

        return dto
    }
}