package com.cj.ticketsys.dao

import com.cj.ticketsys.entities.ClientLoginLog
import com.cj.ticketsys.entities.ClientUser
import org.apache.ibatis.annotations.*

@Mapper
interface ClientUserDao {

    @Select(
        "select * from client_users where user_name=#{userName}"
    )
    @Results(
        Result(column = "user_name", property = "userName"),
        Result(column = "create_time", property = "createTime"),
        Result(column = "serial_no", property = "serialNo"),
        Result(column = "scenic_sid", property = "scenicSid")
    )
    fun getUserBy(userName:String):ClientUser?

    @Select(
            "select * from client_users where serial_no=#{serialNo}"
    )
    @Results(
            Result(column = "user_name", property = "userName"),
            Result(column = "create_time", property = "createTime"),
            Result(column = "serial_no", property = "serialNo"),
            Result(column = "scenic_sid", property = "scenicSid")
    )
    fun getUserByNo(serial_no:String):ClientUser?

    @Insert("insert into client_login_logs(uid,ip,login_time) values(#{uid},#{ip},#{loginTime})")
    fun insertLoginLog(log:ClientLoginLog):Long
}