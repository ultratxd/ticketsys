package com.cj.ticketsys.controller.manage

import com.cj.ticketsys.controller.BaseController
import com.cj.ticketsys.controller.dto.RESULT_FAIL
import com.cj.ticketsys.controller.dto.RESULT_SUCCESS
import com.cj.ticketsys.controller.dto.Result
import com.cj.ticketsys.controller.dto.ResultT
import com.cj.ticketsys.controller.manage.dto.MScenicDto
import com.cj.ticketsys.controller.manage.dto.MScenicSpotDto
import com.cj.ticketsys.controller.manage.transformer.MScenicTransformer
import com.cj.ticketsys.dao.ScenicDao
import com.cj.ticketsys.dao.ScenicSpotDao
import com.cj.ticketsys.entities.Scenic
import com.cj.ticketsys.entities.ScenicSpot
import com.cj.ticketsys.svc.DocTransformer
import com.google.common.base.Strings
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@CrossOrigin(origins = ["*"],maxAge = 3600,allowCredentials = "true")
@RestController
@RequestMapping("/ota/v1/manage/scenic")
class ManageScenicController : BaseController() {

    @Autowired
    private lateinit var scenicDao: ScenicDao

    @Autowired
    private lateinit var scenicSpotDao: ScenicSpotDao

    @Autowired
    private lateinit var mScenicTransformer: DocTransformer<Scenic, MScenicDto>

    @Autowired
    private lateinit var mSpotTransformer: DocTransformer<ScenicSpot, MScenicSpotDto>

    @GetMapping("all")
    fun gets(): ResultT<List<MScenicDto>> {
        val scenics = scenicDao.gets()
        val list = ArrayList<MScenicDto>()
        for (scenic in scenics) {
            list.add(mScenicTransformer.transform(scenic)!!)
        }
        return ResultT(RESULT_SUCCESS, "ok", list)
    }

    @GetMapping("{id}")
    fun get(
        @PathVariable("id", required = true) id: Int
    ): ResultT<MScenicDto> {
        if (id <= 0) {
            return ResultT(RESULT_FAIL, "参数错误")
        }
        val scenic = scenicDao.get(id)
        if (scenic != null) {
            return ResultT(RESULT_SUCCESS, "ok", mScenicTransformer.transform(scenic)!!)
        }
        return ResultT(RESULT_FAIL, "景区不存在")
    }

    @PostMapping("")
    fun createScenic(
        @RequestParam("name", required = true) name: String?,
        @RequestParam("addr", required = true) addr: String?
    ): Result {
        if (Strings.isNullOrEmpty(name) || Strings.isNullOrEmpty(addr)) {
            return Result(RESULT_FAIL, "参数不能为空")
        }
        val scenic = Scenic()
        scenic.name = name!!
        scenic.address = addr!!
        val c = scenicDao.insert(scenic)
        if (c > 0) {
            return Result(RESULT_SUCCESS, "添加成功")
        }
        return Result(RESULT_FAIL, "添加失败")
    }

    @PutMapping("")
    fun updateScenic(
        @RequestParam("id", required = true) id: Int?,
        @RequestParam("name", required = false) name: String?,
        @RequestParam("addr", required = false) addr: String?
    ): Result {
        if (Strings.isNullOrEmpty(name) || Strings.isNullOrEmpty(addr)) {
            return Result(RESULT_FAIL, "参数不能为空")
        }
        if (id == null || id <= 0) {
            return Result(RESULT_FAIL, "参数错误")
        }
        val scenic = scenicDao.get(id) ?: return Result(RESULT_FAIL, "景区不存在")
        scenic.name = name!!
        scenic.address = addr!!
        val c = scenicDao.update(scenic)
        if (c > 0) {
            return Result(RESULT_SUCCESS, "更新成功")
        }
        return Result(RESULT_FAIL, "更新失败")
    }

    @GetMapping("spot/{id}")
    fun getSpot(
        @PathVariable("id", required = true) id: Int
    ): ResultT<MScenicSpotDto> {
        if (id <= 0) {
            return ResultT(RESULT_FAIL, "参数错误")
        }
        val spot = scenicSpotDao.get(id)
        if (spot != null) {
            return ResultT(RESULT_SUCCESS, "ok", mSpotTransformer.transform(spot)!!)
        }
        return ResultT(RESULT_FAIL, "景点不存在")
    }

    @GetMapping("spot/all")
    fun getSpots(
        @RequestParam("pid", required = false) pid: Int?
    ): ResultT<List<MScenicSpotDto>> {
        if (pid == null || pid < 0) {
            return ResultT(RESULT_FAIL, "参数错误", null)
        }
        val spots = scenicSpotDao.gets(pid)
        val list = ArrayList<MScenicSpotDto>()
        for (spot in spots) {
            list.add(mSpotTransformer.transform(spot)!!)
        }
        return ResultT(RESULT_SUCCESS, "ok", list)
    }

    @PostMapping("spot")
    fun createSpot(
        @RequestParam("pid", required = true) pid: Int?,
        @RequestParam("name", required = true) name: String?
    ): Result {
        if (pid == null || pid <= 0) {
            return Result(RESULT_FAIL, "参数错误")
        }
        if (Strings.isNullOrEmpty(name)) {
            return Result(RESULT_FAIL, "参数错误")
        }
        val spot = ScenicSpot()
        spot.name = name!!
        spot.pid = pid
        val c = scenicSpotDao.insert(spot)
        if (c > 0) {
            scenicDao.updateSpotCount(pid, 1)
            return Result(RESULT_SUCCESS, "添加成功")
        }
        return Result(RESULT_FAIL, "添加失败")
    }

    @PutMapping("spot")
    fun updateSpot(
        @RequestParam("id", required = true) id: Int?,
        @RequestParam("name", required = false) name: String?
    ): Result {
        if (id == null || id <= 0) {
            return Result(RESULT_FAIL, "参数错误")
        }
        if (Strings.isNullOrEmpty(name)) {
            return Result(RESULT_FAIL, "参数错误")
        }
        val spot = scenicSpotDao.get(id) ?: return Result(RESULT_FAIL, "景点不存在")
        spot.name = name!!
        val c = scenicSpotDao.update(spot)
        if (c > 0) {
            return Result(RESULT_SUCCESS, "更新成功")
        }
        return Result(RESULT_FAIL, "更新失败")
    }
}