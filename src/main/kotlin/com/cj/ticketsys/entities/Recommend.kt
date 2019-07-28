package com.cj.ticketsys.entities

import java.util.*

class Recommend : PropertyEntity() {
    var id:Int = 0
    var refId:Int = 0
    var type:Int = 0
    var createTime:Date = Date()
    var displayOrder:Int = 0
}