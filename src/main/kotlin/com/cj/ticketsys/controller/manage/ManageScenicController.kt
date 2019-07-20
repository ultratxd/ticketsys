package com.cj.ticketsys.controller.manage

import com.cj.ticketsys.controller.BaseController
import com.cj.ticketsys.controller.dto.Result
import com.cj.ticketsys.controller.dto.ResultT
import com.cj.ticketsys.controller.manage.dto.MScenicDto
import com.cj.ticketsys.controller.manage.dto.MScenicSpotDto
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/ota/v1/manage/scenic")
class ManageScenicController : BaseController() {

    @GetMapping("all")
    fun gets(): ResultT<List<MScenicDto>> {
        return ResultT("", "", null)
    }

    @PostMapping("")
    fun createScenic(
        @RequestParam("name", required = true) name: String,
        @RequestParam("addr", required = true) addr: String
    ): Result {
        return Result("", "")
    }

    @PutMapping("")
    fun updateScenic(
        @RequestParam("id", required = true) id: Int,
        @RequestParam("name", required = false) name: String?,
        @RequestParam("addr", required = false) addr: String?
    ): Result {
        return Result("", "")
    }

    @GetMapping("spot/all")
    fun getSpots(
        @RequestParam("pid", required = false) pid: Int?
    ): ResultT<List<MScenicSpotDto>> {
        return ResultT("", "", null)
    }

    @PostMapping("spot")
    fun createSpot(
        @RequestParam("pid", required = true) pid: Int,
        @RequestParam("name", required = true) name: String
    ):Result {
        return Result("","")
    }

    @PutMapping("spot")
    fun updateSpot(
        @RequestParam("id", required = true) id: Int,
        @RequestParam("name", required = false) name: String?
    ): Result {
        return Result("", "")
    }
}