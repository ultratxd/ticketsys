package com.cj.ticketsys.controller.b2b

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/ota/v1/sync")
class CtripController {

    @PostMapping("/gate_log")
    fun gateLog(
            req: HttpServletRequest
    ) {

    }
}