/*
 * Copyright (c) 2004-2018 Tangxd All Rights Reserved.
 * @Author Tangxd
 * @Description
 * @Date 2018/09/06
 */

package com.cj.ticketsys.svc

interface DocTransformer<S, T> {
    fun transform(data: S): T?
}