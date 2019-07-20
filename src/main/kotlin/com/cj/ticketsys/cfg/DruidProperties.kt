package com.cj.ticketsys.cfg

class DruidProperties {
    var url: String? = null
    var driverClassName: String? = null
    var username: String? = null
    var password: String? = null
    var initialSize: Int = 0
    var minIdle: Int = 0
    var maxWait: Long = 0
    var maxActive: Int = 0
    var minEvictableIdleTimeMillis: Long = 0
}