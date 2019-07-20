package com.cj.ticketsys.entities

import java.util.*

class ScenicSpot:PropertyEntity() {
    var id:Int = 0
    var pid:Int = 0
    var name:String = ""
    var tickets:Int = 0
    var createTime:Date = Date()
}