package com.cj.ticketsys.controller.dto

import com.cj.ticketsys.dao.ScenicSpotDao
import com.cj.ticketsys.entities.Scenic
import com.cj.ticketsys.entities.ScenicSpot
import com.cj.ticketsys.svc.DocTransformer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ScenicDocTransformer : DocTransformer<Scenic, ScenicDto> {

    @Autowired
    private lateinit var scenicSpotDao: ScenicSpotDao

    @Autowired
    private lateinit var scenicSpotDocTransformer: DocTransformer<ScenicSpot, ScenicSpotDto>

    override fun transform(data: Scenic): ScenicDto? {
        val dto = ScenicDto(data.id)
        dto.name = data.name
        dto.address = data.address
        dto.spotCount = data.spots

        val spots = scenicSpotDao.gets(data.id)
        val spotDtos = spots.map { a -> scenicSpotDocTransformer.transform(a)!! }
        dto.spots.addAll(spotDtos)

        return dto
    }
}