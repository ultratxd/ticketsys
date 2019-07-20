/*
 * Copyright (c) 2004-2018 Tangxd All Rights Reserved.
 * @Author Tangxd
 * @Description
 * @Date 2018/09/06
 */

package com.cj.ticketsys.controller.dto


data class ResultT<T> constructor(override val code: String, override val msg: String, val data: T? = null) : Result(code, msg)