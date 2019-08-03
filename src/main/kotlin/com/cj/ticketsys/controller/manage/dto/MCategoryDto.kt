package com.cj.ticketsys.controller.manage.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class MCategoryDto(@JsonProperty("id") val id:Int, val name:String = "")