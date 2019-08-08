package com.cj.ticketsys.controller.manage.transformer

import com.cj.ticketsys.controller.manage.dto.MRecommendDto
import com.cj.ticketsys.entities.Recommend
import com.cj.ticketsys.svc.DocTransformer
import org.springframework.stereotype.Component

@Component
class MRecommendTransformer : DocTransformer<Recommend, MRecommendDto> {
    override fun transform(data: Recommend): MRecommendDto? {
        val dto = MRecommendDto()
        dto.id = data.id
        dto.createTime= data.createTime
        dto.displayOrder= data.displayOrder
        dto.refid = data.refId
        dto.type = data.type
        return dto
    }
}