package com.cj.ticketsys.svc

interface InventoryManagement {
    fun surplus(tpId: Int): Int

    fun incrSolds(tpId: Int, c: Int)

    fun decrSolds(tpId: Int, c: Int)
}