
package com.cj.ticketsys.controller.dto

open class Result(open val code: String, open val msg: String)

val RESULT_SUCCESS = "success"
val RESULT_FAIL = "fail"
val RESULT_UNAUTHORIZED = "unauthorized"