package com.cj.ticketsys.svc

interface SnapshotCreator<T, OutT> {
    fun create(obj: T): OutT
}