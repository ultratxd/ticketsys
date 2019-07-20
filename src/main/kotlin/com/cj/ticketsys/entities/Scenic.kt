package com.cj.ticketsys.entities

import java.util.*

class Scenic: PropertyEntity() {
    var id:Int = 0
    var name:String = ""
    var address:String = ""
    var createTime: Date = Date()
    var spots:Int = 0
}