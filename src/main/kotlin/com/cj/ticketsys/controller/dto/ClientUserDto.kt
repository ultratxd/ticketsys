package com.cj.ticketsys.controller.dto

import com.fasterxml.jackson.annotation.JsonProperty

open class ClientUserDto(@JsonProperty("uid") val uid:Int,
                         @JsonProperty("user_name") val userName:String,
                         @JsonProperty("remark") val remark:String,
                         @JsonProperty("serial_no") val serialNo:String) {

}