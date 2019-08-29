package com.cj.ticketsys.thirdparty.ctrip

abstract class CtripRequest<T : CtripBody> {
    var header: CtripRequestHeader? = null
    abstract var body: T
}