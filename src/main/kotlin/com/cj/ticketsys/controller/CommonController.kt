package com.cj.ticketsys.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/ota/v1/common")
class CommonController: BaseController() {

    @GetMapping("/health")
    fun health(): String {

        return "ok";
    }
}