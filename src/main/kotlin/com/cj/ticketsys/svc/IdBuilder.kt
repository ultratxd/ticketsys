package com.cj.ticketsys.svc

interface IdBuilder {
    fun newId(tag:String): String
}