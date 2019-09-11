package com.cj.ticketsys.controller

import com.cj.ticketsys.controller.dto.*
import com.cj.ticketsys.dao.ClientUserDao
import com.cj.ticketsys.entities.ClientLoginLog
import com.google.common.base.Strings
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/ota/v1/client")
class ClientUserController : BaseController() {

    @Autowired
    private lateinit var clientUserDao: ClientUserDao

    @PostMapping("/login")
    fun login(
        @RequestParam("user_name", required = false) userName: String?,
        @RequestParam("pwd", required = false) pwd: String?,
        req: HttpServletRequest
    ): ResultT<ClientUserDto> {
        if (Strings.isNullOrEmpty(userName) || Strings.isNullOrEmpty(pwd)) {
            return ResultT(RESULT_FAIL, "未填写用户名和密码")
        }
        val user = clientUserDao.getUserBy(userName!!) ?: return ResultT(RESULT_FAIL, "用户不存在")
        if (!user.enabled) {
            return ResultT(RESULT_FAIL, "用户已禁用")
        }
        if (user.pwd != pwd) {
            return ResultT(RESULT_FAIL, "密码错误")
        }
        val llog = ClientLoginLog()
        llog.ip = getIP(req) ?: ""
        llog.uid = user.id
        clientUserDao.insertLoginLog(llog)
        return ResultT(RESULT_SUCCESS, "成功登录", ClientUserDto(user.id, user.userName, user.remark, user.serialNo))
    }
}