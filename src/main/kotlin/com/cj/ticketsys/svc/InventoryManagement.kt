package com.cj.ticketsys.svc

interface InventoryManagement {
    fun surplus(tpId: Int): Int

    fun incrSolds(tktId:Int, tpId: Int, c: Int)

    fun decrSolds(tktId:Int, tpId: Int, c: Int)
}