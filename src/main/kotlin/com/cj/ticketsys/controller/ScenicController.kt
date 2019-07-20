package com.cj.ticketsys.controller

import com.cj.ticketsys.controller.dto.*
import com.cj.ticketsys.dao.ScenicDao
import com.cj.ticketsys.entities.Scenic
import com.cj.ticketsys.svc.DocTransformer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/ota/v1/scenic")
class ScenicController: BaseController(){
    @Autowired
    private lateinit var scenicDao: ScenicDao

    @Autowired
    private lateinit var scenicTransformer: DocTransformer<Scenic, ScenicDto>


    @GetMapping("/all")
    fun getScenics(): ResultT<List<ScenicDto>> {

        val list = scenicDao.gets()
        val sDto = ArrayList<ScenicDto>()
        for (scenic in list) {
            val dto = scenicTransformer.transform(scenic)!!
            sDto.add(dto)
        }
        return ResultT(RESULT_SUCCESS, "", sDto)
    }

    @GetMapping("/{id}")
    fun getScenic(
        @PathVariable("id", required = true) id: Int
    ): ResultT<ScenicDto> {
        if (id <= 0) {
            return ResultT(RESULT_FAIL, "参数错误")
        }
        val scenic = scenicDao.get(id) ?: return ResultT(RESULT_FAIL, "数据不存在")
        val dto = scenicTransformer.transform(scenic)!!
        return ResultT(RESULT_SUCCESS, "ok", dto)
    }


}